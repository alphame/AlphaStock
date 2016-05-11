package org.stargazerlan.alphastock;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by StargazerLan on 2016/4/24.
 */
public class AlphaStock {
    private String webSite = "http://stockpage.10jqka.com.cn/spService/stockcode/Header/realHeader";
    private Pattern reUnicode = Pattern.compile("\\\\u([0-9a-zA-Z]{4})");

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
            if (each.contains("stockname")) {
                fotmatAndSet(each, "stockname", stock);
            }
            if (each.contains("stockcode")) {
                fotmatAndSet(each, "stockcode", stock);
            }
            if (each.contains("xj")) {
                fotmatAndSet(each, "xj", stock);
            }
            if (each.contains("zdf")) {
                fotmatAndSet(each, "zdf", stock);
            }
            if (each.contains("cjl")) {
                fotmatAndSet(each, "cjl", stock);
            }
        }

        return stock;
    }

    private void fotmatAndSet(String text, String type, Stock stock) {
        if (text == null || text.isEmpty() || type == null || type.isEmpty() || stock == null) return;

        String str = text.split(":")[1].replace("\"", "").trim();

        if (type.equals("stockname")) {
            stock.setName(decodeUnicode(str));
        }
        if (type.equals("stockcode")) {
            stock.setCode(str);
        }
        if (type.equals("xj")) {
            stock.setCurrentPrice(str);
        }
        if (type.equals("zdf")) {
            str = str.replace("%", "");
            if (str.startsWith("-")) {
                str = str.replace("-", "");
                stock.setNagetive(true);
            }
            if (str.startsWith("+")) {
                str = str.replace("+", "");
                stock.setNagetive(false);
            }
            stock.setRate(str);
        }
        if (type.equals("cjl")) {
            stock.setVolume(decodeUnicode(str));
        }
    }

    private String decodeUnicode(String s) {
        Matcher m = reUnicode.matcher(s);
        StringBuffer sb = new StringBuffer(s.length());
        while (m.find()) {
            m.appendReplacement(sb,
                    Character.toString((char) Integer.parseInt(m.group(1), 16)));
        }
        m.appendTail(sb);
        return sb.toString();
    }

}
