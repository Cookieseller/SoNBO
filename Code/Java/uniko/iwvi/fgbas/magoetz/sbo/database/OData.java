package uniko.iwvi.fgbas.magoetz.sbo.database;

import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.apache.commons.io.IOUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.retrieve.ODataRawRequest;
import org.apache.olingo.client.core.ODataClientFactory;

import uniko.iwvi.fgbas.magoetz.sbo.SoNBOSession;
import uniko.iwvi.fgbas.magoetz.sbo.objects.Datasource;
import uniko.iwvi.fgbas.magoetz.sbo.objects.Node;
import uniko.iwvi.fgbas.magoetz.sbo.objects.Query;
import uniko.iwvi.fgbas.magoetz.sbo.services.CacheService;
import uniko.iwvi.fgbas.magoetz.sbo.services.authentication.RedirectService;
import uniko.iwvi.fgbas.magoetz.sbo.util.Utilities;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class OData extends QueryService {

    private static final long serialVersionUID = 1L;

    CacheService cache = new CacheService("localCache");

    public JsonArray executeQuery(Datasource datasourceObject, Query queryObject) {
    	FacesContext ctx = FacesContext.getCurrentInstance(); 
        SoNBOSession session = (SoNBOSession) ctx.getApplication().getVariableResolver().resolveVariable(ctx, "soNBOSession");
        
        return executeQuery(datasourceObject, queryObject, session);
    }
    
    /**
     * Execute a query in the given database dialect
     *
     * @param datasourceObject
     * @param queryObject
     * @return
     */
    public JsonArray executeQuery(Datasource datasourceObject, Query queryObject, SoNBOSession session) {

        ODataClient client = ODataClientFactory.getClient();

        client.getConfiguration().setHttpClientFactory(session.getClientFactory());

        String uriRoot 	 = datasourceObject.getHostname() + datasourceObject.getDatabase();
        String entitySet = queryObject.getView();

        String skip 		  = queryObject.getSkip();
        JsonArray resultSet   = new JsonArray();
        JsonArray queryResult = new JsonArray();
        boolean cont 		  = false;

		do {
	        URI uri = client.newURIBuilder(uriRoot)
		    	.appendEntitySetSegment(entitySet)
		    	.filter(queryObject.getString())
		    	.skipToken(skip)
		    	.build();

	        /*
	        String result = cache.get(uri.toString());
	        if (result != null) {
	        	return new JsonParser().parse(result).getAsJsonArray();
	        }*/

	        Utilities.remotePrint(uri.toString());
	        InputStream inputStream;
	        try {
	        	ODataRawRequest request = client.getRetrieveRequestFactory().getRawRequest(uri);
	        	request.setAccept("application/json");
		        request.setContentType("application/json;odata.metadata=full");

		        inputStream = request.execute().getRawResponse();
	        } catch (Exception e) {
	        	Utilities.remotePrint(e.getMessage());
	        	new RedirectService().redirectToAuthentication();
	        	return null;
	        }
	        
	        StringWriter writer 	= new StringWriter();
	        try {
				IOUtils.copy(inputStream, writer, "utf-8");

		        String jsonString = writer.toString();
		        JsonParser parser = new JsonParser();
		        JsonObject obj    = parser.parse(jsonString).getAsJsonObject();
		        queryResult	  = obj.get("value").getAsJsonArray();

		        if (queryObject.isDistinctQuery()) {
		        	HashMap<String, JsonElement> distinctElements = new HashMap<String, JsonElement>();
		        	for (JsonElement jsonElement : queryResult) {
		        		JsonObject jsonObj = jsonElement.getAsJsonObject();
		        		if (jsonObj.has(queryObject.getDistinctField())
		        		&& !distinctElements.containsKey(jsonObj.get(queryObject.getDistinctField()).getAsString())
		        		&& !jsonObj.get(queryObject.getDistinctField()).getAsString().isEmpty()) {
		        			distinctElements.put(jsonObj.get(queryObject.getDistinctField()).getAsString(), jsonElement);
		        		}
		        	}

		        	JsonArray array = new JsonArray();
		        	for (JsonElement el : distinctElements.values()) {
		        		array.add(el);
					}
		        	queryResult = array;
		        }

				for (JsonElement jsonElement : queryResult) {
					Query joinQuery = queryObject.getJoinQuery();
					if (joinQuery == null) {
						resultSet.add(jsonElement);
						continue;
					}

					String queryString = buildQuery(jsonElement.getAsJsonObject(), joinQuery);
					joinQuery.setString(queryString);
					JsonArray arr = executeQuery(datasourceObject, joinQuery);
					for (JsonElement el : arr) {
						resultSet.add(Utilities.mergeJson(jsonElement, el));
					}
				}
		        cache.put(uri.toString(), resultSet.toString());

		        cont = false;
		        if (obj.has("odata.nextLink")) {
		        	uri = URI.create(obj.get("odata.nextLink").getAsString());

		        	List<NameValuePair> params = URLEncodedUtils.parse(uri, "UTF-8");
		        	for (NameValuePair param : params) {
		        		if (param.getName().equals("skiptoken")) {
		        			skip = param.getValue();
		        			cont = true;
		        			break;
		        		}
		        	}
		        }		        
			} catch (Exception e) {
				e.printStackTrace();
			}
        } while (cont);

        return resultSet;
    }
    
    /**
     * For backwards compatibility, this should be removed in the future
     * 
     * @param datasourceObject
     * @param queryObject
     * @param objectId
     * @return
     */
    public JsonObject executeQuery(Datasource datasourceObject, Query queryObject, String objectId) {

        ODataClient client = ODataClientFactory.getClient();

        FacesContext ctx = FacesContext.getCurrentInstance(); 
        SoNBOSession session = (SoNBOSession) ctx.getApplication().getVariableResolver().resolveVariable(ctx, "soNBOSession");
        client.getConfiguration().setHttpClientFactory(session.getClientFactory());

        String uriRoot 	 = datasourceObject.getHostname() + datasourceObject.getDatabase();
        String entitySet = queryObject.getView();
        URI uri 	 	 = client.newURIBuilder(uriRoot).appendEntitySetSegment(entitySet).filter(queryObject.getString()).build();

        InputStream inputStream;
        try {
        	ODataRawRequest request = client.getRetrieveRequestFactory().getRawRequest(uri);
            request.setAccept("application/json");
            request.setContentType("application/json;odata.metadata=full");
            
            inputStream = request.execute().getRawResponse();
        } catch (Exception e) {
        	Utilities.remotePrint(e.getMessage());
        	new RedirectService().redirectToAuthentication();
        	return null;
        }

        
        StringWriter writer 	= new StringWriter();
        JsonObject jsonObject 	= new JsonObject();
        try {
			IOUtils.copy(inputStream, writer, "utf-8");
			
	        String jsonString = writer.toString();
	        JsonParser parser = new JsonParser();
	        JsonObject obj    = parser.parse(jsonString).getAsJsonObject();
	        jsonObject 		  = obj.get("value").getAsJsonObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
        return jsonObject;
    }

    /**
     * @TODO should this be private?
     * Take the configured query and replace all tokens with real values from the given node
     * 
     * @param node
     * @param query
     * @return
     */
    public String buildQuery(Node node, Query query) {
    	List<String> tokens = Utilities.getTokenList(query.getString());
    	Map<String, String> replaceAttributesMap = new HashMap<String, String>();
        for (String replaceAttributeKey : tokens) {
            String replaceAttributeValue = node.getAttributeValueByField(replaceAttributeKey);
            replaceAttributesMap.put(replaceAttributeKey, replaceAttributeValue);
        }
        
    	return Utilities.replaceTokens(query.getString(), replaceAttributesMap);
    }
    
    /**
     * Take the configured query and replace all tokens with real values from the given json
     * 
     * @param node
     * @param query
     * @return
     */
    public String buildQuery(JsonObject json, Query query) {
    	List<String> tokens = Utilities.getTokenList(query.getString());
    	Map<String, String> replaceAttributesMap = new HashMap<String, String>();
        for (String replaceAttributeKey : tokens) {
        	if (json.has(replaceAttributeKey)) {
        		String replaceAttributeValue = json.get(replaceAttributeKey).getAsString();
    			replaceAttributesMap.put(replaceAttributeKey, replaceAttributeValue);
        	}
        }

    	return Utilities.replaceTokens(query.getString(), replaceAttributesMap);
    }
    
    /**
     * Return a json array containing the adjacent nodes
     * 
     * @param node
     * @param query
     * @param datasource
     * @return
     */
    public JsonArray getAdjacentNodes(Node node, Query query, Datasource datasource) {
    	String queryString = buildQuery(node, query);
    	Query adjacentQuery = new Query(query);
    	adjacentQuery.setString(queryString);
    	
    	return executeQuery(datasource, adjacentQuery);
    }
}
