package cn.edu.nju.p.stockPrediction.java.factor;



import cn.edu.nju.p.stockPrediction.java.data.dao.StockDaoImpl;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * get the 12th alpha factor of a stock of an exact date
 * 当天停牌或者前６天　存在停牌的直接返回０
 * 就是判断前８天是否存在停牌，停牌的话直接返回－９９９
 * 除数为０的话　返回－８８８
 */
public class Alpha2 {

    private StockDaoImpl stockDao = StockDaoImpl.getInstance();

    /**
     * get the value of alpha12
     * @param stockCode the code of stock
     * @param date the date
     * @return
     */
    public double getAlpha(String stockCode, LocalDate date) {

        if (stockDao.hasStoppedLast8Days(stockCode, date)) {
            //过去八天存在停牌直接返回-999
            return -999;
        }

        double[] xArray = new double[6];
        double[] yArray = new double[6];

        for (int i = 0; i < 6; i++) {
            xArray[i] = getRankValueOfDelta(stockCode, date); //normalizing
            yArray[i] = getRankValueOfAnother(stockCode, date); //normalizing
            date = stockDao.getLastValidDate(date); //上一个不是周末以及节假日的日期
        }

        PearsonsCorrelation pearsonsCorrelation = new PearsonsCorrelation();
        return -1 * pearsonsCorrelation.correlation(xArray, yArray);
    }

    private double getRankValueOfDelta(String stockCode, LocalDate date) {
        Double maxVolume = getMaxLogVolume(date);
        return divide(getLogVolume(stockCode, date), maxVolume);
    }

    private double getRankValueOfAnother(String stockCode, LocalDate date) {
        Double maxValue = getMaxAnother(date);
        return divide(getValue(stockCode, date), maxValue);
    }

    private double getLogVolume(String stockCode, LocalDate date) {

        //前两天的日期
        LocalDate dayBeforeYes = stockDao.getLastValidDate(stockDao.getLastValidDate(date));

        long volume =0;
        try {
            volume = stockDao.getStockVolume(stockCode, dayBeforeYes);

        } catch (NullPointerException ne) {
            volume = 0;
        }

        if (volume == 0) {
            return 0;
        }
        double logVolume;
        logVolume = Math.log(volume);

        //确保传入的日期是有效日期
        long curVolume = stockDao.getStockVolume(stockCode, date);
        return Math.log(curVolume) - logVolume;
    }

    private double getMaxLogVolume(LocalDate date) {

        List<String> allCodes = stockDao.allCodes(date);
        List<Double> allLogVolumes = new ArrayList<>();
        allCodes.forEach(code -> allLogVolumes.add(getLogVolume(code, date)));

        Double maxVolume = Double.MIN_NORMAL;
        for (Double d : allLogVolumes) {
            if (d > maxVolume) {
                maxVolume = d;
            }
        }
        return maxVolume;
    }

    private double getValue(String code, LocalDate date) {

        double open = stockDao.getStockOpen(code, date);
        double close = stockDao.getStockClose(code, date);
        return divide((close - open), open);
    }

    private double getMaxAnother(LocalDate date) {

        List<String> allCodes = stockDao.allCodes(date);
        List<Double> allValue = new ArrayList<>();
        allCodes.forEach(code -> {
            double open = 0;
            double close = 0;
            try {
                open = stockDao.getStockOpen(code, date);
                close = stockDao.getStockClose(code, date);
            } catch (NullPointerException ne) {
                return;
            }
            if (open == 0) {
                //停牌的话
                return;
            }

            allValue.add((close - open) / open);
        });
        Double maxValue = -999999999999.0;
        for (Double aDouble : allValue) {
            if (aDouble > maxValue) {
                maxValue = aDouble;
            }
        }
        return maxValue;
    }

    private double divide(double a, double b) {

        try {
            BigDecimal bigDecimal_a = new BigDecimal(a);
            BigDecimal bigDecimal_b = new BigDecimal(b);
            return bigDecimal_a.divide(bigDecimal_b, RoundingMode.HALF_UP).doubleValue();
        } catch (NumberFormatException ne) {
            return -999.0;
        }
    }
}
