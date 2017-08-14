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
		
		BusinessObject businessObject = new BusinessObject();
	
		// 1. GET CONFIGURATION DOCUMENT FOR OBJECT TYPE
		
		ConfigurationObject configObject = configService.getConfigurationObject(objectName);
		
		// 2. RETRIEVE INFORMATION FOR BUSINESS OBJECT BASED ON CONFIGURATION
		
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
				// TODO change 7 to datasourceJSON
				JsonObject jsonDatasourceObject = queryService.getJsonObject("datasources", datasource, "7");
				// log json
				Utilities utilities = new Utilities();
				utilities.printJson(jsonDatasourceObject, "json datasource object");
				// get query				
				// TODO change 8 to queryJSON
				JsonObject jsonQueryObject = queryService.getJsonObject("queries", query, "8");
				// log json
				utilities.printJson(jsonQueryObject, "json query object");
				jsonQueryResultObject = queryService.executeQuery(jsonDatasourceObject, jsonQueryObject, objectId);
				queryResult.setJsonObject(jsonQueryResultObject);
				queryResultList.add(queryResult);
				// log json
				utilities.printJson(jsonQueryResultObject, "Parsed queryResult json");
			}
			
			// load attribute key and value into business object
			// TODO: use attribute name instead of fieldname
			JsonElement jsonFirstQueryResultElement = jsonQueryResultObject.get(objectId);
			JsonObject jsonFirstQueryResultObject = jsonFirstQueryResultElement.getAsJsonObject();
			String name = configObjAttr.getName();
			String fieldname = configObjAttr.getFieldname();
			String value = jsonFirstQueryResultObject.get(fieldname).getAsString();
			int displayfield = configObjAttr.getDisplayfield();
			businessObject.addKeyValuePair(name, value, displayfield);
		}	
		
		return businessObject;
	}
}
