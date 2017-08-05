package uniko.iwvi.fgbas.magoetz.sbo;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;
import javax.faces.context.FacesContext;
import lotus.domino.Database;
import lotus.domino.NotesException;
import org.openntf.Utils;
import uniko.iwvi.fgbas.magoetz.sbo.objects.BusinessObject;
import uniko.iwvi.fgbas.magoetz.sbo.objects.ConfigurationObject;
import uniko.iwvi.fgbas.magoetz.sbo.objects.ConfigurationObject.ConfigurationObjectAttribute;
import uniko.iwvi.fgbas.magoetz.sbo.util.QueryResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.xsp.model.domino.DominoUtils;

/**
 * @author Flemming
 *
 */
public class SboManager implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String objectId = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("objectId");
	
	private String objectType = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("objectType");
	
	private ArrayList<String> notificationCodeList = new ArrayList<String>();
	
	public BusinessObject businessObject;
	
	public void init(){
		
		Database notesDB = DominoUtils.getCurrentDatabase();
		this.businessObject = new BusinessObject();
		ArrayList<QueryResult> queryResultList = new ArrayList<QueryResult>();
		
		System.out.println("NEW REQUEST FOR BUSINESS OBJECT");
		
		if(objectId != null && objectType != null) {
			
			// TODO: check if object type exists 
			
			Gson gson = new GsonBuilder().setPrettyPrinting().create();

			// 1. GET CONFIGURATION DOCUMENT FOR OBJECT TYPE
			
			ArrayList<String> configQueryResults = new ArrayList<String>();
			try {
				// return values by key
				configQueryResults = Utils.Dblookup(notesDB, "config", objectType, "1");
			} catch (NotesException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
			
			// convert query results to json objects
			ArrayList<JsonObject> jsonConfigObjects = new ArrayList<JsonObject>();
			
			for(String s : configQueryResults) {			
				JsonObject o = new JsonParser().parse(s).getAsJsonObject();
				jsonConfigObjects.add(o);
			}
			
			JsonObject jsonConfigObject = jsonConfigObjects.get(0);
			String configObjStr = gson.toJson(jsonConfigObject);
			System.out.println("------------");
			System.out.println("Parsed config json");
			System.out.println(configObjStr);
			
			// get config information
			ConfigurationObject configObject = new ConfigurationObject();
			// object type
			JsonElement jsonFirstConfigElement = jsonConfigObject.get(objectType);
			JsonObject jsonFirstConfigObject = jsonFirstConfigElement.getAsJsonObject();			
			String objectType = jsonFirstConfigObject.get("objectType").toString();
			configObject.setObjectType(objectType);
			// peers
			JsonElement firstLevelConfigElementPeers = jsonFirstConfigObject.get("peers");
			String[] peers = gson.fromJson(firstLevelConfigElementPeers, String[].class);
			configObject.setPeers(peers);
			// attributes
			JsonElement firstLevelConfigElementFields = jsonFirstConfigObject.get("attributes");
			JsonObject firstLevelConfigObjectFields = firstLevelConfigElementFields.getAsJsonObject();
			
			Set<String> attributes = firstLevelConfigObjectFields.keySet();
			
			System.out.println("Object attributes: ");
			for(String s : attributes) {
				System.out.println("key " + s);
				JsonElement secondLevelConfigElement = firstLevelConfigObjectFields.get(s);
				JsonObject secondLevelConfigObject = secondLevelConfigElement.getAsJsonObject();
				
				String datasource = secondLevelConfigObject.get("datasource").getAsString();
				String query = secondLevelConfigObject.get("query").getAsString();
				String fieldname = secondLevelConfigObject.get("fieldname").getAsString();
				int displayfield = secondLevelConfigObject.get("displayfield").getAsInt();
				
				configObject.addConfigurationObjectAttribute(datasource, query, fieldname, displayfield);
			}
			
			// 2. RETRIEVE INFORMATION FOR BUSINESS OBJECT BASED ON CONFIGURATION

			// get value for each object attribute
			for(ConfigurationObjectAttribute configObjAttr : configObject.getConfigurationObjectAttributes()) {

				String datasource = configObjAttr.getDatasource();
				String query = configObjAttr.getQuery();
				QueryResult queryResult = new QueryResult(datasource, query);
				
				JsonObject jsonQueryResultObject = this.getQueryResult(queryResultList, queryResult);
				
				if(jsonQueryResultObject == null) {
					// get data source configuration
					ArrayList<String> sourceQueryResults = new ArrayList<String>();
					try {
						// return values by key
						sourceQueryResults = Utils.Dblookup(notesDB, "datasources", datasource, "1");
					} catch (NotesException ex) {
						// TODO Auto-generated catch block
						ex.printStackTrace();
					}
					// convert query results to json objects
					ArrayList<JsonObject> jsonDatasourceObjects = new ArrayList<JsonObject>();
					
					for(String s : sourceQueryResults) {
						JsonObject o = new JsonParser().parse(s).getAsJsonObject();
						jsonDatasourceObjects.add(o);
					}
	
					JsonObject jsonDatasourceObject = jsonDatasourceObjects.get(0);
					JsonElement jsonFirstSourceElement = jsonDatasourceObject.get("datasource");
					JsonObject jsonFirstSourceObject = jsonFirstSourceElement.getAsJsonObject();
					
					//TODO: Evaluate whole query and change to dynamic execution for all query types
					String hostname = jsonFirstSourceObject.get("hostname").getAsString();
					String database = jsonFirstSourceObject.get("database").getAsString();
					
					// get query				
					ArrayList<String> queryConfigResults = new ArrayList<String>();
					try {
						// return values by key
						queryConfigResults = Utils.Dblookup(notesDB, "queries", query, "0");
					} catch (NotesException ex) {
						// TODO Auto-generated catch block
						ex.printStackTrace();
					}
					
					ArrayList<JsonObject> jsonQueryObjects = new ArrayList<JsonObject>();
					
					for(String s : queryConfigResults) {				
						JsonObject o = new JsonParser().parse(s).getAsJsonObject();
						jsonQueryObjects.add(o);
					}
					
					JsonObject jsonQueryObject = jsonQueryObjects.get(0);
					JsonElement jsonFirstQueryElement = jsonQueryObject.get("query");
					JsonObject jsonFirstQueryObject = jsonFirstQueryElement.getAsJsonObject();
					
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
					
					jsonQueryResultObject = jsonQueryResultObjects.get(0);
					queryResult.setJsonObject(jsonQueryResultObject);
					queryResultList.add(queryResult);
					
					String queryResultObjStr = gson.toJson(jsonQueryResultObject);
					System.out.println("------------");
					System.out.println("Parsed queryResult json");
					System.out.println(queryResultObjStr);
				}
				
				JsonElement jsonFirstQueryResultElement = jsonQueryResultObject.get(objectId);
				JsonObject jsonFirstQueryResultObject = jsonFirstQueryResultElement.getAsJsonObject();			
				
				String fieldname = configObjAttr.getFieldname();
				String value = jsonFirstQueryResultObject.get(fieldname).getAsString();
				
				// load attribute key and value into business object
				int displayfield = configObjAttr.getDisplayfield();
				
				switch(displayfield) {
					case 1:
						this.businessObject.displayField1.put(fieldname, value);
					break;
					case 2:
						this.businessObject.displayField2.put(fieldname, value);
					break;
					case 3:
						this.businessObject.displayField3.put(fieldname, value);
					break;
					case 4:
						this.businessObject.displayField4.put(fieldname, value);
					break;
				}
			}			
		}else {
			this.notificationCodeList.add("E1");
		}
	}
	
	/**
	 * Method finds a query result (matching source and query) in a list of query results
	 * 
	 * @param queryResultList: Set of query results
	 * @param resultRequest: Result to be found
	 * @return query result as JsonObject if found, otherwise returns null
	 */
	private JsonObject getQueryResult(ArrayList<QueryResult> queryResultList, QueryResult resultRequest) {
		JsonObject jsonObject = null;
		for(QueryResult qr : queryResultList) {
			if(qr.getSource().equals(resultRequest.getSource()) && qr.getQuery().equals(resultRequest.getQuery())) {
				jsonObject = qr.getJsonObject();
			}
		}
		return jsonObject;
	}

	public ArrayList<String> getNotificationCodeList() {
		return notificationCodeList;
	}
}
