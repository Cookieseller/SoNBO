package uniko.iwvi.fgbas.magoetz.sbo;
import java.io.Serializable;
import java.util.ArrayList;

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
	
	private String objectType = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("objectType");
	
	private ArrayList<String> notificationCodeList = new ArrayList<String>();
	
	public BusinessObject businessObject;
	
	private ObjectService objectService = new ObjectService();
	
	public void init(){
		
		if(objectId != null && objectType != null) {
			
			System.out.println("NEW REQUEST FOR BUSINESS OBJECT");
			
			this.businessObject = objectService.getBusinessObject(objectId, objectType);

		}else {
			this.notificationCodeList.add("E1");
		}
	}

	public ArrayList<String> getNotificationCodeList() {
		return notificationCodeList;
	}
}
