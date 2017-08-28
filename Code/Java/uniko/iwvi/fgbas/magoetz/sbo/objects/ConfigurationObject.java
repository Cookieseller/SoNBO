package uniko.iwvi.fgbas.magoetz.sbo.objects;

import java.util.ArrayList;
import java.util.List;

public class ConfigurationObject {

	private String objectName;
	
	private String objectTitle;
	
	private String objectClass;
	
	private String relationships;
	
	private ArrayList<ConfigurationObjectAttribute> configurationObjectAttributes = new ArrayList<ConfigurationObjectAttribute>();
	
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public String getObjectName() {
		return objectName;
	}

	public String getObjectClass() {
		return objectClass;
	}

	public void setObjectClass(String objectClass) {
		this.objectClass = objectClass;
	}

	public void setRelationships(String relationships) {
		this.relationships = relationships;
	}

	public String getRelationships() {
		return relationships;
	}

	public ArrayList<ConfigurationObjectAttribute> getConfigurationObjectAttributes() {
		return configurationObjectAttributes;
	}

	public void setConfigurationObjectAttributes(
			ArrayList<ConfigurationObjectAttribute> configurationObjectAttributes) {
		this.configurationObjectAttributes = configurationObjectAttributes;
	}
	
	public void addConfigurationObjectAttribute(String name, String datasource, String query, String fieldname, int displayfield) {
		ConfigurationObjectAttribute configObjAttr = new ConfigurationObjectAttribute(name, datasource, query, fieldname, displayfield);
		this.configurationObjectAttributes.add(configObjAttr);
	}
	
	public class ConfigurationObjectAttribute {
			
		private String name;
		private String datasource;
		private String query;
		private String fieldname;
		private int displayfield;
		
		private ConfigurationObjectAttribute(String name, String datasource, String query, String fieldname, int displayfield) {
			this.name = name;
			this.datasource = datasource;
			this.setQuery(query);
			this.fieldname = fieldname;
			this.displayfield = displayfield;
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
	}

	public String getObjectTitle() {
		return objectTitle;
	}

	public void setObjectTitle(String objectTitle) {
		this.objectTitle = objectTitle;
	}
}
