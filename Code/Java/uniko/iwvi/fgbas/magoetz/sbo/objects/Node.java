package uniko.iwvi.fgbas.magoetz.sbo.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import uniko.iwvi.fgbas.magoetz.sbo.util.Utilities;

public class Node implements Serializable {

    private static final long serialVersionUID = 805731509510272843L;

    private String id;

    private String nodeType;

    private String nodeTypeCategory;

    private List<String> nodeTypeCategories;

    private String nodeTitle;

    private String nodeImage;

    private List<NodeTypeAttribute> attributeList = new ArrayList<NodeTypeAttribute>();

    /*
     * returns key value pairs for displayfield
     */
    public HashMap<String, String> getAttributeListForDisplayfield(int displayfieldnumber, Locale locale) {
        HashMap<String, String> attributeListForDisplayfield = new HashMap<String, String>();
        for (NodeTypeAttribute nodeTypeAttribute : attributeList) {
            if (nodeTypeAttribute.getDisplayfield() == displayfieldnumber) {
                // only add if not empty
                if (!nodeTypeAttribute.getValueAsString().equals("")) {
                    String attributeName = nodeTypeAttribute.getName(locale);
                    String attributeValue = nodeTypeAttribute.getValueAsString();
                    attributeListForDisplayfield.put(attributeName, attributeValue);
                }
            }
        }
        return attributeListForDisplayfield;
    }

    public HashMap<String, String> getAttributeListForPreview(Locale locale) {
        HashMap<String, String> attributeListForDisplayfield = new HashMap<String, String>();
        for (NodeTypeAttribute nodeTypeAttribute : attributeList) {
            if (nodeTypeAttribute.isPreview()) {
                // only add if not empty
                if (!nodeTypeAttribute.getValueAsString().equals("")) {
                	String attributeName = nodeTypeAttribute.getName(locale);
                    String attributeValue = nodeTypeAttribute.getValueAsString();
                    attributeListForDisplayfield.put(attributeName, attributeValue);	
                } else {
                	Utilities.remotePrint(nodeTypeAttribute.getName());
                }
            }
        }
        return attributeListForDisplayfield;
    }

    @SuppressWarnings("unchecked")
    public <T> T getAttributeValue(String key) {
        Object value = null;
        for (NodeTypeAttribute nodeTypeAttribute : attributeList) {
            if (nodeTypeAttribute.getName().equals(key)) {
                // TODO change to getValue if it must not be a String
                value = nodeTypeAttribute.getValue();
            }
        }
        return (T) value;
    }

    public NodeTypeAttribute getAttributeOfType(String key, String datatype) {
        for (NodeTypeAttribute nodeTypeAttribute : attributeList) {
            // TODO String is expected (change to getValue)
            if (nodeTypeAttribute.getName().equals(key) && nodeTypeAttribute.getDatatype().equals(datatype)) {
                return nodeTypeAttribute;
            }
        }
        return null;
    }

    public String getAttributeValueAsString(String key) {
        String value = null;
        for (NodeTypeAttribute nodeTypeAttribute : attributeList) {
            if (nodeTypeAttribute.getName().equals(key)) {
                // TODO change to getValue if it must not be a String
                value = nodeTypeAttribute.getValueAsString();
            }
        }
        return value;
    }
    
    public String getAttributeValueByField(String field) {
        String value = null;
        for (NodeTypeAttribute nodeTypeAttribute : attributeList) {
            if (nodeTypeAttribute.getFieldname().equals(field)) {
                // TODO change to getValue if it must not be a String
                value = nodeTypeAttribute.getValueAsString();
            }
        }
        return value;
    }

    public boolean containsAttribute(String key, String value) {
        for (NodeTypeAttribute nodeTypeAttribute : attributeList) {
            // TODO String ist expected (change to getValue)
            if (nodeTypeAttribute.getName().equals(key) && nodeTypeAttribute.getValueAsString().equals(value)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsAttributeOfType(String key, String datatype) {
        for (NodeTypeAttribute nodeTypeAttribute : attributeList) {
            // TODO String is expected (change to getValue)
            if (nodeTypeAttribute.getName().equals(key) && nodeTypeAttribute.getDatatype().equals(datatype)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsAttributeOfTypeWithValue(String key, String datatype, Object value) {
        for (NodeTypeAttribute nodeTypeAttribute : attributeList) {
            if (nodeTypeAttribute.getName().equals(key) && nodeTypeAttribute.getDatatype().equals(datatype) && nodeTypeAttribute.getValue().equals(value)) {
                return true;
            }
        }
        return false;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public String getNodeTitle() {
        return nodeTitle;
    }

    public void setNodeTitle(String nodeTitle) {
        this.nodeTitle = nodeTitle;
    }

    public String getNodeTypeCategory() {
        return nodeTypeCategory;
    }

    public void setNodeTypeCategory(String nodeTypeCategory) {
        this.nodeTypeCategory = nodeTypeCategory;
    }

    public void setNodeTypeCategories(List<String> nodeTypeCategories) {
        this.nodeTypeCategories = nodeTypeCategories;
    }

    public List<String> getNodeTypeCategories() {
        return nodeTypeCategories;
    }

    public void setNodeImage(String nodeImage) {
        this.nodeImage = nodeImage;
    }

    public String getNodeImage() {
        return nodeImage;
    }

    public void setAttributeList(List<NodeTypeAttribute> attributeList) {
        this.attributeList = attributeList;
    }

    public List<NodeTypeAttribute> getAttributeList() {
        return attributeList;
    }

    public void addAttribute(NodeTypeAttribute nodeTypeAttribute) {
        this.attributeList.add(nodeTypeAttribute);
    }
    
    public void addAllAttributes(List<NodeTypeAttribute> nodeTypeAttributes) {
    	this.attributeList.addAll(nodeTypeAttributes);
    }

    public int compareByAttribute(Node otherNode, SortAttribute sortAttribute) {

        if (this == otherNode) {
            return 0;
        }

        String attributeName = sortAttribute.getAttributeName();
        String datatype = sortAttribute.getDatatype();

        // the node which contains the attribute will be preferred
        NodeTypeAttribute thisAttribute = this.getAttributeOfType(attributeName, datatype);
        NodeTypeAttribute othersAttribute = otherNode.getAttributeOfType(attributeName, datatype);

        if (thisAttribute == null && othersAttribute != null) {
            return 1;
        }
        if (thisAttribute == null && othersAttribute == null) {
            return 0;
        }
        if (thisAttribute != null && othersAttribute == null) {
            return -1;
        }

        // if both contain attributes of the specified datatype compare
        if (datatype.equals("String") || datatype.equals("NotesUsername")) {
            String thisAttrString = thisAttribute.getValue();
            String othersAttrString = othersAttribute.getValue();
            return thisAttrString.compareTo(othersAttrString);
        }
        if (datatype.equals("Integer")) {
            int thisAttrInt = thisAttribute.getValue();
            int othersAttrInt = othersAttribute.getValue();
            return Double.compare(thisAttrInt, othersAttrInt);
        }
        // TODO: implement other datatypes
        return 0;
    }
}
