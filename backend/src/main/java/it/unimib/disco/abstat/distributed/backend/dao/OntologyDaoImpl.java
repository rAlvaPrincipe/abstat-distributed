package it.unimib.disco.abstat.distributed.backend.dao;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import it.unimib.disco.abstat.distributed.backend.model.Ontology;

@Repository
public class OntologyDaoImpl implements OntologyDao {

	@Autowired
	MongoTemplate mongoTemplate;
	
	private static final String COLLECTION_NAME = "datasetAndOntology";
	
	public List<Ontology> listOntology() {
		Query query1 = new Query();
		query1.addCriteria(Criteria.where("type").is("ontology"));
		return mongoTemplate.find(query1, Ontology.class, COLLECTION_NAME);
	}

	
	public void add(Ontology ontology) {
		if(!mongoTemplate.collectionExists(COLLECTION_NAME)) {
			mongoTemplate.createCollection(COLLECTION_NAME);
		}
		
		ontology.setId(UUID.randomUUID().toString());
		mongoTemplate.insert(ontology, COLLECTION_NAME);
	}

	
	public void update(Ontology ontology) {
		mongoTemplate.save(ontology,COLLECTION_NAME);
		
	}

	public void delete(Ontology ontology) {
		mongoTemplate.remove(ontology,COLLECTION_NAME);
	}

	@Override
	public Ontology findOntologyById(String id) {
		return mongoTemplate.findById(id, Ontology.class, COLLECTION_NAME);
	}

}
