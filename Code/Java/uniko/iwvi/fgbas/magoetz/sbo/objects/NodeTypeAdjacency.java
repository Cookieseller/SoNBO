package uniko.iwvi.fgbas.magoetz.sbo.objects;

public class NodeTypeAdjacency {
	
	private String sourceNode;
	
	private String targetNode;
	
	private String datasource;
	
	private String query;
	
	public String getSourceNode() {
		return sourceNode;
	}

	public void setSourceNode(String sourceNode) {
		this.sourceNode = sourceNode;
	}

	public String getTargetNode() {
		return targetNode;
	}

	public void setTargetNode(String targetNode) {
	}

	public String getDatasource() {
		return datasource;
	}

	public void setDatasource(String datasource) {
		this.datasource = datasource;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}
}
