package uniko.iwvi.fgbas.magoetz.sbo;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import uniko.iwvi.fgbas.magoetz.sbo.objects.BusinessObject;
import uniko.iwvi.fgbas.magoetz.sbo.services.ObjectService;

/**
 * @author Flemming
 *
 */
public class SboManager implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String objectId = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("objectId");
	
	private String objectName = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("objectName");
	
	private String objectPeers = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("objectPeers");
	
	private String objectRelationship = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("objectRelationship");
	
	private ArrayList<String> notificationCodeList = new ArrayList<String>();
	
	public BusinessObject businessObject;
	
	private ObjectService objectService = new ObjectService();
	
	public void init(){
		
		if(objectId != null && objectName != null) {
			
			System.out.println("NEW REQUEST FOR BUSINESS OBJECT");
			// get business objects
			this.businessObject = objectService.getBusinessObject(objectId, objectName);
			// get list of peer objects
			List<BusinessObject> peerObjectList = objectService.getPeerObjects(businessObject, objectPeers, objectRelationship);
			this.businessObject.setPeerObjectList(peerObjectList);
			
		}else {
			this.notificationCodeList.add("E1");
		}
	}

	public ArrayList<String> getNotificationCodeList() {
		return notificationCodeList;
	}
}
