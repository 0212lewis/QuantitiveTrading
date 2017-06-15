package cn.edu.nju.p.stockPrediction.java.prediction;

import cn.edu.nju.p.dao.StockDao;
import cn.edu.nju.p.stockPrediction.java.factor.Alpha101;
import cn.edu.nju.p.stockPrediction.java.factor.Alpha2;
import cn.edu.nju.p.stockPrediction.java.factor.Alpha26;
import cn.edu.nju.p.utils.beans.ToolSpring;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.IOException;
import java.time.LocalDate;

/**
 * Created by xihao on 17-6-15.
 */
public class Predict {

    public double getBestStockCode(LocalDate date, String code) {

        try {
            MultiLayerNetwork restored = ModelSerializer.restoreMultiLayerNetwork(Thread.currentThread().getContextClassLoader().getResourceAsStream("MyMultiLayerNetWork.zip"));
            Alpha2 alpha2 = new Alpha2();
            Alpha26 alpha26 = new Alpha26();
            Alpha101 alpha101 = new Alpha101();

            double valueOfAlpha2 = alpha2.getAlpha(code, date);
            double valueOfAlpha26 = alpha26.getAlpha(code, date);
            double valueOfAlpha101 = alpha101.getAlpha(code, date);

            INDArray initArray = Nd4j.zeros(1, 3, 1);  //初始化一个序列来预测之后20个序列的输出,使用的是101个股票的初始数据000501_2010.csv
            initArray.putScalar(new int[]{0, 0, 0}, valueOfAlpha2);
            initArray.putScalar(new int[]{0, 1, 0}, valueOfAlpha26);
            initArray.putScalar(new int[]{0, 2, 0}, valueOfAlpha101);

            return restored.rnnTimeStep(initArray).getDouble(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -999;
    }

}
