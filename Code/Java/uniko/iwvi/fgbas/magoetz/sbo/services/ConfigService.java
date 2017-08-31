package uniko.iwvi.fgbas.magoetz.sbo.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import uniko.iwvi.fgbas.magoetz.sbo.objects.ClassObject;
import uniko.iwvi.fgbas.magoetz.sbo.objects.ConfigurationObject;
import uniko.iwvi.fgbas.magoetz.sbo.util.Utilities;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ConfigService implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private QueryService queryService = new QueryService();
	
	public ConfigurationObject getConfigurationObject(String objectName) {
		
		// TODO: check if object type exists 
		JsonObject jsonConfigObject = queryService.getJsonObject("objects", objectName, "objectJSON");
		// log json
		Utilities utilities = new Utilities();
		utilities.printJson(jsonConfigObject, "Parsed object object json");
		
		// get config information
		ConfigurationObject configObject = new ConfigurationObject();
		// object name
		configObject.setObjectName(objectName);
		JsonElement jsonFirstConfigElement = jsonConfigObject.get(objectName);
		JsonObject jsonFirstConfigObject = jsonFirstConfigElement.getAsJsonObject();
		//object title
		String objectTitle = jsonFirstConfigObject.get("objectTitle").getAsString();
		configObject.setObjectTitle(objectTitle);
		// object class
		String objectClass = jsonFirstConfigObject.get("objectClass").getAsString();
		configObject.setObjectClass(objectClass);
		// peers TODO: shift to class (service?)
		/*String peers = queryService.getFieldValue("classes", objectClass, "classPeers");
		List<String> peerList = Arrays.asList(peers.split(","));
		configObject.setPeers(peerList);
		*/
		// attributes
		ArrayList<JsonObject> jsonAttributeObjectList = queryService.getJsonObjects("attributes", objectName, "attributeJSON");
		
		for(JsonObject jsonAttributeObject : jsonAttributeObjectList) {
			
			utilities.printJson(jsonAttributeObject, "Parsed attribute json");
			
			JsonElement jsonFirstAttrElement = jsonAttributeObject.get("attribute");
			JsonObject jsonFirstAttrObject = jsonFirstAttrElement.getAsJsonObject();
			
			String name = jsonFirstAttrObject.get("name").getAsString();
			String datasource = jsonFirstAttrObject.get("datasource").getAsString();
			String query = jsonFirstAttrObject.get("query").getAsString();
			String fieldname = jsonFirstAttrObject.get("fieldname").getAsString();
			int displayfield = jsonFirstAttrObject.get("displayfield").getAsInt();
			boolean preview = jsonFirstAttrObject.get("preview").getAsBoolean();
			
			configObject.addConfigurationObjectAttribute(name, datasource, query, fieldname, displayfield, preview);
		}
		
		return configObject;
	}

	public ClassObject getClassObject(String classObjectName) {
		
		String jsonFromDb = queryService.getFieldValue("classes", classObjectName, "classJSON");
		Gson gson = new Gson();
		return gson.fromJson(jsonFromDb, ClassObject.class);
	}

	public ConfigurationObject getConfigurationObjectByObjectId(String objectId) {
		
		ConfigurationObject resultConfigObject =  null;
		
		// get all configuration documents
		ArrayList<String> objectNames = queryService.getColumnValues("objects", 0);
		
		for(String objectName : objectNames) {
			System.out.println("Object config found: " + objectName);
		}
		
		ArrayList<ConfigurationObject> configObjectList = new ArrayList<ConfigurationObject>();
		for(String objectName : objectNames) {
			configObjectList.add(this.getConfigurationObject(objectName));
		}
		// search main datasource query for json containing object name
		
		for(ConfigurationObject configObject : configObjectList) {
			
			// TODO: change to object individual source and query
			ClassObject classObject = this.getClassObject(configObject.getObjectClass());
			String mainSource = classObject.getClassMainDatasource();
			String mainQuery = classObject.getClassMainQuery();
			System.out.println("mainSource: " + mainSource + " mainQuery: " + mainQuery);
			JsonObject datasourceJSON = queryService.getJsonObject("datasources", mainSource, "datasourceJSON");
			JsonObject queryJSON = queryService.getJsonObject("queries", mainQuery, "queryJSON");
			JsonObject peerObjectJSON = queryService.executeQuery(datasourceJSON, queryJSON, objectId); 
			if(peerObjectJSON != null) {
				// get object type
				JsonElement jsonFirstQueryElement = peerObjectJSON.get(objectId);
				JsonObject jsonFirstQueryObject = jsonFirstQueryElement.getAsJsonObject();
				String objectType = jsonFirstQueryObject.get("objectType").getAsString();
				resultConfigObject = this.getConfigurationObject(objectType);
				break;
			}
		}
		
		return resultConfigObject;
	}
}
