package org.stargazerlan.alphastock;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private StocksListFragment stocksListFragment;
    private boolean isBinded;
    private Stocks stocks;
    private AlphaStock alphaStock;

    private NotificationService notificationService;

    private ServiceConnection serviceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            NotificationService.MBinder mBinder = (NotificationService.MBinder)service;
            notificationService = mBinder.getInstance();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    public void switchToSNFragment(int position) {
        Stock stock = stocks.getStock(position);
        StockNotificationFragment snf = new StockNotificationFragment();
        snf.setStock(stock);

        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.fragment_content, snf);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
    }

    public void switchToMainFragment() {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.fragment_content, stocksListFragment);
        ft.commit();
    }

    public NotificationService getNotificationService() {
        return notificationService;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        alphaStock = new AlphaStock();
        stocks = Stocks.getInstance();
        stocks.setMainActivity(this);
        fragmentsInit();
        serviceInit();
    }



    @Override
    protected void onPause() {
        super.onPause();
        stocks.saveStocks();
    }

    @Override
    public void onStop() {
        super.onStop();
        stocks.saveStocks();
        if (isBinded) {
            unbindService(serviceConn);
            isBinded = false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stocks.saveStocks();
        if (isBinded) {
            unbindService(serviceConn);
            isBinded = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        switch (item.getItemId()) {
            case R.id.addStockItem:
                builder.setTitle(getString(R.string.inputStockCode));
                final View view = inflater.inflate(R.layout.add_stock_dialog, null);
                builder.setView(view);
                builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText input = (EditText) view.findViewById(R.id.addStockEditText);
                        String stockCode = input.getText().toString();
                        if (stockCode == null || stockCode.isEmpty()) return;
                        new AsyncTask<String, String, Stock>() {

                            @Override
                            protected Stock doInBackground(String... params) {
                                Stock stock = alphaStock.search(params[0]);
                                return stock;
                            }

                            @Override
                            protected void onPostExecute(Stock stock) {
                                super.onPostExecute(stock);
                                if (stock != null) stocks.addStock(stock);
                                stocksListFragment.notifyForChange();
                            }
                        }.execute(stockCode);
                    }
                });
                builder.setNegativeButton(R.string.cancel, null);
                builder.create().show();
                break;
            case R.id.clearItem:
                builder.setTitle(R.string.clear);
                builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        stocks.clear();
                        stocksListFragment.notifyForChange();
                    }
                });
                builder.setNegativeButton(R.string.cancel, null);
                builder.create().show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fragmentsInit() {
        stocksListFragment = new StocksListFragment();
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.fragment_content, stocksListFragment);
        ft.commit();
    }

    private void serviceInit() {
        Intent intent = new Intent();
        intent.setClass(this, NotificationService.class);
        isBinded = bindService(intent, serviceConn, Context.BIND_AUTO_CREATE);
        startService(intent);
    }
}
