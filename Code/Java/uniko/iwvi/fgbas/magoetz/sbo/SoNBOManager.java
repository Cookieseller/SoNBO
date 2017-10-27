package uniko.iwvi.fgbas.magoetz.sbo;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.context.FacesContext;
import uniko.iwvi.fgbas.magoetz.sbo.objects.Node;
import uniko.iwvi.fgbas.magoetz.sbo.objects.NodeTypeCategory;
import uniko.iwvi.fgbas.magoetz.sbo.services.ConfigService;
import uniko.iwvi.fgbas.magoetz.sbo.services.NodeService;
import uniko.iwvi.fgbas.magoetz.sbo.util.Utilities;

/**
 * @author Flemming
 *
 */
public class SoNBOManager implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String objectId = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");
	
	private String nodeTypeCategoryName = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("nodeTypeCategory");
	
	private String nodeType = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("nodeType");
	
	public Node businessObject;
	
	private NodeService nodeService = new NodeService();
	
	public ConfigService configService = new ConfigService();
	
	public void init(){
		System.out.println("NEW REQUEST FOR BUSINESS OBJECT");
		System.out.println("===============================");

		// if parameter nodeTypeCategoryName was not set get all 
		if(this.nodeTypeCategoryName == null) {
			this.nodeTypeCategoryName = "all";
		}
		// if parameter nodeType was not set get all 
		if(this.nodeType == null) {
			this.nodeType = "all";
		}
		
		// get business object
		this.businessObject = this.nodeService.getNode(objectId, false);
		
		// TODO: execute tests if necessary
		Test test = new Test();
		//test.javaToJson();
	}
}
