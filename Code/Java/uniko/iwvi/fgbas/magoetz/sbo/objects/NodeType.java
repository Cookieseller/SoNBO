package uniko.iwvi.fgbas.magoetz.sbo.objects;

import java.util.ArrayList;
import java.util.List;

public class NodeType {

	private String nodeTypeName;
	
	private String nodeTypeId;
	
	private String nodeTypeTitle;
	
	private String nodeTypeCategory;
	
	private String adjacencies;
	
	private List<NodeTypeAttribute> nodeTypeAttributes;
	
	public NodeType() {
		this.nodeTypeAttributes = new ArrayList<NodeTypeAttribute>();
	}
	
	public void setNodeTypeName(String nodeTypeName) {
		this.nodeTypeName = nodeTypeName;
	}

	public String getNodeTypeName() {
		return nodeTypeName;
	}

	public void setNodeTypeId(String nodeTypeId) {
		this.nodeTypeId = nodeTypeId;
	}

	public String getNodeTypeId() {
		return nodeTypeId;
	}

	public String getNodeTypeCategory() {
		return nodeTypeCategory;
	}

	public void setNodeTypeCategory(String nodeTypeCategory) {
		this.nodeTypeCategory = nodeTypeCategory;
	}

	public void setAdjacencies(String adjacencies) {
		this.adjacencies = adjacencies;
	}

	public String getAdjacencies() {
		return adjacencies;
	}

	public List<NodeTypeAttribute> getNodeTypeAttributes() {
		return this.nodeTypeAttributes;
	}
	
	public List<NodeTypeAttribute> getPreviewConfigurationNodeAttributes() {
		List<NodeTypeAttribute> previewConfigNodeAttrList = new ArrayList<NodeTypeAttribute>();
		for(NodeTypeAttribute configNodeAttr : this.getNodeTypeAttributes()) {
			if(configNodeAttr.isPreview()) {
				previewConfigNodeAttrList.add(configNodeAttr);
			}
		}
		return previewConfigNodeAttrList;
	}

	public void setConfigurationNodeAttributes(List<NodeTypeAttribute> attributes){
		this.nodeTypeAttributes = attributes;
	}
	
	public void addConfigurationNodeAttribute(NodeTypeAttribute attribute) {
		this.nodeTypeAttributes.add(attribute);
	}

	public String getNodeTypeTitle() {
		return nodeTypeTitle;
	}

	public void setNodeTypeTitle(String nodeTypeTitle) {
		this.nodeTypeTitle = nodeTypeTitle;
	}
}
