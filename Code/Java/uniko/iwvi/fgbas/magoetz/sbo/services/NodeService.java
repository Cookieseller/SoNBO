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
import uniko.iwvi.fgbas.magoetz.sbo.objects.NodeTypeAttribute;
import uniko.iwvi.fgbas.magoetz.sbo.objects.Datasource;
import uniko.iwvi.fgbas.magoetz.sbo.objects.Node;
import uniko.iwvi.fgbas.magoetz.sbo.objects.NodeTypeCategory;
import uniko.iwvi.fgbas.magoetz.sbo.objects.NodeType;
import uniko.iwvi.fgbas.magoetz.sbo.objects.NodeTypeEvent;
import uniko.iwvi.fgbas.magoetz.sbo.objects.Query;
import uniko.iwvi.fgbas.magoetz.sbo.objects.NodeTypeAdjacency;
import uniko.iwvi.fgbas.magoetz.sbo.objects.QueryResult;
import uniko.iwvi.fgbas.magoetz.sbo.util.DBMock;
import uniko.iwvi.fgbas.magoetz.sbo.util.Utilities;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class NodeService implements Serializable {

    private static final long serialVersionUID = 1L;

    private ConfigService configService = new ConfigService();

    private RDSQueryService queryService = new RDSQueryService();

    public Node getNode(String id, String nodeType, boolean nodePreview) {

//		DBMock mock = new DBMock();
//		Node node = mock.getNodeById(id);
//		return node;
        // 1. CREATE NEW BUSINESS OBJECT
        Node node = new Node();

        // set object id and name
        node.setId(id);

        //set node type categories
        List<String> nodeTypeCategories = configService.getAllNodeTypeCategoryNames();
        node.setNodeTypeCategories(nodeTypeCategories);

        // 2. GET CONFIGURATION DOCUMENT FOR OBJECT TYPE

        //NodeType configObject = configService.getConfigurationObject(objectName);
        NodeType configObject = configService.getNodeType(nodeType);
        if (configObject != null) {
            node.setNodeType(configObject.getNodeTypeName());

            // 3. RETRIEVE ATTRIBUTES OF BUSINESS OBJECT

            // set object class
            node.setNodeTypeCategory(configObject.getNodeTypeCategory());

            // set object image
            // TODO: set individual image if available
            NodeTypeCategory nodeTypeCategory = configService.getNodeTypeCategory(node.getNodeTypeCategory());
            node.setNodeImage(nodeTypeCategory.getDefaultImage());

            // get business object attributes
            // if it is an object preview only get preview attributes otherwise all defined
            List<NodeTypeAttribute> nodeTypeAttributes = new ArrayList<NodeTypeAttribute>();
            if (nodePreview) {
                nodeTypeAttributes = configObject.getPreviewAndFilterableConfigurationNodeAttributes();
            } else {
                nodeTypeAttributes = configObject.getNodeTypeAttributes();
            }
            ArrayList<QueryResult> queryResultList = this.getNodeAttributes(nodeTypeAttributes, id, nodePreview);

            String nodeTypeTitleAttrName = configObject.getNodeTypeTitle();
            // load attribute key and value into business object
            node = loadAttributes(node, nodeTypeAttributes, nodeTypeTitleAttrName, queryResultList, nodePreview);

            return node;
        } else {
            System.out.println("No configuration document found for id: " + id);
        }
        return null;
    }

    /*
     * returns list of business object attributes
     */
    private ArrayList<QueryResult> getNodeAttributes(List<NodeTypeAttribute> nodeTypeAttributes, String id, boolean nodePreview) {

        // cache result to prevent redundant queries
        ArrayList<QueryResult> queryResultList = new ArrayList<QueryResult>();

        // get value for each object attribute
        for (NodeTypeAttribute nodeTypeAttribute : nodeTypeAttributes) {

            String datasource = nodeTypeAttribute.getDatasource();
            String query = nodeTypeAttribute.getQuery();
            QueryResult queryResult = new QueryResult(datasource, query);

            // check if query result is already cached
            JsonObject jsonQueryResultObject = queryService.getQueryResult(queryResultList, queryResult);

            if (jsonQueryResultObject == null) {
                // get datasource configuration
                Datasource datasourceObject = queryService.getDatasourceObject(datasource);
                // get query
                Query queryObject = queryService.getQueryObject(query);
                jsonQueryResultObject = queryService.executeQuery(datasourceObject, queryObject, id);
                queryResult.setJsonObject(jsonQueryResultObject);
                queryResultList.add(queryResult);
            }
        }

        return queryResultList;
    }

    private Node loadAttributes(Node businessObject, List<NodeTypeAttribute> nodeTypeAttributes, String nodeTypeTitleAttrName, ArrayList<QueryResult> queryResultList, boolean nodePreview) {

        for (NodeTypeAttribute nodeTypeAttribute : nodeTypeAttributes) {
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
                // add whole nodeTypeAttribute Object with updated value
                businessObject.addAttribute(nodeTypeAttribute);
                //set business object title if attribute is configured as title
                String titleAttribute = nodeTypeTitleAttrName;
                if (titleAttribute.equals(name)) {
                    businessObject.setNodeTitle(value.getAsString());
                }
            } catch (NullPointerException npe) {
                System.out.println("Failed loading attribute: " + name + " from field: " + fieldname);
            }
        }

        return businessObject;
    }

    /**
     * Returns a lide of Nodes which are adjacent to the given one
     *
     * @param Node businessObject
     * @return List<Node>
     */
    public List<Node> getAdjacentNodes(Node businessObject) {
        List<Node> adjacentNodesList = new ArrayList<Node>();
        // determine peer query by source and target object type
        String sourceNodeType = businessObject.getNodeType();

        // execute queries for getting peer object IDs
        ArrayList<String> adjacentNodeIDs = this.getAdjacentNodeIDs(businessObject);

        for (String adjacentNodeId : adjacentNodeIDs) {
            // TODO set node type
            Node adjacentNode = this.getNode(adjacentNodeId, "", true);
            if (adjacentNode != null) {
                adjacentNodesList.add(adjacentNode);
            }
        }

        return adjacentNodesList;
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
    
    private ArrayList<NodeTypeEvent> getQueriesForEvents() {
    	return new ArrayList<NodeTypeEvent>();
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

            //replace attributes in query string with variable values
            String queryString = queryObject.getString();
            this.createAttributeList(queryObject, node);
            //System.out.println("QueryString after replacements: " + string);
            // replace query string
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
        // if first attribute is not of type Array(String)
        if (nodeTypeAttribute == null) {
            // create map with replacements
            Map<String, String> replaceAttributesMap = new HashMap<String, String>();
            for (String replaceAttributeKey : tokenList) {
                // get attribute value from business object
                String replaceAttributeValue = node.getAttributeValueAsString(replaceAttributeKey);
                //convert email to notes username
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

        return "";
    }

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
