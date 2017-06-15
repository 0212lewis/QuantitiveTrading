package cn.edu.nju.p.stockPrediction.java.data;

/**
 * Created by xihao on 17-6-12.
 */
public class StockData {

    private String stockCode;
    private String date;
    private double alpha2;
    private double alpha26;
    private double alpha101;
    private double logClose;

    public StockData(String stockCode, String date, double alpha2, double alpha26, double alpha101, double logClose) {
        this.stockCode = stockCode;
        this.date = date;
        this.alpha2 = alpha2;
        this.alpha26 = alpha26;
        this.alpha101 = alpha101;
        this.logClose = logClose;
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getAlpha2() {
        return alpha2;
    }

    public void setAlpha2(double alpha2) {
        this.alpha2 = alpha2;
    }

    public double getAlpha26() {
        return alpha26;
    }

    public void setAlpha26(double alpha26) {
        this.alpha26 = alpha26;
    }

    public double getAlpha101() {
        return alpha101;
    }

    public void setAlpha101(double alpha101) {
        this.alpha101 = alpha101;
    }

    public double getLogClose() {
        return logClose;
    }

    public void setLogClose(double logClose) {
        this.logClose = logClose;
    }
}
