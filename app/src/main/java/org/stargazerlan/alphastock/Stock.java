package org.stargazerlan.alphastock;

import java.io.Serializable;

/**
 * Created by StargazerLan on 2016/4/24.
 */
public class Stock implements Serializable {
    public final static int BIGGER = 1;
    public final static int SMALLER = 0;

    private String news;
    private String name;
    private String rate;
    private String code;
    private String openPrice;
    private String yClosePrice;
    private String lowestPrice;
    private String highestPrice;
    private String currentPrice;
    private String shares;
    private String volume;
    private String warningPriceBigger;
    private String warningPriceSmaller;
    private String warningRate;
    private String[] buyingBit;
    private String[] sellingBit;
    private String time;
    private String date;
    private boolean isNagetive = false;

    public void copyFrom(Stock stock) {
        if (stock != null) {
            news = stock.getNews();
            name = stock.getName();
            rate = stock.getRate();
            code = stock.getCode();
            openPrice = stock.getOpenPrice();
            yClosePrice = stock.getyClosePrice();
            highestPrice = stock.getHighestPrice();
            currentPrice = stock.getCurrentPrice();
            shares = stock.getShares();
            volume = stock.getVolume();
            if (stock.getWarningPriceBigger() != null) warningPriceBigger = stock.getWarningPriceBigger();
            if (stock.getWarningPriceSmaller() != null) warningPriceSmaller = stock.getWarningPriceSmaller();
            if (stock.getWarningRate() != null) warningRate = stock.getWarningRate();
            buyingBit = stock.getBuyingBit();
            sellingBit = stock.getSellingBit();
            time = stock.getTime();
            date = stock.getDate();
            isNagetive = stock.isNagetive();
        }
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(String openPrice) {
        this.openPrice = openPrice;
    }

    public String getyClosePrice() {
        return yClosePrice;
    }

    public void setyClosePrice(String yClosePrice) {
        this.yClosePrice = yClosePrice;
    }

    public String getShares() {
        return shares;
    }

    public void setShares(String shares) {
        this.shares = shares;
    }

    public String[] getBuyingBit() {
        return buyingBit;
    }

    public void setBuyingBit(String[] buyingBit) {
        this.buyingBit = buyingBit;
    }

    public String[] getSellingBit() {
        return sellingBit;
    }

    public void setSellingBit(String[] sellingBit) {
        this.sellingBit = sellingBit;
    }

    public String getWarningPriceBigger() {
        return warningPriceBigger;
    }

    public void setWarningPriceBigger(String warningPriceBigger) {
        this.warningPriceBigger = warningPriceBigger;
    }

    public String getWarningPriceSmaller() {
        return warningPriceSmaller;
    }

    public void setWarningPriceSmaller(String warningPriceSmaller) {
        this.warningPriceSmaller = warningPriceSmaller;
    }

    public boolean isNagetive() {
        return isNagetive;
    }

    public void setNagetive(boolean nagetive) {
        isNagetive = nagetive;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getRate() {
        return rate;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLowestPrice() {
        return lowestPrice;
    }

    public void setLowestPrice(String lowestPrice) {
        this.lowestPrice = lowestPrice;
    }

    public String getHighestPrice() {
        return highestPrice;
    }

    public void setHighestPrice(String highestPrice) {
        this.highestPrice = highestPrice;
    }

    public String getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(String currentPrice) {
        this.currentPrice = currentPrice;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getNews() {
        return news;
    }

    public void setNews(String news) {
        this.news = news;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWarningRate() {
        return warningRate;
    }

    public void setWarningRate(String warningRate) {
        this.warningRate = warningRate;
    }
}
