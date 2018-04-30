package uniko.iwvi.fgbas.magoetz.sbo.database;

import java.util.ArrayList;

import lotus.domino.DocumentCollection;
import uniko.iwvi.fgbas.magoetz.sbo.objects.Datasource;
import uniko.iwvi.fgbas.magoetz.sbo.objects.Query;
import uniko.iwvi.fgbas.magoetz.sbo.objects.QueryResult;

import com.google.gson.JsonObject;

public interface IQueryService {

    /*
     * return first returned field value as string
     */
    public String getFieldValue(String hostname, String database, String view, String key, String returnField);

    /*
     * return all found field values as string
     */
    public ArrayList<String> getFieldValues(String queryView, String queryKey, String queryFieldname);

    /*
     * return all found field values as string
     */
    public ArrayList<String> getFieldValues(String queryServer, String queryDatabase, String queryView, String queryKey, String queryFieldname);

    public ArrayList<String> getColumnValues(String queryView, int columnNr);

    public DocumentCollection ftSearch(String databasename, String searchString);

    public DocumentCollection ftSearchView(String databasename, String searchString, String viewname);

    /*
     * returns first json object from a query
     */
    public JsonObject getJsonObject(String view, String key, String returnField);

    /*
     * returns all json objects from a query
     */
    public ArrayList<JsonObject> getJsonObjects(String view, String key, String returnField);

    public DocumentCollection executeQueryFTSearch(Datasource datasourceObject, Query queryObject);

    public JsonObject executeQuery(Datasource datasourceObject, Query queryObject, String objectId) throws Exception;

    public Datasource getDatasourceObject(String datasourceName);

    public Query getQueryObject(String queryName);

    /**
     * Method finds a query result (matching source and query) in a list of query results
     *
     * @param queryResultList: Set of query results
     * @param resultRequest:   Result to be found
     * @return query result as JsonObject if found, otherwise returns null
     */
    public JsonObject getQueryResult(ArrayList<QueryResult> queryResultList, QueryResult resultRequest);

    public String getEmailByNotesUsername(String notesUsername);

    public String getNotesUsernameByEmail(String email);
}
