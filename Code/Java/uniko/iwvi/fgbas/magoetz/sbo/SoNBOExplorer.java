package uniko.iwvi.fgbas.magoetz.sbo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicLong;

import uniko.iwvi.fgbas.magoetz.sbo.objects.Filter;
import uniko.iwvi.fgbas.magoetz.sbo.objects.Node;
import uniko.iwvi.fgbas.magoetz.sbo.objects.NodeTypeAttribute;
import uniko.iwvi.fgbas.magoetz.sbo.objects.SortAttribute;
import uniko.iwvi.fgbas.magoetz.sbo.services.ConfigService;

public class SoNBOExplorer implements Serializable {
	
	private static final long serialVersionUID = 805731509510272843L;
	
	private List<Node> adjacentNodeList;
	
	private SortAttribute sortAttribute = new SortAttribute();
	
	private List<Filter> filters = new ArrayList<Filter>();
	
	private static AtomicLong idCounter = new AtomicLong();
	
	private ConfigService configService = new ConfigService();
	
	public void setAdjacentNodeList(List<Node> adjacentNodeList) {
		this.adjacentNodeList = adjacentNodeList;
	}

	public List<Node> getAdjacentNodeList() {
		return adjacentNodeList;
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
	
	/*
	 * returns vector list of (unique) adjacent nodes attributes (name and datatype)
	 */	
	public List<Vector<String>> getAdjacentNodeAttributes() {
		Set<Vector<String>> adjacentNodeAttributeNames = new HashSet<Vector<String>>();
		for(Node adjacentNode : this.adjacentNodeList) {
			List<NodeTypeAttribute> adjacentNodeAttributeList = adjacentNode.getAttributeList();
			for(NodeTypeAttribute adjacentNodeTypeAttribute : adjacentNodeAttributeList) {
				Vector<String> v = new Vector<String>();
				v.add(adjacentNodeTypeAttribute.getName());
				v.add(adjacentNodeTypeAttribute.getDatatype());
				adjacentNodeAttributeNames.add(v);
			}
		}
		return new ArrayList<Vector<String>>(adjacentNodeAttributeNames);
	}
	
	/*
	 * returns vector list of (unique) adjacent nodes attribute values of type string
	 */	
	public List<String> getAdjacentNodeAttributeValues(String attributeName, String attributDatatype) {
		Set<String> attributeValues = new HashSet<String>();
		for(Node adjacentNode : this.adjacentNodeList) {
			if(adjacentNode.containsAttributeOfType(attributeName, attributDatatype)) {
				attributeValues.add(adjacentNode.getAttributeValueAsString(attributeName));
			}
		}
		return new ArrayList<String>(attributeValues);
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
				adjacentNodeListFilteredByNodeType = this.adjacentNodeList;
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
	
	public List<Node> getSortedNodeList(List<Node> nodeList, SortAttribute sortAttribute) {
		
		return nodeList;
	}
	
	public List<Node> getFilteredNodeList(List<Node> nodeList) {
		
		List<Filter> filterList = this.filters;
		if(filterList.size() > 0) {
		Set<Node> filteredNodeSet = new HashSet<Node>();
		Set<Node> excludedNodeSet = new HashSet<Node>();
		for(Filter filter : filterList){
			List<String> attributeList = filter.getAttributeList();
			for(Node node : nodeList) {
				if(attributeList.size() > 0) {
					for(String attributeValue : attributeList) {
						if(node.containsAttributeOfTypeWithValue(filter.getAttributeName(), filter.getAttributeDatatype(), attributeValue)) {
							if(filter.isFilterType()) {
								filteredNodeSet.add(node);
							}else {
								excludedNodeSet.add(node);
							}
						}else if(!filter.isFilterType()){
							filteredNodeSet.add(node);
						}
					}
				}else {
					if(node.containsAttributeOfType(filter.getAttributeName(), filter.getAttributeDatatype())) {
						if(filter.isFilterType()) {
							filteredNodeSet.add(node);
						}else {
							excludedNodeSet.add(node);
						}
					}else if(!filter.isFilterType()){
						filteredNodeSet.add(node);
					}
				}
			}
		}
		filteredNodeSet.removeAll(excludedNodeSet);
		return new ArrayList<Node>(filteredNodeSet);
		}else {
			return nodeList;
		}
	}
	
	public void applySorting(boolean sortType, Vector<String> attributeVector) {
		SortAttribute sortAttribute = new SortAttribute();
		sortAttribute.setSortType(sortType);
		sortAttribute.setAttributeName(attributeVector.firstElement());
		sortAttribute.setDatatype(attributeVector.lastElement());
		this.sortAttribute = sortAttribute;
	}
	
	public void addFilter(String filterType, String attributeName, String attributeDatatype, List<String> attributeList) {
		String filterId = String.valueOf(this.idCounter.getAndIncrement());
		Filter filter = new Filter(filterId);
		filter.setFilterType(Boolean.valueOf(filterType));
		filter.setAttributeName(attributeName);
		filter.setAttributeDatatype(attributeDatatype);
		filter.setAttributeList(attributeList);
		this.filters.add(filter);
	}
	
	public HashMap<String, String> getFilterList() {
		HashMap<String, String> filterList = new HashMap<String, String>();
		for(Filter filter : this.filters) {
			filterList.put(filter.getId(), filter.toString());
		}
		return filterList;
	}
	
	public void removeFilter(String id) {
		Filter filterToRemove = null;
		for(Filter filter : this.filters) {
			if(filter.getId().equals(id)) {
				filterToRemove = filter;
			}
		}
		if(filterToRemove != null) {
			this.filters.remove(filterToRemove);
		}
	}

	public List<Filter> getFilters() {
		return filters;
	}

	public void setFilters(List<Filter> filters) {
		this.filters = filters;
	}
	
	public List<Node> getfilterAndOrderedNodeList(List<Node> nodeList) {
		// filter node list
		return this.getFilteredNodeList(nodeList);
	}

}
