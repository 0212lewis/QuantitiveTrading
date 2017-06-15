package cn.edu.nju.p;

import cn.edu.nju.p.dao.StockDao;
import cn.edu.nju.p.dao.daoutils.InsertTodayStockRun;
import cn.edu.nju.p.dao.daoutils.UpdateTodayStockRun;
import cn.edu.nju.p.stockPrediction.java.prediction.Predict;
import cn.edu.nju.p.utils.CalculateHelper;
import cn.edu.nju.p.utils.beans.ToolSpring;
import org.deeplearning4j.eval.meta.Prediction;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@EnableCaching
public class QuantradingApplication {

	public static void main(String[] args) {

		SpringApplication.run(QuantradingApplication.class, args);
		InsertTodayStockRun insertRun = new InsertTodayStockRun();
		UpdateTodayStockRun updateRun = new UpdateTodayStockRun();
		try {
			insertRun.insertDaily();
			updateRun.Run();
		} catch (Exception e) {
			e.printStackTrace();
		}

		StockDao stockDao = ToolSpring.getBeans(StockDao.class);
		ArrayList<String> codes = (ArrayList<String>) stockDao.getAllStocks();

		Predict predict = new Predict();
		Map<String, Double> fieldRates = new LinkedHashMap<>();
		codes.forEach(stockCode -> fieldRates.put(stockCode, predict.getBestStockCode(LocalDate.of(2017, 6, 14), stockCode)));

		//对收益率进行排序
		List<Map.Entry<String, Double>> rateList = new ArrayList<>(fieldRates.entrySet());
		rateList.sort((rate1, rate2) -> new BigDecimal(rate2.getValue()).compareTo(new BigDecimal(rate1.getValue())));
		System.out.println(rateList.subList(0,9));
//		/*StockDao stockDao = ToolSpring.getBeans(StockDao.class);
//		System.out.println(stockDao.getStockClose("000001", LocalDate.of(2012,2,3)));*/
	}


	@Bean
	public CalculateHelper calculateHelper() {

		return new CalculateHelper(0.0023);
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurerAdapter() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedOrigins("*");
			}
		};
	}

}
