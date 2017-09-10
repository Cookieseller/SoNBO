package uniko.iwvi.fgbas.magoetz.sbo.objects;

public class Attribute {
	
	private String name;
	
	private String datasource;
	
	private String query;
	
	private String fieldname;
	
	private int displayfield;
	
	private boolean preview;
	
	private Attribute() {
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
