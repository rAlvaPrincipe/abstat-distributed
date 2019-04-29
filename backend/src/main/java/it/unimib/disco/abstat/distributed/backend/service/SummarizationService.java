package it.unimib.disco.abstat.distributed.backend.service;

import org.springframework.stereotype.Service;

import it.unimib.disco.abstat.distributed.backend.model.SubmitConfig;


@Service
public interface SummarizationService {

	public void summarizeAsyncWrapper(SubmitConfig subCfg, String email) throws Exception;
	public void summarize(SubmitConfig subCfg, String email) throws Exception ;
	
}
