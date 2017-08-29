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
			
			configObject.addConfigurationObjectAttribute(name, datasource, query, fieldname, displayfield);
		}
		
		return configObject;
	}

	public ClassObject getClassObject(String classObjectName) {
		
		String jsonFromDb = queryService.getFieldValue("classes", classObjectName, "classJSON");
		Gson gson = new Gson();
		return gson.fromJson(jsonFromDb, ClassObject.class);
	}
}
