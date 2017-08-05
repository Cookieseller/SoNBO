package uniko.iwvi.fgbas.magoetz.sbo.services;

import java.io.Serializable;
import java.util.Set;
import uniko.iwvi.fgbas.magoetz.sbo.objects.ConfigurationObject;
import uniko.iwvi.fgbas.magoetz.sbo.util.Utilities;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ConfigService implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private QueryService queryService = new QueryService();
	
	public ConfigurationObject getConfigurationObject(String objectType) {
		
		// TODO: check if object type exists 
		
		JsonObject jsonConfigObject = queryService.getJsonObject("config", objectType, "1");
		// log json
		Utilities utilities = new Utilities();
		utilities.printJson(jsonConfigObject, "Parsed config json");
		
		// get config information
		ConfigurationObject configObject = new ConfigurationObject();
		// object type
		JsonElement jsonFirstConfigElement = jsonConfigObject.get(objectType);
		JsonObject jsonFirstConfigObject = jsonFirstConfigElement.getAsJsonObject();			
		String objectClass = jsonFirstConfigObject.get("objectClass").toString();
		configObject.setObjectClass(objectType);
		// peers
		JsonElement firstLevelConfigElementPeers = jsonFirstConfigObject.get("peers");
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
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
		
		return configObject;
	}
}
