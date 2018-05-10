package uniko.iwvi.fgbas.magoetz.sbo.objects;

import java.util.ArrayList;

public class Query {

    private String name;

    private String type;

    private String command;

    private String view;

    private ArrayList<String> key;

    private String keyValueReturnType;

    private String fieldname;

    private int columnNr;

    private String string;
    
    private String skip;

    private ArrayList<String> queryAttributes;

    public Query() {
    	key = new ArrayList<String>();
    	queryAttributes =new ArrayList<String>();
    }
    
    public Query(Query copy) {
    	this.name = copy.name;
    	this.type = copy.type;
    	this.command = copy.command;
    	this.view = copy.view;
    	this.key = new ArrayList<String>(copy.key);
    	this.keyValueReturnType = copy.keyValueReturnType;
    	this.fieldname = copy.fieldname;
    	this.columnNr = copy.columnNr;
    	this.string = copy.string;
    	this.queryAttributes = new ArrayList<String>(copy.queryAttributes);
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public ArrayList<String> getKey() {
        return key;
    }

    public void setKey(ArrayList<String> key) {
        this.key = key;
    }

    public void setKeyValueReturnType(String keyValueReturnType) {
        this.keyValueReturnType = keyValueReturnType;
    }

    public String getKeyValueReturnType() {
        return keyValueReturnType;
    }

    public String getFieldname() {
        return fieldname;
    }

    public void setFieldname(String fieldname) {
        this.fieldname = fieldname;
    }

    public int getColumnNr() {
        return columnNr;
    }

    public void setColumnNr(int columnNr) {
        this.columnNr = columnNr;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public ArrayList<String> getQueryAttributes() {
        return queryAttributes;
    }

    public void setQueryAttributes(ArrayList<String> attributes) {
        this.queryAttributes = attributes;
    }

	public void setSkip(String skip) {
		this.skip = skip;
	}

	public String getSkip() {
		return skip;
	}
}
