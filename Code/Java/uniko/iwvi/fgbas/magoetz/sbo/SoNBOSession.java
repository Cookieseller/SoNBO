package uniko.iwvi.fgbas.magoetz.sbo;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import uniko.iwvi.fgbas.magoetz.sbo.services.ConnectionsService;

public class SoNBOSession {
	
	private String objectId;
	
	private ConnectionsService connectionsService;

	private List<Vector<String>> chronicList = new ArrayList<Vector<String>>();;
	
	public SoNBOSession() {
		//this.connectionsService = new ConnectionsService("connectionsSSO");
		//this.objectId = connectionsService.getUserEmail();
	}
	
	public String getObjectId() {
		return objectId;
	}
	
	public void clearChronic() {
		this.chronicList.clear();
	}

	public void addChronicEntry(String nodeTitle, String nodeId) {
		/*
		Vector<String> newChronicEntry = new Vector<String>();
		newChronicEntry.add(nodeTitle);
		newChronicEntry.add(nodeId);
		this.chronicList.add(newChronicEntry);*/
	}
	
	public List<Vector<String>> getChronicEntries(int maxEntries) {
		List<Vector<String>> chronicEntries = new ArrayList<Vector<String>>();
		maxEntries = maxEntries == 0 ? chronicList.size() : maxEntries;
		if(chronicList.size() > 1) {
			for(int i = chronicList.size(); i > 1; i--) {
				chronicEntries.add(chronicList.get(i - 1));
				// max number to return
				if(i <= chronicList.size() - maxEntries) {
					break;
				}
			}
		}
		return chronicEntries;
	}
}
