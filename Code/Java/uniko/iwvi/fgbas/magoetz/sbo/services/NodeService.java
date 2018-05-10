package uniko.iwvi.fgbas.magoetz.sbo.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lotus.domino.NotesException;
import uniko.iwvi.fgbas.magoetz.sbo.database.OData;
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

    private OData queryService = new OData();

    /**
     * Returns a node matching the given id and type
     *
     * @param id
     * @param nodeType
     * @param nodePreview
     * @return
     */
    public Node getNode(String id, String nodeType, boolean nodePreview) {

        if (!configService.nodeConfigExists(nodeType)) {
        	return null;
        }

        NodeType nodeTypeConfig 			= configService.getNodeTypeByName(nodeType);
        List<String> nodeTypeCategories 	= configService.getAllNodeTypeCategoryNames();
        NodeTypeCategory nodeTypeCategory 	= configService.getNodeTypeCategoryByName(nodeTypeConfig.getNodeTypeCategory());

        Node node = new Node();
        node.setId(id);
        node.setNodeTypeCategories(nodeTypeCategories);
        node.setNodeType(nodeTypeConfig.getNodeTypeName());
        node.setNodeTypeCategory(nodeTypeConfig.getNodeTypeCategory());
        node.setNodeImage(nodeTypeCategory.getDefaultImage());

        List<NodeTypeAttribute> nodeTypeAttributes = nodeTypeConfig.getNodeTypeAttributes();

        String attributeId = nodeTypeConfig.getNodeTypeId();
        
        // TODO this should be done better
        NodeTypeAttribute idAttribute = null;
        for (NodeTypeAttribute attribute : nodeTypeAttributes) {
        	if (attribute.getName().equals(attributeId)) {
        		idAttribute = attribute;
        		break;
        	}
        }

        idAttribute.setValue(id);
        node.addAttribute(idAttribute);
        nodeTypeAttributes.remove(idAttribute);

        if (nodePreview) {
            nodeTypeAttributes = nodeTypeConfig.getPreviewAndFilterableConfigurationNodeAttributes();
        }
        ArrayList<QueryResult> queryResultList = getNodeAttributeQueries(nodeTypeAttributes, node, nodePreview);
        
        String nodeTypeTitleAttrName = nodeTypeConfig.getNodeTypeTitle();
        node = loadAttributes(node, nodeTypeAttributes, nodeTypeTitleAttrName, queryResultList, nodePreview);

        return node;
    }

    /**
     * 
     * @param jsonNode
     * @param nodeType
     * @return
     */
    private Node jsonToNode(JsonObject jsonNode, NodeType nodeType) {
    	Node node = new Node();
    	List<String> nodeTypeCategories = configService.getAllNodeTypeCategoryNames();
    	
        node.setId(jsonNode.get(nodeType.getNodeTypeIdAttribute().getFieldname()).getAsString());
        node.setNodeTypeCategories(nodeTypeCategories);
        node.setNodeType(nodeType.getNodeTypeName());
        node.setNodeTypeCategory(nodeType.getNodeTypeCategory());
        
        //TODO the node is already complete, no need to query individual attributes, this may however be necessary for other Query Types, 
        // so mb implement a Attribute type which could be query or string
        NodeTypeCategory nodeTypeCategory = configService.getNodeTypeCategoryByName(node.getNodeTypeCategory());
        node.setNodeImage(nodeTypeCategory.getDefaultImage());
        List<NodeTypeAttribute> nodeTypeAttributes = extractAttributesFromJson(jsonNode, nodeType);
        node.addAllAttributes(nodeTypeAttributes);

        String nodeTypeTitleAttrName = nodeType.getNodeTypeTitle();
        //node = loadAttributes(node, nodeTypeAttributes, nodeTypeTitleAttrName, queryResultList, true);
        
        return node;
    }
    
    private List<NodeTypeAttribute> extractAttributesFromJson(JsonObject jsonNode, NodeType nodeType) {
    	List<NodeTypeAttribute> nodeTypeAttributes = nodeType.getPreviewAndFilterableConfigurationNodeAttributes();

    	Node node = new Node();
    	for (NodeTypeAttribute attribute : nodeTypeAttributes) {
    		attribute.setValue(jsonNode.get(attribute.getFieldname()).getAsString());
    		node.addAttribute(attribute);
    	}

    	return nodeTypeAttributes;
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
    private ArrayList<QueryResult> getNodeAttributeQueries(List<NodeTypeAttribute> nodeTypeAttributes, Node node, boolean nodePreview) {

        ArrayList<QueryResult> queryResultList = new ArrayList<QueryResult>();

        for (NodeTypeAttribute nodeTypeAttribute : nodeTypeAttributes) {

            String datasource = nodeTypeAttribute.getDatasource();
            String query = nodeTypeAttribute.getQuery();

            // TODO attributes may come from different queries or even datasources
            QueryResult queryResult = new QueryResult(datasource, query);

            // check if query result is already cached
            JsonObject jsonQueryResultObject = queryService.getQueryResult(queryResultList, queryResult);

            if (jsonQueryResultObject == null) {
                Datasource datasourceObject = queryService.getDatasourceObject(datasource);
                Query queryObject = queryService.getQueryObject(query);

                String replacedString = createAttributeList(queryObject, node);
                queryObject.setString(replacedString);

                jsonQueryResultObject = queryService.executeQuery(datasourceObject, queryObject, "");
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

            ArrayList<String> tokenList = Utilities.getTokenList(queryObject.getString());
            Map<String, String> replaceAttributesMap = new HashMap<String, String>();
            for (String replaceAttributeKey : tokenList) {
                String replaceAttributeValue = businessObject.getAttributeValueAsString(replaceAttributeKey);
                replaceAttributesMap.put(replaceAttributeKey, replaceAttributeValue);
            }
            String replacedQuery = Utilities.replaceTokens(queryObject.getString(), replaceAttributesMap);
            queryObject.setString(replacedQuery);

            JsonArray jsonArray = queryService.executeQuery(datasourceObject, queryObject);

            if (jsonArray.size() <= 0) {
            	return businessObject;
            }

            JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
            queryResult.setJsonObject(jsonObject);

            String name 	  = nodeTypeAttribute.getName();
            String fieldname  = nodeTypeAttribute.getFieldname();
            String value 	  = jsonObject.get(nodeTypeAttribute.getFieldname()).getAsString();

            try {
                String valueJson  = new Gson().toJson(value);

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
                Utilities.remotePrint("Failed loading attribute: " + name + " from field: " + fieldname);
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
				NodeType nodeType = configService.getNodeTypeByName(query.getTargetNode());
				Node node = jsonToNode(element.getAsJsonObject(), nodeType);
				
				nodeList.add(node);
    		}
    	}
    	return nodeList;
    }

    /**
     * Returns a list of events
     * TODO refactor hacking of get only first object of json node list and stuff
     *
     * @param node
     * @return
     */
    public Map<String, String> getEventsForNode(Node node) {
    	Map<String, String> nodeEvents 				  = new LinkedHashMap<String, String>();
        ArrayList<NodeTypeEvent> nodeTypeEventQueries = getNodeTypeEventQueries(node.getNodeType());
        
        for (NodeTypeEvent event : nodeTypeEventQueries) {
        	Query queryObject = queryService.getQueryObject(event.getQuery());
            
            String replacedString = createAttributeList(queryObject, node);
            
            queryObject.setString(replacedString);
            queryObject.setQueryAttributes(new ArrayList<String>());
            String sourceNodeId = node.getId();

            Datasource datasource = queryService.getDatasourceObject(event.getDatasource());
            ArrayList<JsonObject> eventObjects = retrieveEventObjects(datasource, queryObject, sourceNodeId);
            String eventText = replaceDisplayTextWithValues(event.getDisplayText(), eventObjects);
            
            if (eventObjects.size() > 0) {
            	if (eventObjects.get(0).has(event.getDateField())) {
            		nodeEvents.put(eventObjects.get(0).get(event.getDateField()).getAsString(), eventText);		
            	}
            }
            
        }
        
        return nodeEvents;
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
     * Return all NodeTypeEvents for a given node type
     *
     * @param nodeType
     * @return
     */
    private ArrayList<NodeTypeEvent> getNodeTypeEventQueries(String nodeType) {
        ArrayList<String> eventIds = queryService.getFieldValues("(nodeTypeEvents)", nodeType, "eventID");
        ArrayList<NodeTypeEvent> events = new ArrayList<NodeTypeEvent>();
        
        Gson gson = new Gson();
        for (String eventId : eventIds) {
        	String eventJson = queryService.getFieldValue("", "", "nodeTypesEvents", eventId, "eventJson");
        	NodeTypeEvent nodeTypeEvent = gson.fromJson(eventJson, NodeTypeEvent.class);
        	events.add(nodeTypeEvent);
        }
        return events;
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
        ArrayList<String> tokenList = Utilities.getTokenList(queryString);
        
        NodeTypeAttribute nodeTypeAttribute = null;
        if (tokenList.size() > 0) {
            String replaceAttrString = tokenList.get(0);
            nodeTypeAttribute = node.getAttributeOfType(replaceAttrString, "Array(String)");
        }
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
   private ArrayList<JsonObject> retrieveEventObjects(Datasource datasourceObject, Query queryObject, String sourceNodeId) {
	   JsonArray result = queryService.executeQuery(datasourceObject, queryObject);
	   ArrayList<JsonObject> objects = new ArrayList<JsonObject>();
	   for (JsonElement element : result) {
		   objects.add(element.getAsJsonObject());
	   }
       
       return objects;
   }
   
   /**
    * 
    * @param displayText
    * @param eventObjects
    * @return
    */
   private String replaceDisplayTextWithValues(String displayText, ArrayList<JsonObject> eventObjects) {
	   ArrayList<String> tokenList = Utilities.getTokenList(displayText);
       Map<String, String> replaceAttributesMap = new HashMap<String, String>();
       
       if (eventObjects.size() < 1)
    	   return "";
       
       JsonObject node = eventObjects.get(0);
       
       for (String replaceAttributeKey : tokenList) {
    	   if (node.has(replaceAttributeKey)) {
    		   replaceAttributesMap.put(replaceAttributeKey, node.get(replaceAttributeKey).getAsString());   
    	   }
       }
       
       return Utilities.replaceTokens(displayText, replaceAttributesMap);
   }
}
