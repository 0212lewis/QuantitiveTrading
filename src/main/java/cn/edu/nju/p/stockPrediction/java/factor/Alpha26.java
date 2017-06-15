package cn.edu.nju.p.stockPrediction.java.factor;

import cn.edu.nju.p.stockPrediction.java.data.dao.StockDaoImpl;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

/**
 * guarantee the date and the code is effective
 * alpha26的特殊情况是　连续五日的成交量都是第一所以会存在ｎａｎ相关系数的情况
 */
public class Alpha26 {

    private StockDaoImpl stockDao = StockDaoImpl.getInstance();

    public double getAlpha(String code, LocalDate date) {

        double max = -99999;
        for (int i = 0; i < 3; i++) {
            double correlation = getCorrelation(code, date);
            if (correlation > max) {
                max = correlation;
            }
            date = stockDao.getLastValidDate(date);
        }
        return -1 * max;
    }

    private double getCorrelation(String code, LocalDate date) {

        double[] xArray = new double[5];
        double[] yArray = new double[5];
        for (int i = 0; i < 5; i++) {
            xArray[i] = getRankVolumeOfPastFiveDays(code, date);
            yArray[i] = getRankHighOfPastFiveDays(code, date);
            date = stockDao.getLastValidDate(date);
        }
        System.out.println("Alpha26 --- > PearsonCorrelation ------> " + code + "   "+date.toString()+"      "+new PearsonsCorrelation().correlation(xArray, yArray));
        return new PearsonsCorrelation().correlation(xArray, yArray);
    }

    private double getRankHighOfPastFiveDays(String code, LocalDate date) {

        double codeHigh = getTotalHighOfPastFiveDays(code, date);
        List<String> allCodes = stockDao.allCodes(date);
        double maxHigh = 0;
        for (String stockCode : allCodes) {
            double totalHigh = getTotalHighOfPastFiveDays(stockCode, date);
            if (totalHigh > maxHigh) {
                maxHigh = totalHigh;
            }
        }
        return divide(codeHigh, maxHigh);
    }

    private double getTotalHighOfPastFiveDays(String code, LocalDate date) {

        double total = 0.0;
        for (int i = 0; i < 5; i++) {
            if (stockDao.getStockPO(code, date) == null || !stockDao.getStockPO(code, date).isOpen()) {
                return 0;
            }
            total += stockDao.getStockHigh(code, date);
            date = stockDao.getLastValidDate(date);
        }
        return total;
    }

    private double getRankVolumeOfPastFiveDays(String code, LocalDate date) {

        int codeVolume = getTotalVolumeOfPastFiveDays(code, date);
        List<String> allCodes = stockDao.allCodes(date);
        int maxVolume = 0;
        for (String stockCode : allCodes) {
            int volume = getTotalVolumeOfPastFiveDays(stockCode, date);
            if (volume > maxVolume) {
                maxVolume = volume;
            }
        }
        return (double) codeVolume / maxVolume;
    }

    private int getTotalVolumeOfPastFiveDays(String code, LocalDate date) {

        int total = 0;
        for (int i = 0; i < 5; i++) {
            if (stockDao.getStockPO(code, date) == null || !stockDao.getStockPO(code, date).isOpen()) {
                return 0;
            }
            total += stockDao.getStockVolume(code, date);
            date = stockDao.getLastValidDate(date);
        }
        return total;
    }

    private double divide(double a, double b) {

        BigDecimal bigDecimal_a = new BigDecimal(a);
        BigDecimal bigDecimal_b = new BigDecimal(b);
        return bigDecimal_a.divide(bigDecimal_b, RoundingMode.HALF_UP).doubleValue();
    }

}
