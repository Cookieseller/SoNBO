package uniko.iwvi.fgbas.magoetz.sbo.objects;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class Filter implements Serializable {
	
	private static final long serialVersionUID = 805731509510272843L;
	
	private String id;
	
	private boolean filterType;

	private String attributeName;
	
	private String attributeDatatype;
	
	private List<String> attributeList;
	
	public Filter(String id) {
		this.id = id;
	}
	
	public String toString() {
		String type = filterType ? "" : "! ";
		String attributeMeta = attributeName + " - " + attributeDatatype + " - ";
		String attributes = "";
		for(int i = 0; i < this.attributeList.size(); i++) {
			if(i < this.attributeList.size() - 1) {
				attributes += this.attributeList.get(i) + ", ";
			}else {
				attributes += this.attributeList.get(i);
			}
		}
		return type + attributeMeta + attributeList;	
	}

	public String getId() {
		return id;
	}

	public void setFilterType(boolean filterType) {
		this.filterType = filterType;
	}

	public boolean isFilterType() {
		return filterType;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeDatatype(String attributeDatatype) {
		this.attributeDatatype = attributeDatatype;
	}

	public String getAttributeDatatype() {
		return attributeDatatype;
	}

	public void setAttributeList(List<String> attributeList) {
		this.attributeList = attributeList;
	}

	public List<String> getAttributeList() {
		return attributeList;
	}
}
