package uniko.iwvi.fgbas.magoetz.sbo.objects;

import java.util.List;

public class Query {

	private String name;
	
	private String type;
	
	private String command;
	
	private String view;
	
	private List<String> key;
	
	private String keyValueReturnType;
	
	private String fieldname;
	
	private int columnNr;
	
	private String string;
	
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

	public List<String> getKey() {
		return key;
	}

	public void setKey(List<String> key) {
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
}
