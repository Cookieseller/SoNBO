package uniko.iwvi.fgbas.magoetz.sbo.services.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicLong;

import uniko.iwvi.fgbas.magoetz.sbo.objects.Filter;
import uniko.iwvi.fgbas.magoetz.sbo.objects.Node;
import uniko.iwvi.fgbas.magoetz.sbo.services.NodeService;

public class FilterService implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private AtomicLong idCounter = new AtomicLong();
	private final List<Filter> filters = new ArrayList<Filter>();
	
	/**
	 * 
	 * @return
	 */
	public List<Filter> getFilters() {
		return filters;
	}

	/**
	 * Check whether any filters are set
	 *
	 * @return
	 */
	public boolean hasFilters() {
		return filters.size() > 0;
	}

	/**
	 * 
	 * @param filterType
	 * @param attributeName
	 * @param attributeDatatype
	 * @param attributeList
	 */
	public void add(String filterType, String attributeName, String attributeDatatype, List<String> attributeList) {
		String filterId = String.valueOf(idCounter.getAndIncrement());
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
	public void remove(String id) {
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
	 * Return all selectable values for a given Attribute.
	 * Selectable values are determined by the attribute values of adjacent nodes
	 * 
	 * @return
	 */
	public List<String> getValuesForAttribute(Node node, String attributeName, String attributDatatype) {
		AdjacencyService adjacencyService = new AdjacencyService();

		List<Node> adjacentNodes = adjacencyService.getAdjacentNodeList(node);

		Set<String> attributeValues = new HashSet<String>();
		for (Node adjacentNode : adjacentNodes) {
			String attributeValue = adjacentNode.getAttributeValueAsString(attributeName);
			if (attributeValue != null) {
				attributeValues.add(attributeValue);
			}
		}
		return new ArrayList<String>(attributeValues);
	}
}