package uniko.iwvi.fgbas.magoetz.sbo.services;

import java.io.Serializable;
import java.util.ArrayList;
import org.openntf.Utils;

import uniko.iwvi.fgbas.magoetz.sbo.objects.Query;
import uniko.iwvi.fgbas.magoetz.sbo.objects.QueryResult;
import uniko.iwvi.fgbas.magoetz.sbo.util.Utilities;
import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.NotesException;
import lotus.domino.View;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.xsp.model.domino.DominoUtils;

public class QueryService implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/*
	 * return first returned field value as string
	 */
	public String getFieldValue(String view, String key, String returnField) throws IndexOutOfBoundsException{
		
		Database notesDB = DominoUtils.getCurrentDatabase();
		ArrayList<String> queryResults = new ArrayList<String>();
		try {
			// return values by key
			queryResults = Utils.Dblookup(notesDB, view, key, returnField);
		} catch (NotesException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		String fieldValue = new String();
		try {
			fieldValue = queryResults.get(0);
		}catch (IndexOutOfBoundsException nex) {
			System.out.println("No query results for view: " + view + " key: " + key + " returnField: " + returnField);
			throw new IndexOutOfBoundsException();
		}
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
	
	public ArrayList<String> getColumnValues(String queryView, int columnNr) {
		
		Database notesDB = DominoUtils.getCurrentDatabase();
		ArrayList<String> queryResults = new ArrayList<String>();
		try {
			// return values by key
			queryResults = Utils.Dbcolumn(notesDB, queryView, columnNr);
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
	
	public DocumentCollection ftSearchView(String databasename, String searchString, String viewname) {
		
		Database notesDB;
		DocumentCollection resultCollection = null;
		try {
			notesDB = DominoUtils.openDatabaseByName(databasename);
			resultCollection = notesDB.createDocumentCollection();
			View view = notesDB.getView(viewname);
			// TODO: Don't update index on every query 
			notesDB.updateFTIndex(true);
			int count = view.FTSearch(searchString);
			if (count == 0){
		        System.out.println("ftSearchView no documents found in view " + viewname + " with searchString " + searchString);
			}else {
			  Document doc = view.getFirstDocument();
			  Document temp = null;  
			  while (doc != null) {
				  resultCollection.addDocument(doc);
				  temp = view.getNextDocument(doc);
				  doc.recycle();
				  doc = temp;
			  }
			}
	        // Clear the full-text search
	        view.clear();
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
	
	// TODO: write wrapper function for various query types
	public DocumentCollection executeQueryFTSearch(JsonObject jsonDatasourceObject, Query queryObject) {
		
		JsonElement jsonFirstSourceElement = jsonDatasourceObject.get("datasource");
		JsonObject jsonFirstSourceObject = jsonFirstSourceElement.getAsJsonObject();
		
		// TODO: Evaluate whole query and change to dynamic execution for all query types (currently only IBM Domino supported)
		String type = jsonFirstSourceObject.get("type").getAsString();
		String hostname = jsonFirstSourceObject.get("hostname").getAsString();
		String database = jsonFirstSourceObject.get("database").getAsString();
		
		// TODO: queryType was added and has to be processed (e.g. "IBM Domino") 
		String queryString = queryObject.getString();
		
		DocumentCollection resultCollection = this.ftSearch(database, queryString);
		
		return resultCollection;
	}
	
	public JsonObject executeQuery(JsonObject jsonDatasourceObject, Query queryObject, String objectId) {
	
		JsonElement jsonFirstSourceElement = jsonDatasourceObject.get("datasource");
		JsonObject jsonFirstSourceObject = jsonFirstSourceElement.getAsJsonObject();
		
		// TODO: Evaluate whole query and change to dynamic execution for all query types (currently only IBM Domino supported)
		String type = jsonFirstSourceObject.get("type").getAsString();
		String hostname = jsonFirstSourceObject.get("hostname").getAsString();
		String database = jsonFirstSourceObject.get("database").getAsString();
		
		// TODO: queryType was added and has to be processed (e.g. "IBM Domino") 
		String queryCommand = queryObject.getCommand();
		String queryServer = queryObject.getServer();
		String queryDatabase = queryObject.getDatabase();
		String queryView = queryObject.getView();
		// TODO process multiple values (return type is List<String>)
		String queryKey = queryObject.getKey().toString();
		String queryFieldname = queryObject.getFieldname();
		
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
		
		JsonObject jsonObject = new JsonObject();
		try{
			 jsonObject = jsonQueryResultObjects.get(0);
		}catch(IndexOutOfBoundsException ex) {
			return null;
		}
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
