package uniko.iwvi.fgbas.magoetz.sbo.services;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;

import uniko.iwvi.fgbas.magoetz.sbo.database.OData;
import uniko.iwvi.fgbas.magoetz.sbo.exceptions.NodeNotFoundException;
import uniko.iwvi.fgbas.magoetz.sbo.objects.Datasource;
import uniko.iwvi.fgbas.magoetz.sbo.objects.Node;
import uniko.iwvi.fgbas.magoetz.sbo.objects.NodeType;
import uniko.iwvi.fgbas.magoetz.sbo.objects.NodeTypeAdjacency;
import uniko.iwvi.fgbas.magoetz.sbo.objects.NodeTypeAttribute;
import uniko.iwvi.fgbas.magoetz.sbo.objects.NodeTypeCategory;
import uniko.iwvi.fgbas.magoetz.sbo.objects.NodeTypeEvent;
import uniko.iwvi.fgbas.magoetz.sbo.objects.Query;
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
     * @throws NodeNotFoundException 
     */
    public Node getNode(String id, String nodeType, boolean nodePreview) throws NodeNotFoundException {

        if (!configService.nodeConfigExists(nodeType)) {
        	throw new NodeNotFoundException("The selected node does not exist");
        }

        NodeType nodeTypeConfig 				   = configService.getNodeTypeByName(nodeType);
        Node node 								   = createNodeByType(nodeTypeConfig);
        List<NodeTypeAttribute> nodeTypeAttributes = nodeTypeConfig.getNodeTypeAttributes();
        NodeTypeAttribute nodeAttributeId          = nodeTypeConfig.getNodeTypeIdAttribute();

        // TODO the special treatment for the id attribute could be fixed by not defining the id as a default attribute which has to be loaded
        node.setId(id);
        nodeAttributeId.setValue(id);
        node.addAttribute(nodeAttributeId);
        nodeTypeAttributes.remove(nodeAttributeId);

        if (nodePreview) {
            nodeTypeAttributes = nodeTypeConfig.getPreviewAndFilterableConfigurationNodeAttributes();
        }

        List<NodeTypeAttribute> nodeAttributes = getNodeAttributes(node, nodeTypeAttributes);
        node.addAllAttributes(nodeAttributes);
        String title = node.getAttributeValueAsString(nodeTypeConfig.getNodeTypeTitle());
        node.setNodeTitle(title);

        return node;
    }

    /**
     * Create a "default" node of the given type. None of the node specific attributes have been set yet.
     *
     * @param nodeType
     * @return
     */
    private Node createNodeByType(NodeType nodeType) {
    	
        List<String> nodeTypeCategories 	= configService.getAllNodeTypeCategoryNames();
        NodeTypeCategory nodeTypeCategory 	= configService.getNodeTypeCategoryByName(nodeType.getNodeTypeCategory());
        
    	Node node = new Node();
        node.setNodeTypeCategories(nodeTypeCategories);
        node.setNodeType(nodeType.getNodeTypeName());
        node.setNodeTypeCategory(nodeType.getNodeTypeCategory());
        node.setNodeImage(nodeTypeCategory.getDefaultImage());

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

        //String nodeTypeTitleAttrName = nodeType.getNodeTypeTitle();

        List<NodeTypeAttribute> nodeAttributes = getNodeAttributes(node, nodeTypeAttributes);
        node.addAllAttributes(nodeAttributes);

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
     * Returns a list of resolved node attributes
     * @param nodeTypeAttributes
     * @param queryResultList
     * @return
     */
    private List<NodeTypeAttribute> getNodeAttributes(final Node node, final List<NodeTypeAttribute> nodeTypeAttributes) {
    	
    	List<NodeTypeAttribute> attributes = new ArrayList<NodeTypeAttribute>();
    	for (NodeTypeAttribute nodeTypeAttribute : nodeTypeAttributes) {
            String datasource = nodeTypeAttribute.getDatasource();
            String query 	  = nodeTypeAttribute.getQuery();

            Datasource datasourceObject = queryService.getDatasourceObject(datasource);
            Query queryObject 			= queryService.getQueryObject(query);
            String replacedString 		= createAttributeList(node, queryObject);
            queryObject.setString(replacedString);

            JsonArray jsonArray = queryService.executeQuery(datasourceObject, queryObject);

            if (jsonArray.size() <= 0) continue;

            JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
            String value 	  	  = jsonObject.get(nodeTypeAttribute.getFieldname()).getAsString();

            if (!jsonObject.has(nodeTypeAttribute.getFieldname())) {
            	System.out.println("Failed loading attribute: " + nodeTypeAttribute.getName() + " from field: " + nodeTypeAttribute.getFieldname());

            	continue;
            }

            nodeTypeAttribute.setValue(value);
            attributes.add(nodeTypeAttribute);
        }

		return attributes;
    }

    /**
     * Returns a list of Nodes which are adjacent to the given one
     *
     * @param Node businessObject
     * @return List<Node>
     */
    public List<Node> getAdjacentNodes(final Node businessObject) throws Exception{
    	ArrayList<Node> nodeList 			 = new ArrayList<Node>();
    	ArrayList<NodeTypeAdjacency> queries = getQueriesForAdjacenctNodes(businessObject.getNodeType());

    	for (NodeTypeAdjacency query : queries) {
    		Query queryObject 	  = queryService.getQueryObject(query.getQuery());
    		Datasource datasource = queryService.getDatasourceObject(query.getDatasource());

    		String queryString = createAttributeList(businessObject, queryObject);
    		queryObject.setString(queryString);
    		JsonArray jsonArr = queryService.executeQuery(datasource, queryObject);
    		for (JsonElement element : jsonArr) {
				NodeType nodeType = configService.getNodeTypeByName(query.getTargetNode());
				Node node = jsonToNode(element.getAsJsonObject(), nodeType);

				nodeList.add(node);
    		}
    	}
    	return nodeList;
    	
    	/*
    	ArrayList<NodeTypeAdjacency> queries = getQueriesForAdjacenctNodes(businessObject.getNodeType());

    	ExecutorService taskExecutor = Executors.newFixedThreadPool(10);
    	
    	FacesContext ctx = FacesContext.getCurrentInstance(); 
        final SoNBOSession session = (SoNBOSession) ctx.getApplication().getVariableResolver().resolveVariable(ctx, "soNBOSession");

        final CopyOnWriteArrayList<AbstractMap.SimpleEntry<NodeType, JsonElement>> threadSafeList = new CopyOnWriteArrayList<AbstractMap.SimpleEntry<NodeType, JsonElement>>();
    	for (final NodeTypeAdjacency query : queries) {
    		final Query queryObject 	= queryService.getQueryObject(query.getQuery());
    		final Datasource datasource = queryService.getDatasourceObject(query.getDatasource());

    		String queryString = createAttributeList(businessObject, queryObject);
    		queryObject.setString(queryString);
    		
			taskExecutor.execute(new Runnable() {
				public void run() {
					try {
			    		JsonArray jsonArr = queryService.executeQuery(datasource, queryObject, session);
			    		for (JsonElement element : jsonArr) {
			    			NodeType nodeType 	= configService.getNodeTypeByName(query.getTargetNode());
			    			threadSafeList.add(new AbstractMap.SimpleEntry<NodeType, JsonElement>(nodeType, element));
			    		}	
					} catch (Exception e) {
						Utilities.remotePrint(e.getMessage());
					}
				}
	    	});
    	}
    	taskExecutor.shutdown();
    	try {
    		taskExecutor.awaitTermination(30, TimeUnit.SECONDS);
    	} catch (InterruptedException e) {
    		taskExecutor.shutdownNow();
    		Utilities.remotePrint(e.getMessage());
    		System.out.println(e.getMessage());
    		return new ArrayList<Node>();
    	}

    	ArrayList<Node> nodeList = new ArrayList<Node>();
    	for(AbstractMap.SimpleEntry<NodeType, JsonElement> element : threadSafeList) {
    		Node node = jsonToNode(element.getValue().getAsJsonObject(), element.getKey());
    		nodeList.add(node);
    	}

    	for (Node node : nodeList) {
    		Utilities.remotePrint("-------Nodetype: " + node.getNodeType() + " ID: " + node.getId());
    		for (NodeTypeAttribute attr : node.getAttributeList()) {
    			Utilities.remotePrint(attr.getName() + ": " + attr.getValueAsString());
    		}
    	}
    	
    	return nodeList;*/
    }

    /**
     * Returns a list of events
     * TODO refactor hacking of get only first object of json node list and stuff
     *
     * @param node
     * @return
     */
    public List<AbstractMap.SimpleEntry<String, String>> getEventsForNode(Node node) {
    	List<AbstractMap.SimpleEntry<String, String>> events = new ArrayList<AbstractMap.SimpleEntry<String, String>>();
    	Map<String, String> nodeEvents 				  = new LinkedHashMap<String, String>();
        ArrayList<NodeTypeEvent> nodeTypeEventQueries = getNodeTypeEventQueries(node.getNodeType());

        for (NodeTypeEvent event : nodeTypeEventQueries) {
        	Query queryObject = queryService.getQueryObject(event.getQuery());

            String replacedString = createAttributeList(node, queryObject);

            queryObject.setString(replacedString);
            queryObject.setQueryAttributes(new ArrayList<String>());
            String sourceNodeId = node.getId();

            Datasource datasource = queryService.getDatasourceObject(event.getDatasource());
            ArrayList<JsonObject> eventObjects = retrieveEventObjects(datasource, queryObject, sourceNodeId);

            for (JsonObject obj : eventObjects) {
            	String eventText = replaceDisplayTextWithValues(event.getDisplayText(), obj);
            	String dateString = obj.get(event.getDateField()).getAsString();
        		String formattedDate = Utilities.formatDateString(dateString);

        		events.add(new AbstractMap.SimpleEntry<String, String>(formattedDate, eventText));
        		nodeEvents.put(formattedDate, eventText);
            }
        }

        Collections.sort(events, new Comparator<AbstractMap.SimpleEntry<String, String>>() {
			public int compare(SimpleEntry<String, String> arg0, SimpleEntry<String, String> arg1) {
				DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss'Uhr'");
				try {
					Date date1 = dateFormat.parse(arg0.getKey());
					Date date2 = dateFormat.parse(arg1.getKey());

					return date1.compareTo(date2) * -1;
				} catch (ParseException e) {
					e.printStackTrace();
				}

				return 0;
			}
        });
        
        return events;
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
    private synchronized String createAttributeList(Node node, Query queryObject) {
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
	            String replaceAttributeValue = node.getAttributeValueByField(replaceAttributeKey);
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
	private String replaceDisplayTextWithValues(String displayText, JsonObject eventObject) {
		ArrayList<String> tokenList = Utilities.getTokenList(displayText);
		Map<String, String> replaceAttributesMap = new HashMap<String, String>();
       
       
		for (String replaceAttributeKey : tokenList) {
			if (eventObject.has(replaceAttributeKey)) {
				replaceAttributesMap.put(replaceAttributeKey, eventObject.get(replaceAttributeKey).getAsString());   
			}
		}
       
		return Utilities.replaceTokens(displayText, replaceAttributesMap);
	}
}
