package org.stargazerlan.alphastock;

import java.io.Serializable;

/**
 * Created by StargazerLan on 2016/4/24.
 */
public class Stock implements Serializable {
    String news;
    String name;
    String rate;
    String code;
    String lowestPrice;
    String highestPrice;
    String currentPrice;
    String volume;
    String warningPrice;
    String warningRate;

    public void copyOf(Stock stock) {
        if (stock == null) return;

        if (stock.getNews() != null) news = stock.getNews();
        if (stock.getName() != null) name = stock.getName();
        if (stock.getRate() != null) rate = stock.getRate();
        if (stock.getCode() != null) code = stock.getCode();
        if (stock.getLowestPrice() != null) lowestPrice = stock.getLowestPrice();
        if (stock.getHighestPrice() != null) highestPrice = stock.getHighestPrice();
        if (stock.getCurrentPrice() != null) currentPrice = stock.getCurrentPrice();
        if (stock.getVolume() != null) volume = stock.getVolume();
        if (stock.getWarningPrice() != null) warningPrice = stock.getWarningPrice();
        if (stock.getWarningRate() != null) warningRate = stock.getWarningRate();
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

    public String getWarningPrice() {
        return warningPrice;
    }

    public void setWarningPrice(String warningPrice) {
        this.warningPrice = warningPrice;
    }

    public String getWarningRate() {
        return warningRate;
    }

    public void setWarningRate(String warningRate) {
        this.warningRate = warningRate;
    }
}
