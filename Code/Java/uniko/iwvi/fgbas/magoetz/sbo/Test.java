package uniko.iwvi.fgbas.magoetz.sbo;

import java.util.ArrayList;

import com.google.gson.Gson;

import uniko.iwvi.fgbas.magoetz.sbo.objects.ClassObject;
import uniko.iwvi.fgbas.magoetz.sbo.services.QueryService;

public class Test {
	
	private QueryService queryService = new QueryService();

	public void javaToJson() {
		
		// create java object
		ClassObject classObject = new ClassObject();
		classObject.setClassMainDatasource("devil");
		classObject.setClassMainQuery("getPeople");
		classObject.setClassName("person");
		ArrayList<String> classPeers = new ArrayList<String>();
		classPeers.add("person");
		classPeers.add("teaching");
		classObject.setClassPeers(classPeers);
		classObject.setClassRelationships("role");
		
		// convert java object to json
		Gson gson = new Gson();
		String json = gson.toJson(classObject, ClassObject.class);
		System.out.println("GSON: " + json);
		
		// get json from db and convert to java object
		String jsonFromDb = queryService.getFieldValue("classes", "person", "classJSON");
		ClassObject classObjectFromDb = gson.fromJson(jsonFromDb, ClassObject.class);
		System.out.println("Java object classObject from json:");
		System.out.println("Class name: " + classObjectFromDb.getClassName());		
	}
}
