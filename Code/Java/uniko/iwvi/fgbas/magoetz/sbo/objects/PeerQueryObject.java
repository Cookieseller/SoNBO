package uniko.iwvi.fgbas.magoetz.sbo.objects;

import com.google.gson.JsonObject;

public class PeerQueryObject {
	
	private String peerSourceObject;
	
	private String peerTargetObject;
	
	private String peerDatasource;
	
	private String peerQuery;

	public String getPeerSourceObject() {
		return peerSourceObject;
	}

	public void setPeerSourceObject(String peerSourceObject) {
		this.peerSourceObject = peerSourceObject;
	}

	public String getPeerTargetObject() {
		return peerTargetObject;
	}

	public void setPeerTargetObject(String peerTargetObject) {
		this.peerTargetObject = peerTargetObject;
	}

	public String getPeerDatasource() {
		return peerDatasource;
	}

	public void setPeerDatasource(String peerDatasource) {
		this.peerDatasource = peerDatasource;
	}

	public String getPeerQuery() {
		return peerQuery;
	}

	public void setPeerQuery(String peerQuery) {
		this.peerQuery = peerQuery;
	}
}
