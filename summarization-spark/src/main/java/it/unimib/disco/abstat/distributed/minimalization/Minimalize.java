package it.unimib.disco.abstat.distributed.minimalization;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.spark.sql.Row;
import org.apache.spark.sql.expressions.MutableAggregationBuffer;
import org.apache.spark.sql.expressions.UserDefinedAggregateFunction;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;


public class Minimalize  extends UserDefinedAggregateFunction {
	
	private static final long serialVersionUID = -4890387983928318765L;
	
	private static Model ontology;
	private List<String> subclassRelations;
	private TypeGraph graph;
	
	public Minimalize(String ontology_path) throws Exception{
		ontology = new Model(ontology_path,"RDF/XML");
		Concepts concepts = extractConcepts(ontology.getOntologyModel());
		this.graph = new TypeGraph(concepts, subclassRelations);
	}
	
	
	@Override
	public StructType inputSchema() {
	    List<StructField> inputFields = new ArrayList<>();
	    inputFields.add(DataTypes.createStructField("input", DataTypes.StringType, true));
	    return DataTypes.createStructType(inputFields);
	}

	@Override
	public StructType bufferSchema() {
	    List<StructField> bufferFields = new ArrayList<>();
	    bufferFields.add(DataTypes.createStructField("bufferArray", DataTypes.createArrayType(DataTypes.StringType), true));
	    return DataTypes.createStructType(bufferFields);
	}

	@Override
	public DataType dataType() {
		return DataTypes.createArrayType(DataTypes.StringType);
	}

	@Override
	public boolean deterministic() {
		return false;
	}


	@Override
	public void initialize(MutableAggregationBuffer buffer) {
		buffer.update(0, new ArrayList<String>());
		
	}

	@Override
	public void update(MutableAggregationBuffer buffer, Row input) {
		 List<String> newValue = new ArrayList<String>();
		 newValue.addAll(buffer.getList(0));
         newValue.add(input.getString(0));
         buffer.update(0, newValue);
	}

	@Override
	public void merge(MutableAggregationBuffer buffer1, Row buffer2) {
		 List<String> newValue = new ArrayList<String>();
		 newValue.addAll(buffer1.getList(0));
		 newValue.addAll(buffer2.getList(0));
         buffer1.update(0, newValue);
	}

	@Override
	public Object evaluate(Row buffer) {
        return minimalize(buffer.getList(0));
	}
	
	
//--------------------------------------------------------------------------
	
	public List<String> minimalize(List<String> set) {
		Set<String> allTypes = new HashSet<String>();
		Set<String> minimalTypes = new HashSet<String>();
		allTypes.addAll(set);

		String firstMinimalType = (String) allTypes.toArray()[0];
		minimalTypes.add(firstMinimalType);
		allTypes.remove(firstMinimalType);
		
		boolean minimal= true;
		for(String concept: allTypes) {
			Iterator<String> it = minimalTypes.iterator();
			while(it.hasNext()) {
				String minimalType = it.next();
				if(!graph.pathsBetween(minimalType, concept).isEmpty())
					minimal = false;
				if(!graph.pathsBetween(concept, minimalType).isEmpty())
					it.remove();
			}
			if(minimal)
				minimalTypes.add(concept);
			minimal = true;
		}

		List<String> output = new ArrayList<String>();
		output.addAll(minimalTypes);
		return output;
	}


	private Concepts extractConcepts(OntModel ontology) {
		ConceptExtractor cExtract = new ConceptExtractor();
		cExtract.setConcepts(ontology, true);
		
		Concepts concepts = new Concepts();
		concepts.setConcepts(cExtract.getConcepts());
		concepts.setExtractedConcepts(cExtract.getExtractedConcepts());
		concepts.setObtainedBy(cExtract.getObtainedBy());
		
		OntologySubclassOfExtractor extractor = new OntologySubclassOfExtractor();
		extractor.setConceptsSubclassOf(concepts, ontology);
		
		this.subclassRelations = new ArrayList<String>();
		for(List<OntClass> subClasses : extractor.getConceptsSubclassOf().getConceptsSubclassOf()){
			this.subclassRelations.add(subClasses.get(0) + "##" + subClasses.get(1));
		}

		return concepts;
	}

}
