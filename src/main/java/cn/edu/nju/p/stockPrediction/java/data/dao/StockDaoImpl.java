package cn.edu.nju.p.stockPrediction.java.data.dao;

import cn.edu.nju.p.stockPrediction.java.data.StockPO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by dell- on 2017/6/9.
 */
public class StockDaoImpl  {

    /**
     * 外层的String是股票代码 000001
     * 内层的String是日期 例如2016-01-01
     *
     */
    private static Map<String, Map<String, StockPO>> map2017;
    private static Map<String, Map<String, StockPO>> map2016;
    private static Map<String, Map<String, StockPO>> map2015;
    private static Map<String, Map<String, StockPO>> map2014;
    private static Map<String, Map<String, StockPO>> map2013;
    private static Map<String, Map<String, StockPO>> map2012;
    private static Map<String, Map<String, StockPO>> map2011;
    private static Map<String, Map<String, StockPO>> map2010;
    private static Map<String, Map<String, StockPO>> map2009;
    private static Map<String, Map<String, StockPO>> map2008;
    private static Map<String, Map<String, StockPO>> map2007;
    private static Map<String, Map<String, StockPO>> map2006;
    private static Map<String, Map<String, StockPO>> map2005;
    private static StockDataTxtHelperImpl helper = StockDataTxtHelperImpl.getInstance();

    private static final StockDaoImpl INSTANCE = new StockDaoImpl();

    private StockDaoImpl() {
        long a=System.currentTimeMillis();
//        map2005 = helper.getStockData("2005");
//        map2006 = helper.getStockData("2006");
//        map2007 = helper.getStockData("2007");
//        map2008 = helper.getStockData("2008");
//        map2009 = helper.getStockData("2009");
//        map2010 = helper.getStockData("2010");
//        map2011 = helper.getStockData("2011");
//        map2012 = helper.getStockData("2012");
//        map2013 = helper.getStockData("2013");
//        map2014 = helper.getStockData("2014");
//        map2015 = helper.getStockData("2015");
//        map2016 = helper.getStockData("2016");
        map2017 = helper.getStockData("2017");
        System.out.println("初始化耗时 : "+(System.currentTimeMillis()-a)/1000f+" 秒 ");
    }

    public static StockDaoImpl getInstance(){
        return INSTANCE;
    }

    private static Map<String, Map<String, StockPO>> getMap(LocalDate date) {
        int year = date.getYear();
        switch (year) {
            case 2005:
                return map2005;
            case 2006:
                return map2006;
            case 2007:
                return  map2007;
            case 2008:
                return map2008;
            case 2009:
                return map2009;
            case 2010:
                return map2010;
            case 2011:
                return map2011;
            case 2012:
                return map2012;
            case 2013:
                return map2013;
            case 2014:
                return map2014;
            case 2015:
                return map2015;
            case 2016:
                return map2016;
            case 2017:
                return map2017;
            default:
                break;
        }
        return null;
    }

    public StockPO getStockPO(String code, LocalDate date) {
        Map<String, Map<String, StockPO>> map = StockDaoImpl.getMap(date);
        return map.get(code).get(date.toString());
    }

    public Double getStockOpen(String code, LocalDate date) {
        Map<String, Map<String, StockPO>> map = StockDaoImpl.getMap(date);
        return map.get(code).get(date.toString()).getOpen();
    }

    public Double getStockHigh(String code, LocalDate date) {
        Map<String, Map<String, StockPO>> map = StockDaoImpl.getMap(date);
        return map.get(code).get(date.toString()).getHigh();
    }

    public Double getStockLow(String code, LocalDate date) {
        Map<String, Map<String, StockPO>> map = StockDaoImpl.getMap(date);
 return map.get(code).get(date.toString()).getLow();
    }

    public Double getStockClose(String code, LocalDate date) {
        Map<String, Map<String, StockPO>> map = StockDaoImpl.getMap(date);
        return map.get(code).get(date.toString()).getClose();
    }

    public Long getStockVolume(String code, LocalDate date) {
        Map<String, Map<String, StockPO>> map = StockDaoImpl.getMap(date);
        return map.get(code).get(date.toString()).getVolume();
    }

    public Double getStockAdjClose(String code, LocalDate date) {
        return getStockClose(code, date);
    }

    public String getStockName(String code) {
        LocalDate date = LocalDate.of(2017, 06, 01);
        return map2017.get(code).get(date.toString()).getName();
    }

    public String getStockMarket(String code) {
        LocalDate date = LocalDate.of(2017, 06, 01);
        return map2017.get(code).get(date.toString()).getMarket();
    }

    public String getStockCode(String name) {
        Set<Map.Entry<String, Map<String, StockPO>>> entryseSet=map2017.entrySet();
        for (Map.Entry<String, Map<String, StockPO>> set : entryseSet) {
            Map<String, StockPO> inMap=set.getValue();
            Set<Map.Entry<String, StockPO>> inSet = inMap.entrySet();
            for(Map.Entry<String, StockPO> set2 : inSet) {
                if (set2.getValue().getName().equals(name)) {
                    return set2.getValue().getCode();
                }
            }
        }
        return null;
    }

    public List<StockPO> getPOList(String date) {
        List<StockPO> poList = new ArrayList<>();
        LocalDate localDate = LocalDate.parse(date);
        Map<String, Map<String, StockPO>> map = StockDaoImpl.getMap(localDate);

        Set<Map.Entry<String, Map<String, StockPO>>> entrySet=map.entrySet();
        for (Map.Entry<String, Map<String, StockPO>> set : entrySet) {
            Map<String, StockPO> inMap=set.getValue();
            Set<Map.Entry<String, StockPO>> inSet = inMap.entrySet();
            for(Map.Entry<String, StockPO> set2 : inSet) {
                poList.add(set2.getValue());
            }
        }
        return poList;
    }

    public String getStockSector(String code) {
        String sector="";
        if(code.startsWith("600")||code.startsWith("601")
                ||code.startsWith("000")||code.startsWith("001")){
            sector="主板";
        }
        else if(code.startsWith("002")){
            sector="中小板";
        }else if(code.startsWith("300")){
            sector="创业板";
        }else{
            sector="没有找到对应板块";
        }
        return sector;
    }

    public List<String> getStockBySector(String sector) {
        return null;
    }

    public List<String> getAllStocks() {
        return null;
    }

    public boolean hasStoppedLast8Days(String code, LocalDate date) {

        for (int i = 0; i < 9; i++) {
            if (getStockPO(code, date) == null || !getStockPO(code, date).isOpen()) {
                return true;
            }
            date = getLastValidDate(date);
        }
        return false;
    }

    public List<String> allCodes(LocalDate date) {
        return new ArrayList<>(getMap(date).keySet());
    }

    public List<LocalDate> getBetweenDates(LocalDate beginDate, LocalDate endDate) {

        List<LocalDate> dateList = new ArrayList<>();

        //获得输入日期之间的所有日期
        while (beginDate.isBefore(endDate)) {
            dateList.add(beginDate);
            beginDate = beginDate.plusDays(1);
        }
        dateList.add(endDate);

        String code = "000002";
        Predicate<LocalDate> notVacation = localDate -> getStockPO(code, localDate) != null;

        return dateList.parallelStream().filter(notVacation).sorted().collect(Collectors.toList());
    }

    public LocalDate getLastValidDate(LocalDate date) {

        String code = "000002";
        date = date.minusDays(1);
        while (getStockPO(code, date) == null) {
            date = date.minusDays(1);
        }
        return date;
    }

    public static void main(String[] args) {

        StockDaoImpl stockDao = new StockDaoImpl();

        String code = "000001";
        LocalDate date = LocalDate.of(2005, 2, 16);
        System.out.println(stockDao.getStockHigh(code, date));
        System.out.println(stockDao.getStockHigh(code, date));

//        System.out.println(new StockDaoImpl().getLastValidDate(LocalDate.of(2017,6,10)));
        LocalDate beginDate = LocalDate.of(2005, 2, 3);
        LocalDate endDate = LocalDate.of(2017, 5, 6);
        while (beginDate.isBefore(endDate)) {
            System.out.println(stockDao.getMap(beginDate).size());
            beginDate = beginDate.plusYears(1);
        }
    }
}
