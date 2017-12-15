package uniko.iwvi.fgbas.magoetz.sbo;
import java.io.Serializable;
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
import uniko.iwvi.fgbas.magoetz.sbo.services.NodeService;
import uniko.iwvi.fgbas.magoetz.sbo.util.Texts;

/**
 * @author Flemming
 *
 */
public class SoNBOManager implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String objectId = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");
	
	private String nodeTypeCategoryName = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("nodeTypeCategory");
	
	private String nodeType = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("nodeType");
	
	public Node businessObject;
	
	private List<Node> adjacentNodeList;
	
	private SortAttribute sortAttribute;
	
	private List<Filter> filters = new ArrayList<Filter>();
	
	private AtomicLong idCounter = new AtomicLong();
	
	private ConfigService configService = new ConfigService();
	
	private NodeService nodeService = new NodeService();
	
	private ConnectionsService connectionsService = new ConnectionsService("connectionsSSO");
	
	private Texts texts;
	
	public void init(Locale locale){
		System.out.println("NEW REQUEST FOR BUSINESS OBJECT");
		System.out.println("===============================");
		
		// set my userEmail as objectId on invoke
		if(this.objectId == null) {
			this.objectId = connectionsService.getUserEmail();
		}
		
		// initialize translation
		this.texts = new Texts(locale);
		// if parameter nodeTypeCategoryName was not set get all 
		if(this.nodeTypeCategoryName == null) {
			this.nodeTypeCategoryName = "all";
		}
		// if parameter nodeType was not set get all 
		if(this.nodeType == null) {
			this.nodeType = "all";
		}
		
		// get business object
		this.businessObject = this.nodeService.getNode(objectId, false);
		this.loadAdjacentNodes(this.businessObject, locale);
	}

	private void loadAdjacentNodes(Node node, Locale locale) {
		List<Node> adjacentNodeCategoryList = new ArrayList<Node>();
		List<String> nodeTypeCategoryNames = configService.getAllNodeTypeCategoryNames();
		for(String adjacentNodeTypeCategory : nodeTypeCategoryNames) {
			List<Node> objects = nodeService.getAdjacentNodes(node, adjacentNodeTypeCategory);
			adjacentNodeCategoryList.addAll(objects);
			this.setAdjacentNodeList(adjacentNodeCategoryList);
		}
	}
	
	public void setAdjacentNodeList(List<Node> adjacentNodeList) {
		this.adjacentNodeList = adjacentNodeList;
	}

	public List<Node> getAdjacentNodeList() {
		return adjacentNodeList;
	}
	
	public List<String> getNodeAdjacencyNamesByCategory(String nodeTypeCategory) {
		List<String> nodeAdjacencies = new ArrayList<String>();
		if((nodeTypeCategory.equals("all") || nodeTypeCategory.equals(""))) {
			for(String nodeTypeCategoryName : this.businessObject.getNodeTypeCategories()) {
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
	 * returns vector list of (unique) adjacent nodes attributes (attributeName translated)
	 */	
	public List<Vector<String>> getAdjacentNodeAttributes(Locale locale) {
		Set<Vector<String>> adjacentNodeAttributeNames = new HashSet<Vector<String>>();
		for(Node adjacentNode : this.adjacentNodeList) {
			List<NodeTypeAttribute> adjacentNodeAttributeList = adjacentNode.getAttributeList();
			for(NodeTypeAttribute adjacentNodeTypeAttribute : adjacentNodeAttributeList) {
				Vector<String> v = new Vector<String>();
				v.add(adjacentNodeTypeAttribute.getName());
				v.add(adjacentNodeTypeAttribute.getDatatype());
				v.add(adjacentNodeTypeAttribute.getTranslatedName(locale));
				adjacentNodeAttributeNames.add(v);
			}
		}
		List<Vector<String>> adjacentNodeAttributeList = new ArrayList<Vector<String>>(adjacentNodeAttributeNames);
		return sortVectorList(adjacentNodeAttributeList, 2);
	}
	
	/*
	 * returns vector list of (unique) adjacent nodes attributes which are filterable (attributeName translated)
	 */	
	public List<Vector<String>> getAdjacentNodeFilterableAttributes(Locale locale) {
		Set<Vector<String>> adjacentNodeAttributeNames = new HashSet<Vector<String>>();
		for(Node adjacentNode : this.adjacentNodeList) {
			List<NodeTypeAttribute> adjacentNodeAttributeList = adjacentNode.getAttributeList();
			for(NodeTypeAttribute adjacentNodeTypeAttribute : adjacentNodeAttributeList) {
				if(adjacentNodeTypeAttribute.isFilterable()) {
					Vector<String> v = new Vector<String>();
					v.add(adjacentNodeTypeAttribute.getName());
					v.add(adjacentNodeTypeAttribute.getDatatype());
					v.add(adjacentNodeTypeAttribute.getTranslatedName(locale));
					adjacentNodeAttributeNames.add(v);
				}
			}
		}
		List<Vector<String>> adjacentNodeAttributeList = new ArrayList<Vector<String>>(adjacentNodeAttributeNames);
		return sortVectorList(adjacentNodeAttributeList, 2);
	}
	
	/*
	 * Sorts list of vectors alphabetically after specified vector item (int) and returns list
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
	
	public List<Node> getSortedNodeList(List<Node> nodeList, final SortAttribute sortAttribute) {
		
       Collections.sort(nodeList, new Comparator<Node>() {
			public int compare(Node n0, Node n1) {
				int comparison = n0.compareByAttribute(n1, sortAttribute);
				return comparison;
			}
        });
		// if sortType is descending
		if(!sortAttribute.isSortType()) {
			Collections.reverse(nodeList);
		}
		
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
	
	public List<Vector<String>> getFilterList() {
		List<Vector<String>> filterList = new ArrayList<Vector<String>>();
		for(Filter filter : this.filters) {
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
	
	public List<Node> getfilterAndSortedNodeList(List<Node> nodeList) {
		// filter node list
		List<Node> filteredAndSortedList = this.getFilteredNodeList(nodeList);
		// sort node list
		if(this.sortAttribute != null) {
			filteredAndSortedList = this.getSortedNodeList(filteredAndSortedList, this.sortAttribute);
		}
		return filteredAndSortedList;
	}
	
	public ResourceBundle getTranslationBundle() {
		return texts.getBundle();
	}
}
