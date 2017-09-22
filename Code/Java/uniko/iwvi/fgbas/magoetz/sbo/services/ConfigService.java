package uniko.iwvi.fgbas.magoetz.sbo.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import uniko.iwvi.fgbas.magoetz.sbo.objects.NodeTypeAttribute;
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
		ArrayList<JsonObject> jsonNodeAttributeList = queryService.getJsonObjects("nodeTypeAttributes", nodeTypeName, "attributeJSON");
		
		for(JsonObject jsonNodeAttribute : jsonNodeAttributeList) {
			NodeTypeAttribute attribute = gson.fromJson(jsonNodeAttribute, NodeTypeAttribute.class);
			nodeType.addConfigurationNodeAttribute(attribute);
		}
		
		return nodeType;
	}
	
	public List<String> getAllNodeTypeCategoryNames() {
		return queryService.getColumnValues("nodeTypeCategories", 0);
	} 

	public NodeTypeCategory getNodeTypeCategory(String nodeTypeCategory) {
		
		String jsonFromDb = queryService.getFieldValue("nodeTypeCategories", nodeTypeCategory, "nodeTypeCategoryJSON");
		Gson gson = new Gson();
		return gson.fromJson(jsonFromDb, NodeTypeCategory.class);
	}

	public NodeType getNodeTypeById(String id) {
		
		NodeType resultNodeType =  null;
		
		//get all node type definitions
		ArrayList<String> nodeTypes = queryService.getColumnValues("nodeTypes", 0);
		ArrayList<NodeType> nodeTypeList = new ArrayList<NodeType>();
		for(String nodeType : nodeTypes) {
			nodeTypeList.add(this.getNodeType(nodeType));
		}
		// search for node with id and return node type
		for(NodeType nodeType : nodeTypeList) {
			// get datasource and query of id attribute
			NodeTypeAttribute nodeTypeAttribute = nodeType.getNodeTypeIdAttribute();
			JsonObject jsonDatasourceObject = queryService.getJsonObject("datasources", nodeTypeAttribute.getDatasource(), "datasourceJSON");				
			JsonObject jsonQueryObject = queryService.getJsonObject("queries", nodeTypeAttribute.getQuery(), "queryJSON");
			//replace attributes in query string with variable values
			Gson gson = new Gson();
			Datasource datasourceObject = gson.fromJson(jsonDatasourceObject, Datasource.class);
			Query queryObject = gson.fromJson(jsonQueryObject, Query.class);
			JsonObject json = queryService.executeQuery(datasourceObject, queryObject, id);
			if(json != null) {
				return nodeType;
			}
		}
		return resultNodeType;
	}
}
