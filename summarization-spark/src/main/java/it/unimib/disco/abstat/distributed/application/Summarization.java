package it.unimib.disco.abstat.distributed.application;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import static org.apache.spark.sql.functions.*;

import it.unimib.disco.abstat.distributed.minimalization.Minimalize;
import it.unimib.disco.abstat.distributed.minimalization.Triple;

public class Summarization {
	
	private SparkSession session;
	private String dataset_file;
	private String ontology_file;
	private String output_dir;

	public Summarization(String master, String dataset, String ontology, String output_dir) {
		session = SparkSession.builder().appName("ABSTAT-spark").master(master).getOrCreate();
		this.dataset_file = dataset;
		this.ontology_file = ontology;
		this.output_dir = output_dir;
	}
	
	
	public static void main(String[] args) throws Exception {
		Summarization s = new Summarization(args[0], args[1], args[2], args[3]);
		
		JavaRDD<String> input = s.session.read().textFile(s.dataset_file).javaRDD();
		Splitter splitter = new Splitter();
		JavaRDD<Triple> rdd = splitter.calculate(input);
		Dataset<Row> data = s.session.createDataFrame(rdd, Triple.class);
		data.createOrReplaceTempView("dataset");

		Minimalize minimalize = new Minimalize(s.ontology_file);
		s.session.udf().register("minimalize", minimalize);
		
		s.split();
		s.session.catalog().dropTempView("dataset");
		s.calculateMinimalTypes();
		s.session.catalog().dropTempView("typing_triples");
		s.countConcepts();
		s.countDatatypes();
		s.countProperties();
		s.calculateDatatypeAKP();
		s.session.catalog().dropTempView("dt_triples");
		s.session.catalog().dropTempView("datatype_akp");
		s.calculateObjectAKP();	
		s.calculateObjectCardinalities();
	}
	
	
	public void split() {
		/* isolate typing asserts */
		session.sql("SELECT subject, object " + 
					"FROM dataset "  + 
					"WHERE type = 'typing'" 
				   ).createOrReplaceTempView("typing_triples");	
		
		/* isolate object relation asserts */
		session.sql("SELECT  subject, predicate, object " + 
					"FROM dataset "  + 
					"WHERE type = 'object_relational'" 
				   ).createOrReplaceTempView("object_triples");
		
		/* isolate datatype relation asserts */
		session.sql("SELECT  subject, predicate, object, " +                     
					"   CASE" + 
					"      WHEN datatype IS NULL THEN 'http://www.w3.org/2000/01/rdf-schema#Literal' " + 
					"      ELSE datatype " + 
					"   END AS datatype " +
					"FROM dataset "  + 
					"WHERE type = 'dt_relational'" 
				   ).createOrReplaceTempView("dt_triples");	
	}
	

	public void countConcepts() {
		session.sql("SELECT minimalType, COUNT(*) AS freq FROM mTypes_dataset GROUP BY minimalType")
		.write().option("sep", ";").csv(output_dir + "/spark-concepts");
	}
	
	
	public void countDatatypes() {
		session.sql("SELECT datatype, COUNT(*) AS freq FROM dt_triples GROUP BY datatype")
		.write().option("sep", ";").csv(output_dir + "/spark-datatypes");
	}
	
	
	public void countProperties() {
		session.sql("SELECT predicate, COUNT(*) AS freq FROM dt_triples GROUP BY predicate")
		.write().option("sep", ";").csv(output_dir + "/spark-datatype-properties");
		session.sql("SELECT predicate, COUNT(*) AS freq FROM object_triples GROUP BY predicate")
		.write().option("sep", ";").csv(output_dir + "/spark-object-properties");
	}
	
	
	public void calculateMinimalTypes() {
		/* minimal types per entity */
		session.sql("SELECT subject AS entity, minimalize(object) AS minimalTypes " + 
					"FROM typing_triples " + 
					"GROUP BY subject"
					).createOrReplaceTempView("mTypes_dataset");
		
		/* minimal type per entity */
		session.sql("SELECT entity, explode(minimalTypes) AS minimalType " + 
					"FROM mTypes_dataset"
					).createOrReplaceTempView("mTypes_dataset");
	}
	
	
	public void calculateDatatypeAKP() {
		/* calculate minimal types for each subject */
		session.sql("SELECT " + 
					"   CASE" + 
					"      WHEN minimalType IS NULL THEN 'http://www.w3.org/2002/07/owl#Thing' " + 
					"      ELSE minimalType " + 
					"   END AS subj_Type, " +
					"predicate, datatype AS obj_Type " +
					"FROM " + 
					"   mTypes_dataset " +
					"   RIGHT OUTER JOIN " + 
					"      dt_triples " + 
					"      ON mTypes_dataset.entity = dt_triples.subject "
					).createOrReplaceTempView("datatype_akp");
		
		/* group by AKP and calculate freq */
		session.sql("SELECT subj_Type, predicate, obj_Type, COUNT(*) AS freq  " +
					"FROM datatype_akp " + 
					"GROUP BY subj_Type, predicate, obj_Type "
					).write().option("sep", ";").csv(output_dir + "/spark-datatype-akp");
	}
	
	
	public void calculateObjectAKP() {
		/* calculate minimal types for each subject */
		session.sql("SELECT" + 
					"   CASE" + 
					"      WHEN minimalType IS NULL THEN 'http://www.w3.org/2002/07/owl#Thing' " + 
					"      ELSE minimalType " + 
					"   END AS subj_Type, " +
					"predicate, object " + 
					"FROM " + 
					"   mTypes_dataset " +
					"   RIGHT OUTER JOIN " + 
					"      object_triples " + 
					"      ON mTypes_dataset.entity = object_triples.subject "
					).createOrReplaceTempView("object_akp");
			
		/* calculate minimal types for each object */
		session.sql("SELECT subj_Type, predicate, " + 
					"   CASE" + 
					"      WHEN minimalType IS NULL THEN 'http://www.w3.org/2002/07/owl#Thing' " + 
					"      ELSE minimalType " + 
					"   END AS obj_Type " +
					"FROM " + 
					"   mTypes_dataset " +
					"   RIGHT OUTER JOIN " + 
					"      object_akp " + 
					"      ON mTypes_dataset.entity = object_akp.object "
				).createOrReplaceTempView("object_akp");
		
		/* group by AKP and calculate freq */
		session.sql("SELECT subj_Type, predicate, obj_Type, COUNT(*) AS freq  " +
					"FROM object_akp " + 
					"GROUP BY subj_Type, predicate, obj_Type "
					).write().option("sep", ";").csv(output_dir + "/spark-object-akp");
	}
	

	public void calculateObjectCardinalities() {
		Dataset<Row> grezzo = session.sql("SELECT " + 
				"   CASE" + 
				"      WHEN mts.minimalType IS NULL THEN 'http://www.w3.org/2002/07/owl#Thing' " + 
				"      ELSE mts.minimalType " + 
				"   END AS subj_Type, " +
				"   t.subject, t.predicate, t.object, " + 
				"   CASE" + 
				"      WHEN mto.minimalType IS NULL THEN 'http://www.w3.org/2002/07/owl#Thing' " + 
				"      ELSE mto.minimalType " + 
				"   END AS obj_Type " +
				"FROM " + 
				"   mTypes_dataset mts" +
				"   RIGHT OUTER JOIN object_triples t ON mts.entity = t.subject " +
				"   LEFT OUTER JOIN mTypes_dataset mto ON mto.entity = t.object " 
			)
		.withColumn("AKP", concat(col("subj_Type"), lit("##"),col("predicate"), lit("##"), col("obj_Type")) );
		
		grezzo.groupBy("subject", "AKP").agg(count("subject").alias("count")).
		groupBy("AKP").agg(max("count").alias("max"), round(avg("count")).alias("avg"), min("count").alias("min"))
		.createOrReplaceTempView("object_cardinalities2");
		
		grezzo.groupBy("object", "AKP").agg(count("object").alias("count")).
		groupBy("AKP").agg(max("count").alias("max"), round(avg("count")).alias("avg"), min("count").alias("min"))
		.createOrReplaceTempView("object_cardinalities1");
		
		session.sql("SELECT c1.AKP, c1.max, c1.avg, c1.min, c2.max AS max2, c2.avg AS avg2, c2.min AS min2 from object_cardinalities1 c1 JOIN object_cardinalities2 c2 ON c1.AKP = c2.AKP")
		.write().option("sep", ";").csv(output_dir + "/spark-object-cardinalities");
		
	}	

}