package org.stargazerlan.alphastock;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PersistableBundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AddStockDialog.DialogListener {
    private ListView stocksListView;
    private AlphaStock alphaStock;
    private ArrayList<Stock> stocks;
    private MyListViewAdapter myListViewAdapter;
    private AddStockDialog addStockDialog;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private StocksAutoUpdate stocksAutoUpdate;
    private NotificationService notificationService;
    private boolean isBinded;

    private final static String TAG = "MainActivity";

    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                myListViewAdapter.notifyDataSetChanged();
            }
        }
    };

    private ServiceConnection serviceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            notificationService = ((NotificationService.MBinder)service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            notificationService = null;
            isBinded = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        alphaStock = new AlphaStock();

        preferences = getSharedPreferences(this.getLocalClassName(), MODE_PRIVATE);
        editor = preferences.edit();

        stocks = getStocks();

        listViewInit();
        addStockDialog = new AddStockDialog();
        stocksAutoUpdateStart();
        serviceInit();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        AlertDialog.Builder alertBuilder;
        switch (item.getItemId()) {
            case R.id.deleteStockItem:
                alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setTitle("Delete this stock?");
                alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        stocks.remove(info.position - 1);
                        myListViewAdapter.notifyDataSetChanged();
                    }
                });
                alertBuilder.setNegativeButton("Cancel", null);
                alertBuilder.create().show();
                return true;
            case R.id.addForAlarmItem:
                alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setTitle("Set the price and rate to be alarmed.");
                View v = getLayoutInflater().inflate(R.layout.add_alarm_dialog, null);
                alertBuilder.setView(v);
                final EditText addPrice = (EditText)v.findViewById(R.id.addAlarmPrice);
                final EditText addRate = (EditText)v.findViewById(R.id.addAlarmRate);

                alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String code = stocks.get(info.position - 1).getCode();
                        String price = addPrice.getText().toString();
                        String rate = addRate.getText().toString();
                        Stock stock = new Stock();
                        if (code != null) stock.setCode(code);
                        if (price != null) stock.setWarningPrice(price);
                        if (rate != null) stock.setWarningRate(rate);
                        if (notificationService != null) {
                            notificationService.addStock(stock);
                            Log.v(TAG, "alarm stock added.");
                        } else Log.v(TAG, "adding alarm stock failed.");
                    }
                });
                alertBuilder.setNegativeButton("CANCEL", null);
                alertBuilder.create().show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        saveStocks();
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveStocks();
        unbindService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveStocks();
        unbindService();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addStockItem:
                addStockDialog.show(getFragmentManager(), "AddStockDialog");
                break;
            case R.id.clearItem:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Clear all stocks?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        stocks.clear();
                        myListViewAdapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create().show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void serviceInit() {
        Intent intent = new Intent();
        intent.setClass(this, NotificationService.class);
        startService(intent);
        isBinded = bindService(intent, serviceConn, BIND_AUTO_CREATE);
    }

    private void unbindService() {
        if (isBinded) {
            unbindService(serviceConn);
            isBinded = false;
        }
    }

    private void listViewInit() {
        stocksListView = (ListView) findViewById(R.id.stocksListView);
        myListViewAdapter = new MyListViewAdapter(this, stocks);
        stocksListView.addHeaderView(getLayoutInflater().inflate(R.layout.list_view_header, null));
        stocksListView.setAdapter(myListViewAdapter);
        registerForContextMenu(stocksListView);
    }

    private void stocksAutoUpdateStart() {
        stocksAutoUpdate = new StocksAutoUpdate(stocks, uiHandler);
        stocksAutoUpdate.start();
    }

    private void saveStocks() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(stocks);
            oos.close();
            String encoded = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
            editor.putString("stocks", encoded);
            editor.commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Stock> getStocks() {
        ArrayList<Stock> stocks = new ArrayList<Stock>();
        String str = preferences.getString("stocks", null);
        if (str == null) return stocks;
        byte[] buff = Base64.decode(str, Base64.DEFAULT);
        try {
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(buff));
            stocks = (ArrayList<Stock>) ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
            return stocks;
        }
        return stocks;
    }

    private String inputStrFilter(String text) {
        String result = "";
        if (text == null || text.isEmpty() || text.length() != 6) return result;
        try {
            int i = Integer.valueOf(text);
            result = text;
        } catch (Exception e) {
            Log.v("Stock Code Error", e.toString());
            return result;
        }
        return result;
    }

    @Override
    public void onPositiveButtonClicked(AddStockDialog dialog) {
        String text = dialog.getStockCode();
        if (inputStrFilter(text).isEmpty()) return;

        new AsyncTask<String, Stock, Stock>() {

            @Override
            protected Stock doInBackground(String... params) {
                Stock stock = alphaStock.search(params[0]);
                stocks.add(stock);
                return stock;
            }

            @Override
            protected void onPostExecute(Stock stock) {
                super.onPostExecute(stock);
                myListViewAdapter.notifyDataSetChanged();
            }
        }.execute(text);
    }


}
