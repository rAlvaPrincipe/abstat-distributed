package it.unimib.disco.abstat.distributed.backend.dao;

import java.util.List;

import it.unimib.disco.abstat.distributed.backend.model.Dataset;

public interface DatasetDao {
	
	public List<Dataset> listDataset();
	
	public void add(Dataset dataset);
	
	public void update(Dataset dataset);
	
	public void delete(Dataset dataset);
	
	public Dataset findDatasetById(String id);
	
}

