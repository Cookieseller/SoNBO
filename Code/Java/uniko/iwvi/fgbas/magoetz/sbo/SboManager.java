package uniko.iwvi.fgbas.magoetz.sbo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;
import javax.faces.context.FacesContext;
import lotus.domino.Database;
import lotus.domino.NotesException;
import org.openntf.Utils;
import uniko.iwvi.fgbas.magoetz.sbo.objects.BusinessObject;
import uniko.iwvi.fgbas.magoetz.sbo.objects.ConfigurationObject;
import uniko.iwvi.fgbas.magoetz.sbo.objects.ConfigurationObject.ConfigurationObjectAttribute;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.xsp.model.domino.DominoUtils;

public class SboManager implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String objectId = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("objectId");
	
	public BusinessObject businessObject;

	public void init(){
		
		if(objectId != null) {
			Database notesDB = DominoUtils.getCurrentDatabase();

			// 0. GET OBJECT AND TYPE OF SPECIFIED OBJECT
			
			ArrayList<String> objectQueryResults = new ArrayList<String>();
			try {
				// return all of the column values from the documents in the view sboPeople
				//results = Utils.Dbcolumn("", "test.nsf", "sboPeople", 1, params);
				// return values by key
				objectQueryResults = Utils.Dblookup("", "test.nsf", "sboPeople", "gebelsauer@uni-koblenz.de", "1");
				
			} catch (NotesException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// convert query results to json objects
			ArrayList<JsonObject> jsonObjects = new ArrayList<JsonObject>();
			
			for(String s : objectQueryResults) {
				System.out.println("------------");
				System.out.println("Incoming string");
				System.out.println(s);
				
				JsonObject o = new JsonParser().parse(s).getAsJsonObject();
				jsonObjects.add(o);
			}
			
			JsonObject obj = jsonObjects.get(0);
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String objString = gson.toJson(obj);
			System.out.println("------------");
			System.out.println("Parsed json");
			System.out.println(objString);
			
			// get object type
			JsonElement objectElement = obj.get(objectId);
			JsonObject secondLevelObject = objectElement.getAsJsonObject();
			String objType = secondLevelObject.get("objectType").getAsString();
			System.out.println("Object type: " + objType);
			
			// 1. GET CONFIGURATION DOCUMENT FOR OBJECT TYPE
			
			ArrayList<String> configQueryResults = new ArrayList<String>();
			try {
				// return values by key
				configQueryResults = Utils.Dblookup(notesDB, "config", objType, "1");
			} catch (NotesException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
			
			// convert query results to json objects
			ArrayList<JsonObject> jsonConfigObjects = new ArrayList<JsonObject>();
			
			for(String s : configQueryResults) {
				System.out.println("============");
				System.out.println("Incoming string");
				System.out.println(s);
				
				JsonObject o = new JsonParser().parse(s).getAsJsonObject();
				jsonConfigObjects.add(o);
			}
			
			JsonObject jsonConfigObject = jsonConfigObjects.get(0);
			String configObjStr = gson.toJson(jsonConfigObject);
			System.out.println("------------");
			System.out.println("Parsed config json");
			System.out.println(configObjStr);
			
			// get config information
			ConfigurationObject configObject = new ConfigurationObject();
			// object type
			JsonElement jsonFirstConfigElement = jsonConfigObject.get(objType);
			JsonObject jsonFirstConfigObject = jsonFirstConfigElement.getAsJsonObject();			
			String objectType = jsonFirstConfigObject.get("objectType").toString();
			configObject.setObjectType(objectType);
			// peers
			JsonElement firstLevelConfigElementPeers = jsonFirstConfigObject.get("peers");
			String[] peers = gson.fromJson(firstLevelConfigElementPeers, String[].class);
			configObject.setPeers(peers);
			// attributes
			JsonElement firstLevelConfigElementFields = jsonFirstConfigObject.get("attributes");
			JsonObject firstLevelConfigObjectFields = firstLevelConfigElementFields.getAsJsonObject();
			
			Set<String> attributes = firstLevelConfigObjectFields.keySet();
			
			System.out.println("Object attributes: ");
			for(String s : attributes) {
				System.out.println("Key: " + s);
				JsonElement secondLevelConfigElement = firstLevelConfigObjectFields.get(s);
				JsonObject secondLevelConfigObject = secondLevelConfigElement.getAsJsonObject();
				
				String datasource = secondLevelConfigObject.get("datasource").getAsString();
				int query = secondLevelConfigObject.get("query").getAsInt();
				String fieldname = secondLevelConfigObject.get("fieldname").getAsString();
				int displayfield = secondLevelConfigObject.get("displayfield").getAsInt();
				
				configObject.addConfigurationObjectAttribute(datasource, query, fieldname, displayfield);
			}
			
			// 2. RETRIEVE INFORMATION FOR BUSINESS OBJECT BASED ON CONFIGURATION
			
			this.businessObject = new BusinessObject();

			for(ConfigurationObjectAttribute configObjAttr : configObject.getConfigurationObjectAttributes()) {

				String datasource = configObjAttr.getDatasource();
				// get data source configuration
				ArrayList<String> sourceQueryResults = new ArrayList<String>();
				try {
					// return values by key
					//sourceQueryResults = Utils.Dblookup(notesDB, "datasources", datasource, "1");
					sourceQueryResults = Utils.Dblookup(notesDB, "datasources", datasource, "1");
				} catch (NotesException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
				// convert query results to json objects
				ArrayList<JsonObject> jsonDatasourceObjects = new ArrayList<JsonObject>();
				
				for(String s : sourceQueryResults) {
					JsonObject o = new JsonParser().parse(s).getAsJsonObject();
					jsonDatasourceObjects.add(o);
				}

				JsonObject jsonDatasourceObject = jsonDatasourceObjects.get(0);
				JsonElement jsonFirstSourceElement = jsonDatasourceObject.get("datasource");
				JsonObject jsonFirstSourceObject = jsonFirstSourceElement.getAsJsonObject();
				
				String hostname = jsonFirstSourceObject.get("hostname").getAsString();
				String database = jsonFirstSourceObject.get("database").getAsString();
				
				// execute query to get results
				configObjAttr.getQuery();
				
				// extract field value
				configObjAttr.getFieldname();
				
				// load attribute key and value into business object
				configObjAttr.getDisplayfield();
			}			
		}
	}
}
