package it.unimib.disco.abstat.distributed.application;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import it.unimib.disco.abstat.distributed.minimalization.Minimalize;
import it.unimib.disco.abstat.distributed.minimalization.Triple;

public class Summarization {
	
	private SparkSession session;

	public static void main(String[] args) throws Exception {
		Summarization s = new Summarization();
		s.session = SparkSession.builder().appName("Abstat-spark").config("key", "value").master("local").getOrCreate();
		JavaRDD<String> input = s.session.read().textFile(args[0]).javaRDD();

		Splitter splitter = new Splitter();
		JavaRDD<Triple> rdd = splitter.calculate(input);
		Dataset<Row> data = s.session.createDataFrame(rdd, Triple.class);
		data.createOrReplaceTempView("dataset");

		Minimalize minimalize = new Minimalize(args[1]);
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
	}
	
	
	public void split() {
		/* isolate typing asserts */
		session.sql("SELECT subject, object " + 
					"FROM dataset "  + 
					"WHERE type = 'typing'" 
				   ).createOrReplaceTempView("typing_triples");	
		
		/* isolate object relation asserts */
		session.sql("SELECT DISTINCT subject, predicate, object " + 
					"FROM dataset "  + 
					"WHERE type = 'object_relational'" 
				   ).createOrReplaceTempView("object_triples");
		
		/* isolate datatype relation asserts */
		session.sql("SELECT DISTINCT subject, predicate, object, " +                     
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
		.write().option("sep", ";").csv("/home/renzo/Desktop/spark-out/spark-concepts");
	}
	
	
	public void countDatatypes() {
		session.sql("SELECT datatype, COUNT(*) AS freq FROM dt_triples GROUP BY datatype")
		.write().option("sep", ";").csv("/home/renzo/Desktop/spark-out/spark-datatypes");
	}
	
	
	public void countProperties() {
		session.sql("SELECT predicate, COUNT(*) AS freq FROM dt_triples GROUP BY predicate")
		.write().option("sep", ";").csv("/home/renzo/Desktop/spark-out/spark-datatype-properties");
		session.sql("SELECT predicate, COUNT(*) AS freq FROM object_triples GROUP BY predicate")
		.write().option("sep", ";").csv("/home/renzo/Desktop/spark-out/spark-object-properties");
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
					).write().option("sep", ";").csv("/home/renzo/Desktop/spark-out/spark-datatype-akp");
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
					).write().option("sep", ";").csv("/home/renzo/Desktop/spark-out/spark-object-akp");
	}

}