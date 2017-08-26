package uniko.iwvi.fgbas.magoetz.sbo.services;

import java.io.Serializable;
import java.util.ArrayList;
import org.openntf.Utils;
import uniko.iwvi.fgbas.magoetz.sbo.util.QueryResult;
import uniko.iwvi.fgbas.magoetz.sbo.util.Utilities;
import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.NotesException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.xsp.model.domino.DominoUtils;

public class QueryService implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/*
	 * return first returned field value as string
	 */
	public String getFieldValue(String view, String key, String returnField) {
		
		Database notesDB = DominoUtils.getCurrentDatabase();
		ArrayList<String> queryResults = new ArrayList<String>();
		try {
			// return values by key
			queryResults = Utils.Dblookup(notesDB, view, key, returnField);
		} catch (NotesException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		String fieldValue = queryResults.get(0);
		return fieldValue;
	}

	/*
	 * return all found field values as string
	 */
	public ArrayList<String> getFieldValues(String queryServer, String queryDatabase, String queryView, String queryKey, String queryFieldname) {
		
		ArrayList<String> queryResults = new ArrayList<String>();
		try {
			// return values by key
			queryResults = Utils.Dblookup(queryServer, queryDatabase, queryView, queryKey, queryFieldname);
		} catch (NotesException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		return queryResults;
	}
	
	public DocumentCollection ftSearch(String databasename, String searchString) {
		
		Database notesDB;
		DocumentCollection resultCollection = null;
		try {
			notesDB = DominoUtils.openDatabaseByName(databasename);
			// TODO: Don't update index on every query 
			notesDB.updateFTIndex(true);
			resultCollection = notesDB.FTSearch(searchString);
		} catch (NotesException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return resultCollection;
	}
	
	/*
	 * returns first json object from a query
	 */
	public JsonObject getJsonObject(String view, String key, String returnField) {
		
		Database notesDB = DominoUtils.getCurrentDatabase();
		
		ArrayList<String> queryResults = new ArrayList<String>();
		try {
			// return values by key
			queryResults = Utils.Dblookup(notesDB, view, key, returnField);
		} catch (NotesException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		
		// convert query results to json objects
		ArrayList<JsonObject> jsonObjects = new ArrayList<JsonObject>();
		
		for(String s : queryResults) {			
			JsonObject o = new JsonParser().parse(s).getAsJsonObject();
			jsonObjects.add(o);
		}
		
		JsonObject jsonObject = jsonObjects.get(0);
		
		return jsonObject;
	}
	
	/*
	 * returns all json objects from a query
	 */
	public ArrayList<JsonObject> getJsonObjects(String view, String key, String returnField) {
		
		Database notesDB = DominoUtils.getCurrentDatabase();
		
		ArrayList<String> queryResults = new ArrayList<String>();
		try {
			// return values by key
			queryResults = Utils.Dblookup(notesDB, view, key, returnField);
		} catch (NotesException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		
		// convert query results to json objects
		ArrayList<JsonObject> jsonObjects = new ArrayList<JsonObject>();
		
		for(String s : queryResults) {			
			JsonObject o = new JsonParser().parse(s).getAsJsonObject();
			jsonObjects.add(o);
		}
		
		return jsonObjects;
	}
	
	public JsonObject executeQuery(JsonObject jsonDatasourceObject, JsonObject jsonQueryObject, String objectId) {
		
		JsonElement jsonFirstSourceElement = jsonDatasourceObject.get("datasource");
		JsonObject jsonFirstSourceObject = jsonFirstSourceElement.getAsJsonObject();
		
		// TODO: Evaluate whole query and change to dynamic execution for all query types (currently only IBM Domino supported)
		String type = jsonFirstSourceObject.get("type").getAsString();
		String hostname = jsonFirstSourceObject.get("hostname").getAsString();
		String database = jsonFirstSourceObject.get("database").getAsString();
		
		JsonElement jsonFirstQueryElement = jsonQueryObject.get("query");
		JsonObject jsonFirstQueryObject = jsonFirstQueryElement.getAsJsonObject();
		
		// TODO: queryType was added and has to be processed (e.g. "IBM Domino") 
		String queryCommand = jsonFirstQueryObject.get("command").getAsString();
		String queryServer = jsonFirstQueryObject.get("server").getAsString();
		String queryDatabase = jsonFirstQueryObject.get("database").getAsString();
		String queryView = jsonFirstQueryObject.get("view").getAsString();
		String queryKey = jsonFirstQueryObject.get("key").getAsString();
		String queryFieldname = jsonFirstQueryObject.get("fieldname").getAsString();
		
		// TODO in this case objectId, but in other cases?
		queryKey = objectId;
		
		// execute query to get results
		// TODO make variable dependent on query command
		ArrayList<String> queryResults = new ArrayList<String>();
		try {
			// return values by key
			queryResults = Utils.Dblookup(queryServer, queryDatabase, queryView, queryKey, queryFieldname);
			
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
		
		// get json object from query result
		ArrayList<JsonObject> jsonQueryResultObjects = new ArrayList<JsonObject>();
		
		for(String s : queryResults) {
			JsonObject o = new JsonParser().parse(s).getAsJsonObject();
			jsonQueryResultObjects.add(o);
		}
		
		JsonObject jsonObject = jsonQueryResultObjects.get(0);
		// log json
		Utilities utilities = new Utilities();
		utilities.printJson(jsonObject, "query result json object");
		return jsonObject;
	}
	
	/**
	 * Method finds a query result (matching source and query) in a list of query results
	 * 
	 * @param queryResultList: Set of query results
	 * @param resultRequest: Result to be found
	 * @return query result as JsonObject if found, otherwise returns null
	 */
	public JsonObject getQueryResult(ArrayList<QueryResult> queryResultList, QueryResult resultRequest) {
		JsonObject jsonObject = null;
		for(QueryResult qr : queryResultList) {
			if(qr.getSource().equals(resultRequest.getSource()) && qr.getQuery().equals(resultRequest.getQuery())) {
				jsonObject = qr.getJsonObject();
			}
		}
		return jsonObject;
	}
}
