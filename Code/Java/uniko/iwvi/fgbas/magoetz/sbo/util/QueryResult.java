package uniko.iwvi.fgbas.magoetz.sbo.util;

import java.io.Serializable;

import com.google.gson.JsonObject;

public class QueryResult {
	
	private String source = "";
	
	private String query = "";
	
	private JsonObject jsonObject = null;
	
	public QueryResult(String source, String query) {
		this.source = source;
		this.query = query;
	}
	
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public JsonObject getJsonObject() {
		return jsonObject;
	}
	public void setJsonObject(JsonObject jsonObject) {
		this.jsonObject = jsonObject;
	}
}
