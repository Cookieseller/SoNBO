package uniko.iwvi.fgbas.magoetz.sbo.objects;

import java.util.ArrayList;
import java.util.List;

public class Configuration {

	private String nodeTypeName;
	
	private String nodeTypeTitle;
	
	private String nodeTypeCategory;
	
	private String adjacencies;
	
	private ArrayList<ConfigurationNodeAttribute> configurationNodeAttributes = new ArrayList<ConfigurationNodeAttribute>();
	
	public void setNodeTypeName(String nodeTypeName) {
		this.nodeTypeName = nodeTypeName;
	}

	public String getNodeTypeName() {
		return nodeTypeName;
	}

	public String getNodeTypeCategory() {
		return nodeTypeCategory;
	}

	public void setNodeTypeCategory(String nodeTypeCategory) {
		this.nodeTypeCategory = nodeTypeCategory;
	}

	public void setAdjacencies(String adjacencies) {
		this.adjacencies = adjacencies;
	}

	public String getAdjacencies() {
		return adjacencies;
	}

	public ArrayList<ConfigurationNodeAttribute> getConfigurationNodeAttributes() {
		return configurationNodeAttributes;
	}
	
	public ArrayList<ConfigurationNodeAttribute> getPreviewConfigurationNodeAttributes() {
		
		ArrayList<ConfigurationNodeAttribute> previewConfigNodeAttrList = new ArrayList<ConfigurationNodeAttribute>();
		for(ConfigurationNodeAttribute configNodeAttr : this.getConfigurationNodeAttributes()) {
			if(configNodeAttr.isPreview()) {
				previewConfigNodeAttrList.add(configNodeAttr);
			}
		}
		return previewConfigNodeAttrList;
	}

	public void setConfigurationNodeAttributes(
			ArrayList<ConfigurationNodeAttribute> configurationNodeAttributes) {
		this.configurationNodeAttributes = configurationNodeAttributes;
	}
	
	public void addConfigurationNodeAttribute(String name, String datasource, String query, String fieldname, int displayfield, boolean preview) {
		ConfigurationNodeAttribute configNdAttr = new ConfigurationNodeAttribute(name, datasource, query, fieldname, displayfield, preview);
		this.configurationNodeAttributes.add(configNdAttr);
	}
	
	public class ConfigurationNodeAttribute {
			
		private String name;
		private String datasource;
		private String query;
		private String fieldname;
		private int displayfield;
		private boolean preview;
		
		private ConfigurationNodeAttribute(String name, String datasource, String query, String fieldname, int displayfield, boolean preview) {
			this.name = name;
			this.datasource = datasource;
			this.setQuery(query);
			this.fieldname = fieldname;
			this.displayfield = displayfield;
			this.setPreview(preview);
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

		public void setPreview(boolean preview) {
			this.preview = preview;
		}

		public boolean isPreview() {
			return preview;
		}
	}

	public String getNodeTypeTitle() {
		return nodeTypeTitle;
	}

	public void setNodeTypeTitle(String nodeTypeTitle) {
		this.nodeTypeTitle = nodeTypeTitle;
	}
}
