package uniko.iwvi.fgbas.magoetz.sbo;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.context.FacesContext;
import uniko.iwvi.fgbas.magoetz.sbo.objects.BusinessObject;
import uniko.iwvi.fgbas.magoetz.sbo.objects.ClassObject;
import uniko.iwvi.fgbas.magoetz.sbo.services.ConfigService;
import uniko.iwvi.fgbas.magoetz.sbo.services.ObjectService;
import uniko.iwvi.fgbas.magoetz.sbo.util.Utilities;

/**
 * @author Flemming
 *
 */
public class SboManager implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String objectId = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");
	
	private String objectPeers = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("nodeType1");
	
	private String objectRelationship = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("nodeType2");
	
	private ArrayList<String> notificationCodeList = new ArrayList<String>();
	
	public BusinessObject businessObject;
	
	private ObjectService objectService = new ObjectService();
	
	public void init(){
		
		if(objectId != null) {
			
			System.out.println("NEW REQUEST FOR BUSINESS OBJECT");
			// get business objects
			this.businessObject = objectService.getBusinessObject(objectId, false);
			// TODO reload peer object list if other objectPeer was chosen
			// get list of peer objects (all)
			ConfigService configService = new ConfigService();
			ClassObject classObject = configService.getClassObject(businessObject.getObjectClass());
			//List<BusinessObject> allPeerObjectList = new ArrayList<BusinessObject>();
			List<BusinessObject> peerObjectList = new ArrayList<BusinessObject>();
			
			HashSet<String> objectRelationships = new HashSet<String>();
			//List<BusinessObject> filteredPeerObjectList = new ArrayList<BusinessObject>();
			HashSet<BusinessObject> filteredPeerObjectList = new HashSet<BusinessObject>();
			
			for(String peers : classObject.getClassPeers())  {
				List<BusinessObject> objects = objectService.getPeerObjects(businessObject, peers);
				//allPeerObjectList.addAll(objects);
				if(this.objectPeers.equals(peers) || this.objectPeers.equals("all")) {
					peerObjectList.addAll(objects);
				}
				this.businessObject.setPeerObjectList(peerObjectList);
				objectRelationships.addAll(objectService.getObjectRelationships(peers));
				filteredPeerObjectList.addAll(objectService.getFilteredBusinessObjects(businessObject, objectRelationship, peers));
			}
			List<String> relationshipList = new ArrayList<String>();
			for(String relationship : objectRelationships) {
				relationshipList.add(relationship);
			}
			List<BusinessObject> peerList = new ArrayList<BusinessObject>();
			for(BusinessObject filteredPeerObject : filteredPeerObjectList) {
				peerList.add(filteredPeerObject);
			}
			this.businessObject.setObjectRelationships(relationshipList);
			this.businessObject.setFilteredPeerObjectList(peerList);

			// TODO: execute tests if necessary
			Test test = new Test();
			//test.javaToJson();
			
		}else {
			this.notificationCodeList.add("E1");
		}
	}

	public ArrayList<String> getNotificationCodeList() {
		return notificationCodeList;
	}
}
