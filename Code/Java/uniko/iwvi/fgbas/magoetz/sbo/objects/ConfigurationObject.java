package uniko.iwvi.fgbas.magoetz.sbo.objects;

import java.util.ArrayList;

public class ConfigurationObject {

	private String objectClass;
	
	private String[] peers;
	
	private ArrayList<ConfigurationObjectAttribute> configurationObjectAttributes = new ArrayList<ConfigurationObjectAttribute>();
	
	public String getObjectClass() {
		return objectClass;
	}

	public void setObjectClass(String objectType) {
		this.objectClass = objectType;
	}

	public String[] getPeers() {
		return peers;
	}

	public void setPeers(String[] peers) {
		this.peers = peers;
	}

	public ArrayList<ConfigurationObjectAttribute> getConfigurationObjectAttributes() {
		return configurationObjectAttributes;
	}

	public void setConfigurationObjectAttributes(
			ArrayList<ConfigurationObjectAttribute> configurationObjectAttributes) {
		this.configurationObjectAttributes = configurationObjectAttributes;
	}
	
	public void addConfigurationObjectAttribute(String datasource, String query, String fieldname, int displayfield) {
		ConfigurationObjectAttribute configObjAttr = new ConfigurationObjectAttribute(datasource, query, fieldname, displayfield);
		this.configurationObjectAttributes.add(configObjAttr);
	}
	
	public class ConfigurationObjectAttribute {
			
		private String datasource;
		private String query;
		private String fieldname;
		private int displayfield;
		
		private ConfigurationObjectAttribute(String datasource, String query, String fieldname, int displayfield) {
			this.datasource = datasource;
			this.setQuery(query);
			this.fieldname = fieldname;
			this.displayfield = displayfield;
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
}
