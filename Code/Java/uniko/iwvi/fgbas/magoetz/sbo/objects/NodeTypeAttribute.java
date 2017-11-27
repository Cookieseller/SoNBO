package uniko.iwvi.fgbas.magoetz.sbo.objects;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Locale;

import uniko.iwvi.fgbas.magoetz.sbo.services.QueryService;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;

public class NodeTypeAttribute implements Serializable {
	
	private static final long serialVersionUID = 805731509510272843L;
	
	private String name;
	
	private String nameDE;
	
	private String datasource;
	
	private String query;
	
	private String fieldname;
	
	private String datatype;
	
	private int displayfield;
	
	private boolean preview;
	
	private String value;
	
	private QueryService queryService = new QueryService();
	
	private NodeTypeAttribute() {
	}
	
	public String getName() {
		return name;
	}
	
	public String getName(Locale locale) {
		System.out.println("Language EQUALS: " + locale.getLanguage());
		System.out.println("nameDE: " + this.nameDE);
		if(locale.getLanguage().equals("de") && (!"".equals(this.nameDE))) {
			return nameDE;
		}else {
			return name;
		}
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNameDE(String nameDE) {
		this.nameDE = nameDE;
	}

	public String getNameDE() {
		return nameDE;
	}
	
	public String getTranslatedName(Locale locale) {
		if(locale.getLanguage().equals("de")) {
			return this.nameDE;
		}else {
			return this.name;
		}
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

	public void setValue(String value) {
		this.value = value;
	}

	@SuppressWarnings("unchecked")
	public <T> T getValue() {
		// TODO add all datatypes
		Gson gson = new Gson();
		JsonElement value = gson.fromJson(this.value, JsonElement.class);
		if(this.datatype.equals("String")) {
			return (T) value.getAsString();
		}else if(this.datatype.equals("Integer")) {
			return (T) value.getAsBigInteger();
		}else if(this.datatype.equals("Array(String)")) {
			return (T) value.getAsJsonArray();
		}else if(this.datatype.equals("NotesUsername")) {
			return (T) value.getAsString();
		}	
		return (T) value;
	}
	
	public String getValueAsString() {
		Gson gson = new Gson();
		JsonElement value = gson.fromJson(this.value, JsonElement.class);
		//TODO support arrays etc.
		try{
		if(this.datatype.equals("String")) {
			return value.getAsString();
		}else if(this.datatype.equals("Integer")) {
			return String.valueOf(value.getAsInt());
		}else if(this.datatype.equals("Array(String)")) {
			JsonArray jsonArray = value.getAsJsonArray();
			String stringValue = "";
			Iterator<JsonElement> it = jsonArray.iterator();
			while(it.hasNext()) {
				stringValue += it.next().getAsString();
				if(it.hasNext()) {
					stringValue += ", ";
				}
			}
			return stringValue;
		}else if(this.datatype.equals("NotesUsername")) {
			return queryService.getEmailByNotesUsername(value.getAsString());
		}
		}catch(NullPointerException npe) {
			System.out.println("NullPointerException for datatype: " + this.datatype);
		}
		return "";
	}
}
