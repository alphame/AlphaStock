package org.stargazerlan.alphastock;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;

/**
 * Created by StargazerLan on 2016/4/24.
 */
public class AlphaStock {
    private String webSite = "http://stockpage.10jqka.com.cn/spService/stockcode/Header/realHeader";

    public Stock search(String code) {
        Stock stock = new Stock();
        if (code == null) return stock;
        String site = webSite.replace("stockcode", code);
        Document doc = null;
        try {
            doc = Jsoup.connect(site).timeout(3000).get();
        } catch (IOException e) {
            e.printStackTrace();
            return stock;
        }

        String[] titles = doc.text().split(",");
        for (String each : titles) {
            if (each.contains("stockcode")) {
                stock.setCode(removeTheSymbols(each));
            }
            if (each.contains("xj")) {
                stock.setCurrentPrice(removeTheSymbols(each));
            }
            if (each.contains("zdf")) {
                stock.setRate(removeTheSymbols(each));
            }
            if (each.contains("cjl")) {
                stock.setVolume(removeTheSymbols(each));
            }
            if (each.contains("zg")) {
                stock.setHighestPrice(removeTheSymbols(each));
            }
            if (each.contains("zd")) {
                stock.setLowestPrice(removeTheSymbols(each));
            }
        }

        return stock;
    }

    private String removeTheSymbols(String text) {
        if (text != null && !text.isEmpty())
            return text.split(":")[1].replace("\"", "");
        else return null;
    }
}
