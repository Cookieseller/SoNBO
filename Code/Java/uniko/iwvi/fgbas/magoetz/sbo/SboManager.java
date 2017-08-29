package uniko.iwvi.fgbas.magoetz.sbo;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import uniko.iwvi.fgbas.magoetz.sbo.objects.BusinessObject;
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
			// get list of peer objects
			
			List<BusinessObject> peerObjectList = objectService.getPeerObjects(businessObject, objectPeers);
			this.businessObject.setPeerObjectList(peerObjectList);
			// get relationships of peer objects
			List<String> objectRelationships = objectService.getObjectRelationships(businessObject, objectPeers); 
			this.businessObject.setObjectRelationships(objectRelationships);
			// set filters
			List<BusinessObject> filteredPeerObjectList = objectService.getFilteredBusinessObjects(businessObject, objectRelationship);
			this.businessObject.setFilteredPeerObjectList(filteredPeerObjectList);
			
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
