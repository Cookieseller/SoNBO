package uniko.iwvi.fgbas.magoetz.sbo.objects;

import java.io.Serializable;
import java.util.ArrayList;

public class NodeTypeCategory implements Serializable {

    private static final long serialVersionUID = 805731509510272843L;

    private String name;

    private String nameDE;

    private String nameEN;

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

    public void setNameDE(String nameDE) {
        this.nameDE = nameDE;
    }

    public String getNameDE() {
        return nameDE;
    }

    public void setNameEN(String nameEN) {
        this.nameEN = nameEN;
    }

    public String getNameEN() {
        return nameEN;
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
