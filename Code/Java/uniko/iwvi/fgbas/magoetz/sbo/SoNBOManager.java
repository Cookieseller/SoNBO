package uniko.iwvi.fgbas.magoetz.sbo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Vector;

import javax.faces.context.FacesContext;

import uniko.iwvi.fgbas.magoetz.sbo.objects.Filter;
import uniko.iwvi.fgbas.magoetz.sbo.objects.Node;
import uniko.iwvi.fgbas.magoetz.sbo.objects.NodeTypeAttribute;
import uniko.iwvi.fgbas.magoetz.sbo.objects.SortAttribute;
import uniko.iwvi.fgbas.magoetz.sbo.services.ConfigService;
import uniko.iwvi.fgbas.magoetz.sbo.services.ConnectionsService;
import uniko.iwvi.fgbas.magoetz.sbo.services.NodeService;
import uniko.iwvi.fgbas.magoetz.sbo.services.ui.FilterService;
import uniko.iwvi.fgbas.magoetz.sbo.util.Texts;
import uniko.iwvi.fgbas.magoetz.sbo.util.Utilities;

/**
 * SoNBOManager is a view bean, implementing the backend interface for the main XPage.
 * 
 * @author Flemming
 * 
 */
public class SoNBOManager implements Serializable {

    private static final long serialVersionUID = 1L;

    private String objectId = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");

    private String nodeType = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("nodeType");

    private String nodeTypeCategoryName = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("nodeTypeCategory");

    public Node selectedNode;

    private List<Node> adjacentNodeList;

    private SortAttribute sortAttribute;

    public final FilterService filterService = new FilterService();

    private ConfigService configService = new ConfigService();

    private Texts texts;

    /**
     * Set the language, the initial node and load its adjacencies
     *
     * @param locale
     * @throws Exception
     */
    public void init(Locale locale) throws Exception {

        if (objectId == null) {
            ConnectionsService connectionsService = new ConnectionsService("connectionsSSO");
            objectId = connectionsService.getUserEmail();
        }

        if (nodeTypeCategoryName == null) {
            nodeTypeCategoryName = "Personen";
        }

        if (nodeType == null) {
            nodeType = "Mitarbeiter";
        }

        texts                   = new Texts(locale);
        NodeService nodeService = new NodeService();
        selectedNode            = nodeService.getNode(objectId, nodeType, false);
        adjacentNodeList        = nodeService.getAdjacentNodes(selectedNode);
    }
    
    /**
     * Get all adjacencies to a node by the given type. NodeTypeCategory all or empty means all adjacencies
     * regardless of type.
     * 
     * @param nodeTypeCategory
     * @return
     */
    public List<String> getNodeAdjacencyNamesByCategory(String nodeTypeCategory) {
        List<String> nodeAdjacencies = new ArrayList<String>();
        
        if (nodeTypeCategory.equals("all") || nodeTypeCategory.equals("")) {
            for (String nodeTypeCategoryName : selectedNode.getNodeTypeCategories()) {
                List<String> nodeTypes = configService.getAllNodeTypeNamesByCategory(nodeTypeCategoryName);
                nodeAdjacencies.addAll(nodeTypes);
            }
        } else {
            nodeAdjacencies = configService.getAllNodeTypeNamesByCategory(nodeTypeCategory);
        }

        return nodeAdjacencies;
    }

    /**
     * Load all attributes for adjacent nodes
     *
     * @return
     */
    private List<NodeTypeAttribute> getAdjacentNodeAttributes() {
        List<NodeTypeAttribute> adjacentNodesAttributes = new ArrayList<NodeTypeAttribute>();

        for (Node adjacentNode : this.adjacentNodeList) {
            List<NodeTypeAttribute> adjacentNodeAttributeList = adjacentNode.getAttributeList();
            adjacentNodesAttributes.addAll(adjacentNodeAttributeList);
        }
        return adjacentNodesAttributes;
    }

    /**
     * TODO rename, we don't load the actual attributes but a set of vectors containing strings of attributes
     *
     * Returns vector list of (unique) adjacent nodes attributes (attributeName translated)
     *
     * @param locale
     * @return
     */
    public List<Vector<String>> getAdjacentNodeAttributes(Locale locale) {

        Set<Vector<String>> adjacentNodeAttributeNames = new HashSet<Vector<String>>();
        List<NodeTypeAttribute> attributeList = getAdjacentNodeAttributes();

        for (NodeTypeAttribute attribute : attributeList) {
            adjacentNodeAttributeNames.add(attribute.getAttributesForPreview(locale));
        }

        List<Vector<String>> adjacentNodeAttributeList = new ArrayList<Vector<String>>(adjacentNodeAttributeNames);
        return sortVectorList(adjacentNodeAttributeList, 2);
    }

    /**
     * Returns a list of vectors of (unique) adjacent node attributes, which are filterable (attributeName translated)
     *
     * @param locale
     * @return
     */
    public List<Vector<String>> getAdjacentNodeFilterableAttributes(Locale locale) {
        Set<Vector<String>> adjacentNodeAttributeNames = new HashSet<Vector<String>>();
        List<NodeTypeAttribute> attributeList = getAdjacentNodeAttributes();

        for (NodeTypeAttribute attribute : attributeList) {
            if (attribute.isFilterable()) {
                adjacentNodeAttributeNames.add(attribute.getAttributesForPreview(locale));
            }
        }

        List<Vector<String>> adjacentNodeAttributeList = new ArrayList<Vector<String>>(adjacentNodeAttributeNames);

        return sortVectorList(adjacentNodeAttributeList, 2);
    }

    /**
     * Sorts list of vectors alphabetically after specified vector item (int) and returns list
     *
     * @param vectorSet
     * @param sortItem
     * @return
     */
    public List<Vector<String>> sortVectorList(List<Vector<String>> vectorSet, final int sortItem) {
        List<Vector<String>> list = new ArrayList<Vector<String>>(vectorSet);
        Collections.sort(list, new Comparator<Vector<String>>() {
            public int compare(Vector<String> v0, Vector<String> v1) {
                String i0 = v0.get(sortItem);
                String i1 = v1.get(sortItem);
                int comparison = i0.compareTo(i1);
                return comparison;
            }
        });
        return list;
    }

    /**
     * Returns vector list of (unique) adjacent nodes attribute values of type string
     *
     * @param attributeName
     * @param attributDatatype
     * @return
     */
    public List<String> getAdjacentNodeAttributeValues(String attributeName, String attributDatatype) {
        Set<String> attributeValues = new HashSet<String>();
        for (Node adjacentNode : this.adjacentNodeList) {
            String attributeValue = adjacentNode.getAttributeValueAsString(attributeName);
            if (attributeValue != null) {
                attributeValues.add(attributeValue);
            }
        }
        return new ArrayList<String>(attributeValues);
    }

    /**
     * Filtered by NodeTypeCategory
     *
     * @param nodeTypeCategory
     * @return
     */
    public List<Node> getAdjacentNodeListFilteredByCategory(String nodeTypeCategory) {

        List<Node> adjacentNodeListFilteredByCategory = new ArrayList<Node>();

        if (nodeTypeCategory.equals("all") || nodeTypeCategory.equals("")) {
            return this.adjacentNodeList;
        } else {
            for (Node adjacentNode : adjacentNodeList) {
                if (adjacentNode.getNodeTypeCategory().equals(nodeTypeCategory)) {
                    adjacentNodeListFilteredByCategory.add(adjacentNode);
                }
            }
        }
        return adjacentNodeListFilteredByCategory;
    }

    /**
     * Filtered by NodeType
     *
     * @param nodeTypeCategory
     * @param nodeType
     * @return
     * @throws Exception
     */
    public List<Node> getAdjacentNodeListFilteredByNodeType(String nodeTypeCategory, String nodeType) throws Exception {

        List<Node> adjacentNodeListFilteredByNodeType = new ArrayList<Node>();
        boolean nodeTypeAll         = nodeType.equals("all") || nodeType.equals("");
        boolean nodeTypeCategoryAll = nodeTypeCategory.equals("all") || nodeTypeCategory.equals("");

        if (nodeTypeCategoryAll && nodeTypeAll) {
            return adjacentNodeList;
        }

        if (!nodeTypeCategoryAll && nodeTypeAll) {
            List<String> nodeTypes = this.configService.getAllNodeTypeNamesByCategory(nodeTypeCategory);

            for (Node adjacentNode : adjacentNodeList) {
                if (nodeTypes.contains(adjacentNode.getNodeType())) {
                    adjacentNodeListFilteredByNodeType.add(adjacentNode);
                }
            }
        } else {
            for (Node adjacentNode : adjacentNodeList) {
                if (adjacentNode.getNodeType().equals(nodeType)) {
                    adjacentNodeListFilteredByNodeType.add(adjacentNode);
                }
            }
        }

        return adjacentNodeListFilteredByNodeType;
    }

    /**
     *
     * @param nodeList
     * @param sortAttribute
     * @return
     */
    public List<Node> getSortedNodeList(List<Node> nodeList, final SortAttribute sortAttribute) {

        Collections.sort(nodeList, new Comparator<Node>() {
            public int compare(Node n0, Node n1) {
                int comparison = n0.compareByAttribute(n1, sortAttribute);
                return comparison;
            }
        });
        // if sortType is descending
        if (!sortAttribute.isSortType()) {
            Collections.reverse(nodeList);
        }

        return nodeList;
    }

    /**
     *
     * @param nodeList
     * @return
     */
    public List<Node> getFilteredNodeList(List<Node> nodeList) {

    	List<Filter> filters = filterService.getFilters();
        if (filters.size() <= 0) {
            return nodeList;
        }

        Set<Node> filteredNodeSet = new HashSet<Node>();
        Set<Node> excludedNodeSet = new HashSet<Node>();
        for (Filter filter : filters) {
            List<String> attributeList = filter.getAttributeList();
            for (Node node : nodeList) {
                if (attributeList.size() > 0) {
                    for (String attributeValue : attributeList) {
                        if (node.containsAttributeOfTypeWithValue(filter.getAttributeName(), filter.getAttributeDatatype(), attributeValue)) {
                            if (filter.isFilterType()) {
                                filteredNodeSet.add(node);
                            } else {
                                excludedNodeSet.add(node);
                            }
                        } else if (!filter.isFilterType()) {
                            filteredNodeSet.add(node);
                        }
                    }
                } else {
                    if (node.containsAttributeOfType(filter.getAttributeName(), filter.getAttributeDatatype())) {
                        if (filter.isFilterType()) {
                            filteredNodeSet.add(node);
                        } else {
                            excludedNodeSet.add(node);
                        }
                    } else if (!filter.isFilterType()) {
                        filteredNodeSet.add(node);
                    }
                }
            }
        }
        filteredNodeSet.removeAll(excludedNodeSet);
        return new ArrayList<Node>(filteredNodeSet);
    }

    /**
     *
     * @param sortType
     * @param attributeVector
     */
    public void applySorting(boolean sortType, Vector<String> attributeVector) {
        SortAttribute sortAttribute = new SortAttribute();
        sortAttribute.setSortType(sortType);
        sortAttribute.setAttributeName(attributeVector.firstElement());
        sortAttribute.setDatatype(attributeVector.lastElement());
        this.sortAttribute = sortAttribute;
    }

    /**
     * Returns a list of all adjacent nodes, based on the currently selected node
     *
     * @return
     */
    public List<Node> getAdjacentNodeList() {
        return adjacentNodeList;
    }

    /**
     *
     * @param nodeList
     * @return
     */
    public List<Node> getfilterAndSortedNodeList(List<Node> nodeList) {
        List<Node> filteredAndSortedList = this.getFilteredNodeList(nodeList);
        if (this.sortAttribute != null) {
            filteredAndSortedList = this.getSortedNodeList(filteredAndSortedList, this.sortAttribute);
        }
        return filteredAndSortedList;
    }

    /**
     *
     * @return
     */
    public ResourceBundle getTranslationBundle() {
        return texts.getBundle();
    }

    /**
     *
     * @param maxEntries
     * @param nodeType
     * @param nodeID
     * @return
     */
    public Map<String, String> getActivityStreamEntries(int maxEntries, String nodeType, String nodeID) {
    	NodeService nodeService = new NodeService();
        Node node = nodeService.getNode(nodeID, nodeType, false);
        
        Map<String, String> events = nodeService.getEventsForNode(node);
        return events;
    }
}
