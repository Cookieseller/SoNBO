package uniko.iwvi.fgbas.magoetz.sbo.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.NotesException;
import uniko.iwvi.fgbas.magoetz.sbo.objects.NodeTypeAttribute;
import uniko.iwvi.fgbas.magoetz.sbo.objects.Datasource;
import uniko.iwvi.fgbas.magoetz.sbo.objects.Node;
import uniko.iwvi.fgbas.magoetz.sbo.objects.NodeTypeCategory;
import uniko.iwvi.fgbas.magoetz.sbo.objects.NodeType;
import uniko.iwvi.fgbas.magoetz.sbo.objects.Query;
import uniko.iwvi.fgbas.magoetz.sbo.objects.NodeTypeAdjacency;
import uniko.iwvi.fgbas.magoetz.sbo.objects.QueryResult;
import uniko.iwvi.fgbas.magoetz.sbo.util.Utilities;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class NodeService implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private ConfigService configService = new ConfigService();
	
	private QueryService queryService = new QueryService();

	public Node getNode(String id, boolean nodePreview) {
		
		// 1. CREATE NEW BUSINESS OBJECT
		Node node = new Node();
	
		// set object id and name
		node.setId(id);
			System.out.println("NodeId " + id);
		//set node type categories
		List<String> nodeTypeCategories = configService.getAllNodeTypeCategoryNames();
		node.setNodeTypeCategories(nodeTypeCategories);
		
		// 2. GET CONFIGURATION DOCUMENT FOR OBJECT TYPE

		//NodeType configObject = configService.getConfigurationObject(objectName);
		NodeType configObject = configService.getNodeTypeById(id);
		if(configObject != null) {
			node.setNodeType(configObject.getNodeTypeName());
			
			// 3. RETRIEVE ATTRIBUTES OF BUSINESS OBJECT
			
			// set object class
			node.setNodeTypeCategory(configObject.getNodeTypeCategory());
			
			// set object image
			// TODO: set individual image if available
			NodeTypeCategory nodeTypeCategory = configService.getNodeTypeCategory(node.getNodeTypeCategory());
			node.setNodeImage(nodeTypeCategory.getDefaultImage());
			
			// get business object attributes
			ArrayList<QueryResult> queryResultList = this.getNodeAttributes(configObject, id, nodePreview);
			
			// load attribute key and value into business object
			node = loadAttributes(node, configObject, queryResultList, nodePreview);
		
			return node;
		}else {
			System.out.println("No configuration document found for id: " + id);
		}
		return null;
	}
	
	/*
	 * returns list of business object attributes
	 */
	private ArrayList<QueryResult> getNodeAttributes(NodeType config, String id, boolean nodePreview) {
		
		// cache result to prevent redundant queries
		ArrayList<QueryResult> queryResultList = new ArrayList<QueryResult>();
		
		// if it is an object preview only get preview attributes otherwise all defined
		List<NodeTypeAttribute> nodeTypeAttributes =  new ArrayList<NodeTypeAttribute>();
		if(nodePreview){
			nodeTypeAttributes =  config.getPreviewConfigurationNodeAttributes();
		}else {
			nodeTypeAttributes =  config.getNodeTypeAttributes();
		}
		
		// get value for each object attribute
		for(NodeTypeAttribute nodeTypeAttribute : nodeTypeAttributes) {

			String datasource = nodeTypeAttribute.getDatasource();
			String query = nodeTypeAttribute.getQuery();
			System.out.println("Query: " + query);
			QueryResult queryResult = new QueryResult(datasource, query);
			
			// check if query result is already cached
			JsonObject jsonQueryResultObject = queryService.getQueryResult(queryResultList, queryResult);
			
			if(jsonQueryResultObject == null) {
				// get datasource configuration
				JsonObject jsonDatasourceObject = queryService.getJsonObject("datasources", datasource, "datasourceJSON");
				Gson gson = new Gson();
				Datasource datasourceObject = gson.fromJson(jsonDatasourceObject, Datasource.class);
				// log json
				//Utilities utilities = new Utilities();
				//utilities.printJson(jsonDatasourceObject, "json datasource object");
				// get query				
				JsonObject jsonQueryObject = queryService.getJsonObject("queries", query, "queryJSON");
				Query queryObject = gson.fromJson(jsonQueryObject, Query.class);
				// log json
				//utilities.printJson(jsonQueryObject, "json query object");
				jsonQueryResultObject = queryService.executeQuery(datasourceObject, queryObject, id);
				queryResult.setJsonObject(jsonQueryResultObject);
				queryResultList.add(queryResult);
				// log json
				//utilities.printJson(jsonQueryResultObject, "Parsed queryResult json");
			}
		}	
		
		return queryResultList;
	}
	
	private Node loadAttributes(Node businessObject, NodeType configuration, ArrayList<QueryResult> queryResultList, boolean nodePreview) {

		// TODO: all attributes should be loaded, but only the preview ones displayed
		// if it is an object preview only process preview attributes otherwise all defined
		List<NodeTypeAttribute> configurationNodeAttributes =  new ArrayList<NodeTypeAttribute>();
		if(nodePreview){
			configurationNodeAttributes =  configuration.getPreviewConfigurationNodeAttributes();
		}else {
			configurationNodeAttributes =  configuration.getNodeTypeAttributes();
		}
		
		for(NodeTypeAttribute nodeTypeAttribute : configuration.getNodeTypeAttributes()) {
			// get name and fieldname of attribute
			String name = nodeTypeAttribute.getName();
			String fieldname = nodeTypeAttribute.getFieldname();
			// get query result for config object attribute
			String datasource = nodeTypeAttribute.getDatasource();
			String query = nodeTypeAttribute.getQuery();
			QueryResult queryResult = new QueryResult(datasource, query);
			try {
				JsonObject jsonQueryResultObject = queryService.getQueryResult(queryResultList, queryResult);
				// extract value from query result
				JsonElement value = jsonQueryResultObject.get(fieldname);
				Gson gson = new Gson();
				String valueJson = gson.toJson(value);
				nodeTypeAttribute.setValue(valueJson);
				int displayfield = nodeTypeAttribute.getDisplayfield();
				// add whole nodeTypeAttribute Object with updated value
				businessObject.addAttribute(nodeTypeAttribute);
				//set business object title if attribute is configured as title
				String titleAttribute = configuration.getNodeTypeTitle();
				if(titleAttribute.equals(name)) {
					businessObject.setNodeTitle(value.getAsString());
				} 
			}catch(NullPointerException npe) {
				System.out.println("Failed loading attribute: " + name + " from field: " + fieldname);
			}
		}
		
		return businessObject;
	}

	public List<Node> getAdjacentNodes(Node businessObject, String nodeTypeCategoryName) {
		
		System.out.println("Adjacent nodes");
		System.out.println("==============");
		
		List<Node> adjacentNodesList = new ArrayList<Node>();
		// get peer query for object type
		// execute peer query and retrieve peer object ids
		String queryString = "";
		
		// determine peer query by source and target object type
		String sourceNodeType = businessObject.getNodeType();
		//System.out.println("sourceNodeType: " + sourceNodeType);
	
		// Get children of class objectPeers ( = target object names) e.g. person -> employee, student etc.
		ArrayList<String> adjacentNodeTypes = configService.getAllNodeTypeNamesByCategory(nodeTypeCategoryName);
		
		if(adjacentNodeTypes.size() > 0) {
			// get peer queries for source object <-> target objects
			String adjacencyQueryString = this.buildAdjacentNodesQueryString(adjacentNodeTypes, sourceNodeType);
			
			//execute query for getting node relationships
			ArrayList<NodeTypeAdjacency> adjacencyQueryList = getAdjacencyQueries(adjacencyQueryString);
	
			// execute queries for getting peer object IDs
			ArrayList<String> adjacentNodeIDs = this.getAdjacentNodeIDs(businessObject, adjacencyQueryList);
			
			for(String adjacentNodeId : adjacentNodeIDs) {
				Node adjacentNode = this.getNode(adjacentNodeId, true);
				if(adjacentNode != null) {
					adjacentNodesList.add(adjacentNode);
				}
			}
		}
		return adjacentNodesList;
	}
	
	private ArrayList<NodeTypeAdjacency> getAdjacencyQueries(String adjacencyQueryString) {
		
		DocumentCollection resultCollectionAdjacenyQueryList = queryService.ftSearchView("", adjacencyQueryString, "nodeTypeAdjacencies");
		
		ArrayList<NodeTypeAdjacency> adjacencyQueryList = new ArrayList<NodeTypeAdjacency>();
		
		try {
			if(resultCollectionAdjacenyQueryList != null) {
				Gson gson = new Gson();
				for(int i=1; i<=resultCollectionAdjacenyQueryList.getCount(); i++) {
					Document doc = resultCollectionAdjacenyQueryList.getNthDocument(i);
					String adjacencyId = doc.getItemValueString("adjacencyId");
					String adjacencyQueryJSON = queryService.getFieldValue("", "", "nodeTypeAdjacencies", adjacencyId, "adjacencyQueryJSON");					
					// retrieve query and database
					NodeTypeAdjacency adjacencyQuery = gson.fromJson(adjacencyQueryJSON, NodeTypeAdjacency.class);
					adjacencyQueryList.add(adjacencyQuery);
					// test print out
					//System.out.println("adjacencyQuery: " + adjacencyQuery.getQuery());
				}
			}else {
				System.out.println("Result of query " + adjacencyQueryString + " is null.");
			}
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		return adjacencyQueryList;		
	}
	
	private String buildAdjacentNodesQueryString(ArrayList<String> adjacentNodeTypes, String sourceNodeType) {
		String adjacencyQueryString = "";
		for(int i=0; i<adjacentNodeTypes.size(); i++) {
			//System.out.println("Result string adjacent node name: " + adjacentNodeTypes.get(i));
			adjacencyQueryString += "[adjacencySourceNode] = " + sourceNodeType + " AND [adjacencyTargetNode] = " + adjacentNodeTypes.get(i);
			if(i < adjacentNodeTypes.size() - 1) {
				adjacencyQueryString += " OR ";
			}
		}
		//System.out.println("adjacencyQueryString: " + adjacencyQueryString);
		return adjacencyQueryString;
	}
	
	private ArrayList<String> getAdjacentNodeIDs(Node businessObject, ArrayList<NodeTypeAdjacency> adjacencyQueryList) {
		// execute queries for getting peer object IDs
		ArrayList<String> adjacentNodeIDs = new ArrayList<String>();
		for(NodeTypeAdjacency adjacencyQuery : adjacencyQueryList) {
		
			// get datasource and query for peer query
			JsonObject jsonDatasourceObject = queryService.getJsonObject("datasources", adjacencyQuery.getDatasource(), "datasourceJSON");				
			JsonObject jsonQueryObject = queryService.getJsonObject("queries", adjacencyQuery.getQuery(), "queryJSON");
			//replace attributes in query string with variable values
			Gson gson = new Gson();
			Datasource datasourceObject = gson.fromJson(jsonDatasourceObject, Datasource.class);
			Query queryObject = gson.fromJson(jsonQueryObject, Query.class);
			String string = queryObject.getString();
			// create map with replacements
			ArrayList<String> replaceAttributesList = Utilities.getTokens(string);
			Map<String, String> replaceAttributesMap = new HashMap<String, String>();
			for(String replaceAttributeKey : replaceAttributesList) {
				// get attribute value from business object
				String replaceAttributeValue = businessObject.getAttributeValueAsString(replaceAttributeKey);
				//convert email to notes username
				//System.out.println("Attribute Value: " + replaceAttributeValue);
				if(queryObject.getKeyValueReturnType().equals("getEmailAsNotesUsername")) {
					replaceAttributeValue = this.queryService.getNotesUsernameByEmail(replaceAttributeValue);
				}
				//System.out.println("Attribute Value: " + replaceAttributeValue);
				replaceAttributesMap.put(replaceAttributeKey, replaceAttributeValue);
			}
			// replace [key] in string with variable values
			string = Utilities.replaceTokens(string, replaceAttributesMap);
			System.out.println("QueryString after replacements: " + string);
			// replace query string
			queryObject.setString(string);
			
			String targetNodeName = adjacencyQuery.getTargetNode();
			String targetNodeIdKey = queryService.getFieldValue("", "", "nodeTypes", targetNodeName, "nodeTypeId");
				System.out.println("targetNodeIdKey: " + targetNodeIdKey);
			String sourceNodeId = businessObject.getId();
				System.out.println("sourceNodeIdKey: " + sourceNodeId);
			
			ArrayList<String> resultAdjacentNodeIDs = this.retrieveAdjacentNodeIDs(datasourceObject, queryObject, sourceNodeId);
			// test
			for(String nodeId : resultAdjacentNodeIDs) {
				System.out.println("AdjacentNodeID: " + nodeId);
			}
			adjacentNodeIDs.addAll(resultAdjacentNodeIDs);
		}
		return adjacentNodeIDs;
	}
	
	private ArrayList<String> retrieveAdjacentNodeIDs(Datasource datasourceObject, Query queryObject, String sourceNodeId) {
		
		DocumentCollection resultCollectionAdjacentNodesIDs = queryService.executeQueryFTSearch(datasourceObject, queryObject); 
		// get targetObjectIdKeys
		List<String> targetNodeIdKeys = queryObject.getKey();
		// extract object IDs from resultCollection
		ArrayList<String> adjacentNodeIds = new ArrayList<String>();
		if(resultCollectionAdjacentNodesIDs != null) {
			try {
				for(int i=1; i<=resultCollectionAdjacentNodesIDs.getCount(); i++) {
					Document doc = resultCollectionAdjacentNodesIDs.getNthDocument(i);
					//System.out.println(doc.generateXML());
					for(String targetNodeIdKey : targetNodeIdKeys) {
						// expect multiple values
						Vector<String> nodeIds = doc.getItemValue(targetNodeIdKey);
						for(String nodeId : nodeIds) {
							// add to peer object id list if it is not the object id itself
							if(!nodeId.equals(sourceNodeId)) {
								adjacentNodeIds.add(nodeId);
							}
						}
					}
				}
			} catch (NotesException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			System.out.println("Result of query to get adjacentNodeIDs of is null.");
		}
		return adjacentNodeIds;
	}
}
