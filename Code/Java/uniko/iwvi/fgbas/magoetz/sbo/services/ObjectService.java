package uniko.iwvi.fgbas.magoetz.sbo.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.NotesException;
import uniko.iwvi.fgbas.magoetz.sbo.objects.BusinessObject;
import uniko.iwvi.fgbas.magoetz.sbo.objects.ClassObject;
import uniko.iwvi.fgbas.magoetz.sbo.objects.ConfigurationObject;
import uniko.iwvi.fgbas.magoetz.sbo.objects.QueryObject;
import uniko.iwvi.fgbas.magoetz.sbo.objects.RelationshipQueryObject;
import uniko.iwvi.fgbas.magoetz.sbo.objects.ConfigurationObject.ConfigurationObjectAttribute;
import uniko.iwvi.fgbas.magoetz.sbo.util.QueryResult;
import uniko.iwvi.fgbas.magoetz.sbo.util.Utilities;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ObjectService implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private ConfigService configService = new ConfigService();
	
	private QueryService queryService = new QueryService();

	public BusinessObject getBusinessObject(String objectId, boolean objectPreview) {
		
		// 1. CREATE NEW BUSINESS OBJECT
		BusinessObject businessObject = new BusinessObject();
	
		// set object id and name
		businessObject.setObjectId(objectId);
		//businessObject.setObjectName(objectName);
		
		// 2. GET CONFIGURATION DOCUMENT FOR OBJECT TYPE

		//ConfigurationObject configObject = configService.getConfigurationObject(objectName);
		ConfigurationObject configObject = configService.getConfigurationObjectByObjectId(objectId);
		businessObject.setObjectName(configObject.getObjectName());
		
		// 3. RETRIEVE ATTRIBUTES OF BUSINESS OBJECT
		
		// set object class
		businessObject.setObjectClass(configObject.getObjectClass());
		
		// set object image
		// TODO: set individual image if available
		ClassObject classObject = configService.getClassObject(businessObject.getObjectClass());
		businessObject.setObjectImage(classObject.getClassDefaultImage());
		
		// get business object attributes
		ArrayList<QueryResult> queryResultList = getObjectAttributes(configObject, objectId, objectPreview);
		
		// load attribute key and value into business object
		businessObject = loadAttributes(businessObject, configObject, queryResultList, objectPreview);
	
		return businessObject;
	}
	
	/*
	 * returns list of business object attributes
	 */
	private ArrayList<QueryResult> getObjectAttributes(ConfigurationObject configObject, String objectId, boolean objectPreview) {
		
		// cache result to prevent redundant queries
		ArrayList<QueryResult> queryResultList = new ArrayList<QueryResult>();
		
		// if it is an object preview only get preview attributes otherwise all defined
		ArrayList<ConfigurationObjectAttribute> configurationObjectAttributes =  new ArrayList<ConfigurationObjectAttribute>();
		if(objectPreview){
			configurationObjectAttributes =  configObject.getPreviewConfigurationObjectAttributes();
		}else {
			configurationObjectAttributes =  configObject.getConfigurationObjectAttributes();
		}
		
		// get value for each object attribute
		for(ConfigurationObjectAttribute configObjAttr : configurationObjectAttributes) {

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
				Gson gson = new Gson();
				QueryObject queryObject = gson.fromJson(jsonQueryObject, QueryObject.class);
				// log json
				utilities.printJson(jsonQueryObject, "json query object");
				jsonQueryResultObject = queryService.executeQuery(jsonDatasourceObject, queryObject, objectId);
				queryResult.setJsonObject(jsonQueryResultObject);
				queryResultList.add(queryResult);
				// log json
				//utilities.printJson(jsonQueryResultObject, "Parsed queryResult json");
			}
		}	
		
		return queryResultList;
	}
	
	private BusinessObject loadAttributes(BusinessObject businessObject, ConfigurationObject configObject, ArrayList<QueryResult> queryResultList, boolean objectPreview) {

		// TODO
		// if it is an object preview only process preview attributes otherwise all defined
		ArrayList<ConfigurationObjectAttribute> configurationObjectAttributes =  new ArrayList<ConfigurationObjectAttribute>();
		if(objectPreview){
			configurationObjectAttributes =  configObject.getPreviewConfigurationObjectAttributes();
		}else {
			configurationObjectAttributes =  configObject.getConfigurationObjectAttributes();
		}
		
		for(ConfigurationObjectAttribute configObjAttr : configObject.getConfigurationObjectAttributes()) {
			// get name and fieldname of attribute
			String name = configObjAttr.getName();
			String fieldname = configObjAttr.getFieldname();
			// get query result for config object attribute
			String datasource = configObjAttr.getDatasource();
			String query = configObjAttr.getQuery();
			QueryResult queryResult = new QueryResult(datasource, query);
			JsonObject jsonQueryResultObject = queryService.getQueryResult(queryResultList, queryResult);
			// extract value from query result
			JsonElement jsonFirstQueryResultElement = jsonQueryResultObject.get(businessObject.getObjectId());
			JsonObject jsonFirstQueryResultObject = jsonFirstQueryResultElement.getAsJsonObject();
			String value = jsonFirstQueryResultObject.get(fieldname).getAsString();
			int displayfield = configObjAttr.getDisplayfield();
			
			businessObject.addKeyValuePair(name, value, displayfield);
			//set business object title if attribute is configured as title
			String titleAttribute = configObject.getObjectTitle();
			if(titleAttribute.equals(name)) {
				businessObject.setObjectTitle(value);
			}
		}
		
		return businessObject;
	}

	public List<BusinessObject> getPeerObjects(BusinessObject businessObject, String objectPeers) {
		
		// TODO: refactor method (too big!)
		
		System.out.println("Peer objects");
		System.out.println("============");
		// TODO: Only implemented for object person (main source / query)
		// TODO: Define queries person -> teaching, teaching -> person.... // only one def needed because bidirectional -> Class
		
		List<BusinessObject> peerObjectList = new ArrayList<BusinessObject>();
		// get peer query for object type
		// execute peer query and retrieve peer object ids
		String queryString = "";
		
		// determine peer query by source and target object type
		String sourceObjectName = businessObject.getObjectName();
				System.out.println("sourceObjectName: " + sourceObjectName);
		String sourceObjectId = businessObject.getObjectId();
				System.out.println("sourceObjectId: " + sourceObjectId);
	
		// Get children of class objectPeers ( = target object names) e.g. person -> employee, student etc.
		ArrayList<String> peerObjectNames = getChildrenNodes(objectPeers);
		
		// get peer queries for source object <-> target objects
		// build query string for getting object relationships
		String peerObjectRelationshipQuery = "";
		for(int i=0; i<peerObjectNames.size(); i++) {
			System.out.println("Result string peer object name: " + peerObjectNames.get(i)); // should be employee in this case
			peerObjectRelationshipQuery += "[relationshipSourceObject] = " + sourceObjectName + " AND [relationshipTargetObject] = " + peerObjectNames.get(i);
			if(i < peerObjectNames.size() - 1) {
				peerObjectRelationshipQuery += " OR ";
			}
		}
		System.out.println("peerObjectNamesQueryString: " + peerObjectRelationshipQuery);
		DocumentCollection resultCollectionPeerRelationships = queryService.ftSearchView("", peerObjectRelationshipQuery, "relationships");
		
		//execute query for getting object relationships
		ArrayList<RelationshipQueryObject> peerQueryList = new ArrayList<RelationshipQueryObject>();
		try {
			if(resultCollectionPeerRelationships != null) {
				Gson gson = new Gson();
				for(int i=1; i<=resultCollectionPeerRelationships.getCount(); i++) {
					Document doc = resultCollectionPeerRelationships.getNthDocument(i);
					String peerRelationshipName = doc.getItemValueString("peerRelationshipName");
					String peerQueryJSON = queryService.getFieldValue("relationships", peerRelationshipName, "relationshipQueryJSON");					
					// retrieve query and database
					RelationshipQueryObject peerQuery = gson.fromJson(peerQueryJSON, RelationshipQueryObject.class);
					peerQueryList.add(peerQuery);
					// test print out
					System.out.println("peerQuery: " + peerQuery.getQuery());
				}
			}else {
				System.out.println("Result of query " + peerObjectRelationshipQuery + " is null.");
			}
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// execute queries for getting peer object IDs
		ArrayList<String> peerObjectIDs = new ArrayList<String>();
		for(RelationshipQueryObject peerQuery : peerQueryList) {
		
			// get datasource and query for peer query
			JsonObject jsonDatasourceObject = queryService.getJsonObject("datasources", peerQuery.getDatasource(), "datasourceJSON");				
			JsonObject jsonQueryObject = queryService.getJsonObject("queries", peerQuery.getQuery(), "queryJSON");
			//replace attributes in query string with variable values
			Gson gson = new Gson();
			QueryObject queryObject = gson.fromJson(jsonQueryObject, QueryObject.class);
			String string = queryObject.getString();
			Utilities utilities = new Utilities();
			// create map with replacements
			ArrayList<String> replaceAttributesList = utilities.getTokens(string);
			Map<String, String> replaceAttributesMap = new HashMap<String, String>();
			for(String replaceAttributeKey : replaceAttributesList) {
				// get attribute value from business object
				String replaceAttributeValue = businessObject.getAttributeValue(replaceAttributeKey);
				replaceAttributesMap.put(replaceAttributeKey, replaceAttributeValue);
			}
			// replace [key] in string with variable values
			string = utilities.replaceTokens(string, replaceAttributesMap);
			System.out.println("QueryString after replacements: " + string);
			// replace query string
			queryObject.setString(string);
			
			String targetObjectName = peerQuery.getTargetObject();
			String targetObjectIdKey = queryService.getFieldValue("objects", targetObjectName, "objectId");
			
				System.out.println("targetObjectIdKey: " + targetObjectIdKey);
				System.out.println("targetObjectIdKey: " + sourceObjectId);
			
			// TODO: 2. change paramter to query object, 3. in method extract keys as targetObjectIDs
			ArrayList<String> resultPeerObjectIDs = this.getPeerObjectIDs(jsonDatasourceObject, queryObject, sourceObjectId);
			// test
			for(String objectId : resultPeerObjectIDs) {
				System.out.println("PeerObjectID: " + objectId);
			}
			peerObjectIDs.addAll(resultPeerObjectIDs);
		}
		
		for(String peerObjectId : peerObjectIDs) {
			peerObjectList.add(getBusinessObject(peerObjectId, true));
		}
		return peerObjectList;
	}
	
	private ArrayList<String> getChildrenNodes(String objectPeers) {
		ArrayList<String> peerObjectNames = new ArrayList<String>();
		String queryStringObjectNames = "FIELD objectClass = " + objectPeers;
		DocumentCollection resultCollectionObjectNames = queryService.ftSearchView("", queryStringObjectNames, "objects");
		try {
			if(resultCollectionObjectNames != null) {
				for(int i=1; i<=resultCollectionObjectNames.getCount(); i++) {
					Document doc = resultCollectionObjectNames.getNthDocument(i);
					String objectName = doc.getItemValueString("objectName");
							System.out.println("childObjectName: " + objectName);
					peerObjectNames.add(objectName);
				}
			}else {
				System.out.println("Result of query " + queryStringObjectNames + " is null.");
			}
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return peerObjectNames;
	}
	
	private ArrayList<String> getPeerObjectIDs(JsonObject jsonDatasourceObject, QueryObject queryObject, String sourceObjectId) {
		
		DocumentCollection resultCollectionPeerObjectIDs = queryService.executeQueryFTSearch(jsonDatasourceObject, queryObject); 
		// get targetObjectIdKeys
		List<String> targetObjectIdKeys = queryObject.getKey();
		// extract object IDs from resultCollection
		ArrayList<String> peerObjectIds = new ArrayList<String>();
		if(resultCollectionPeerObjectIDs != null) {
			try {
				for(int i=1; i<=resultCollectionPeerObjectIDs.getCount(); i++) {
					Document doc = resultCollectionPeerObjectIDs.getNthDocument(i);
					System.out.println(doc.generateXML());
					for(String targetObjectIdKey : targetObjectIdKeys) {
						String objectId = doc.getItemValueString(targetObjectIdKey);
						// add to peer object id list if it is not the object id itself
						if(!objectId.equals(sourceObjectId)) {
							peerObjectIds.add(objectId);
						}
					}
				}
			} catch (NotesException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			System.out.println("Result of query to get peerObjectIds of is null.");
		}
		return peerObjectIds;
	}

	public List<BusinessObject> getFilteredBusinessObjects(BusinessObject businessObject, String peerNodeType, String objectPeers) {
		
		List<BusinessObject> filteredPeerObjectList = new ArrayList<BusinessObject>();
		if(peerNodeType != null && !peerNodeType.equals("all")) {
			for(BusinessObject peerObject : businessObject.getPeerObjectList()) {	
				if(peerObject.getObjectName().equals(peerNodeType)) {
					filteredPeerObjectList.add(peerObject);
				}
			}
		}else {
			filteredPeerObjectList = businessObject.getPeerObjectList();
		}
		return filteredPeerObjectList;
	}

	public List<String> getObjectRelationships(String objectPeers) {	
		// get class object -> relationship attribute
		ClassObject classObject = configService.getClassObject(objectPeers);
		String classRelationship = classObject.getClassRelationships();
		// get all children node types
		ArrayList<String> peerObjectNames = getChildrenNodes(objectPeers);
		
		return peerObjectNames;
	}
}
