package uniko.iwvi.fgbas.magoetz.sbo.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import uniko.iwvi.fgbas.magoetz.sbo.services.ConfigService;

public class Node implements Serializable {
	
	private static final long serialVersionUID = 805731509510272843L;
	
	private String id;
	
	private String nodeType;
	
	private String nodeTypeCategory;
	
	private List<String> nodeTypeCategories;
	
	private String nodeTitle;
	
	private List<Node> adjacentNodeList;
	
	private String nodeImage;
	
	private List<NodeTypeAttribute> attributeList = new ArrayList<NodeTypeAttribute>();
	
	private ConfigService configService = new ConfigService();
	
	/*
	 * returns key value pairs for displayfield
	 */
	public HashMap<String, String> getAttributeListForDisplayfield(int displayfieldnumber) {
		HashMap<String, String> attributeListForDisplayfield = new HashMap<String, String>();
		for(NodeTypeAttribute nodeTypeAttribute : attributeList) {
			if(nodeTypeAttribute.getDisplayfield() == displayfieldnumber) {
				String attributeName = nodeTypeAttribute.getName();
				String attributeValue = nodeTypeAttribute.getValueAsString();
				attributeListForDisplayfield.put(attributeName, attributeValue);
			}
		}
		return attributeListForDisplayfield;
	}
	
	public <T> T getAttributeValue(String key) {
		Object value = null;
		for(NodeTypeAttribute nodeTypeAttribute : attributeList) {
			if(nodeTypeAttribute.getName().equals(key)) {
				// TODO change to getValue if it must not be a String
				value = nodeTypeAttribute.getValue();
			}
		}
		return (T) value;
	}
	
	public <T> T getAttributeValueAsString(String key) {
		Object value = null;
		for(NodeTypeAttribute nodeTypeAttribute : attributeList) {
			if(nodeTypeAttribute.getName().equals(key)) {
				// TODO change to getValue if it must not be a String
				value = nodeTypeAttribute.getValueAsString();
			}
		}
		return (T) value;
	}

	public boolean containsAttribute(String key, String value) {
		for(NodeTypeAttribute nodeTypeAttribute : attributeList) {
			// TODO String ist expected (change to getValue)
			if(nodeTypeAttribute.getName().equals(key) && nodeTypeAttribute.getValueAsString().equals(value)) {
				return true;
			}
		}
		return false;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public String getNodeType() {
		return nodeType;
	}

	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}

	public String getNodeTitle() {
		return nodeTitle;
	}

	public void setNodeTitle(String nodeTitle) {
		this.nodeTitle = nodeTitle;
	}

	public void setAdjacentNodeList(List<Node> adjacentNodeList) {
		this.adjacentNodeList = adjacentNodeList;
	}

	public List<Node> getAdjacentNodeList() {
		return adjacentNodeList;
	}
	
	/*
	 * filtered by NodeTypeCategory
	 */
	public List<Node> getAdjacentNodeListFilteredByCategory(String nodeTypeCategory) {
		
		List<Node> adjacentNodeListFilteredByCategory = new ArrayList<Node>();
		
		if(nodeTypeCategory.equals("all") || nodeTypeCategory.equals("")) {
			return this.adjacentNodeList;
		}else {
			for(Node adjacentNode : adjacentNodeList) {
				if(adjacentNode.getNodeTypeCategory().equals(nodeTypeCategory)) {
					adjacentNodeListFilteredByCategory.add(adjacentNode);
				}
			}
		}
		return adjacentNodeListFilteredByCategory;
	}

	/*
	 * filtered by NodeType
	 */
	public List<Node> getAdjacentNodeListFilteredByNodeType(String nodeTypeCategory, String nodeType) {
		
		List<Node> adjacentNodeListFilteredByNodeType = new ArrayList<Node>();		
		
		if((nodeTypeCategory.equals("all") || nodeTypeCategory.equals(""))) {
			// category all and nodeType all
			if(nodeType.equals("all") || nodeType.equals("")) {
				return this.adjacentNodeList;
			// category all and specific nodeType
			}else {
				for(Node adjacentNode : adjacentNodeList) {
					if(adjacentNode.getNodeType().equals(nodeType)) {
						adjacentNodeListFilteredByNodeType.add(adjacentNode);
					}
				}
			}
		}else {
			// category specific and nodeType all
			if(nodeType.equals("all") || nodeType.equals("")) {
				List<String> nodeTypes = this.configService.getAllNodeTypeNamesByCategory(nodeTypeCategory);
				
				for(String nodeTypeName : nodeTypes) {
					for(Node adjacentNode : adjacentNodeList) {
						if(adjacentNode.getNodeType().equals(nodeTypeName)) {
							adjacentNodeListFilteredByNodeType.add(adjacentNode);
						}
					}
				}
			// category specific and nodeType specific
			}else {
				for(Node adjacentNode : adjacentNodeList) {
					if(adjacentNode.getNodeType().equals(nodeType)) {
						adjacentNodeListFilteredByNodeType.add(adjacentNode);
					}
				}
			}
		}
		
		return adjacentNodeListFilteredByNodeType;
	}
	
	public List<String> getNodeAdjacencyNamesByCategory(String nodeTypeCategory) {
		List<String> nodeAdjacencies = new ArrayList<String>();
		if((nodeTypeCategory.equals("all") || nodeTypeCategory.equals(""))) {
			for(String nodeTypeCategoryName : this.nodeTypeCategories) {
				List<String> nodeTypes = this.configService.getAllNodeTypeNamesByCategory(nodeTypeCategoryName);
				nodeAdjacencies.addAll(nodeTypes);
			}
		}else {
			nodeAdjacencies = this.configService.getAllNodeTypeNamesByCategory(nodeTypeCategory);
		}
		
		return nodeAdjacencies;
	}

	public String getNodeTypeCategory() {
		return nodeTypeCategory;
	}

	public void setNodeTypeCategory(String nodeTypeCategory) {
		this.nodeTypeCategory = nodeTypeCategory;
	}

	public void setNodeTypeCategories(List<String> nodeTypeCategories) {
		this.nodeTypeCategories = nodeTypeCategories;
	}

	public List<String> getNodeTypeCategories() {
		return nodeTypeCategories;
	}

	public void setNodeImage(String nodeImage) {
		this.nodeImage = nodeImage;
	}

	public String getNodeImage() {
		return nodeImage;
	}

	public void setAttributeList(List<NodeTypeAttribute> attributeList) {
		this.attributeList = attributeList;
	}

	public List<NodeTypeAttribute> getAttributeList() {
		return attributeList;
	}
	
	public void addAttribute(NodeTypeAttribute nodeTypeAttribute) {
		this.attributeList.add(nodeTypeAttribute);
	}
}
