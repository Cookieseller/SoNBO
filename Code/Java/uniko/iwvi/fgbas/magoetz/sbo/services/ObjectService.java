package uniko.iwvi.fgbas.magoetz.sbo.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.NotesException;

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
		
		// set object class
		businessObject.setObjectClass(configObject.getObjectClass());
		
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
		
		return businessObject;
	}

	public List<BusinessObject> getPeerObjects(BusinessObject businessObject, String objectPeers, String objectRelationship) {
		
		System.out.println("Peer objects");
		System.out.println("============");
		// TODO: Only implemented for object person (main source / query)
		
		List<BusinessObject> peerObjectList = new ArrayList<BusinessObject>();
		// get peer query for object type
			// TODO
		// execute peer query and retrieve peer object ids
		
		// TODO: should be non static
		String organization = businessObject.getAttributeValue("organization");
		String department = businessObject.getAttributeValue("department");
		String key = department;
		String queryString = "\"Universitšt Koblenz-Landau\" AND FGBAS";
		DocumentCollection resultCollection = queryService.ftSearch("test.nsf", queryString);
		ArrayList<String> peerObjectIds = new ArrayList<String>();
		try {
			if(resultCollection != null) {
				for(int i=1; i<=resultCollection.getCount(); i++) {
					Document doc = resultCollection.getNthDocument(i);
					//System.out.println(doc.generateXML());
					String email = doc.getItemValueString("email");
					// add to peer object id list if it is not the object id itself
					if(!email.equals(businessObject.getObjectId())) {
						peerObjectIds.add(email);
					}
				}
			}else {
				System.out.println("Result of query " + queryString + " is null.");
			}
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// get main object source from config
		String mainSource = queryService.getFieldValue("classes", businessObject.getObjectClass(), "classMainDatasource");
		String mainQuery = queryService.getFieldValue("classes", businessObject.getObjectClass(), "classMainQuery");
		System.out.println("Main source: " + mainSource);
		System.out.println("Main query: " + mainQuery);
		JsonObject datasourceJSON = queryService.getJsonObject("datasources", mainSource, "datasourceJSON");
		JsonObject queryJSON = queryService.getJsonObject("queries", mainQuery, "queryJSON");
		System.out.println(datasourceJSON.toString());
		System.out.println(queryJSON.toString());
		// get peer objects from main object source
		ArrayList<JsonObject> peerObjectJsonList = new ArrayList<JsonObject>();
		for(String peerObjectId : peerObjectIds) {
			JsonObject peerObjectJSON = queryService.executeQuery(datasourceJSON, queryJSON, peerObjectId); 
			if(peerObjectJSON != null) {
				peerObjectJsonList.add(peerObjectJSON);
				BusinessObject peerObject = new BusinessObject();
				peerObject.setObjectId(peerObjectId);
				// TODO set further attributes such as class?
				JsonElement jsonFirstQueryElement = peerObjectJSON.get(peerObjectId);
				JsonObject jsonFirstQueryObject = jsonFirstQueryElement.getAsJsonObject();
				// TODO get preview attributes from config object?
				String fullname = jsonFirstQueryObject.get("fullName").getAsString();
				// TODO: Object type dependent
				peerObject.setObjectTitle(fullname);
				peerObject.addKeyValuePair("fullname", fullname, 1);
				String email = jsonFirstQueryObject.get("email").getAsString();
				peerObject.addKeyValuePair("email", email, 1);
				String employeeId = jsonFirstQueryObject.get("employeeId").getAsString();
				peerObject.addKeyValuePair("employeeId", employeeId, 1);
				peerObjectList.add(peerObject);
			}
		}
		
		return peerObjectList;
	}
}
