package uniko.iwvi.fgbas.magoetz.sbo.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import uniko.iwvi.fgbas.magoetz.sbo.objects.Node;

public class DBMock implements Serializable {
	private static final long serialVersionUID = 1L;
	private HashMap<String, Node> map = new HashMap<String, Node>();
	private HashMap<String, ArrayList<Node>> adjNodes = new HashMap<String, ArrayList<Node>>();
	
	public DBMock()
	{
		final Node node1 = createNode("1308", "Mathias Riedle", new ArrayList<String>() {{add("Name");}}, "User", new ArrayList<String>() {{add("User"); add("Bestellung (Lieferant)");}}, "UserType");
		final Node node2 = createNode("1309", "Berit Gebel-Sauer", new ArrayList<String>() {{add("Name");}}, "User", new ArrayList<String>() {{add("User");}}, "PersonType");
		final Node node3 = createNode("1310", "Flemming Götz", new ArrayList<String>() {{add("Name");}}, "User 2", new ArrayList<String>() {{add("User");}}, "User 2");
		final Node node4 = createNode("1311", "Petra Schubert", new ArrayList<String>() {{add("Name");}}, "User 2", new ArrayList<String>() {{add("User");}}, "User");
		final Node node5 = createNode("1312", "Rechnung", new ArrayList<String>() {{add("Name");}}, "Bestellung (Lieferant)", new ArrayList<String>() {{add("Bestellung (Lieferant)");}}, "Bestellung (Lieferant) Type");
		final Node node6 = createNode("1313", "Rechnung 2", new ArrayList<String>() {{add("Name");}}, "Bestellung (Lieferant)", new ArrayList<String>() {{add("Bestellung (Lieferant)");}}, "BestellungType");

		map.put("1308", node1);
		map.put("1309", node2);
		map.put("1310", node3);
		map.put("1311", node4);
		map.put("1312", node5);
		map.put("1312", node6);
		
		adjNodes.put("1308", new ArrayList<Node>() {{add(node2); add(node3); add(node4); add(node5); add(node6);}});
		adjNodes.put("1309", new ArrayList<Node>() {{add(node1); add(node3); add(node4); add(node5);}});
		adjNodes.put("1310", new ArrayList<Node>() {{add(node2); add(node1); add(node4); add(node5);}});
		adjNodes.put("1311", new ArrayList<Node>() {{add(node2); add(node3); add(node1); add(node5);}});
		adjNodes.put("1312", new ArrayList<Node>() {{add(node2); add(node3); add(node4); add(node1);}});
		adjNodes.put("1312", new ArrayList<Node>() {{add(node2); add(node3); add(node4); add(node1); add(node6);}});
	}
	
	private Node createNode(final String id, final String title, final ArrayList<String> attributes, final String category, 
			final ArrayList<String> categories, final String nodeType)
	{
		return new Node() {{
			setId(id);
			setNodeTitle(title);
			setNodeTypeCategory(category);
			setNodeTypeCategories(categories);
			setNodeType(nodeType);
		}};
	}
	
	public Node getNodeById(String id)
	{
		return map.get(id);
	}
	
	public ArrayList<Node> getAdjNodeList(Node node)
	{
		return adjNodes.get(node.getId());
	}
}
