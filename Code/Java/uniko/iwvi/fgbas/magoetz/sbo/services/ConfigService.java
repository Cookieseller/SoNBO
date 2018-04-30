package uniko.iwvi.fgbas.magoetz.sbo.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.NotesException;
import uniko.iwvi.fgbas.magoetz.sbo.database.NotesDB;
import uniko.iwvi.fgbas.magoetz.sbo.database.QueryService;
import uniko.iwvi.fgbas.magoetz.sbo.objects.Datasource;
import uniko.iwvi.fgbas.magoetz.sbo.objects.NodeType;
import uniko.iwvi.fgbas.magoetz.sbo.objects.NodeTypeAttribute;
import uniko.iwvi.fgbas.magoetz.sbo.objects.NodeTypeCategory;
import uniko.iwvi.fgbas.magoetz.sbo.objects.Query;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class ConfigService implements Serializable {

    private static final long serialVersionUID = 1L;

    private NotesDB configQueryService = new NotesDB();
    
    private QueryService queryService = new QueryService();

    /**
     * Return NodeType by it's name
     *
     * @param nodeTypeName
     * @return
     * 
     * @throws NotesException 
     */
    public NodeType getNodeTypeByName(String nodeTypeName) throws NotesException {

        // TODO: check if object type exists
    	JsonObject jsonNodeType 					= configQueryService.getJsonObject("nodeTypes", nodeTypeName, "nodeTypeJSON");
		ArrayList<JsonObject> jsonNodeAttributeList = configQueryService.getJsonObjects("nodeTypeAttributes", nodeTypeName, "attributeJSON");

        Gson gson 		  = new Gson();
        NodeType nodeType = gson.fromJson(jsonNodeType, NodeType.class);

        for (JsonObject jsonNodeAttribute : jsonNodeAttributeList) {
            NodeTypeAttribute attribute = gson.fromJson(jsonNodeAttribute, NodeTypeAttribute.class);
            nodeType.addConfigurationNodeAttribute(attribute);
        }

        return nodeType;
    }

    /**
     * 
     * @param nodeTypeCategoryName
     * @return
     * @throws Exception
     */
    public ArrayList<String> getAllNodeTypeNamesByCategory(String nodeTypeCategoryName) {
        ArrayList<String> adjacentNodeTypes 		 = new ArrayList<String>();
        String queryStringNodeTypes 				 = "FIELD nodeTypeCategory = " + nodeTypeCategoryName;
        DocumentCollection resultCollectionNodeTypes = configQueryService.ftSearchView("", queryStringNodeTypes, "nodeTypes");
        
        if (resultCollectionNodeTypes == null) {
        	System.out.println("Result of query " + queryStringNodeTypes + " is null.");
        	return adjacentNodeTypes;
        }

        try {
            for (int i = 1; i <= resultCollectionNodeTypes.getCount(); i++) {
                Document document = resultCollectionNodeTypes.getNthDocument(i);
                String nodeType   = document.getItemValueString("nodeTypeName");
                adjacentNodeTypes.add(nodeType);
            }
        } catch (NotesException e) {
            e.printStackTrace();
        }
        return adjacentNodeTypes;
    }

    /**
     * 
     * @param nodeTypeCategory
     * @return
     */
    public NodeTypeCategory getNodeTypeCategoryByName(String nodeTypeCategory) {
        String jsonFromDb = configQueryService.getFieldValue("", "", "nodeTypeCategories", nodeTypeCategory, "nodeTypeCategoryJSON");

        return new Gson().fromJson(jsonFromDb, NodeTypeCategory.class);
    }

    /**
     * Identifies a NodeType by the given ID
     * 
     * @param id
     * @return
     */
    public NodeType getNodeTypeById(String id) {

        NodeType resultNodeType = null;

        // lookup if node type was determined before
        String nodeTypeName = configQueryService.getFieldValue("", "", "nodeTypeIdMapping", id, "nodeTypeMappingType");
        if (nodeTypeName == null) {
            // if node type name was not cached yet try to determine it
            resultNodeType = this.determineNodeTypeById(id);
            // add entry to mapping table
            this.addNodeTypeId(id, resultNodeType);
        } else {
            if (nodeTypeName.equals("NOT DETERMINABLE")) {
                return null;
            } else {
        		try {
					resultNodeType = this.getNodeTypeByName(nodeTypeName);
				} catch (NotesException e) {
					e.printStackTrace();
					resultNodeType = null;
				}	
            }
        }

        return resultNodeType;
    }

	/**
	 * Function determines node type by searching for a matching db entry with id and (option) filter attribute
	 * 
	 * @param id
	 * @return
	 */
    private NodeType determineNodeTypeById(String id) {

        NodeType resultNodeType = null;
        //get all node type definitions
        ArrayList<String> nodeTypes = configQueryService.getColumnValues("nodeTypes", 0);
        ArrayList<NodeType> nodeTypeList = new ArrayList<NodeType>();
        for (String nodeType : nodeTypes) {
            try {
            	NodeType node = this.getNodeTypeByName(nodeType);
				nodeTypeList.add(node);
			} catch (NotesException e) {
				e.printStackTrace();
			}
        }
        // search for node with id and return node type
        for (NodeType nodeType : nodeTypeList) {
            // get datasource and query of id attribute
            NodeTypeAttribute nodeTypeAttribute = nodeType.getNodeTypeIdAttribute();
            if (nodeTypeAttribute != null) {
                //replace attributes in query string with variable values
                Datasource datasourceObject = configQueryService.getDatasourceObject(nodeTypeAttribute.getDatasource());
                Query queryObject = configQueryService.getQueryObject(nodeTypeAttribute.getQuery());
                // set fieldname of id attribute as key to be retrieved (FTSearch)
                ArrayList<String> idList = new ArrayList<String>();
                idList.add(nodeTypeAttribute.getFieldname());
                queryObject.setKey(idList);
                JsonObject json = queryService.executeQuery(datasourceObject, queryObject, id);
                if (json != null) {
                    // check if required key and value for node type exist
                    String key = configQueryService.getFieldValue("", "", "nodeTypes", nodeType.getNodeTypeName(), "nodeTypeKey");
                    String value = configQueryService.getFieldValue("", "", "nodeTypes", nodeType.getNodeTypeName(), "nodeTypeValue");
                    if (key != null && value != null && !value.equals("") && !key.equals("")) {
                        //check if json contains required key value pair
                        String jsonPrimitive = json.get(key).toString();
                        boolean contains = jsonPrimitive.contains(value);
                        if (contains) {
                            resultNodeType = nodeType;
                            break;
                        }
                    } else {
                        resultNodeType = nodeType;
                        break;
                    }
                }
            }
        }
        return resultNodeType;
    }

    /**
     * 
     * @param id
     * @param nodeType
     */
    private void addNodeTypeId(String id, NodeType nodeType) {
        // always add the id
        List<Vector<String>> dataList = new ArrayList<Vector<String>>();
        Vector<String> dataVector1 = new Vector<String>();
        dataVector1.add("nodeTypeMappingId");
        dataVector1.add(id);
        dataList.add(dataVector1);
        // add NodeType
        Vector<String> dataVector2 = new Vector<String>();
        dataVector2.add("nodeTypeMappingType");
        if (nodeType != null) {
            dataVector2.add(nodeType.getNodeTypeName());
        } else {
            dataVector2.add("NOT DETERMINABLE");
            dataList.add(dataVector2);
        }
        dataList.add(dataVector2);
        configQueryService.addEntry(dataList, "nodeTypeID");
    }
    
    /**
     * Returns all node type names
     *
     * @return
     */
    public List<String> getAllNodeTypeCategoryNames() {
        return configQueryService.getColumnValues("nodeTypeCategories", 0);
    }

    /**
     * Returns all localized node type names
     * 
     * @param locale
     * @return
     */
    public List<String> getAllNodeTypeCategoryNames(Locale locale) {
        if (locale.getLanguage().equals("de")) {
            return configQueryService.getColumnValues("nodeTypeCategories", 1);
        }

        return configQueryService.getColumnValues("nodeTypeCategories", 0);
    }
}
