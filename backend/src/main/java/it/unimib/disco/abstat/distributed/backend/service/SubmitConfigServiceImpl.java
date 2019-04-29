package it.unimib.disco.abstat.distributed.backend.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import it.unimib.disco.abstat.distributed.backend.dao.SubmitConfigDao;
import it.unimib.disco.abstat.distributed.backend.model.SubmitConfig;

@Service
public class SubmitConfigServiceImpl implements SubmitConfigService {

	@Autowired
	SubmitConfigDao submitConfigDao;

	public List<SubmitConfig> listSubmitConfig() {
		return submitConfigDao.listSubmitConfig();
	}
	
	
	public List<SubmitConfig> listSubmitConfig(Boolean loaded, Boolean indexed, String search) {
		 List<SubmitConfig> list = submitConfigDao.listSubmitConfig();
		 List<SubmitConfig> output = new ArrayList<SubmitConfig>();
		 
		for (SubmitConfig el : list) {
			if (search != null) {
				if (loaded != null && indexed != null) {
					if (el.isLoadedMongoDB() == loaded && el.isIndexedSolr() == indexed && el.getDsName().contains(search))
						output.add(el);
				} 
				else if (loaded != null) {
					if (el.isLoadedMongoDB() == loaded && el.getDsName().contains(search))
						output.add(el);
				} 
				else if (indexed != null) {
					if (el.isIndexedSolr() == indexed && el.getDsName().contains(search))
						output.add(el);
				} 
				else {
					if (el.getDsName().contains(search))
						output.add(el);
				}
			} 
			else {
				if (loaded != null && indexed != null) {
					if (el.isLoadedMongoDB() == loaded && el.isIndexedSolr() == indexed)
						output.add(el);
				} 
				else if (loaded != null) {
					if (el.isLoadedMongoDB() == loaded)
						output.add(el);
				} 
				else if (indexed != null) {
					if (el.isIndexedSolr() == indexed)
						output.add(el);
				} 
				else 
					output.add(el);
			}
		}
		 return output;
	}
	
	
	public String listSubmitConfigJSON(Boolean loaded, Boolean indexed, String search) {
		List<SubmitConfig> summaries =  this.listSubmitConfig(loaded, indexed, search);
		String out = "";
		ObjectMapper mapper = new ObjectMapper();
		ArrayNode array = mapper.createArrayNode();
		
		for(SubmitConfig summary : summaries) {
			JsonNode node = mapper.convertValue(summary, JsonNode.class);
			ObjectNode object = (ObjectNode) node;
			object.remove("summaryPath");
			array.add(object);
		}
		
		try { out = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(array);}
		catch(Exception e) {e.printStackTrace(); }
		
		return "{ \"summaries\": " + out + "}";
	}

	
	
 /* returns the dataset list of the summaries which satisfy the constraints*/
	public Set<String> datasetsUsed(Boolean loaded, Boolean indexed){
		List<SubmitConfig> results =  this.listSubmitConfig(loaded, indexed, null);
		Set<String> datasets = new HashSet<String>();
		
		for(SubmitConfig result : results) 
			datasets.add(result.getDsName());
		
		return datasets;
	}
	
	
	public void add(SubmitConfig submitConfig) {
		submitConfigDao.add(submitConfig);
		
	}

	
	public void delete(SubmitConfig submitConfig) {
		submitConfigDao.delete(submitConfig);
		
	}
	

	public SubmitConfig findSubmitConfigById(String id) {
		return submitConfigDao.findSubmitConfigById(id);
	}

	
	public void update(SubmitConfig submitConfig) {
		submitConfigDao.update(submitConfig);
	}
	
}