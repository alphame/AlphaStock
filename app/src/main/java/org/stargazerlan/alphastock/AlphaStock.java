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
    private String webSite = "http://hq.sinajs.cn/list=stockcode";

    public Stock search(String code) {
        Stock stock = new Stock();
        if (code == null) return null;

        String site = webSite.replace("stockcode", marketDetection(code));

        Document doc = null;
        try {
            doc = Jsoup.connect(site).ignoreContentType(true).timeout(3000).get();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        String data = doc.text();
        if (data != null) {
            String str = data.split(";")[0];
            if (str.isEmpty() || str.equals("FAILED")) return null;
        }

        data = dataFormat(data);
        String[] titles = data.split(",");
        if (titles.length < 2) return null;
        stockValuesInflate(stock, code, titles);
        return stock;
    }

    private void stockValuesInflate(Stock stock, String code, String[] titles) {
        stock.setName(titles[0]);
        stock.setCode(marketDetection(code));
        stock.setOpenPrice(titles[1]);
        stock.setyClosePrice(titles[2]);
        stock.setCurrentPrice(titles[3]);
        stock.setHighestPrice(titles[4]);
        stock.setLowestPrice(titles[5]);
        stock.setRate(convertStrToRate(titles[2], titles[3]));
        stock.setShares(titles[8]);
        stock.setVolume(titles[9]);

        if (stock.getRate().startsWith("-")) {
            stock.setRate(stock.getRate().replace("-", ""));
            stock.setNagetive(true);
        }

        String[] buyingBit = new String[5];
        buyingBit[0] = titles[10] + ":" + titles[11];
        buyingBit[1] = titles[12] + ":" + titles[13];
        buyingBit[2] = titles[14] + ":" + titles[15];
        buyingBit[3] = titles[16] + ":" + titles[17];
        buyingBit[4] = titles[18] + ":" + titles[19];
        stock.setBuyingBit(buyingBit);

        String[] sellingBit = new String[5];
        sellingBit[0] = titles[20] + ":" + titles[21];
        sellingBit[1] = titles[22] + ":" + titles[23];
        sellingBit[2] = titles[24] + ":" + titles[25];
        sellingBit[3] = titles[26] + ":" + titles[27];
        sellingBit[4] = titles[28] + ":" + titles[29];
        stock.setSellingBit(sellingBit);

        stock.setDate(titles[30]);
        stock.setTime(titles[31]);
    }

    private String convertStrToRate(String yClosePrice, String currentPrice) {
        String result = "0.00";
        float ycp, cp;
        try {
            ycp = Float.valueOf(yClosePrice);
            cp = Float.valueOf(currentPrice);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return result;
        }

        float rate = (cp - ycp) / ycp * 100;
        return String.format("%.2f", rate);
    }

    private String marketDetection(String stockcode) {
        if (stockcode.length() == 6) {
            if (stockcode.startsWith("15") || stockcode.startsWith("00") || stockcode.startsWith("3")) {
                return "sz" + stockcode;
            }
            if (stockcode.startsWith("60")) {
                return "sh" + stockcode;
            }
        }
        return stockcode;
    }

    private String dataFormat(String data) {
        String result;
        result = data.replaceAll("var hq_str_.*?=", "");
        result = result.replace("\"", "");
        return result;
    }

    private String decodeUnicode(String s) {
        Pattern reUnicode = Pattern.compile("\\\\u([0-9a-zA-Z]{4})");
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
