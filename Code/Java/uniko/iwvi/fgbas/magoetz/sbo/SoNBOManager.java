package uniko.iwvi.fgbas.magoetz.sbo;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicLong;

import javax.faces.context.FacesContext;

import uniko.iwvi.fgbas.magoetz.sbo.objects.Filter;
import uniko.iwvi.fgbas.magoetz.sbo.objects.Node;
import uniko.iwvi.fgbas.magoetz.sbo.objects.NodeTypeAttribute;
import uniko.iwvi.fgbas.magoetz.sbo.objects.SortAttribute;
import uniko.iwvi.fgbas.magoetz.sbo.services.ConfigService;
import uniko.iwvi.fgbas.magoetz.sbo.services.ConnectionsService;
import uniko.iwvi.fgbas.magoetz.sbo.services.DatabaseService;
import uniko.iwvi.fgbas.magoetz.sbo.services.NodeService;
import uniko.iwvi.fgbas.magoetz.sbo.util.Texts;

/**
 * @author Flemming
 */
public class SoNBOManager implements Serializable {

    private static final long serialVersionUID = 1L;

    private String objectId = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");

    private String nodeType = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("nodeType");

    private String nodeTypeCategoryName = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("nodeTypeCategory");

    public Node selectedNode;

    private List<Node> adjacentNodeList;

    private SortAttribute sortAttribute;

    private List<Filter> filters = new ArrayList<Filter>();

    private AtomicLong idCounter = new AtomicLong();

    private ConfigService configService = new ConfigService();

    private DatabaseService databaseService = new DatabaseService();

    private ConnectionsService connectionsService = new ConnectionsService("connectionsSSO");

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
     * Returns a list of all adjacent nodes, based on the currently selected node
     *
     * @return
     */
    public List<Node> getAdjacentNodeList() {
        return adjacentNodeList;
    }

    /**
     *
     * @param nodeTypeCategory
     * @return
     * @throws Exception
     */
    public List<String> getNodeAdjacencyNamesByCategory(String nodeTypeCategory) throws Exception {
        List<String> nodeAdjacencies = new ArrayList<String>();
        if ((nodeTypeCategory.equals("all") || nodeTypeCategory.equals(""))) {
            for (String nodeTypeCategoryName : this.selectedNode.getNodeTypeCategories()) {
                List<String> nodeTypes = this.configService.getAllNodeTypeNamesByCategory(nodeTypeCategoryName);
                nodeAdjacencies.addAll(nodeTypes);
            }
        } else {
            nodeAdjacencies = this.configService.getAllNodeTypeNamesByCategory(nodeTypeCategory);
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
     * @TODO rename, we don't load the actual attributes but a set of vectors containing strings of attributes
     *
     * returns vector list of (unique) adjacent nodes attributes (attributeName translated)
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
     * Returns vector list of (unique) adjacent nodes attributes which are filterable (attributeName translated)
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
     * filtered by NodeTypeCategory
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
     * filtered by NodeType
     *
     * @param nodeTypeCategory
     * @param nodeType
     * @return
     * @throws Exception
     */
    public List<Node> getAdjacentNodeListFilteredByNodeType(String nodeTypeCategory, String nodeType) throws Exception {

        List<Node> adjacentNodeListFilteredByNodeType = new ArrayList<Node>();
        bool nodeTypeAll         = nodeType.equals("all") || nodeType.equals("");
        bool nodeTypeCategoryAll = nodeTypeCategory.equals("all") || nodeTypeCategory.equals("");

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
     *
     * @param filterType
     * @param attributeName
     * @param attributeDatatype
     * @param attributeList
     */
    public void addFilter(String filterType, String attributeName, String attributeDatatype, List<String> attributeList) {
        String filterId = String.valueOf(this.idCounter.getAndIncrement());
        Filter filter = new Filter(filterId);
        filter.setFilterType(Boolean.valueOf(filterType));
        filter.setAttributeName(attributeName);
        filter.setAttributeDatatype(attributeDatatype);
        filter.setAttributeList(attributeList);
        this.filters.add(filter);
    }

    /**
     *
     * @return
     */
    public List<Vector<String>> getFilterList() {
        List<Vector<String>> filterList = new ArrayList<Vector<String>>();
        for (Filter filter : this.filters) {
            Vector<String> vector = new Vector<String>();
            vector.add(filter.getId());
            vector.add(filter.getAttributeName());
            vector.add(filter.getAttributeDatatype());
            vector.add(filter.getAttributeListAsString());
            String filterType = filter.isFilterType() ? "" : "!";
            vector.add(filterType);
            filterList.add(vector);
        }
        return filterList;
    }

    /**
     *
     * @param id
     */
    public void removeFilter(String id) {
        Filter filterToRemove = null;
        for (Filter filter : this.filters) {
            if (filter.getId().equals(id)) {
                filterToRemove = filter;
            }
        }
        if (filterToRemove != null) {
            this.filters.remove(filterToRemove);
        }
    }

    /**
     *
     * @return
     */
    public List<Filter> getFilters() {
        return filters;
    }

    /**
     *
     * @param filters
     */
    public void setFilters(List<Filter> filters) {
        this.filters = filters;
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
    public List<Vector<AbstractMap.SimpleEntry<Integer, String>>> getActivityStreamEntries(int maxEntries, String nodeType, String nodeID) {
        //Node node = nodeService.getNode(nodeID, nodeType, false);

        return new ArrayList<Vector<AbstractMap.SimpleEntry<Integer, String>>>();
    }
}
