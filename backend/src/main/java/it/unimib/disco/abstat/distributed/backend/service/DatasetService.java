package it.unimib.disco.abstat.distributed.backend.service;

import java.util.List;

import it.unimib.disco.abstat.distributed.backend.model.Dataset;


public interface DatasetService {

	public List<Dataset> listDataset();
	
	public String listDatasetJSON();
	
	public void add(Dataset dataset);
	
	public void update(Dataset dataset);
	
	public void delete(Dataset dataset);
	
	public Dataset findDatasetById(String id);
	
}
