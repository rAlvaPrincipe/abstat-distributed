package it.unimib.disco.abstat.distributed.backend.service;

import java.util.List;
import java.util.Set;

import it.unimib.disco.abstat.distributed.backend.model.SubmitConfig;


public interface SubmitConfigService {
	
	public List<SubmitConfig> listSubmitConfig();
	
	public List<SubmitConfig> listSubmitConfig(Boolean loaded, Boolean indexed, String search);
	
	public String listSubmitConfigJSON(Boolean loaded, Boolean indexed, String search);

	public Set<String> datasetsUsed(Boolean loaded, Boolean indexed);
	
	public void add(SubmitConfig submitConfig);
	
	public void delete(SubmitConfig submitConfig);
	
	public void update(SubmitConfig submitConfig);
	
	public SubmitConfig findSubmitConfigById(String id);
	
	
}
