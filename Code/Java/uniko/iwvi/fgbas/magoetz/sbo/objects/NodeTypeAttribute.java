package uniko.iwvi.fgbas.magoetz.sbo.objects;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import uniko.iwvi.fgbas.magoetz.sbo.database.QueryService;
import uniko.iwvi.fgbas.magoetz.sbo.util.Utilities;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class NodeTypeAttribute implements Serializable {

    private static final long serialVersionUID = 805731509510272843L;

    private String name;

    private String nameDE;

    private String nameEN;

    private String datasource;

    private String query;

    private String fieldname;

    private String datatype;

    private int displayfield;

    private boolean filterable;

    private boolean preview;

    private String value;

    private QueryService queryService = new QueryService();

    private NodeTypeAttribute() {
    }

    public String getName() {
        return name;
    }

    public String getName(Locale locale) {
        if (locale.getLanguage().equals("de") && (!"".equals(this.nameDE))) {
            return nameDE;
        } else if (locale.getLanguage().contains("en") && (!"".equals(this.nameEN))) {
            return nameEN;
        } else {
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

    public void setNameEN(String nameEN) {
        this.nameEN = nameEN;
    }

    public String getNameEN() {
        return nameEN;
    }

    public String getTranslatedName(Locale locale) {
        if (locale.getLanguage().equals("de") && !"".equals(this.nameDE)) {
            return this.nameDE;
        } else {
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

    public boolean isFilterable() {
        return filterable;
    }

    public void setFilterable(boolean filterable) {
        this.filterable = filterable;
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
        if (this.datatype.equals("String")) {
            return (T) value.getAsString();
        } else if (this.datatype.equals("Integer")) {
            return (T) value.getAsBigInteger();
        } else if (this.datatype.equals("Array(String)")) {
            return (T) value.getAsJsonArray();
        } else if (this.datatype.equals("NotesUsername")) {
            return (T) value.getAsString();
        }
        return (T) value;
    }

    public String getValueAsString() {
        return this.value;
    }

    /**
     * Representation of a NodeTypeAttribute for rendering
     *
     * @param locale
     * @return
     */
    public Vector<String> getAttributesForPreview(Locale locale) {
        Vector<String> v = new Vector<String>();
        v.add(name);
        v.add(datatype);
        v.add(getTranslatedName(locale));

        return v;
    }
}
