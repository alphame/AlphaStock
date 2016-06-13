package org.stargazerlan.alphastock;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class NotificationService extends Service {
    private Stocks stocks;
    private AlphaStock alphaStock;
    private boolean isPause = false;
    private WatcherThread watcherThread;


    public class MBinder extends Binder {
        NotificationService getInstance() {
            return NotificationService.this;
        }
    }

    private class WatcherThread extends Thread {
        @Override
        public void run() {


            while (true) {
                if (stocks.getSize() > 0 && !isPause && UpdatePeriod.isInPeriod()) {
                    for (int i = 0; i < stocks.getSize(); i++) {
                        Stock each = stocks.getStock(i);
                        String warningPriceBigger = each.getWarningPriceBigger();
                        String warningPriceSmaller = each.getWarningPriceSmaller();
                        String warningRate = each.getWarningRate();

                        Stock newOne = alphaStock.search(each.getCode());
                        if (newOne == null) continue;
                        String currentPrice = newOne.getCurrentPrice();
                        String currentRate = newOne.getRate();
                        if (currentPrice.matches("0.?0*")) continue;

                        if (inputValidatoin(warningPriceBigger)) {
                            if (Float.valueOf(currentPrice) > Float.valueOf(warningPriceBigger)) {
                                makeNotification(each.getName(), currentPrice, warningPriceBigger);
                            }

                        }
                        if (inputValidatoin(warningPriceSmaller)) {
                            if (Float.valueOf(currentPrice) < Float.valueOf(warningPriceSmaller)) {
                                makeNotification(each.getName(), currentPrice, warningPriceSmaller);
                            }

                        }
                        if (inputValidatoin(warningRate)) {
                            if (Float.valueOf(currentRate) > Float.valueOf(warningRate)) {
                                makeNotification(each.getName(), currentRate, warningRate);
                            }

                        }
                    }
                }

                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void makeNotification(String stockName, String currentPrice, String warningPrice) {
        NotificationManager  manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder   builder = new NotificationCompat.Builder(this);
        builder.setAutoCancel(true);
        builder.setSmallIcon(R.drawable.ic_stat_name);
        builder.setContentTitle(stockName + getString(R.string.reached));
        builder.setTicker(stockName + getString(R.string.reached));
        builder.setWhen(System.currentTimeMillis());
        builder.setDefaults(Notification.DEFAULT_ALL);
        Intent intent = new Intent().setClass(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pIntent);

        builder.setContentText(getString(R.string.currentValue) + ":" + currentPrice + "\n"
                    + getString(R.string.warningValue) + ":" + warningPrice);
        manager.notify(stockName.hashCode() + 1, builder.build());

    }

    private boolean inputValidatoin(String value) {
        if (value != null && !value.isEmpty()
                && !value.matches("0.?0*")) {
            return true;
        }
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        alphaStock = new AlphaStock();
        stocks = Stocks.getInstance();
        watcherThread = new WatcherThread();
        watcherThread.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public NotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MBinder();
    }

}
