package base;
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;

import org.apache.spark.mllib.clustering.KMeans;
import org.apache.spark.mllib.clustering.KMeansModel;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;

import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import java.util.Objects;

public class ConstructingGraphMain {
  public static void main(String[] args) throws IOException {
    
	// Off the log on the console
	Logger.getLogger("org").setLevel(Level.OFF);
	Logger.getLogger("akka").setLevel(Level.OFF);
	
	// Run the clustering
	final int NUM_CLUSTERS = 1500;
	final int NUM_ITERATIONS = 10;
	String title = "BigData";

	// Make session
	SparkSession spark = SparkSession
			  .builder()
			  .appName("JavaKMeansExample")
			  .config("spark.master", "local")
			  .getOrCreate();
	
	// Import the input file
	JavaRDD<String> inputRDD = spark.sparkContext()
			  .textFile("data/total_trajectory", 1)
			  .toJavaRDD();

	// Make the train set using on the kmeans clustering     
	JavaRDD<Vector> vectorRDD = inputRDD.map(s -> {
		  String[] sarray = s.split(" ");
		  EarthCordinateManger ecm = new EarthCordinateManger();
		  LonLatAltitute lla = new LonLatAltitute(Double.parseDouble(sarray[0]),Double.parseDouble(sarray[1]),0);
		  Coordinate xyz = ecm.LLAtoXYZ(lla);
		  double[] points = new double[3];		  
		  points[0] = xyz.getX();
		  points[1] = xyz.getY();
		  points[2] = xyz.getZ();
		  
		  return Vectors.dense(points);
	});
	  
    KMeansModel clusters = KMeans.train(vectorRDD.rdd(), NUM_CLUSTERS, NUM_ITERATIONS);
    
    // Evaluate clustering by computing Within Set Sum of Squared Errors
    double WSSSE = clusters.computeCost(vectorRDD.rdd());
    System.out.println("Within Set Sum of Squared Errors = " + WSSSE);
    
	clusters.toPMML("data/clusters_nodes"+title);
 
    // Make the rows
	JavaRDD<Row> peopleRDD = inputRDD.map((Function<String, Row>) s -> {
		  String[] sarray = s.split(" ");
		  
		  EarthCordinateManger ecm = new EarthCordinateManger();
		  LonLatAltitute lla = new LonLatAltitute(Double.parseDouble(sarray[0]),Double.parseDouble(sarray[1]),0);
		  Coordinate xyz = ecm.LLAtoXYZ(lla);
		  
		  double[] points = new double[3];		  
		  points[0] = xyz.getX();
		  points[1] = xyz.getY();
		  points[2] = xyz.getZ();

		  double value = clusters.predict(Vectors.dense(points));
		  return RowFactory.create(Long.parseLong(sarray[2]),Long.parseLong(sarray[3]),Double.toString(value),sarray[0],sarray[1]);
	});
	
	// Make schema
	List<StructField> fields = new ArrayList<>();
	fields.add(DataTypes.createStructField("people_id", DataTypes.LongType, true));
	fields.add(DataTypes.createStructField("date", DataTypes.LongType, true));
	fields.add(DataTypes.createStructField("node_id", DataTypes.StringType, true));
	fields.add(DataTypes.createStructField("lat", DataTypes.StringType, true));
	fields.add(DataTypes.createStructField("lon", DataTypes.StringType, true));
	StructType schema = DataTypes.createStructType(fields);
	
	// Make the node path using SparkSQL
	Dataset<Row> peopleDataFrame = spark.createDataFrame(peopleRDD, schema);
	peopleDataFrame.createOrReplaceTempView("source");
	peopleDataFrame.createOrReplaceTempView("dest");
	Dataset<Row> combine_df = spark.sql("SELECT s.date, s.people_id, s.node_id, d.node_id, s.lat, s.lon FROM source as s, dest as d WHERE s.date == (d.date - 1) "
			+ "AND s.people_id == d.people_id").orderBy("s.date","s.people_id");
	combine_df.rdd().saveAsTextFile("data/clusters_edges"+title);
	
	spark.close();
  }
}
