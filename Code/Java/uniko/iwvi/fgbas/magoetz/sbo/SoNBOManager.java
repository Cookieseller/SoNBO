package uniko.iwvi.fgbas.magoetz.sbo;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;

import javax.faces.context.FacesContext;

import uniko.iwvi.fgbas.magoetz.sbo.objects.Filter;
import uniko.iwvi.fgbas.magoetz.sbo.objects.Node;
import uniko.iwvi.fgbas.magoetz.sbo.objects.NodeTypeCategory;
import uniko.iwvi.fgbas.magoetz.sbo.objects.SortAttribute;
import uniko.iwvi.fgbas.magoetz.sbo.services.ConfigService;
import uniko.iwvi.fgbas.magoetz.sbo.services.NodeService;
import uniko.iwvi.fgbas.magoetz.sbo.util.Utilities;

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
	
	public List<Filter> filters = new ArrayList<Filter>();
	
	public SortAttribute sortAttribute = new SortAttribute();
	
	private NodeService nodeService = new NodeService();
	
	public ConfigService configService = new ConfigService();
	
	private static AtomicLong idCounter = new AtomicLong();
	
	public void init(){
		System.out.println("NEW REQUEST FOR BUSINESS OBJECT");
		System.out.println("===============================");

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
		
		// TODO: execute tests if necessary
		Test test = new Test();
		//test.javaToJson();
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
}
