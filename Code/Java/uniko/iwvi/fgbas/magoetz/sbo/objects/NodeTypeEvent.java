package uniko.iwvi.fgbas.magoetz.sbo.objects;

public class NodeTypeEvent {

    private String id;

    private String nodeType;

    private String datasource;

    private String query;
    
    private String displayText;
    
    private String dateField;

    public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}

	public String getNodeType() {
		return nodeType;
	}

	public void setDatasource(String datasource) {
		this.datasource = datasource;
	}

	public String getDatasource() {
		return datasource;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getQuery() {
		return query;
	}

	public void setDisplayText(String displayText) {
		this.displayText = displayText;
	}

	public String getDisplayText() {
		return displayText;
	}

	public void setDateField(String dateField) {
		this.dateField = dateField;
	}

	public String getDateField() {
		return dateField;
	}
}
