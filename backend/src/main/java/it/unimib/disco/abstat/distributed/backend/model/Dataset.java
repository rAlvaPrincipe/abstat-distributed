package it.unimib.disco.abstat.distributed.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Dataset {
	
	@Id
	private String id;
	private String name;
	private String path;
	private String timestamp;
	private String type;
	boolean split;
	
	
	public Dataset() {
		super();
	}

	public Dataset(String id, String name, String path, boolean split, String timestamp, String type) {
		super();
		this.id = id;
		this.name = name;
		this.path = path;
		this.split = split;
		this.timestamp = timestamp;
		this.type = type;
	}

	public String getId() { return id; }
	public void setId(String id) { this.id = id; }

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	public String getPath() { return path; }
	public void setPath(String path) { this.path = path; }

	public String getTimestamp() { return timestamp; }
	public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

	public boolean isSplit() { return split; }
	public void setSplit(boolean split) { this.split = split; }

	public String getType() { return type; }
	public void setType(String type) { this.type = type; }
}
