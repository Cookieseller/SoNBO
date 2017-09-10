package uniko.iwvi.fgbas.magoetz.sbo.objects;

import java.io.Serializable;
import java.util.ArrayList;

public class NodeTypeCategory {
	
	private String name;
	
	private String mainDatasource;
	
	private String mainQuery;
	
	private ArrayList<String> adjacentNodeTypeCategories;
	
	private String defaultImage;

	private String adjacencies;
	
	public NodeTypeCategory() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMainDatasource() {
		return mainDatasource;
	}

	public void setMainDatasource(String mainDatasource) {
		this.mainDatasource = mainDatasource;
	}

	public String getMainQuery() {
		return mainQuery;
	}

	public void setMainQuery(String mainQuery) {
		this.mainQuery = mainQuery;
	}

	public void setAdjacentNodeTypeCategories(
			ArrayList<String> adjacentNodeTypeCategories) {
		this.adjacentNodeTypeCategories = adjacentNodeTypeCategories;
	}

	public ArrayList<String> getAdjacentNodeTypeCategories() {
		return adjacentNodeTypeCategories;
	}

	public String getDefaultImage() {
		return defaultImage;
	}

	public void setDefaultImage(String defaultImage) {
		this.defaultImage = defaultImage;
	}

	public String getAdjacencies() {
		return adjacencies;
	}

	public void setAdjacencies(String adjacencies) {
		this.adjacencies = adjacencies;
	}
}
