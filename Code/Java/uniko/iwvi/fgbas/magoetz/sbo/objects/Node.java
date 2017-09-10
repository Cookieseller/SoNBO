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
	
	// String arrays containing attribute names for displaying in respective field
	// TODO: Maybe change to separate lists for fields and single list for attributes?
	private HashMap<String, String> attribteList1 = new HashMap<String, String>();
	private HashMap<String, String> attribteList2 = new HashMap<String, String>();
	private HashMap<String, String> attribteList3 = new HashMap<String, String>();
	private HashMap<String, String> attribteList4 = new HashMap<String, String>();
	
	public HashMap<String, String> getAttribteList1() {
		return attribteList1;
	}

	public void setAttribteList1(HashMap<String, String> attribteList1) {
		this.attribteList1 = attribteList1;
	}

	public HashMap<String, String> getAttribteList2() {
		return attribteList2;
	}

	public void setAttribteList2(HashMap<String, String> attribteList2) {
		this.attribteList2 = attribteList2;
	}

	public HashMap<String, String> getAttribteList3() {
		return attribteList3;
	}

	public void setAttribteList3(HashMap<String, String> attribteList3) {
		this.attribteList3 = attribteList3;
	}

	public HashMap<String, String> getAttribteList4() {
		return attribteList4;
	}

	public void setAttribteList4(HashMap<String, String> attribteList4) {
		this.attribteList4 = attribteList4;
	}

	public void addKeyValuePair(String key, String value, int displayfield) {
		
		switch(displayfield) {
			case 1:
				this.attribteList1.put(key, value);
			break;
			case 2:
				this.attribteList2.put(key, value);
			break;
			case 3:
				this.attribteList3.put(key, value);
			break;
			case 4:
				this.attribteList4.put(key, value);
			break;
		}
	}
	
	public String getAttributeValue(String key) {
		String attributeValue = this.attribteList1.get(key);
		if(attributeValue == null) {
			attributeValue = this.attribteList2.get(key);
			if(attributeValue == null) {
				attributeValue = this.attribteList3.get(key);
				if(attributeValue == null) {
					attributeValue = this.attribteList4.get(key);
				}
			}
		}
		return attributeValue;
	}
	
/*
	public boolean containsAttribute(String key, String value) {
		String attributeValue = this.attribteList1.get(key);
		if(attributeValue == null) {
			attributeValue = this.attribteList2.get(key);
			if(attributeValue == null) {
				attributeValue = this.attribteList3.get(key);
				if(attributeValue == null) {
					attributeValue = this.attribteList4.get(key);
				}
			}
		}
		return attributeValue.equals(value) ? true : false;
	}
	
*/
	public boolean containsAttribute(String key, String value) {
		boolean containsKeyValue = this.containsAttrSubstring(key, value, this.attribteList1);
		if(!containsKeyValue) {
			containsKeyValue = this.containsAttrSubstring(key, value, this.attribteList2);
			if(!containsKeyValue) {
				containsKeyValue = this.containsAttrSubstring(key, value, this.attribteList3);
				if(!containsKeyValue) {
					containsKeyValue = this.containsAttrSubstring(key, value, this.attribteList4);
				}
			}
		}
		return containsKeyValue;
	}
	
	// helper function - temporary TODO
	public boolean containsAttrSubstring(String key, String value, HashMap<String, String> hashMap) {
		boolean containsKeyValue = false;
		// prevent to work on the same hash map (undefined behaviour)
		HashMap anotherHashMap = (HashMap) hashMap.clone();
	    Iterator it = anotherHashMap.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<String, String> pair = (Map.Entry)it.next();
	        boolean containsValue = pair.getValue().contains(value);
	        boolean containsKey = false;
	        if(containsValue) {
	        	containsKey = pair.getKey().equals(key);
	        	if(containsKey && containsValue) {
	        		containsKeyValue = true;
	        		break;
	        	}
	        }
	        it.remove(); // avoids a ConcurrentModificationException
	    }
		return containsKeyValue;
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
}
