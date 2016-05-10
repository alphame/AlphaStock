package org.stargazerlan.alphastock;

import android.nfc.Tag;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Lan Baoping on 2016/5/3.
 */
public class StocksAutoUpdate extends Thread {
    private ArrayList<Stock> stocks;
    private Handler uiHandler;
    private int updateInterval = 5000;
    private AlphaStock alphaStock = new AlphaStock();

    private final static String TAG = "StocksAutoUpdate Thread";

    public StocksAutoUpdate(ArrayList<Stock> stocks, Handler uiHandler) {
        this.stocks = stocks;
        this.uiHandler = uiHandler;
    }

    @Override
    public void run() {
        while (true) {
            if (stocks!= null && !stocks.isEmpty()) {
                for (Stock each : stocks) each.copyOf(alphaStock.search(each.getCode()));
            }
            try {
                Thread.sleep(updateInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Message msg = Message.obtain();
            msg.what = 1;
            uiHandler.sendMessage(msg);
            Log.v(TAG, "Stocks refreshed.");
        }
    }
}
