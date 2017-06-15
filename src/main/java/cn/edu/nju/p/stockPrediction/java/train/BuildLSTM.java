package cn.edu.nju.p.stockPrediction.java.train;

import cn.edu.nju.p.stockPrediction.java.iterator.StockDataIterator;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.earlystopping.EarlyStoppingConfiguration;
import org.deeplearning4j.earlystopping.EarlyStoppingResult;
import org.deeplearning4j.earlystopping.saver.LocalFileModelSaver;
import org.deeplearning4j.earlystopping.scorecalc.DataSetLossCalculator;
import org.deeplearning4j.earlystopping.termination.MaxEpochsTerminationCondition;
import org.deeplearning4j.earlystopping.termination.MaxTimeIterationTerminationCondition;
import org.deeplearning4j.earlystopping.trainer.EarlyStoppingTrainer;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.BackpropType;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.GravesLSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by xihao on 17-6-12.
 */
public class BuildLSTM {

    private static final int IN_NUM = 3;
    private static final int OUT_NUM = 1;
    public static final int EPOCHES = 1000; //355次迭代的话已经差不多收敛了

    private static final int lstmLayer1Size = 50;
    private static final int lstmLayer2Size = 100;

    public static void getModel(int inNum, int outNum) {

        /*//初始化用户界面后端
        UIServer uiServer = UIServer.getInstance();

        //设置网络信息（随时间变化的梯度、分值等）的存储位置。这里将其存储于内存。
        StatsStorage statsStorage = new InMemoryStatsStorage();         //或者： new FileStatsStorage(File)，用于后续的保存和载入

        //将StatsStorage实例连接至用户界面，让StatsStorage的内容能够被可视化
        uiServer.attach(statsStorage);
*/
//        net.setListeners(new StatsListener(statsStorage));

        //大部分的参数都可以进行调整来寻找最优的拟合效果
        MultiLayerConfiguration configuration;
        configuration = new NeuralNetConfiguration.Builder()
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT).iterations(1) //随机梯度下降优化
                .learningRate(0.1)
                .rmsDecay(0.5)
                .seed(12345)
                .regularization(true) //正则化
                .l2(0.001) //正则化系数的权重 0.001
                .dropOut(0.5) //丢弃法，采用0.5的丢弃率
                .weightInit(WeightInit.XAVIER)
                .updater(Updater.RMSPROP)
                .list()
                .layer(0, new GravesLSTM.Builder().nIn(inNum).nOut(lstmLayer1Size).activation("tanh").build())
                .layer(1, new GravesLSTM.Builder().nIn(lstmLayer1Size).nOut(lstmLayer2Size).activation("tanh").build())
                .layer(2, new RnnOutputLayer.Builder(LossFunctions.LossFunction.MSE).activation("identity").nIn(lstmLayer2Size).nOut(outNum).build())
                .pretrain(false)
                .backprop(true)
                .backpropType(BackpropType.TruncatedBPTT) //使用截断式BPTT
                .tBPTTForwardLength(30)
                .tBPTTBackwardLength(30)
                .build();

        MultiLayerNetwork network = new MultiLayerNetwork(configuration);
        network.init();
        network.setListeners(new ScoreIterationListener(1));
        //然后添加StatsListener来在网络定型时收集这些信息
//        network.setListeners(new StatsListener(statsStorage));

        StockDataIterator trainIterator = new StockDataIterator();
        trainIterator.loadDataOfPath("/home/xihao/文档/stock_alpha/2016",240);
        StockDataIterator testIterator = new StockDataIterator();
        testIterator.loadDataOfPath("/home/xihao/文档/stock_alpha/2017",60);

        String directory = "/home/xihao/文档/net";

        EarlyStoppingConfiguration earlyStoppingConfiguration = new EarlyStoppingConfiguration.Builder()
                .epochTerminationConditions(new MaxEpochsTerminationCondition(200))
                .iterationTerminationConditions(new MaxTimeIterationTerminationCondition(120, TimeUnit.MINUTES))
                .scoreCalculator(new DataSetLossCalculator(testIterator, true))
                .evaluateEveryNEpochs(5)
                .modelSaver(new LocalFileModelSaver(directory))
                .build();

        EarlyStoppingTrainer trainer = new EarlyStoppingTrainer(earlyStoppingConfiguration, configuration, trainIterator);

        //开始早停定型：
        EarlyStoppingResult result = trainer.fit();

//显示结果：
        System.out.println("Termination reason: " + result.getTerminationReason());
        System.out.println("Termination details: " + result.getTerminationDetails());
        System.out.println("Total epochs: " + result.getTotalEpochs());
        System.out.println("Best epoch number: " + result.getBestModelEpoch());
        System.out.println("Score at best epoch: " + result.getBestModelScore());

//获得最优模型：
        MultiLayerNetwork bestModel = (MultiLayerNetwork) result.getBestModel();

        File locationToSave = new File("MyMultiLayerNetWork.zip");
        boolean saveUpdater = true;
        try {
            ModelSerializer.writeModel(bestModel, locationToSave, saveUpdater);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        return network;
    }

    public static void train(MultiLayerNetwork network, StockDataIterator iterator) {

        File baseDir = new File("/home/xihao/文档/stock_alpha/2016"); //使用2010年的数据
        File[] allFiles = baseDir.listFiles(); //获取所有的csv文件

        //进行迭代训练,每次迭代使用的csv都是不一样的，一次迭代八组样本
        for (int i = 0; i < EPOCHES; i++) {
            DataSet dataSets = null;
            iterator.loadData(allFiles[i], 8, 30);
            while (iterator.hasNext()) {
                dataSets = iterator.next();
                network.fit(dataSets);
            }
            System.out.println("======================>完成第" + i + "次完整训练");

            //训练完成之后对一个序列进行预测　其20天的对数收益率
            for(int j=0;j<1;j++) {
                INDArray output = network.rnnTimeStep(getInitArray());
                System.out.println("Prediction of " + (j + 1) + " day: " + output.getDouble(0));
            }

            System.out.println("=====================>完成第" + i + "次预测");
            network.rnnClearPreviousState();
        }
    }

    public static INDArray getInitArray(){
        INDArray initArray = Nd4j.zeros(1, 3, 1);  //初始化一个序列来预测之后20个序列的输出,使用的是101个股票的初始数据000501_2010.csv
        initArray.putScalar(new int[]{0, 0, 0}, -0.6788565846);
        initArray.putScalar(new int[]{0, 1, 0}, -0.903279576647717);
        initArray.putScalar(new int[]{0, 2, 0}, -0.8695652174);
        return initArray;
    }

    public static void main(String[] args) {

        /*StockDataIterator iterator = new StockDataIterator();
        MultiLayerNetwork network = getModel(IN_NUM, OUT_NUM);
        train(network, iterator);*/
        getModel(3,1);
    }
}
