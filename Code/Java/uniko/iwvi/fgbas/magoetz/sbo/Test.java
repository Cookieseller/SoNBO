package uniko.iwvi.fgbas.magoetz.sbo;

import java.util.ArrayList;

import com.google.gson.Gson;

import uniko.iwvi.fgbas.magoetz.sbo.objects.NodeTypeCategory;
import uniko.iwvi.fgbas.magoetz.sbo.services.QueryService;

public class Test {
	
	private QueryService queryService = new QueryService();

	public void javaToJson() {
		
		// create java object
		NodeTypeCategory classObject = new NodeTypeCategory();
		classObject.setMainDatasource("devil");
		classObject.setMainQuery("getPeople");
		classObject.setName("person");
		ArrayList<String> classPeers = new ArrayList<String>();
		classPeers.add("person");
		classPeers.add("teaching");
		classObject.setAdjacentNodeTypeCategories(classPeers);
		classObject.setAdjacencies("role");
		
		// convert java object to json
		Gson gson = new Gson();
		String json = gson.toJson(classObject, NodeTypeCategory.class);
		System.out.println("GSON: " + json);
		
		// get json from db and convert to java object
		String jsonFromDb = queryService.getFieldValue("classes", "person", "classJSON");
		NodeTypeCategory classObjectFromDb = gson.fromJson(jsonFromDb, NodeTypeCategory.class);
		System.out.println("Java object classObject from json:");
		System.out.println("Class name: " + classObjectFromDb.getName());		
	}
}
