package uniko.iwvi.fgbas.magoetz.sbo.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.NotesException;
import uniko.iwvi.fgbas.magoetz.sbo.objects.Datasource;
import uniko.iwvi.fgbas.magoetz.sbo.objects.NodeType;
import uniko.iwvi.fgbas.magoetz.sbo.objects.NodeTypeAttribute;
import uniko.iwvi.fgbas.magoetz.sbo.objects.NodeTypeCategory;
import uniko.iwvi.fgbas.magoetz.sbo.objects.Query;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class ConfigService implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private QueryService queryService = new QueryService();
	
	public NodeType getNodeType(String nodeTypeName) {
		
		// TODO: check if object type exists 
		JsonObject jsonNodeType = queryService.getJsonObject("nodeTypes", nodeTypeName, "nodeTypeJSON");
		
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
	
	public List<String> getAllNodeTypeCategoryNames(Locale locale) {
		if(locale.getLanguage().equals("de")) {
			return queryService.getColumnValues("nodeTypeCategories", 1);
		}else {
			return queryService.getColumnValues("nodeTypeCategories", 0);
		}
	} 
	
	public ArrayList<String> getAllNodeTypeNamesByCategory(String nodeTypeCategoryName) {
		ArrayList<String> adjacentNodeTypes = new ArrayList<String>();
		String queryStringNodeTypes = "FIELD nodeTypeCategory = " + nodeTypeCategoryName;
		DocumentCollection resultCollectionNodeTypes = queryService.ftSearchView("", queryStringNodeTypes, "nodeTypes");
		try {
			if(resultCollectionNodeTypes != null) {
				for(int i=1; i<=resultCollectionNodeTypes.getCount(); i++) {
					Document doc = resultCollectionNodeTypes.getNthDocument(i);
					String nodeType = doc.getItemValueString("nodeTypeName");
					adjacentNodeTypes.add(nodeType);
				}
			}else {
				System.out.println("Result of query " + queryStringNodeTypes + " is null.");
			}
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return adjacentNodeTypes;
	}

	public NodeTypeCategory getNodeTypeCategory(String nodeTypeCategory) {
		
		String jsonFromDb = queryService.getFieldValue("", "", "nodeTypeCategories", nodeTypeCategory, "nodeTypeCategoryJSON");
		Gson gson = new Gson();
		return gson.fromJson(jsonFromDb, NodeTypeCategory.class);
	}

	public NodeType getNodeTypeById(String id) {
		
		NodeType resultNodeType =  null;
		
		// lookup if node type was determined before
		String nodeTypeName = queryService.getFieldValue("", "", "(nodeTypeIdMapping)", id, "nodeTypeMappingType");
		if(nodeTypeName == null) {
			// if node type name was not cached yet try to determine it
			resultNodeType = this.determineNodeTypeById(id);
		}else {
			resultNodeType = this.getNodeType(nodeTypeName);
			System.out.println("Mapping table result - Id " + id + " is of type " + nodeTypeName);
		}
		
		return resultNodeType;
	}
	
	/*
	 * Function determines node type by searching for a matching db entry with id and (option) filter attribute
	 */
	private NodeType determineNodeTypeById(String id) {
		
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
			if(nodeTypeAttribute != null) {				
				//replace attributes in query string with variable values
				Datasource datasourceObject = queryService.getDatasourceObject(nodeTypeAttribute.getDatasource());
				Query queryObject = queryService.getQueryObject(nodeTypeAttribute.getQuery());
				// set fieldname of id attribute as key to be retrieved (FTSearch) 
				List<String> idList = new ArrayList<String>();
				idList.add(nodeTypeAttribute.getFieldname());
				queryObject.setKey(idList);
				JsonObject json = queryService.executeQuery(datasourceObject, queryObject, id);
				if(json != null) {
					//System.out.println("Sufficient data found for nodeType: " + nodeType.getNodeTypeName());
					// check if required key and value for node type exist
					String key = queryService.getFieldValue("", "", "nodeTypes", nodeType.getNodeTypeName(), "nodeTypeKey");
					String value = queryService.getFieldValue("", "", "nodeTypes", nodeType.getNodeTypeName(), "nodeTypeValue");
					if(key != null && value != null && !value.equals("") && !key.equals("")) {
						//check if json contains required key value pair
						String jsonPrimitive = json.get(key).toString();
						boolean contains = jsonPrimitive.contains(value);
						if(contains) {
							System.out.println("Determined that Id is of the following nodeType: " + nodeType.getNodeTypeName());
							resultNodeType = nodeType;
							break;
						}
					}else {
						System.out.println("Determined that Id is of the following nodeType: " + nodeType.getNodeTypeName());
						resultNodeType = nodeType;
						break;
					}
				}
			}
		}
		// if node type could be determined add entry to mapping table
		if(resultNodeType != null) {
			List<Vector<String>> dataList = new ArrayList<Vector<String>>();
			Vector<String> dataVector1 = new Vector<String>();
			dataVector1.add("nodeTypeMappingId");
			dataVector1.add(id);
			dataList.add(dataVector1);
			Vector<String> dataVector2 = new Vector<String>();
			dataVector2.add("nodeTypeMappingType");
			dataVector2.add(resultNodeType.getNodeTypeName());
			dataList.add(dataVector2);
			queryService.addEntry(dataList, "nodeTypeID");
		}
		return resultNodeType;
	}
}
