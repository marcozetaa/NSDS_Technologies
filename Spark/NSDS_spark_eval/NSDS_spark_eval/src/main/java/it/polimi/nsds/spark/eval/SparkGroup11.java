package it.polimi.nsds.spark.eval;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static org.apache.spark.sql.functions.*;

/**
 * Group number: 11
 * Group members:
 * Marco Zanghieri
 * Luca Rondini
 * Francesco Scandale
 */
public class SparkGroup11 {
    public static void main(String[] args) throws TimeoutException {
        final String master = args.length > 0 ? args[0] : "local[4]";
        final String filePath = args.length > 1 ? args[1] : "./";

        final SparkSession spark = SparkSession
                .builder()
                .master(master)
                .appName("SparkEval")
                .getOrCreate();
        spark.sparkContext().setLogLevel("ERROR");

        final List<StructField> citiesRegionsFields = new ArrayList<>();
        citiesRegionsFields.add(DataTypes.createStructField("city", DataTypes.StringType, false));
        citiesRegionsFields.add(DataTypes.createStructField("region", DataTypes.StringType, false));
        final StructType citiesRegionsSchema = DataTypes.createStructType(citiesRegionsFields);

        final List<StructField> citiesPopulationFields = new ArrayList<>();
        citiesPopulationFields.add(DataTypes.createStructField("id", DataTypes.IntegerType, false));
        citiesPopulationFields.add(DataTypes.createStructField("city", DataTypes.StringType, false));
        citiesPopulationFields.add(DataTypes.createStructField("population", DataTypes.IntegerType, false));
        final StructType citiesPopulationSchema = DataTypes.createStructType(citiesPopulationFields);

        final Dataset<Row> citiesPopulation = spark
                .read()
                .option("header", "true")
                .option("delimiter", ";")
                .schema(citiesPopulationSchema)
                .csv(filePath + "files/cities_population.csv");

        final Dataset<Row> citiesRegions = spark
                .read()
                .option("header", "true")
                .option("delimiter", ";")
                .schema(citiesRegionsSchema)
                .csv(filePath + "files/cities_regions.csv");


        // TODO: query Q1
        System.out.println("\n\n\n Q1: Total population for each region\n");

        final Dataset<Row> q1 = citiesRegions
                .join(citiesPopulation,"city")
                .groupBy("region").sum("population")
                .withColumnRenamed("sum(population)","total_population");

        q1.show();

        // TODO: query Q2
        System.out.println("\n\n\nQ2: Number of cities and population of the most populated city for each region\n");


        final Dataset<Row> q2 = citiesRegions
                .groupBy("region").count()
                .join(  // Subquery to get view of most populated city for each region
                        citiesRegions.join(citiesPopulation,"city")
                            .groupBy("region").max("population")
                        ,"region")
                .withColumnRenamed("max(population)","max_population");

        q2.show();


        // JavaRDD where each element is an integer and represents the population of a city
        JavaRDD<Integer> population = citiesPopulation.toJavaRDD().map(r -> r.getInt(2));

        //TODO: Q3

        System.out.println("\n\n\nQ3: Evolution of the population in Italy\n");

        // Assigning to every tuple the corresponding evolution
        JavaRDD<Tuple2<Double, Double>> city_population = population.map(w -> {
            if(w > 1000){ // population is greater than 1000
                return new Tuple2<>(w.doubleValue(),0.01);
            }
            else{
                return new Tuple2<>(w.doubleValue(),-0.01);
            }
        });

        JavaRDD<Tuple2<Double, Double>> old_city_population = city_population;
        city_population.cache();

        int iteration = 0;
        double sum = sumAmount(city_population);
        final double threshold = 100000000;

        while (sum < threshold) {

            city_population = city_population.map(i -> new Tuple2<>(i._1*(1+i._2), i._2));

            city_population.cache();
            sum = sumAmount(city_population);

            old_city_population.unpersist();
            old_city_population = city_population;
            iteration++;
        }

        System.out.println("Sum: " + sum + " after " + iteration + " iterations");


        // Bookings: the value represents the city of the booking
        final Dataset<Row> bookings = spark
                .readStream()
                .format("rate")
                .option("rowsPerSecond", 100)
                .load();

        // TODO query 4

        System.out.println("\n\n\nQ4: Total number of bookings for each region, in a window of 30 seconds, sliding every 5 seconds\n");

        final StreamingQuery q4 = bookings
                .join(citiesPopulation, bookings.col("value").equalTo(citiesPopulation.col("id")))
                .join(citiesRegions,"city")
                .groupBy(col("region"),
                        window(col("timestamp"),"30 seconds","5 seconds")
                )
                .count()
                .writeStream()
                .outputMode("update")
                .format("console")
                .start();

        try {
            q4.awaitTermination();
        } catch (final StreamingQueryException e) {
            e.printStackTrace();
        }

        spark.close();
    }

    // Returns the total population of all cities
    private static final double sumAmount(JavaRDD<Tuple2<Double, Double>> population) {
        return population
                .mapToDouble(a -> a._1)
                .sum();
    }

}