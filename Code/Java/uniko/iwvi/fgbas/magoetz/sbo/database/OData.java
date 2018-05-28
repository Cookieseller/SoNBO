package uniko.iwvi.fgbas.magoetz.sbo.database;

import java.io.InputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.faces.context.FacesContext;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.NotesException;
import lotus.domino.View;

import org.apache.commons.io.IOUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.retrieve.ODataRawRequest;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.client.core.http.BasicAuthHttpClientFactory;
import org.openntf.Utils;

import uniko.iwvi.fgbas.magoetz.sbo.SoNBOManager;
import uniko.iwvi.fgbas.magoetz.sbo.SoNBOSession;
import uniko.iwvi.fgbas.magoetz.sbo.objects.Datasource;
import uniko.iwvi.fgbas.magoetz.sbo.objects.Node;
import uniko.iwvi.fgbas.magoetz.sbo.objects.Query;
import uniko.iwvi.fgbas.magoetz.sbo.objects.QueryResult;
import uniko.iwvi.fgbas.magoetz.sbo.services.CacheService;
import uniko.iwvi.fgbas.magoetz.sbo.util.Utilities;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.xsp.model.domino.DominoUtils;

public class OData implements IQueryService, Serializable {

    private static final long serialVersionUID = 1L;

    /*
     * return first returned field value as string
     */
    public String getFieldValue(String hostname, String database, String view, String key, String returnField) {

        Database notesDB = DominoUtils.getCurrentDatabase();
        ArrayList<String> queryResults = new ArrayList<String>();
        try {
            // return values by key
            queryResults = Utils.Dblookup(notesDB, view, key, returnField);
        } catch (NotesException ex) {
            ex.printStackTrace();
        }
        String fieldValue = new String();
        try {
            fieldValue = queryResults.get(0);
        } catch (IndexOutOfBoundsException nex) {
            System.out.println("No query results for view: " + view + " key: " + key + " returnField: " + returnField);
            return null;
        }
        return fieldValue;
    }

    /*
     * return all found field values as string
     */
    public ArrayList<String> getFieldValues(String queryView, String queryKey, String queryFieldname) {

        Database notesDB = DominoUtils.getCurrentDatabase();
        ArrayList<String> queryResults = new ArrayList<String>();
        try {
            // return values by key
            queryResults = Utils.Dblookup(notesDB, queryView, queryKey, queryFieldname);
        } catch (NotesException ex) {
            ex.printStackTrace();
        }
        return queryResults;
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
            //notesDB.updateFTIndex(true);
            resultCollection = notesDB.FTSearch(searchString);
        } catch (NotesException e1) {
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
            //notesDB.updateFTIndex(true);
            int count = view.FTSearch(searchString);
            if (count == 0) {
                System.out.println("ftSearchView no documents found in view " + viewname + " with searchString " + searchString);
            } else {
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
            ex.printStackTrace();
        }

        // convert query results to json objects
        ArrayList<JsonObject> jsonObjects = new ArrayList<JsonObject>();

        for (String s : queryResults) {
            JsonObject o = new JsonParser().parse(s).getAsJsonObject();
            jsonObjects.add(o);
        }

        JsonObject jsonObject = null;
        try {
            jsonObject = jsonObjects.get(0);
        } catch (IndexOutOfBoundsException ex) {
            System.out.println("No JSON object found in view: " + view + " with key " + key + " and returnField " + returnField);
        }

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
            ex.printStackTrace();
        }

        // convert query results to json objects
        ArrayList<JsonObject> jsonObjects = new ArrayList<JsonObject>();

        for (String s : queryResults) {
            JsonObject o = new JsonParser().parse(s).getAsJsonObject();
            jsonObjects.add(o);
        }

        return jsonObjects;
    }

    // TODO: write wrapper function for various query types
    public DocumentCollection executeQueryFTSearch(Datasource datasourceObject, Query queryObject) {

        // TODO: Evaluate whole query and change to dynamic execution for all query types (currently only IBM Domino supported)
        //String type = datasourceObject.getType();
        String database = datasourceObject.getDatabase();

        // TODO: queryType was added and has to be processed (e.g. "IBM Domino")
        String queryString = queryObject.getString();
        String viewName = queryObject.getView();

        Database notesDB = DominoUtils.getCurrentDatabase();
        DocumentCollection resultCollection = null;
        try {
            resultCollection = notesDB.createDocumentCollection();
            if (viewName.equals("")) {
                resultCollection = this.ftSearch(database, queryString);
            } else {
                resultCollection = this.ftSearchView(database, queryString, viewName);
            }

        } catch (NotesException e) {
            e.printStackTrace();
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
        return resultCollection;
    }

    public JsonArray executeQuery(Datasource datasourceObject, Query queryObject) {
    	CacheService cache = new CacheService("queryCache");
        ODataClient client = ODataClientFactory.getClient();

        FacesContext ctx = FacesContext.getCurrentInstance(); 
        SoNBOSession session = (SoNBOSession) ctx.getApplication().getVariableResolver().resolveVariable(ctx, "soNBOSession");
        client.getConfiguration().setHttpClientFactory(session.getClientFactory());

        String uriRoot 	 = datasourceObject.getHostname() + datasourceObject.getDatabase();
        String entitySet = queryObject.getView();

        String skip 		  = queryObject.getSkip();
        JsonArray resultSet   = new JsonArray();
        JsonArray queryResult = new JsonArray();
        boolean cont 		  = false;

		do {
	        URI uri = client.newURIBuilder(uriRoot)
		    	.appendEntitySetSegment(entitySet)
		    	.filter(queryObject.getString())
		    	.skipToken(skip)
		    	.build();

	        String result = cache.get(uri.toString());
	        if (result != null) {
	        	return new JsonParser().parse(result).getAsJsonArray();
	        }

	        InputStream inputStream;
	        try {
	        	ODataRawRequest request = client.getRetrieveRequestFactory().getRawRequest(uri);
	        	request.setAccept("application/json");
		        request.setContentType("application/json;odata.metadata=full");

		        inputStream = request.execute().getRawResponse();
	        } catch (Exception e) {
	        	Utilities.redirectToAuthentication();
	        	return null;
	        }
	        
	        StringWriter writer 	= new StringWriter();
	        try {
				IOUtils.copy(inputStream, writer, "utf-8");

		        String jsonString = writer.toString();
		        JsonParser parser = new JsonParser();
		        JsonObject obj    = parser.parse(jsonString).getAsJsonObject();
		        queryResult	  = obj.get("value").getAsJsonArray();
		        resultSet.addAll(queryResult);
		        cache.put(uri.toString(), resultSet.toString());

		        cont = false;
		        if (obj.has("odata.nextLink")) {
		        	uri = URI.create(obj.get("odata.nextLink").getAsString());

		        	List<NameValuePair> params = URLEncodedUtils.parse(uri, "UTF-8");
		        	for (NameValuePair param : params) {
		        		if (param.getName().equals("skiptoken")) {
		        			skip = param.getValue();
		        			Utilities.remotePrint("Skiptoken found: " + skip);
		        			cont = true;
		        			break;
		        		}
		        	}
		        }		        
			} catch (Exception e) {
				e.printStackTrace();
			}
        } while (cont);

        return resultSet;
    }

    public JsonObject executeQuery(Datasource datasourceObject, Query queryObject, String objectId) {

        ODataClient client = ODataClientFactory.getClient();

        FacesContext ctx = FacesContext.getCurrentInstance(); 
        SoNBOSession session = (SoNBOSession) ctx.getApplication().getVariableResolver().resolveVariable(ctx, "soNBOSession");
        client.getConfiguration().setHttpClientFactory(session.getClientFactory());

        String uriRoot 	 = datasourceObject.getHostname() + datasourceObject.getDatabase();
        String entitySet = queryObject.getView();
        URI uri 	 	 = client.newURIBuilder(uriRoot).appendEntitySetSegment(entitySet).filter(queryObject.getString()).build();

        InputStream inputStream;
        try {
        	ODataRawRequest request = client.getRetrieveRequestFactory().getRawRequest(uri);
            request.setAccept("application/json");
            request.setContentType("application/json;odata.metadata=full");
            
            inputStream = request.execute().getRawResponse();
        } catch (Exception e) {
        	Utilities.redirectToAuthentication();
        	return null;
        }

        
        StringWriter writer 	= new StringWriter();
        JsonObject jsonObject 	= new JsonObject();
        try {
			IOUtils.copy(inputStream, writer, "utf-8");
			
	        String jsonString = writer.toString();
	        JsonParser parser = new JsonParser();
	        JsonObject obj    = parser.parse(jsonString).getAsJsonObject();
	        jsonObject 		  = obj.get("value").getAsJsonObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
        return jsonObject;
    }

    public Datasource getDatasourceObject(String datasourceName) {

        String searchString = "FIELD datasourcename = \"" + datasourceName + "\"";
        Document datasourceDoc = null;
        Datasource datasource = new Datasource();
        try {
            datasourceDoc = this.ftSearchView("", searchString, "datasources").getFirstDocument();
            if (datasourceDoc != null) {
                datasource.setName(datasourceDoc.getItemValueString("datasourcename"));
                datasource.setType(datasourceDoc.getItemValueString("datasourceType"));
                datasource.setHostname(datasourceDoc.getItemValueString("datasourceHostname"));
                datasource.setDatabase(datasourceDoc.getItemValueString("datasourceDatabase"));
                datasource.setAuth(Boolean.getBoolean(datasourceDoc.getItemValueString("datasourceAuth")));
                datasource.setUser(datasourceDoc.getItemValueString("datasourceUser"));
                datasource.setPassword(datasourceDoc.getItemValueString("datasourcePassword"));
            }
        } catch (NotesException e) {
            System.out.println("Error retrieving datasource with name: " + datasourceName);
            e.printStackTrace();
        }
        return datasource;
    }

    /**
     * @TODO should this be private?
     * Take the configured query and replace all tokens with real values from the given node
     * 
     * @param node
     * @param query
     * @return
     */
    public String buildQuery(Node node, Query query) {
    	List<String> tokens = Utilities.getTokenList(query.getString());
    	Map<String, String> replaceAttributesMap = new HashMap<String, String>();
        for (String replaceAttributeKey : tokens) {
            String replaceAttributeValue = node.getAttributeValueByField(replaceAttributeKey);
            replaceAttributesMap.put(replaceAttributeKey, replaceAttributeValue);
        }
        
        Utilities.remotePrint(Utilities.replaceTokens(query.getString(), replaceAttributesMap));
    	return Utilities.replaceTokens(query.getString(), replaceAttributesMap);
    }
    
    /**
     * 
     * @param node
     * @param query
     * @param datasource
     * @return
     */
    public JsonArray getAdjacentNodes(Node node, Query query, Datasource datasource) {
    	String queryString = buildQuery(node, query);
    	Query adjacentQuery = new Query(query);
    	adjacentQuery.setString(queryString);
    	
    	return executeQuery(datasource, adjacentQuery);
    }
    
    @SuppressWarnings("unchecked")
    public Query getQueryObject(String queryName) {
        String searchString = "FIELD queryName = \"" + queryName + "\"";
        Document queryDoc = null;
        Query query = new Query();
        try {
            queryDoc = this.ftSearchView("", searchString, "queries").getFirstDocument();
            if (queryDoc != null) {
                query.setName(queryDoc.getItemValueString("queryName"));
                query.setType(queryDoc.getItemValueString("queryType"));
                query.setCommand(queryDoc.getItemValueString("queryCommand"));
                query.setView(queryDoc.getItemValueString("queryView"));
                Vector<String> keysVector = (Vector<String>) queryDoc.getItemValue("queryKey");
                query.setKey(new ArrayList<String>(keysVector));
                query.setKeyValueReturnType(queryDoc.getItemValueString("queryKeyValueReturnType"));
                query.setFieldname(queryDoc.getItemValueString("queryFieldname"));
                query.setColumnNr((int) queryDoc.getItemValueDouble("queryColumnNr"));
                query.setString(queryDoc.getItemValueString("queryString"));
                query.setSkip(queryDoc.getItemValueString("querySkip"));
            }
        } catch (NotesException e) {
            System.out.println("Error retrieving query with name: " + queryName);
            e.printStackTrace();
        }
        return query;
    }

    /**
     * Method finds a query result (matching source and query) in a list of query results
     *
     * @param queryResultList: Set of query results
     * @param resultRequest:   Result to be found
     * @return query result as JsonObject if found, otherwise returns null
     */
    public JsonObject getQueryResult(ArrayList<QueryResult> queryResultList, QueryResult resultRequest) {
        JsonObject jsonObject = null;
        for (QueryResult qr : queryResultList) {
            if (qr.getSource().equals(resultRequest.getSource()) && qr.getQuery().equals(resultRequest.getQuery())) {
                jsonObject = qr.getJsonObject();
            }
        }
        return jsonObject;
    }

    public String getEmailByNotesUsername(String notesUsername) {
        return this.getFieldValue("", "GEDYSIntraWare8\\georga.nsf", "Usernames", notesUsername, "email");
    }

    public String getNotesUsernameByEmail(String email) {
        return this.getFieldValue("", "GEDYSIntraWare8\\georg.nsf", "SoNBO\\(Emails)", email, "username");
    }

    protected void addEntry(List<Vector<String>> dataList, String formName) {

        try {
            Database db = DominoUtils.getCurrentDatabase();
            Document doc = db.createDocument();

            doc.replaceItemValue("Form", formName);
            for (Vector<String> dataVector : dataList) {
                doc.replaceItemValue(dataVector.get(0), dataVector.get(1));
            }
            doc.save();

        } catch (NotesException e) {
            throw new RuntimeException(e);
        }
    }
}
