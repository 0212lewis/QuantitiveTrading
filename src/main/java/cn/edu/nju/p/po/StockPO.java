package cn.edu.nju.p.po;

/**
 * serial 记录编号
 * date 日期（月/日/年）
 * open 开盘指数
 * high 最高指数
 * low 最低指数
 * close 收盘指数
 * volumn 成交量
 * adjClose 复权后的收盘指数
 * code 股票代码
 * name 股票名称
 * market 市场名称
 * time 查询时间
 * currentPrice 目前价格
 *
 * @author dc
 *
 */


public class StockPO {

    //	private int serial;
    private String date;
    private double open;
    private double high;
    private double low;
    private double close;
    private int volume;
    private double adjClose;
    private String code;
    private String name;
    private String market;
    private String time;
    private double currentPrice;

    public StockPO(String date,double open,double high,double low,
                   double close,int volume,double adjClose,String code,String name,String market,String time,double currentPrice){
//		this.serial=serial;
        this.date = date;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
        this.adjClose = adjClose;
        this.code = code;
        this.name = name;
        this.market = market;
        this.time = time;
        this.currentPrice = currentPrice;
    }


//	public int getSerial(){
//		return serial;
//	}

    public String getDate(){
        return date;
    }

    public double getOpen(){
        return open;
    }

    public double gethigh(){
        return high;
    }

    public double getLow(){
        return low;
    }

    public double getClose(){
        return close;
    }

    public int getVolume(){
        return volume;
    }

    public double getAdjClose(){
        return adjClose;
    }

    public String getCode(){
        return code;
    }

    public String getName(){
        return name;
    }

    public String getMarket(){
        return market;
    }

   public String getTime() {
       return time;
   }

   public void setTime(String time) {
       this.time = time;
   }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }
}

