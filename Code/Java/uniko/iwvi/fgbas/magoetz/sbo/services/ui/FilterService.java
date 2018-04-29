package uniko.iwvi.fgbas.magoetz.sbo.services.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicLong;

import uniko.iwvi.fgbas.magoetz.sbo.objects.Filter;

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
}