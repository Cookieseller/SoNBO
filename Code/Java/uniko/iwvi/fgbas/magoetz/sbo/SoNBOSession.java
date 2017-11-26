package uniko.iwvi.fgbas.magoetz.sbo;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class SoNBOSession {

	private List<Vector<String>> chronicList = new ArrayList<Vector<String>>();;
	
	public void addChronicEntry(String nodeTitle, String nodeId) {
		Vector<String> newChronicEntry = new Vector<String>();
		newChronicEntry.add(nodeTitle);
		newChronicEntry.add(nodeId);
		this.chronicList.add(newChronicEntry);
	}
	
	public List<Vector<String>> getChronicEntries(int maxEntries) {
		List<Vector<String>> chronicEntries = new ArrayList<Vector<String>>();
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
