package it.unimib.disco.abstat.distributed.backend.service;

import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import it.unimib.disco.abstat.distributed.backend.dao.OntologyDao;
import it.unimib.disco.abstat.distributed.backend.model.Ontology;

@Service
public class OntologyServiceImpl implements OntologyService {

	@Autowired
	OntologyDao ontologyDao;

	
	public List<Ontology> listOntology() {
		List<Ontology> list = ontologyDao.listOntology();
		Iterator<Ontology> iterator = list.iterator();
		while(iterator.hasNext()) {
			Ontology ont = iterator.next();
			if(ont.getId().equals("empty_ontology"))
				iterator.remove();
		}
		
		return list;
	}

	public String listOntologyJSON() {
		List<Ontology> ontologies = this.listOntology();
		String out = "";
		ObjectMapper mapper = new ObjectMapper();
		ArrayNode array = mapper.createArrayNode();
		
		for(Ontology ontology : ontologies) {
			JsonNode node = mapper.convertValue(ontology, JsonNode.class);
			ObjectNode object = (ObjectNode) node;
			object.remove("path");
			object.remove("type");
			array.add(object);
		}
		try { out = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(array);}
		catch(Exception e) {e.printStackTrace(); }
		
		return "{ \"ontologies\": " + out + "}";
	}
	
	public void add(Ontology ontology) {
		ontologyDao.add(ontology);
		
	}

	
	public void update(Ontology ontology) {
		ontologyDao.update(ontology);
	}

	
	public void delete(Ontology ontology) {
		ontologyDao.delete(ontology);
	}

	
	public Ontology findOntologyById(String id) {
		return ontologyDao.findOntologyById(id);
	}
	
	
}
