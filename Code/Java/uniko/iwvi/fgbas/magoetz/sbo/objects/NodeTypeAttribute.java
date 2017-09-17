package uniko.iwvi.fgbas.magoetz.sbo.objects;

import java.io.Serializable;
import java.util.Iterator;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class NodeTypeAttribute implements Serializable {
	
	private static final long serialVersionUID = 805731509510272843L;
	
	private String name;
	
	private String datasource;
	
	private String query;
	
	private String fieldname;
	
	private String datatype;
	
	private int displayfield;
	
	private boolean preview;
	
	private transient JsonElement value;
	
	private NodeTypeAttribute() {
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDatasource() {
		return datasource;
	}
	
	public void setDatasource(String datasource) {
		this.datasource = datasource;
	}
	
	public String getFieldname() {
		return fieldname;
	}
	
	public void setFieldname(String fieldname) {
		this.fieldname = fieldname;
	}
	
	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}

	public String getDatatype() {
		return datatype;
	}

	public int getDisplayfield() {
		return displayfield;
	}
	
	public void setDisplayfield(int displayfield) {
		this.displayfield = displayfield;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getQuery() {
		return query;
	}

	public void setPreview(boolean preview) {
		this.preview = preview;
	}

	public boolean isPreview() {
		return preview;
	}

	public void setValue(JsonElement value) {
		this.value = value;
	}

	@SuppressWarnings("unchecked")
	public <T> T getValue() {
		// TODO add all datatypes
		if(this.datatype.equals("String")) {
			return (T) this.value.getAsString();
		}else if(this.datatype.equals("Integer")) {
			return (T) this.value.getAsBigInteger();
		}else if(this.datatype.equals("Array(String)")) {
			return (T) this.value.getAsJsonArray();
		}
		
		return (T) value;
	}
	
	public String getValueAsString() {
		//TODO support arrays etc.
		if(this.datatype.equals("String")) {
			return this.value.getAsString();
		}else if(this.datatype.equals("Integer")) {
			return String.valueOf(this.value.getAsInt());
		}else if(this.datatype.equals("Array(String)")) {
			JsonArray jsonArray = this.value.getAsJsonArray();
			String stringValue = "";
			Iterator<JsonElement> it = jsonArray.iterator();
			while(it.hasNext()) {
				stringValue += it.next().getAsString();
				if(it.hasNext()) {
					stringValue += ", ";
				}
			}
			return stringValue;
		}
		return this.value.toString();
	}
}
