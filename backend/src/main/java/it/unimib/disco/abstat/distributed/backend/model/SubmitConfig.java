package it.unimib.disco.abstat.distributed.backend.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class SubmitConfig {

	@Id
	private String id;
	private String dsId;
	private String dsName;
	private List<String> listOntId;
	private List<String> listOntNames;
	private String summaryPath;
	private String timestamp;
	//options..
	private boolean tipoMinimo;
	private boolean inferences;
	private boolean cardinalita;
	private boolean richCardinalities;
	private boolean propertyMinimaliz;
	//indexing and loading
	private boolean loadedMongoDB;
	private boolean indexedSolr;
	
	
	
	public SubmitConfig() {
		super();
	}
	public SubmitConfig(String id, String dsId, ArrayList<String> listOntId) {
		super();
		this.id = id;
		this.dsId = dsId;
		this.listOntId = listOntId;
		this.loadedMongoDB = false;
		this.indexedSolr = false;
	}
	
	public String getId() { return id; }
	public void setId(String id) { this.id = id; }
	
	public String getDsId() { return dsId; }
	public void setDsId(String dsId) { this.dsId = dsId; }
	
	public String getDsName() { return dsName; }
	public void setDsName(String dsName) { this.dsName = dsName; }
	
	public void addOntId(String ontId) { listOntId.add(ontId); }
	
	public List<String> getListOntId() { return listOntId; }
	public void setListOntId(List<String> listOntId) { this.listOntId = listOntId; }
	
	public List<String> getListOntNames() { return listOntNames; }
	public void setListOntNames(List<String> listOntNames) { this.listOntNames = listOntNames; }
	
	public String getSummaryPath() { return summaryPath; }
	public void setSummaryPath(String summaryPath) { this.summaryPath = summaryPath; }
	
	public String getTimestamp() { return timestamp; }
	public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
	
	public boolean isTipoMinimo() { return tipoMinimo; }
	public void setTipoMinimo(boolean tipoMinimo) { this.tipoMinimo = tipoMinimo; }
	
	public boolean isInferences() { return inferences; }
	public void setInferences(boolean inferences) { this.inferences = inferences; }
	
	public boolean isCardinalita() { return cardinalita; }
	public void setCardinalita(boolean cardinalita) { this.cardinalita = cardinalita; }
	
	public boolean isRichCardinalities() { return richCardinalities; }
	public void setRichCardinalities(boolean richCardinalities) { this.richCardinalities = richCardinalities; }
	
	public boolean isPropertyMinimaliz() { return propertyMinimaliz; }
	public void setPropertyMinimaliz(boolean propertyMinimaliz) { this.propertyMinimaliz = propertyMinimaliz; }
	
	public boolean isLoadedMongoDB() { return loadedMongoDB; }
	public void setLoadedMongoDB(boolean loadedMongoDB) { this.loadedMongoDB = loadedMongoDB; }
	
	public boolean isIndexedSolr() { return indexedSolr; }
	public void setIndexedSolr(boolean indexedSolr) { this.indexedSolr = indexedSolr; }
}
