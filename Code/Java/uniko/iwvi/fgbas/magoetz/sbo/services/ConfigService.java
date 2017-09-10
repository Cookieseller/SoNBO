package uniko.iwvi.fgbas.magoetz.sbo.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import uniko.iwvi.fgbas.magoetz.sbo.objects.Attribute;
import uniko.iwvi.fgbas.magoetz.sbo.objects.Datasource;
import uniko.iwvi.fgbas.magoetz.sbo.objects.NodeTypeCategory;
import uniko.iwvi.fgbas.magoetz.sbo.objects.NodeType;
import uniko.iwvi.fgbas.magoetz.sbo.objects.Query;
import uniko.iwvi.fgbas.magoetz.sbo.util.Utilities;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ConfigService implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private QueryService queryService = new QueryService();
	
	public NodeType getNodeType(String nodeTypeName) {
		
		// TODO: check if object type exists 
		JsonObject jsonNodeType = queryService.getJsonObject("nodeTypes", nodeTypeName, "nodeTypeJSON");
		// log json
		Utilities utilities = new Utilities();
		//utilities.printJson(jsonNodeType, "Parsed nodeType json");
		
		// get config information
		Gson gson = new Gson();
		NodeType nodeType = gson.fromJson(jsonNodeType, NodeType.class);
		
		// attributes
		ArrayList<JsonObject> jsonNodeAttributeList = queryService.getJsonObjects("attributes", nodeTypeName, "attributeJSON");
		
		for(JsonObject jsonNodeAttribute : jsonNodeAttributeList) {
			Attribute attribute = gson.fromJson(jsonNodeAttribute, Attribute.class);
			nodeType.addConfigurationNodeAttribute(attribute);
		}
		
		return nodeType;
	}

	public NodeTypeCategory getNodeTypeCategory(String nodeTypeCategory) {
		
		String jsonFromDb = queryService.getFieldValue("nodeTypeCategories", nodeTypeCategory, "nodeTypeCategoryJSON");
		Gson gson = new Gson();
		return gson.fromJson(jsonFromDb, NodeTypeCategory.class);
	}

	public NodeType getNodeTypeById(String id) {
		
		NodeType resultNodeType =  null;
		
		// get all configuration documents
		ArrayList<String> nodeTypes = queryService.getColumnValues("nodeTypes", 0);
		ArrayList<NodeType> configList = new ArrayList<NodeType>();
		for(String nodeType : nodeTypes) {
			configList.add(this.getNodeType(nodeType));
		}
		// search main datasource query for json containing object name
		for(NodeType config : configList) {
			
			// TODO: change to object individual source and query
			NodeTypeCategory nodeTypeCategory = this.getNodeTypeCategory(config.getNodeTypeCategory());
			String mainSource = nodeTypeCategory.getMainDatasource();
			String mainQuery = nodeTypeCategory.getMainQuery();
			System.out.println("mainSource: " + mainSource + " mainQuery: " + mainQuery);
			JsonObject datasourceJSON = queryService.getJsonObject("datasources", mainSource, "datasourceJSON");
			JsonObject queryJSON = queryService.getJsonObject("queries", mainQuery, "queryJSON");
			Gson gson = new Gson();
			Datasource datasourceObject = gson.fromJson(datasourceJSON, Datasource.class);
			Query queryObject = gson.fromJson(queryJSON, Query.class);
			JsonObject adjacentNodeJSON = queryService.executeQuery(datasourceObject, queryObject, id); 
			if(adjacentNodeJSON != null) {
				// get object type
				// TODO revise
				JsonElement jsonFirstQueryElement = adjacentNodeJSON.get(id);
				JsonObject jsonFirstQueryObject = jsonFirstQueryElement.getAsJsonObject();
				String nodeType = jsonFirstQueryObject.get("objectType").getAsString();
				resultNodeType = this.getNodeType(nodeType);
				break;
			}
		}
		
		return resultNodeType;
	}
}
