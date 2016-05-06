package org.stargazerlan.alphastock;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by StargazerLan on 2016/4/30.
 */
public class MyListViewAdapter extends BaseAdapter {
    private Context context;
    private List<Stock> stocks;
    private LayoutInflater layoutInflater;


    public MyListViewAdapter (Context context, List<Stock> stocks) {
        this.context = context;
        this.stocks = stocks;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return stocks.size();
    }

    @Override
    public Object getItem(int position) {
        return stocks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Stock stock = stocks.get(position);
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.listviewitem, parent, false);
        }

        TextView code = (TextView)convertView.findViewById(R.id.codeTextView);
        TextView curPrice = (TextView)convertView.findViewById(R.id.curPriceTextView);
        TextView rate = (TextView)convertView.findViewById(R.id.rateTextView);
        code.setText(stock.getCode());
        curPrice.setText(stock.getCurrentPrice());
        rate.setText(stock.getRate());
        setTextColorByRate(stock, code, curPrice, rate);
        return convertView;
    }

    private void setTextColorByRate(Stock stock, TextView... params) {
        if (params.length != 3 || stock == null || stock.getRate() == null) return;
        if (stock.getRate().trim().startsWith("-")) {
            for (TextView each:params) each.setTextColor(Color.rgb(0,255,0));
        } else if (stock.getRate().equals("0.00%")) {
            for (TextView each:params) each.setTextColor(Color.rgb(0, 0, 255));
        } else {
            for (TextView each:params) each.setTextColor(Color.rgb(255, 0, 0));
        }
    }
}
