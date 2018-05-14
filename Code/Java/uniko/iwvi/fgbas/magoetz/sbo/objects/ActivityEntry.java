package uniko.iwvi.fgbas.magoetz.sbo.objects;

import java.io.Serializable;

public class ActivityEntry implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;

	private String title;

	private String message;

	private String receiver;

	private String date;

	private String type;

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDate() {
		return date;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}