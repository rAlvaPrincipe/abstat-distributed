package it.unimib.disco.abstat.distributed.backend.dao;

import java.util.List;

import it.unimib.disco.abstat.distributed.backend.model.Ontology;


public interface OntologyDao {
	
	public List<Ontology> listOntology();
	
	public void add(Ontology ontology);
	
	public void update(Ontology ontology);
	
	public void delete(Ontology ontology);
	
	public Ontology findOntologyById(String id);
	
}