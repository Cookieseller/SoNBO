package uniko.iwvi.fgbas.magoetz.sbo.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import uniko.iwvi.fgbas.magoetz.sbo.objects.NodeTypeCategory;
import uniko.iwvi.fgbas.magoetz.sbo.objects.Configuration;
import uniko.iwvi.fgbas.magoetz.sbo.objects.Query;
import uniko.iwvi.fgbas.magoetz.sbo.util.Utilities;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ConfigService implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private QueryService queryService = new QueryService();
	
	public Configuration getConfiguration(String nodeType) {
		
		// TODO: check if object type exists 
		JsonObject jsonConfigObject = queryService.getJsonObject("objects", nodeType, "objectJSON");
		// log json
		Utilities utilities = new Utilities();
		utilities.printJson(jsonConfigObject, "Parsed object object json");
		
		// get config information
		Configuration configuration = new Configuration();
		// object name
		configuration.setNodeTypeName(nodeType);
		JsonElement jsonFirstConfigElement = jsonConfigObject.get(nodeType);
		JsonObject jsonFirstConfigObject = jsonFirstConfigElement.getAsJsonObject();
		//object title
		String objectTitle = jsonFirstConfigObject.get("objectTitle").getAsString();
		configuration.setNodeTypeTitle(objectTitle);
		// object class
		String objectClass = jsonFirstConfigObject.get("objectClass").getAsString();
		configuration.setNodeTypeCategory(objectClass);
		// peers TODO: shift to class (service?)
		/*String peers = queryService.getFieldValue("classes", objectClass, "classPeers");
		List<String> peerList = Arrays.asList(peers.split(","));
		configObject.setPeers(peerList);
		*/
		// attributes
		ArrayList<JsonObject> jsonNodeAttributeList = queryService.getJsonObjects("attributes", nodeType, "attributeJSON");
		
		for(JsonObject jsonNodeAttribute : jsonNodeAttributeList) {
			
			utilities.printJson(jsonNodeAttribute, "Parsed attribute json");
			
			JsonElement jsonFirstAttrElement = jsonNodeAttribute.get("attribute");
			JsonObject jsonFirstAttrObject = jsonFirstAttrElement.getAsJsonObject();
			
			String name = jsonFirstAttrObject.get("name").getAsString();
			String datasource = jsonFirstAttrObject.get("datasource").getAsString();
			String query = jsonFirstAttrObject.get("query").getAsString();
			String fieldname = jsonFirstAttrObject.get("fieldname").getAsString();
			int displayfield = jsonFirstAttrObject.get("displayfield").getAsInt();
			boolean preview = jsonFirstAttrObject.get("preview").getAsBoolean();
			
			configuration.addConfigurationNodeAttribute(name, datasource, query, fieldname, displayfield, preview);
		}
		
		return configuration;
	}

	public NodeTypeCategory getNodeTypeCategory(String nodeTypeCategory) {
		
		String jsonFromDb = queryService.getFieldValue("classes", nodeTypeCategory, "classJSON");
		Gson gson = new Gson();
		return gson.fromJson(jsonFromDb, NodeTypeCategory.class);
	}

	public Configuration getConfigurationById(String id) {
		
		Configuration resultConfigObject =  null;
		
		// get all configuration documents
		ArrayList<String> nodeTypes = queryService.getColumnValues("objects", 0);
		ArrayList<Configuration> configList = new ArrayList<Configuration>();
		for(String nodeType : nodeTypes) {
			configList.add(this.getConfiguration(nodeType));
		}
		// search main datasource query for json containing object name
		for(Configuration config : configList) {
			
			// TODO: change to object individual source and query
			NodeTypeCategory nodeTypeCategory = this.getNodeTypeCategory(config.getNodeTypeCategory());
			String mainSource = nodeTypeCategory.getMainDatasource();
			String mainQuery = nodeTypeCategory.getMainQuery();
			System.out.println("mainSource: " + mainSource + " mainQuery: " + mainQuery);
			JsonObject datasourceJSON = queryService.getJsonObject("datasources", mainSource, "datasourceJSON");
			JsonObject queryJSON = queryService.getJsonObject("queries", mainQuery, "queryJSON");
			Gson gson = new Gson();
			Query queryObject = gson.fromJson(queryJSON, Query.class);
			JsonObject adjacentNodeJSON = queryService.executeQuery(datasourceJSON, queryObject, id); 
			if(adjacentNodeJSON != null) {
				// get object type
				JsonElement jsonFirstQueryElement = adjacentNodeJSON.get(id);
				JsonObject jsonFirstQueryObject = jsonFirstQueryElement.getAsJsonObject();
				String nodeType = jsonFirstQueryObject.get("objectType").getAsString();
				resultConfigObject = this.getConfiguration(nodeType);
				break;
			}
		}
		
		return resultConfigObject;
	}
}
