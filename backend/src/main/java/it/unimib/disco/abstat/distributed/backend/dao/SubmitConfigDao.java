package it.unimib.disco.abstat.distributed.backend.dao;

import java.util.List;

import it.unimib.disco.abstat.distributed.backend.model.SubmitConfig;


public interface SubmitConfigDao {
	
	public List<SubmitConfig> listSubmitConfig();
	
	public void add(SubmitConfig submitConfig);
	
	public void update(SubmitConfig submitConfig);
	
	public void delete(SubmitConfig submitConfig);
	
	public SubmitConfig findSubmitConfigById(String id);
	
}