package uniko.iwvi.fgbas.magoetz.sbo.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class Node implements Serializable {
	
	private static final long serialVersionUID = 805731509510272843L;
	
	private String id;
	
	private String nodeType;
	
	private String nodeTypeCategory;
	
	private String nodeTitle;

	private List<Node> allAdjacentNodeList;
	
	private List<Node> adjacentNodeList;
	
	private List<Node> filteredAdjacentNodeList;
	
	private List<String> nodeAdjacencies;
	
	private String nodeImage;
	
	private List<NodeTypeAttribute> attributeList = new ArrayList<NodeTypeAttribute>();
	
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

	public void setAllAdjacentNodeList(List<Node> allAdjacentNodeList) {
		this.allAdjacentNodeList = allAdjacentNodeList;
	}

	public List<Node> getAllAdjacentNodeList() {
		return allAdjacentNodeList;
	}

	public void setAdjacentNodeList(List<Node> adjacentNodeList) {
		this.adjacentNodeList = adjacentNodeList;
	}

	public List<Node> getAdjacentNodeList() {
		return adjacentNodeList;
	}

	public void setFilteredAdjacentNodeList(List<Node> filteredAdjacentNodeList) {
		this.filteredAdjacentNodeList = filteredAdjacentNodeList;
	}

	public List<Node> getFilteredAdjacentNodeList() {
		return filteredAdjacentNodeList;
	}

	public List<String> getNodeAdjacencies() {
		return nodeAdjacencies;
	}

	public void setNodeAdjacencies(List<String> nodeAdjacencies) {
		this.nodeAdjacencies = nodeAdjacencies;
	}

	public String getNodeTypeCategory() {
		return nodeTypeCategory;
	}

	public void setNodeTypeCategory(String nodeTypeCategory) {
		this.nodeTypeCategory = nodeTypeCategory;
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
