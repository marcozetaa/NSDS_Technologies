package it.polimi.middleware.spark.batch.iterative;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

/**
 * Start from a dataset of investments. Each element is a Tuple2(amount_owned, interest_rate).
 * At each iteration the new amount is (amount_owned * (1+interest_rate)).
 *
 * Implement an iterative algorithm that computes the new amount for each investment and stops
 * when the overall amount overcomes 1000.
 */
public class InvestmentSimulator {
    public static void main(String[] args) {
        final String master = args.length > 0 ? args[0] : "local[1]";
        final String filePath = args.length > 1 ? args[1] : "./";
        final double threshold = 1000;

        final SparkConf conf = new SparkConf().setMaster(master).setAppName("InvestmentSimulator");
        final JavaSparkContext sc = new JavaSparkContext(conf);
        sc.setLogLevel("ERROR");

        final JavaRDD<String> textFile = sc.textFile(filePath + "files/iterative/investment.txt");

        //Transforms each line inot a tuple (amount_owned, investment_rate)
        JavaRDD<Tuple2<Double,Double>> investments = textFile.map(w -> {
            String[] values = w.split(" ");
            double amountOwned = Double.parseDouble(values[0]);
            double investment_rate = Double.parseDouble(values[1]);
            return new Tuple2<>(amountOwned, investment_rate);
        });

        // TODO

        int iteration = 0;
        double sum = sumAmount(investments);

        while(sum < threshold){
            iteration++;
            investments = investments.map(i -> {
                System.out.println("Iteration " + i);
                return new Tuple2<>(i._1 * (1 + i._2), i._2);
            });

            //Cache last iteration result for the next job, otherwise every job must reiterate form start till i
            investments.cache();
            sum = sumAmount(investments);
        }

        System.out.println("Sum: " + sum + " after " + iteration + " iterations");
        sc.close();
    }

    private static final double sumAmount(JavaRDD<Tuple2<Double,Double>> investment){
        return investment
                .mapToDouble(a -> a._1)
                .sum();
    }

}
