package uniko.iwvi.fgbas.magoetz.sbo.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class BusinessObject implements Serializable {
	
	private static final long serialVersionUID = 805731509510272843L;
	
	private String objectId;
	
	private String objectName;
	
	private String objectClass;
	
	private String objectTitle;

	private List<String> objectPeers;

	private List<BusinessObject> peerObjectList;
	
	// String arrays containing attribute names for displaying in respective field	
	private HashMap<String, String> attribteList1 = new HashMap<String, String>();
	private HashMap<String, String> attribteList2 = new HashMap<String, String>();
	private HashMap<String, String> attribteList3 = new HashMap<String, String>();
	private HashMap<String, String> attribteList4 = new HashMap<String, String>();
	
	public HashMap<String, String> getAttribteList1() {
		return attribteList1;
	}

	public void setAttribteList1(HashMap<String, String> attribteList1) {
		this.attribteList1 = attribteList1;
	}

	public HashMap<String, String> getAttribteList2() {
		return attribteList2;
	}

	public void setAttribteList2(HashMap<String, String> attribteList2) {
		this.attribteList2 = attribteList2;
	}

	public HashMap<String, String> getAttribteList3() {
		return attribteList3;
	}

	public void setAttribteList3(HashMap<String, String> attribteList3) {
		this.attribteList3 = attribteList3;
	}

	public HashMap<String, String> getAttribteList4() {
		return attribteList4;
	}

	public void setAttribteList4(HashMap<String, String> attribteList4) {
		this.attribteList4 = attribteList4;
	}

	public void addKeyValuePair(String key, String value, int displayfield) {
		
		switch(displayfield) {
			case 1:
				this.attribteList1.put(key, value);
			break;
			case 2:
				this.attribteList2.put(key, value);
			break;
			case 3:
				this.attribteList3.put(key, value);
			break;
			case 4:
				this.attribteList4.put(key, value);
			break;
		}
	}
	
	public String getAttributeValue(String key) {
		String attributeValue = this.attribteList1.get(key);
		if(attributeValue == null) {
			attributeValue = this.attribteList2.get(key);
			if(attributeValue == null) {
				attributeValue = this.attribteList3.get(key);
				if(attributeValue == null) {
					attributeValue = this.attribteList4.get(key);
				}
			}
		}
		return attributeValue;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getObjectId() {
		return objectId;
	}
	
	public void setPeers(List<String> peerList) {
		this.objectPeers = peerList;
	}

	public List<String> getPeers() {
		return this.objectPeers;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectType) {
		this.objectName = objectType;
	}

	public String getObjectTitle() {
		return objectTitle;
	}

	public void setObjectTitle(String objectTitle) {
		this.objectTitle = objectTitle;
	}

	public void setPeerObjectList(List<BusinessObject> peerObjectList) {
		this.peerObjectList = peerObjectList;
	}

	public List<BusinessObject> getPeerObjectList() {
		return peerObjectList;
	}

	public String getObjectClass() {
		return objectClass;
	}

	public void setObjectClass(String objectClass) {
		this.objectClass = objectClass;
	}
}
