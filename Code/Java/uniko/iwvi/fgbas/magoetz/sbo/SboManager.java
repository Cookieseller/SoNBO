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
	
	private String objectName = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("name");
	
	private String objectPeers = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("peers");
	
	private String objectRelationship = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("relationship");
	
	private ArrayList<String> notificationCodeList = new ArrayList<String>();
	
	public BusinessObject businessObject;
	
	private ObjectService objectService = new ObjectService();
	
	public void init(){
		
		if(objectId != null && objectName != null) {
			
			System.out.println("NEW REQUEST FOR BUSINESS OBJECT");
			// get business objects
			this.businessObject = objectService.getBusinessObject(objectId, objectName);
			// TODO reload peer object list if other objectPeer was chosen
			// get list of peer objects (all)
			ConfigService configService = new ConfigService();
			ClassObject classObject = configService.getClassObject(businessObject.getObjectClass());
			List<BusinessObject> allPeerObjectList = new ArrayList<BusinessObject>();
			List<BusinessObject> peerObjectList = new ArrayList<BusinessObject>();
			
			HashSet<String> objectRelationships = new HashSet<String>();
			List<BusinessObject> filteredPeerObjectList = new ArrayList<BusinessObject>();
			
			boolean addAll = this.objectPeers.equals("all");
			
			for(String peers : classObject.getClassPeers())  {
				List<BusinessObject> objects = objectService.getPeerObjects(businessObject, peers);
				allPeerObjectList.addAll(objects);
				if(this.objectPeers.equals(peers) || this.objectPeers.equals("all")) {
					peerObjectList.addAll(objects);
				}
				this.businessObject.setPeerObjectList(peerObjectList);
				objectRelationships.addAll(objectService.getObjectRelationships(businessObject, peers, addAll));
				filteredPeerObjectList.addAll(objectService.getFilteredBusinessObjects(businessObject, objectRelationship, peers));
			}
			List<String> relationshipList = new ArrayList<String>();
			for(String relationship : objectRelationships) {
				relationshipList.add(relationship);
			}
			this.businessObject.setObjectRelationships(relationshipList);
			//filteredPeerObjectList.addAll(objectService.getFilteredBusinessObjects(businessObject, objectRelationship, objectPeers));
			this.businessObject.setFilteredPeerObjectList(filteredPeerObjectList);
			/*
			for(BusinessObject bo : businessObject.getFilteredPeerObjectList()) {
				System.out.println(bo.getObjectTitle());
				Iterator it = bo.getAttribteList1().entrySet().iterator();
				while(it.hasNext()) {
					Map.Entry<String, String> s = (Entry<String, String>) it.next();
					System.out.println(s.getKey() + " = " + s.getValue());
					it.remove();
				}
			}
			*/
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
