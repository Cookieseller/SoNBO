package uniko.iwvi.fgbas.magoetz.sbo.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.NotesException;
import uniko.iwvi.fgbas.magoetz.sbo.objects.Datasource;
import uniko.iwvi.fgbas.magoetz.sbo.objects.Node;
import uniko.iwvi.fgbas.magoetz.sbo.objects.NodeType;
import uniko.iwvi.fgbas.magoetz.sbo.objects.NodeTypeAdjacency;
import uniko.iwvi.fgbas.magoetz.sbo.objects.NodeTypeAttribute;
import uniko.iwvi.fgbas.magoetz.sbo.objects.NodeTypeCategory;
import uniko.iwvi.fgbas.magoetz.sbo.objects.NodeTypeEvent;
import uniko.iwvi.fgbas.magoetz.sbo.objects.Query;
import uniko.iwvi.fgbas.magoetz.sbo.objects.QueryResult;
import uniko.iwvi.fgbas.magoetz.sbo.util.Utilities;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class NodeService implements Serializable {

    private static final long serialVersionUID = 1L;

    private ConfigService configService = new ConfigService();

    private ODataQueryService queryService = new ODataQueryService();

    /**
     * Returns a node matching the given id and type
     *
     * @param id
     * @param nodeType
     * @param nodePreview
     * @return
     * @throws Exception 
     */
    public Node getNode(String id, String nodeType, boolean nodePreview) {

        NodeType configObject = configService.getNodeType(nodeType);
        if (configObject == null) {
            System.out.println("No configuration document found for id: " + id);

            return null;
        }

        Node node = new Node();
        node.setId(id);
        List<String> nodeTypeCategories = configService.getAllNodeTypeCategoryNames();
        node.setNodeTypeCategories(nodeTypeCategories);

        //NodeType configObject = configService.getConfigurationObject(objectName);
        node.setNodeType(configObject.getNodeTypeName());
        node.setNodeTypeCategory(configObject.getNodeTypeCategory());

        NodeTypeCategory nodeTypeCategory = configService.getNodeTypeCategory(node.getNodeTypeCategory());
        node.setNodeImage(nodeTypeCategory.getDefaultImage());
        
        List<NodeTypeAttribute> nodeTypeAttributes = configObject.getNodeTypeAttributes();
        if (nodePreview) {
            nodeTypeAttributes = configObject.getPreviewAndFilterableConfigurationNodeAttributes();
        }
        ArrayList<QueryResult> queryResultList = getNodeAttributes(nodeTypeAttributes, id, nodePreview);

        String nodeTypeTitleAttrName = configObject.getNodeTypeTitle();
        node = loadAttributes(node, nodeTypeAttributes, nodeTypeTitleAttrName, queryResultList, nodePreview);

        return node;
    }
    
    private Node jsonToNode(JsonObject jsonNode, NodeType nodeType) {
    	Node node = new Node();
    	List<String> nodeTypeCategories = configService.getAllNodeTypeCategoryNames();
    	
        node.setId(jsonNode.get(nodeType.getNodeTypeId()).getAsString());
        node.setNodeTypeCategories(nodeTypeCategories);
        node.setNodeType(nodeType.getNodeTypeName());
        node.setNodeTypeCategory(nodeType.getNodeTypeCategory());
        
        NodeTypeCategory nodeTypeCategory = configService.getNodeTypeCategory(node.getNodeTypeCategory());
        node.setNodeImage(nodeTypeCategory.getDefaultImage());
        List<NodeTypeAttribute> nodeTypeAttributes = nodeType.getPreviewAndFilterableConfigurationNodeAttributes();
        ArrayList<QueryResult> queryResultList = getNodeAttributes(nodeTypeAttributes, jsonNode.get(nodeType.getNodeTypeId()).getAsString(), true);

        String nodeTypeTitleAttrName = nodeType.getNodeTypeTitle();
        node = loadAttributes(node, nodeTypeAttributes, nodeTypeTitleAttrName, queryResultList, true);
        
        return node;
    }

    /**
     * Returns a list of business object attributes
     *
     * @param nodeTypeAttributes
     * @param id
     * @param nodePreview
     * @return
     * @throws Exception 
     */
    private ArrayList<QueryResult> getNodeAttributes(List<NodeTypeAttribute> nodeTypeAttributes, String id, boolean nodePreview) {

        ArrayList<QueryResult> queryResultList = new ArrayList<QueryResult>();

        for (NodeTypeAttribute nodeTypeAttribute : nodeTypeAttributes) {

            String datasource = nodeTypeAttribute.getDatasource();
            String query = nodeTypeAttribute.getQuery();
            QueryResult queryResult = new QueryResult(datasource, query);

            // check if query result is already cached
            JsonObject jsonQueryResultObject = queryService.getQueryResult(queryResultList, queryResult);

            if (jsonQueryResultObject == null) {
                Datasource datasourceObject = queryService.getDatasourceObject(datasource);
                Query queryObject = queryService.getQueryObject(query);
                jsonQueryResultObject = queryService.executeQuery(datasourceObject, queryObject, id);
                queryResult.setJsonObject(jsonQueryResultObject);
                queryResultList.add(queryResult);
            }
        }

        return queryResultList;
    }

    /**
     * @TODO loadAttributes should return a list of attributes not alter businessObject
     *
     * @param businessObject
     * @param nodeTypeAttributes
     * @param nodeTypeTitleAttrName
     * @param queryResultList
     * @param nodePreview
     * @return
     */
    private Node loadAttributes(Node businessObject, List<NodeTypeAttribute> nodeTypeAttributes, String nodeTypeTitleAttrName, ArrayList<QueryResult> queryResultList, boolean nodePreview) {

        for (NodeTypeAttribute nodeTypeAttribute : nodeTypeAttributes) {

            String datasource = nodeTypeAttribute.getDatasource();
            String query = nodeTypeAttribute.getQuery();
            QueryResult queryResult = new QueryResult(datasource, query);

            Datasource datasourceObject = queryService.getDatasourceObject(datasource);
            Query queryObject = queryService.getQueryObject(query);
            JsonArray jsonArray = queryService.executeQuery(datasourceObject, queryObject);
            if (jsonArray.size() <= 0) {
            	Utilities.remotePrint("(NodeService::loadAttributes) jsonArray: " + jsonArray.toString());
            	return businessObject;
            }
            
            JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
            queryResult.setJsonObject(jsonObject);
  
            String name 	  = nodeTypeAttribute.getName();
            String fieldname  = nodeTypeAttribute.getFieldname();
            String value 	  = jsonObject.get(nodeTypeAttribute.getFieldname()).getAsString();

            try {
                String valueJson  = new Gson().toJson(value);
                
                Utilities.remotePrint("(NodeService::loadAttributes) valueJson: " + valueJson + " Value: " + value);
                nodeTypeAttribute.setValue(valueJson);
                businessObject.addAttribute(nodeTypeAttribute);
                
                /*
                String titleAttribute = nodeTypeTitleAttrName;
                if (titleAttribute.equals(name)) {
                    businessObject.setNodeTitle(jsonObject.getAsString());
                }
                */
            } catch (NullPointerException npe) {
                System.out.println("Failed loading attribute: " + name + " from field: " + fieldname);
            }
        }

        return businessObject;
    }

    /**
     * Returns a list of Nodes which are adjacent to the given one
     *
     * @param Node businessObject
     * @return List<Node>
     */
    public List<Node> getAdjacentNodes(Node businessObject) {
    	ArrayList<Node> nodeList 			 = new ArrayList<Node>();
    	ArrayList<NodeTypeAdjacency> queries = getQueriesForAdjacenctNodes(businessObject.getNodeType());
    	
    	for (NodeTypeAdjacency query : queries) {
    		Query queryObject 	  = queryService.getQueryObject(query.getQuery());
    		Datasource datasource = queryService.getDatasourceObject(query.getDatasource());
    		
    		String queryString = createAttributeList(queryObject, businessObject);
    		queryObject.setString(queryString);
    		JsonArray jsonArr = queryService.executeQuery(datasource, queryObject);
    		for (JsonElement element : jsonArr) {
    			Utilities.remotePrint("(NodeService::getAdjacentNodes) element: " + element.toString());
    			NodeType nodeType = configService.getNodeType(query.getTargetNode());
    			Node node = jsonToNode(element.getAsJsonObject(), nodeType);
    			
    			nodeList.add(node);
    		}
    	}
    	
    	return nodeList;
    	
    	/*
        List<Node> adjacentNodesList = new ArrayList<Node>();
        String sourceNodeType = businessObject.getNodeType();

        ArrayList<String> adjacentNodeIDs = getAdjacentNodeIDs(businessObject);

        for (String adjacentNodeId : adjacentNodeIDs) {
            // TODO set node type
            Node adjacentNode = getNode(adjacentNodeId, "", true);
            if (adjacentNode != null) {
                adjacentNodesList.add(adjacentNode);
            }
        }

        return adjacentNodesList;*/
    }

    /**
     * Returns a list of events
     *
     * @param node
     * @return
     */
    public List<String> getEventsForNode(Node node) {
        return getNodeEvents(node);
    }

    /**
     * Returns a list of queries which can be used to get all its adjacent nodes
     *
     * @param String sourceNodeType
     * @return ArrayList<String>
     */
    private ArrayList<NodeTypeAdjacency> getQueriesForAdjacenctNodes(String sourceNodeType) {
        ArrayList<String> adjacencyIds = queryService.getFieldValues("(nodeTypeAdjacenciesSource)", sourceNodeType, "adjacencyId");
        ArrayList<NodeTypeAdjacency> adjacencyQueryList = new ArrayList<NodeTypeAdjacency>();
        Gson gson = new Gson();
        for (String adjacencyId : adjacencyIds) {
            String adjacencyQueryJSON = queryService.getFieldValue("", "", "nodeTypeAdjacencies", adjacencyId, "adjacencyQueryJSON");
            // retrieve query and database
            NodeTypeAdjacency adjacencyQuery = gson.fromJson(adjacencyQueryJSON, NodeTypeAdjacency.class);
            adjacencyQueryList.add(adjacencyQuery);
        }
        return adjacencyQueryList;
    }

    /**
     *
     * @param node
     * @return
     */
    private ArrayList<String> getNodeEvents(Node node) {
        ArrayList<NodeTypeEvent> nodeTypeEvents = getNodeTypeEvents(node.getNodeType());

        ArrayList<String> adjacentNodeIDs = new ArrayList<String>();

        for (NodeTypeEvent event : nodeTypeEvents) {
            Query queryObject = queryService.getQueryObject(event.getQuery());

            //replace attributes in query string with variable values
            String queryString = queryObject.getString();
            this.createAttributeList(queryObject, node);
            //System.out.println("QueryString after replacements: " + string);
            // replace query string
            queryObject.setString(queryString);
            queryObject.setQueryAttributes(new ArrayList<String>());
            String sourceNodeId = node.getId();

            Datasource datasource = queryService.getDatasourceObject(event.getDatasource());
            ArrayList<String> resultAdjacentNodeIDs = this.retrieveAdjacentNodeIDs(datasource, queryObject, sourceNodeId);
            adjacentNodeIDs.addAll(resultAdjacentNodeIDs);
        }
        return adjacentNodeIDs;
    }

    /**
     * Return all event queries for a given node type
     *
     * @param nodeType
     * @return
     */
    private ArrayList<NodeTypeEvent> getNodeTypeEvents(String nodeType) {
        ArrayList<String> nodeTypeNames = queryService.getFieldValues("(nodeTypeEventsSource)", nodeType, "nodeTypeEventName");

        ArrayList<NodeTypeEvent> nodeEvents = new ArrayList<NodeTypeEvent>();
        Gson gson = new Gson();
        for (String nodeTypeName : nodeTypeNames) {
            String eventQueryJSON = queryService.getFieldValue("", "", "nodeTypeEventsSource", nodeTypeName, "eventJson");

            NodeTypeEvent adjacencyQuery = gson.fromJson(eventQueryJSON, NodeTypeEvent.class);
            nodeEvents.add(adjacencyQuery);
        }
        return nodeEvents;
    }

    /**
     * Get Adjacent Nodes for the given Node
     *
     * @param Node node
     * @return ArrayList<String>
     */
    private ArrayList<String> getAdjacentNodeIDs(Node node) {
        ArrayList<NodeTypeAdjacency> adjacencyQueryList = getQueriesForAdjacenctNodes(node.getNodeType());
        ArrayList<String> adjacentNodeIDs = new ArrayList<String>();

        for (NodeTypeAdjacency adjacencyQuery : adjacencyQueryList) {
            Query queryObject = queryService.getQueryObject(adjacencyQuery.getQuery());

            String queryString = queryObject.getString();
            queryString = this.createAttributeList(queryObject, node);

            queryObject.setString(queryString);
            queryObject.setQueryAttributes(new ArrayList<String>());
            String sourceNodeId = node.getId();

            Datasource datasource = queryService.getDatasourceObject(adjacencyQuery.getDatasource());
            ArrayList<String> resultAdjacentNodeIDs = this.retrieveAdjacentNodeIDs(datasource, queryObject, sourceNodeId);
            adjacentNodeIDs.addAll(resultAdjacentNodeIDs);
        }
        return adjacentNodeIDs;
    }

    /**
     * Creates the attribute list by matching the placeholders in the queryString with the given nodes attributes.
     * The attribute List can later be used to create a full query string in the given DB dialect.
     *
     * @param String queryString
     * @param Node   node
     * @return String
     */
    private String createAttributeList(Query queryObject, Node node) {
        String queryString = queryObject.getString();
        ArrayList<String> attributeList = new ArrayList<String>();
        ArrayList<String> tokenList = Utilities.getTokenList(queryString);

        NodeTypeAttribute nodeTypeAttribute = null;
        if (tokenList.size() > 0) {
            String replaceAttrString = tokenList.get(0);
            nodeTypeAttribute = node.getAttributeOfType(replaceAttrString, "Array(String)");
        }
        Utilities.remotePrint("(NodeService::createAttributeList) tokenList: " + Utilities.ListToString(tokenList));
        // if first attribute is not of type Array(String)
        if (nodeTypeAttribute == null) {
            // create map with replacements
            Map<String, String> replaceAttributesMap = new HashMap<String, String>();
            for (String replaceAttributeKey : tokenList) {
                String replaceAttributeValue = node.getAttributeValueAsString(replaceAttributeKey);
                if (queryObject.getKeyValueReturnType().equals("getEmailAsNotesUsername")) {
                    replaceAttributeValue = this.queryService.getNotesUsernameByEmail(replaceAttributeValue);
                }
                replaceAttributesMap.put(replaceAttributeKey, replaceAttributeValue);
            }
            // replace [key] in string with variable values
            queryString = Utilities.replaceTokens(queryString, replaceAttributesMap);
            // if first attribute is of type Array(String) concatenate values
        } else {
            String[] stringValues = nodeTypeAttribute.getValueAsString().split(",");
            String[] words = queryString.split(" ");
            queryString = "";
            String fieldname = words[1];
            for (int i = 0; i < stringValues.length; i++) {
                if (i < stringValues.length - 1) {
                    queryString += "FIELD " + fieldname + " CONTAINS " + stringValues[i] + " OR ";
                } else {
                    queryString += "FIELD " + fieldname + " CONTAINS " + stringValues[i];
                }
            }
        }
        return queryString;
    }

    /**
     *
     * @param datasourceObject
     * @param queryObject
     * @param sourceNodeId
     * @return
     */
    @SuppressWarnings("unchecked")
    private ArrayList<String> retrieveAdjacentNodeIDs(Datasource datasourceObject, Query queryObject, String sourceNodeId) {

        DocumentCollection resultCollectionAdjacentNodesIDs = queryService.executeQueryFTSearch(datasourceObject, queryObject);
        // get targetObjectIdKeys
        List<String> targetNodeIdKeys = queryObject.getKey();
        // extract object IDs from resultCollection
        ArrayList<String> adjacentNodeIds = new ArrayList<String>();
        if (resultCollectionAdjacentNodesIDs != null) {
            try {
                for (int i = 1; i <= resultCollectionAdjacentNodesIDs.getCount(); i++) {
                    Document doc = resultCollectionAdjacentNodesIDs.getNthDocument(i);
                    //System.out.println(doc.generateXML());
                    for (String targetNodeIdKey : targetNodeIdKeys) {
                        // expect multiple values
                        Vector<String> nodeIds = (Vector<String>) doc.getItemValue(targetNodeIdKey);
                        for (String nodeId : nodeIds) {
                            // add to peer object id list if it is not the object id itself
                            if (!nodeId.equals(sourceNodeId)) {
                                adjacentNodeIds.add(nodeId);
                            }
                        }
                    }
                }
            } catch (NotesException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            System.out.println("Result of query to get adjacentNodeIDs of is null.");
        }
        return adjacentNodeIds;
    }
}
