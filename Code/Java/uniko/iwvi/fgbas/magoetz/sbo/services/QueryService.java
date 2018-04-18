package uniko.iwvi.fgbas.magoetz.sbo.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.NotesException;
import lotus.domino.View;
import org.openntf.Utils;
import uniko.iwvi.fgbas.magoetz.sbo.objects.Datasource;
import uniko.iwvi.fgbas.magoetz.sbo.objects.Query;
import uniko.iwvi.fgbas.magoetz.sbo.objects.QueryResult;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.xsp.model.domino.DominoUtils;

public class QueryService implements Serializable, IQueryService {

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
            // TODO Auto-generated catch block
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
            // TODO Auto-generated catch block
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
            //notesDB.updateFTIndex(true);
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
            // TODO Auto-generated catch block
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
        //String hostname = datasourceObject.getHostname();
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
        return resultCollection;
    }

    /**
     * @TODO Implement caching
     *
     * @param datasourceObject
     * @param queryObject
     * @param objectId
     * @return
     */
    public JsonObject executeQuery(Datasource datasourceObject, Query queryObject, String objectId) {

        // TODO: Evaluate whole query and change to dynamic execution for all query types (currently only IBM Domino supported)
        //String type = datasourceObject.getType();
        String hostname = datasourceObject.getHostname();
        String database = datasourceObject.getDatabase();

        // TODO: queryType was added and has to be processed (e.g. "IBM Domino")
        //String queryCommand = queryObject.getCommand();
        //String queryServer = queryObject.getServer();
        //String queryDatabase = queryObject.getDatabase();
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
            queryResults = Utils.Dblookup(hostname, database, queryView, queryKey, queryFieldname);

        } catch (NotesException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // get json object from query result
        ArrayList<JsonObject> jsonQueryResultObjects = new ArrayList<JsonObject>();

        for (String s : queryResults) {
            JsonObject o = new JsonParser().parse(s).getAsJsonObject();
            jsonQueryResultObjects.add(o);
        }

        JsonObject jsonObject = new JsonObject();
        try {
            jsonObject = jsonQueryResultObjects.get(0);
        } catch (IndexOutOfBoundsException ex) {
            return null;
        }
        // log json
        //Utilities utilities = new Utilities();
        //utilities.printJson(jsonObject, "query result json object");
        return jsonObject;
    }

    @SuppressWarnings("unchecked")
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
            // TODO Auto-generated catch block
            System.out.println("Error retrieving datasource with name: " + datasourceName);
            e.printStackTrace();
        }
        return datasource;
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
            }
        } catch (NotesException e) {
            // TODO Auto-generated catch block
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
        /*
		Session session = DominoUtils.getCurrentSession();
		try {
			Directory dir = session.getDirectory();
			// TODO local version
			DirectoryNavigator dirnav = dir.lookupNames("($Users)", notesUsername, "InternetAddress");
			dirnav.findFirstName();
			if(dirnav.getCurrentMatches() > 0) {
				Vector dirent = dirnav.getFirstItemValue();
				for(Object obj : dirent) {
					return obj.toString();
				}
			}			
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "n/a";
		*/
    }

    public String getNotesUsernameByEmail(String email) {

        return this.getFieldValue("", "GEDYSIntraWare8\\georg.nsf", "SoNBO\\(Emails)", email, "username");
		
		/* 
		String notesUsername = "not found";
		
		DocumentCollection resultCollection = this.ftSearchView("names.nsf", email, "($Users)");
		try {
			Document doc = resultCollection.getFirstDocument();
			notesUsername = doc.getItemValueString("MailAddress");
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return notesUsername;
		*/
		
		/*
		Session session = DominoUtils.getCurrentSession();
		try {
			Directory dir = session.getDirectory();
			// TODO local version
			DirectoryNavigator dirnav = dir.lookupNames("($Users)", email, "InternetAddress");
			dirnav.findFirstName();
			if(dirnav.getCurrentMatches() > 0) {
				Vector dirent = dirnav.getFirstItemValue();
				for(Object obj : dirent) {
					System.out.println(obj.toString());
					//return obj.toString();
				}
			}			
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "n/a";
		*/
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
