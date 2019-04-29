package it.unimib.disco.abstat.distributed.backend.dao;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import it.unimib.disco.abstat.distributed.backend.model.SubmitConfig;


@Repository
public class SubmitConfigDaoImpl implements SubmitConfigDao {
	
	@Autowired
	MongoTemplate mongoTemplate;
	
	private static final String COLLECTION_NAME = "submitConfig";

	
	public List<SubmitConfig> listSubmitConfig() {
		return mongoTemplate.findAll(SubmitConfig.class, COLLECTION_NAME);
	}

	
	public void add(SubmitConfig submitConfig) {
		if(!mongoTemplate.collectionExists(SubmitConfig.class)) {
			mongoTemplate.createCollection(SubmitConfig.class);
		}
		if(submitConfig.getId() == null)
			submitConfig.setId(UUID.randomUUID().toString());
		mongoTemplate.insert(submitConfig, COLLECTION_NAME);
	}
	
	
	public void delete(SubmitConfig submitConfig) {
		mongoTemplate.remove(submitConfig, COLLECTION_NAME);
	}

	
	public SubmitConfig findSubmitConfigById(String id) {
		return mongoTemplate.findById(id, SubmitConfig.class);
	}

	
	public void update(SubmitConfig submitConfig) {
		mongoTemplate.save(submitConfig,COLLECTION_NAME);
	}

}

