package cn.edu.nju.p.stockPrediction.java.iterator;

import cn.edu.nju.p.stockPrediction.java.data.StockData;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.DataSetPreProcessor;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by xihao on 17-6-12.
 */
public class StockDataIterator implements DataSetIterator{

    private static final int VECTOR_SIZE = 3; //three input and one output

    //每批次的训练数据组数
    private int batchNum = 8; //每次训练取8组数据

    private int exampleLength = 30; //每组训练数据的长度

    //数据集
    private List<StockData> dataList;

    //存放剩余数组的index信息 保存的是每一组训练数据的起始index
    private List<Integer> dataRecord;

    public StockDataIterator(){
        dataRecord = new ArrayList<>();
        dataList = new ArrayList<>();
    }

    public boolean loadData(File file,int limit) {
        //use default settings
        try {
            readDataFromFile(file,60);
        } catch (IOException e) {
            return false;
        }
        resetDataRecord();
        return true;
    }

    /**
     * 加载目录下文件进入内存
     * @param filePath
     */
    public void loadDataOfPath(String filePath,int limit) {
        File baseDir = new File(filePath);
        File[] allFiles = baseDir.listFiles();
        for (File file : allFiles) {
            loadData(file,limit);
        }
    }

    public boolean loadData(File file, int batchNum, int exampleLength) {
        this.batchNum = batchNum;
        this.exampleLength = exampleLength;
        try {
            readDataFromFile(file,240);
        } catch (IOException e) {
            return false;
        }
        resetDataRecord();
        return true;
    }

    private void resetDataRecord(){

        dataRecord.clear();
        int totalGroupNum = dataList.size() / (exampleLength + 1);
        for (int i = 0; i < totalGroupNum; i++) {
            dataRecord.add(i * exampleLength);
        }

    }

    /**
     * Reads all data of the corresponding file path
     *
     * @param file file
     * @throws IOException
     */
    public void readDataFromFile(File file, int limit) throws IOException {

        FileInputStream fileInputStream = new FileInputStream(file);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream, "UTF-8"));
        bufferedReader.readLine(); //读取首行
        String line = bufferedReader.readLine();

        System.out.println("读取数据...");
        int count = 0;
        while (line != null && count <= limit) {

            String arr[] = line.split(",");
            double[] nums = new double[4];
            for (int i = 0; i < 4; i++) {
                nums[i] = Double.valueOf(arr[i + 2]);
            }
            StockData stockData = new StockData(arr[0], arr[1], nums[0], nums[1], nums[2], nums[3]);
            dataList.add(stockData);
            count++;
            line = bufferedReader.readLine();
        }

        System.out.println("读取数据完毕...");
        fileInputStream.close();
        bufferedReader.close();
    }

    public DataSet next(int batchNum) {

        if (dataRecord.size() <= 0) {
            throw new RuntimeException("没有剩余的组数");
        }

        int actualBatchSize = Math.min(batchNum, dataRecord.size()); //训练组数不足batchNum的时候使用全部剩余的组数
        int actualExampleLength = Math.min(exampleLength, dataList.size() - dataRecord.get(0) - 1);//样本数据不足一组的话　全部使用

        INDArray input = Nd4j.create(new int[]{actualBatchSize, VECTOR_SIZE, actualExampleLength}, 'f');
        INDArray label = Nd4j.create(new int[]{actualBatchSize, 1, actualExampleLength}, 'f');

        for (int i = 0; i < actualBatchSize; i++) {
            int index = dataRecord.remove(0); //减少index记录
            int endIndex = Math.min(index + exampleLength, dataList.size() - 1);
            for(int j=index;j<endIndex;j++) {
                int timeSerialNum = endIndex - j - 1;//时间序号，时间比较早的序号较大
                StockData curData = dataList.get(i);
                input.putScalar(new int[]{i, 0, timeSerialNum}, curData.getAlpha2());
                input.putScalar(new int[]{i, 1, timeSerialNum}, curData.getAlpha26());
                input.putScalar(new int[]{i, 2, timeSerialNum}, curData.getAlpha101());

                label.putScalar(new int[]{i, 0, timeSerialNum}, curData.getLogClose());
            }
            if (dataRecord.size() <= 0) {
                //没有训练数据的话，直接break
                break;
            }
        }
        return new DataSet(input, label);
    }

    /**
     * Total examples in the iterator
     *
     * @return
     */
    @Override
    public int totalExamples() {
        return dataList.size() / exampleLength;
    }

    /**
     * Input columns for the dataset
     *
     * @return
     */
    @Override
    public int inputColumns() {
        return VECTOR_SIZE;
    }

    /**
     * The number of labels for the dataset
     *
     * @return
     */
    @Override
    public int totalOutcomes() {
        return 1;
    }

    /**
     * Is resetting supported by this DataSetIterator? Many DataSetIterators do support resetting,
     * but some don't
     *
     * @return true if reset method is supported; false otherwise
     */
    @Override
    public boolean resetSupported() {
        return true;
    }

    /**
     * Does this DataSetIterator support asynchronous prefetching of multiple DataSet objects?
     * Most DataSetIterators do, but in some cases it may not make sense to wrap this iterator in an
     * iterator that does asynchronous prefetching. For example, it would not make sense to use asynchronous
     * prefetching for the following types of iterators:
     * (a) Iterators that store their full contents in memory already
     * (b) Iterators that re-use features/labels arrays (as future next() calls will overwrite past contents)
     * (c) Iterators that already implement some level of asynchronous prefetching
     * (d) Iterators that may return different data depending on when the next() method is called
     *
     * @return true if asynchronous prefetching from this iterator is OK; false if asynchronous prefetching should not
     * be used with this iterator
     */
    @Override
    public boolean asyncSupported() {
        return false;
    }

    /**
     * Resets the iterator back to the beginning
     */
    @Override
    public void reset() {
        resetDataRecord();
    }

    /**
     * Batch size
     *
     * @return
     */
    @Override
    public int batch() {
        return batchNum;
    }

    /**
     * The current cursor if applicable
     *
     * @return
     */
    @Override
    public int cursor() {
        return 0;
    }

    /**
     * Total number of examples in the dataset
     *
     * @return
     */
    @Override
    public int numExamples() {
        return totalExamples();
    }

    /**
     * Set a pre processor
     *
     * @param preProcessor a pre processor to set
     */
    @Override
    public void setPreProcessor(DataSetPreProcessor preProcessor) {

    }

    /**
     * Returns preprocessors, if defined
     *
     * @return
     */
    @Override
    public DataSetPreProcessor getPreProcessor() {
        return null;
    }

    /**
     * Get dataset iterator record reader labels
     */
    @Override
    public List<String> getLabels() {
        return null;
    }

    /**
     * Returns {@code true} if the iteration has more elements.
     * (In other words, returns {@code true} if {@link #next} would
     * return an element rather than throwing an exception.)
     *
     * @return {@code true} if the iteration has more elements
     */
    @Override
    public boolean hasNext() {
        return dataRecord.size() > 0;
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration
     * @throws  if the iteration has no more elements
     */
    @Override
    public DataSet next() {
        return next(batchNum);
    }

    /**
     * Removes from the underlying collection the last element returned
     * by this iterator (optional operation).  This method can be called
     * only once per call to {@link #next}.  The behavior of an iterator
     * is unspecified if the underlying collection is modified while the
     * iteration is in progress in any way other than by calling this
     * method.
     *
     * @throws UnsupportedOperationException if the {@code remove}
     *                                       operation is not supported by this iterator
     * @throws IllegalStateException         if the {@code next} method has not
     *                                       yet been called, or the {@code remove} method has already
     *                                       been called after the last call to the {@code next}
     *                                       method
     * @implSpec The default implementation throws an instance of
     * {@link UnsupportedOperationException} and performs no other action.
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove unsupported");
    }

    /**
     * Performs the given action for each remaining element until all elements
     * have been processed or the action throws an exception.  Actions are
     * performed in the order of iteration, if that order is specified.
     * Exceptions thrown by the action are relayed to the caller.
     *
     * @param action The action to be performed for each element
     * @throws NullPointerException if the specified action is null
     * @implSpec <p>The default implementation behaves as if:
     * <pre>{@code
     *     while (hasNext())
     *         action.accept(next());
     * }</pre>
     * @since 1.8
     */
    @Override
    public void forEachRemaining(Consumer<? super DataSet> action) {

    }
}
