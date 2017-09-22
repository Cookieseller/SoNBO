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
	
	public NodeTypeCategory nodeTypeCategory;
	
	private NodeService nodeService = new NodeService();
	
	private ConfigService configService = new ConfigService();
	
	public void init(){
			
		System.out.println("NEW REQUEST FOR BUSINESS OBJECT");
		System.out.println("===============================");

		// get business object
		this.businessObject = this.nodeService.getNode(objectId, false);
		
		// get list of peer objects (all)
		List<Node> adjacentNodeList = new ArrayList<Node>();
		HashSet<String> objectRelationships = new HashSet<String>();
		HashSet<Node> filteredPeerObjectList = new HashSet<Node>();
		
		// if parameter nodeTypeCategoryName was not set get all 
		if(this.nodeTypeCategoryName == null) {
			this.nodeTypeCategoryName = "all";
		}
		
		// set node type category
		this.nodeTypeCategory = this.configService.getNodeTypeCategory(businessObject.getNodeTypeCategory());
		
		for(String adjacentNodeTypeCategory : this.nodeTypeCategory.getAdjacentNodeTypeCategories())  {
			System.out.println("AdjacentNodeTypeCategory: " + adjacentNodeTypeCategory);
			List<Node> objects = nodeService.getAdjacentNodes(businessObject, adjacentNodeTypeCategory);
			//allPeerObjectList.addAll(objects);
			if(this.nodeTypeCategoryName.equals(adjacentNodeTypeCategory) || this.nodeTypeCategoryName.equals("all")) {
				adjacentNodeList.addAll(objects);
			}
			this.businessObject.setAdjacentNodeList(adjacentNodeList);
			objectRelationships.addAll(nodeService.getAdjacentNodeTypes(adjacentNodeTypeCategory));
			filteredPeerObjectList.addAll(nodeService.getFilteredResultList(businessObject, nodeType, adjacentNodeTypeCategory));
		}
		List<String> relationshipList = new ArrayList<String>();
		for(String relationship : objectRelationships) {
			relationshipList.add(relationship);
		}
		List<Node> adjacencyList = new ArrayList<Node>();
		for(Node filteredPeerObject : filteredPeerObjectList) {
			adjacencyList.add(filteredPeerObject);
		}
		
		this.businessObject.setNodeAdjacencies(relationshipList);
		this.businessObject.setFilteredAdjacentNodeList(adjacencyList);

		// TODO: execute tests if necessary
		Test test = new Test();
		//test.javaToJson();
	}
}
