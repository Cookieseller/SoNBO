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

	public BusinessObject getBusinessObject(String objectId, String objectType) {
		
		BusinessObject businessObject = new BusinessObject();
	
		// 1. GET CONFIGURATION DOCUMENT FOR OBJECT TYPE
		
		ConfigurationObject configObject = configService.getConfigurationObject(objectType);
		
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
				// get data source configuration			
				JsonObject jsonDatasourceObject = queryService.getJsonObject("datasources", datasource, "datasource");
				// log json
				Utilities utilities = new Utilities();
				utilities.printJson(jsonDatasourceObject, "json datasource object");
				// get query				
				JsonObject jsonQueryObject = queryService.getJsonObject("queries", query, "query");
				// log json
				utilities.printJson(jsonQueryResultObject, "json query object");
				jsonQueryResultObject = queryService.executeQuery(jsonDatasourceObject, jsonQueryObject);
				queryResult.setJsonObject(jsonQueryResultObject);
				queryResultList.add(queryResult);
				// log json
				utilities.printJson(jsonQueryResultObject, "Parsed queryResult json");
			}
			
			// load attribute key and value into business object
			String fieldname = configObjAttr.getFieldname();
			JsonElement jsonFirstQueryResultElement = jsonQueryResultObject.get(objectId);
			JsonObject jsonFirstQueryResultObject = jsonFirstQueryResultElement.getAsJsonObject();			
			String value = jsonFirstQueryResultObject.get(fieldname).getAsString();
			int displayfield = configObjAttr.getDisplayfield();
			businessObject.addKeyValuePair(fieldname, value, displayfield);
		}	
		
		return businessObject;
	}
}
