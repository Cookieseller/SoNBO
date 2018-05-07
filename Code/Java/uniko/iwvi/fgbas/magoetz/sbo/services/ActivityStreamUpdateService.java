package uniko.iwvi.fgbas.magoetz.sbo.services;

import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.retrieve.ODataRawRequest;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.client.core.http.BasicAuthHttpClientFactory;

import uniko.iwvi.fgbas.magoetz.sbo.database.OData;
import uniko.iwvi.fgbas.magoetz.sbo.database.QueryService;
import uniko.iwvi.fgbas.magoetz.sbo.objects.Datasource;
import uniko.iwvi.fgbas.magoetz.sbo.objects.Query;
import uniko.iwvi.fgbas.magoetz.sbo.util.Utilities;
import lotus.domino.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.xsp.model.domino.DominoUtils;

public class ActivityStreamUpdateService {
	private final long startOffset = 1355000L;

	public void updateActivityStream() {
		Database db = DominoUtils.getCurrentDatabase();
        ConfigService configService = new ConfigService();
        OData odata = new OData();

        try {
			ViewEntryCollection documents = db.getView("nodeTypesActivities").getAllEntries();
			Document document = documents.getFirstEntry().getDocument();
			while (document != null) {

				String queryName = document.getItemValueString("activityEntryQuery");
				document = documents.getNextEntry().getDocument();
			}
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        OData queryService = new OData();
        ArrayList<String> adjacencyIds = queryService.getFieldValues("(nodeTypeAdjacenciesSource)", "sourceNodeType", "adjacencyId");

        Utilities.remotePrint("Update called");
	}
	
	/*
	public JsonArray executeQuery(Datasource datasourceObject, Query queryObject) {
        ODataClient client = ODataClientFactory.getClient();

        client.getConfiguration().setHttpClientFactory(new BasicAuthHttpClientFactory("mriedle", "/\"Gulpop32"));
        
        String uriRoot 	 = "http://ec-dev-nav.bas.uni-koblenz.de:7048/DynamicsNAV80/OData/Company('Küchenland%20GmbH')";
        String entitySet = "";
        URI uri 	 	 = client.newURIBuilder(uriRoot).appendEntitySetSegment(entitySet).filter(queryObject.getString()).build();

        Utilities.remotePrint(uri.toString());
        
        //String result = cache.get(uri.toString());
        if (result != null) {
        	JsonParser parser = new JsonParser();
        
        	return parser.parse(result).getAsJsonArray();
        }
        
        
        ODataRawRequest request = client.getRetrieveRequestFactory().getRawRequest(uri);
        request.setAccept("application/json");
        request.setContentType("application/json;odata.metadata=full");
        
        InputStream inputStream = request.execute().getRawResponse();
        StringWriter writer 	= new StringWriter();
        JsonArray resultSet 	= new JsonArray();
        try {
			IOUtils.copy(inputStream, writer, "utf-8");
			
	        String jsonString = writer.toString();
	        JsonParser parser = new JsonParser();
	        JsonObject obj    = parser.parse(jsonString).getAsJsonObject();
	        resultSet 		  = obj.get("value").getAsJsonArray();
	        cache.put(uri.toString(), resultSet.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

        return resultSet;
	}*/
}
