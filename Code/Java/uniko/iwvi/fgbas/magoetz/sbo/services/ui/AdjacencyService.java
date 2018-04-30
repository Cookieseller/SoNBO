package uniko.iwvi.fgbas.magoetz.sbo.services.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicLong;

import uniko.iwvi.fgbas.magoetz.sbo.objects.Filter;
import uniko.iwvi.fgbas.magoetz.sbo.objects.Node;
import uniko.iwvi.fgbas.magoetz.sbo.objects.NodeTypeAttribute;
import uniko.iwvi.fgbas.magoetz.sbo.services.ConfigService;
import uniko.iwvi.fgbas.magoetz.sbo.services.NodeService;
import uniko.iwvi.fgbas.magoetz.sbo.util.Utilities;

public class AdjacencyService implements Serializable {

	private static final long serialVersionUID = 1L;

	private final List<Node> adjacentNodeList = new ArrayList<Node>();
	
	private final ConfigService configService = new ConfigService();
	
	public AdjacencyService(Node node) {
		// TODO Auto-generated constructor stub
	}
	
    /**
     * Returns a list of all adjacent nodes, based on the currently selected node
     *
     * @return
     */
    public List<Node> getAdjacentNodes(Node node) {
    	NodeService nodeService = new NodeService();

        List<Node> adjacentNodes = nodeService.getAdjacentNodes(node);
        adjacentNodeList.addAll(adjacentNodes);
        
        return adjacentNodes;
    }
    
	/**
	 * Returns vector list of (unique) adjacent nodes attribute values of type string
	 * 
	 * @param attributeName
	 * @param attributDatatype
	 * @return
	 */
	public List<String> getAdjacentNodeAttributeValues(Node node, String attributeName, String attributDatatype) {
		Set<String> attributeValues = new HashSet<String>();
		for (Node adjacentNode : getAdjacentNodes(node)) {
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
		boolean nodeTypeAll = nodeType.equals("all") || nodeType.equals("");
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
	 * Get all adjacencies to a node by the given type. NodeTypeCategory all or empty means all adjacencies regardless
	 * of type.
	 * 
	 * @param nodeTypeCategory
	 * @return
	 */
	public List<String> getNodeAdjacencyNamesByCategory(String nodeTypeCategory) {
		List<String> nodeAdjacencies 	= new ArrayList<String>();
		ConfigService configService  	= new ConfigService();
		
		if (nodeTypeCategory.equals("all") || nodeTypeCategory.equals("")) {
			for (String nodeTypeCategoryName : configService.getAllNodeTypeCategoryNames()) {
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
}