package uniko.iwvi.fgbas.magoetz.sbo.objects;

import java.io.Serializable;

public class SortAttribute implements Serializable {

    private static final long serialVersionUID = 805731509510272843L;

    private boolean sortType;

    private String attributeName;

    private String datatype;

    public boolean isSortType() {
        return sortType;
    }

    public void setSortType(boolean sortType) {
        this.sortType = sortType;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

}
