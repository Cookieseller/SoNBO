package uniko.iwvi.fgbas.magoetz.sbo.services;

import java.io.Serializable;
import java.util.ArrayList;
import uniko.iwvi.fgbas.magoetz.sbo.objects.BusinessObject;
import uniko.iwvi.fgbas.magoetz.sbo.objects.ConfigurationObject;
import uniko.iwvi.fgbas.magoetz.sbo.objects.ConfigurationObject.ConfigurationObjectAttribute;
import uniko.iwvi.fgbas.magoetz.sbo.util.QueryResult;
import uniko.iwvi.fgbas.magoetz.sbo.util.Utilities;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ObjectService implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private ConfigService configService = new ConfigService();
	
	private QueryService queryService = new QueryService();

	public BusinessObject getBusinessObject(String objectId, String objectName) {
		
		// 1. CREATE NEW BUSINESS OBJECT
		BusinessObject businessObject = new BusinessObject();
	
		// set object id and name
		businessObject.setObjectId(objectId);
		businessObject.setObjectName(objectName);
		
		// 2. GET CONFIGURATION DOCUMENT FOR OBJECT TYPE
		
		ConfigurationObject configObject = configService.getConfigurationObject(objectName);
		
		// 3. RETRIEVE ATTRIBUTES OF BUSINESS OBJECT
		
		// set object peers
		businessObject.setPeers(configObject.getPeers());
		
		// cache result to prevent redundant queries
		ArrayList<QueryResult> queryResultList = new ArrayList<QueryResult>();
		
		// get value for each object attribute
		for(ConfigurationObjectAttribute configObjAttr : configObject.getConfigurationObjectAttributes()) {

			String datasource = configObjAttr.getDatasource();
			String query = configObjAttr.getQuery();
			System.out.println("Query: " + query);
			QueryResult queryResult = new QueryResult(datasource, query);
			
			// check if query result is already cached
			JsonObject jsonQueryResultObject = queryService.getQueryResult(queryResultList, queryResult);
			
			if(jsonQueryResultObject == null) {
				// get datasource configuration			
				JsonObject jsonDatasourceObject = queryService.getJsonObject("datasources", datasource, "datasourceJSON");
				// log json
				Utilities utilities = new Utilities();
				utilities.printJson(jsonDatasourceObject, "json datasource object");
				// get query				
				JsonObject jsonQueryObject = queryService.getJsonObject("queries", query, "queryJSON");
				// log json
				utilities.printJson(jsonQueryObject, "json query object");
				jsonQueryResultObject = queryService.executeQuery(jsonDatasourceObject, jsonQueryObject, objectId);
				queryResult.setJsonObject(jsonQueryResultObject);
				queryResultList.add(queryResult);
				// log json
				utilities.printJson(jsonQueryResultObject, "Parsed queryResult json");
			}
			
			// load attribute key and value into business object
			JsonElement jsonFirstQueryResultElement = jsonQueryResultObject.get(objectId);
			JsonObject jsonFirstQueryResultObject = jsonFirstQueryResultElement.getAsJsonObject();
			String name = configObjAttr.getName();
			String fieldname = configObjAttr.getFieldname();
			String value = jsonFirstQueryResultObject.get(fieldname).getAsString();
			int displayfield = configObjAttr.getDisplayfield();
			businessObject.addKeyValuePair(name, value, displayfield);
			//set business object title
			String titleAttribute = configObject.getObjectTitle();
			if(titleAttribute.equals(name)) {
				businessObject.setObjectTitle(value);
			}
		}	
		
		// 4. RETRIEVE PEER OBJECTS
		
		
		
		return businessObject;
	}
}
