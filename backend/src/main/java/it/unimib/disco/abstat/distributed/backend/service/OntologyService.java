package it.unimib.disco.abstat.distributed.backend.service;

import java.util.List;

import it.unimib.disco.abstat.distributed.backend.model.Ontology;

public interface OntologyService {

	public List<Ontology> listOntology();
	
	public String listOntologyJSON();
	
	public void add(Ontology ontology);
	
	public void update(Ontology ontology);
	
	public void delete(Ontology ontology);
	
	public Ontology findOntologyById(String id);
	
}
