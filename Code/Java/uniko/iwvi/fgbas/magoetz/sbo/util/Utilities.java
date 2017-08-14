package uniko.iwvi.fgbas.magoetz.sbo.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class Utilities {
	
	public void printJson(JsonObject json, String info) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String jsonString = gson.toJson(json);
		System.out.println("------------");
		System.out.println(info);
		System.out.println(jsonString);
	}
}
