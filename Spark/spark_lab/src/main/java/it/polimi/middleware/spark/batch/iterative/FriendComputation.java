package it.polimi.middleware.spark.batch.iterative;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.List;

public class FriendComputation {

    private static final boolean useCache = true;

    public static void main(String[] args) {
        final String master = args.length > 0 ? args[0] : "local[4]";
        final String filePath = args.length > 1 ? args[1] : "./";
        final String appName = useCache ? "FriendsCache" : "FriendsNoCache";

        final SparkConf conf = new SparkConf().setMaster(master).setAppName("FriendComputation");
        final JavaSparkContext sc = new JavaSparkContext(conf);
        sc.setLogLevel("ERROR");

        final SparkSession spark = SparkSession
                .builder()
                .master(master)
                .appName(appName)
                .getOrCreate();
        spark.sparkContext().setLogLevel("ERROR");

        final List<StructField> mySchemaField = new ArrayList<>();
        mySchemaField.add(DataTypes.createStructField("user",DataTypes.StringType,false));
        mySchemaField.add(DataTypes.createStructField("friend",DataTypes.StringType,false));
        final StructType mySchema = DataTypes.createStructType(mySchemaField);

        final Dataset<Row> input = spark
                .read()
                .option("header","false")
                .option("delimiter",",")
                .schema(mySchema)
                .csv(filePath + "files/friends/friends.csv");

        Dataset<Row> allFriends = input;
        long oldCount = 0;
        long newCount = allFriends.count();
        int iteration = 0;

        System.out.println("All Friends show");
        allFriends.show();
        
        // TODO

        while (newCount > oldCount) {
            iteration++;

            System.out.println("New friends show");

            // One could also join allFriends with allFriends.
            // It is a tradeoff between the number of joins and the size of the tables to join.
            Dataset<Row> newFriends = allFriends
                    .withColumnRenamed("friend", "to-join")
                    .join(
                            input.withColumnRenamed("user", "to-join"),
                            "to-join"
                    )
                    .drop("to-join");

            newFriends.show();

            System.out.println("All Friends show unione with newFriends");

            allFriends = allFriends
                    .union(newFriends)
                    .distinct();

            allFriends.show();

            if (useCache) {
                allFriends.cache();
            }
            oldCount = newCount;
            newCount = allFriends.count();
            System.out.println("Iteration: " + iteration + " - Count: " + newCount);
        }

        allFriends.show();

        sc.close();
    }
}
