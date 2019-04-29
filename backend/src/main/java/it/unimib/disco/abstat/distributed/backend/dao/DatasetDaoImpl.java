package it.unimib.disco.abstat.distributed.backend.dao;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import it.unimib.disco.abstat.distributed.backend.model.Dataset;


@Repository
public class DatasetDaoImpl implements DatasetDao {
	
	@Autowired
	MongoTemplate mongoTemplate;
	
	private static final String COLLECTION_NAME = "datasetAndOntology";
	
	
	public List<Dataset> listDataset() {
		Query query1 = new Query();
		query1.addCriteria(Criteria.where("type").is("dataset"));
		return mongoTemplate.find(query1, Dataset.class, COLLECTION_NAME);
	}

	
	public void add(Dataset dataset) {
		if(!mongoTemplate.collectionExists(COLLECTION_NAME)) {
			mongoTemplate.createCollection(COLLECTION_NAME);
		}
		
		dataset.setId(UUID.randomUUID().toString());
		mongoTemplate.insert(dataset, COLLECTION_NAME);
		
	}


	public void update(Dataset dataset) {
		mongoTemplate.save(dataset, COLLECTION_NAME);
		
	}

	
	public void delete(Dataset dataset) {
		mongoTemplate.remove(dataset, COLLECTION_NAME);
		
	}

	public Dataset findDatasetById(String id) {
		
		return mongoTemplate.findById(id, Dataset.class, COLLECTION_NAME);
	}

}
