package it.unimib.disco.abstat.distributed.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import it.unimib.disco.abstat.distributed.backend.dao.DatasetDao;
import it.unimib.disco.abstat.distributed.backend.model.Dataset;

@Service
public class DatasetServiceImpl implements DatasetService{
	
	@Autowired
	DatasetDao datasetDao;

	
	public List<Dataset> listDataset() {
		return datasetDao.listDataset();
	}

	public String listDatasetJSON() {
		List<Dataset> datasets = this.listDataset();
		String out = "";
		ObjectMapper mapper = new ObjectMapper();
		ArrayNode array = mapper.createArrayNode();
		
		for(Dataset dataset : datasets) {
			JsonNode node = mapper.convertValue(dataset, JsonNode.class);
			ObjectNode object = (ObjectNode) node;
			object.remove("path");
			object.remove("split");
			object.remove("type");
			array.add(object);
		}
		try { out = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(array);}
		catch(Exception e) {e.printStackTrace(); }
		
		return "{ \"datasets\": " + out + "}";
	}
	
	public void add(Dataset dataset) {
		datasetDao.add(dataset);
	}

	
	public void update(Dataset dataset) {
		datasetDao.update(dataset);
	}

	
	public void delete(Dataset dataset) {
		datasetDao.delete(dataset);
		
	}

	
	public Dataset findDatasetById(String id) {
		return datasetDao.findDatasetById(id);
	}
	
	

}
