package org.stargazerlan.alphastock;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;

public class NotificationService extends Service {
    private AlphaStock alphaStock = new AlphaStock();
    private ArrayList<Stock> stocks = new ArrayList<Stock>();
    Notification.Builder notificationBuilder;
    NotificationManager notificationManager;

    private final static String TAG = "NotificationService";
    private boolean isRunning = true;

    public class MBinder extends Binder {
        public NotificationService getService() {
            return NotificationService.this;
        }
    }

    public NotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationBuilder = new Notification.Builder(NotificationService.this);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread(new Runnable() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void run() {
                while (isRunning) {
                    if (stocks != null && !stocks.isEmpty()) {
                        for (Stock each : stocks) {
                            Stock newOne = alphaStock.search(each.getCode());
                            try {
                                if (Float.valueOf(newOne.getCurrentPrice()) >= Float.valueOf(each.getWarningPrice())) {
                                    makeNotification(each, newOne, "price");
                                }
                                if (Float.valueOf(newOne.getRate()) >= Float.valueOf(each.getWarningRate())) {
                                    makeNotification(each, newOne, "rate");
                                }
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                                continue;
                            }
                        }
                    }
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        Log.v(TAG, "notification service started.");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
    }

    public void addStock(Stock stock) {
        if (stock != null && !stock.getCode().isEmpty()
                && stock.getWarningPrice() != null
                && !stock.getWarningPrice().isEmpty()
                && stock.getWarningRate() != null
                && !stock.getWarningRate().isEmpty()) stocks.add(stock);
    }

    public void delStock(Stock stock) {
        if (stock != null) stocks.remove(stock);
    }

    public void clear() {
        stocks.clear();
    }

    private String getRidOfTheSymbols(String value) {
        if (value == null || value.isEmpty()) return null;
        return null;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void makeNotification(Stock each, Stock newOne, String type) {
        if (newOne == null || each == null || type == null || type.isEmpty()) return;
        if (type.equals("price")) {
            notificationBuilder.setSmallIcon(R.drawable.ic_stat_name);
            notificationBuilder.setContentTitle(each.getCode() + " has reached the warning price");
            notificationBuilder.setContentText("current price:" + newOne.getCurrentPrice());
            notificationManager.notify(0, notificationBuilder.build());
        }
        if (type.equals("rate")) {
            notificationBuilder.setSmallIcon(R.drawable.ic_stat_name);
            notificationBuilder.setContentTitle(each.getCode() + " has reached the warning rate");
            notificationBuilder.setContentText("current rate:" + newOne.getRate());
            notificationManager.notify(0, notificationBuilder.build());
        }
    }
}
