package uniko.iwvi.fgbas.magoetz.sbo.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.ViewEntryCollection;
import uniko.iwvi.fgbas.magoetz.sbo.database.IQueryService;
import uniko.iwvi.fgbas.magoetz.sbo.database.NotesDB;
import uniko.iwvi.fgbas.magoetz.sbo.objects.Datasource;
import uniko.iwvi.fgbas.magoetz.sbo.objects.Query;
import uniko.iwvi.fgbas.magoetz.sbo.util.Utilities;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ibm.sbt.services.client.ClientServicesException;
import com.ibm.sbt.services.client.connections.profiles.Profile;
import com.ibm.sbt.services.client.connections.profiles.ProfileService;
import com.ibm.xml.crypto.util.Base64;
import com.ibm.xsp.model.domino.DominoUtils;

public class ActivityStreamUpdateService {

	private final NotesDB configQueryService = new NotesDB();
	
	public void updateActivityStream() throws IOException {
		Database db = DominoUtils.getCurrentDatabase();
		String version = Runtime.class.getPackage().getImplementationVersion();
		Utilities.remotePrint(version);

        try {
			ViewEntryCollection documents = db.getView("nodeTypesActivities").getAllEntries();
			Document document = documents.getFirstEntry().getDocument();
			while (document != null) {
				
				String datasourceName = document.getItemValueString("activityEntryDatasource");
				String queryName = document.getItemValueString("activityEntryQuery");
				
				Datasource datasource = configQueryService.getDatasourceObject(datasourceName);
				Query query 		  = configQueryService.getQueryObject(queryName);
				GeneralConfigService generalConfigService = new GeneralConfigService();
				String skip = generalConfigService.getConfigEntryByName("GeneralLogOffset").get("configEntryValue").getAsString();
				query.setSkip(skip);
				
				IQueryService service = QueryServiceFactory.getQueryServiceByDatasource(datasourceName);
				JsonArray result = service.executeQuery(datasource, query);

				long highestEntry = Long.parseLong(skip);
				for (JsonElement el : result) {
					if (!el.getAsJsonObject().has("Entry_No")) {
						Utilities.remotePrint("No entry no");
						continue;
					}
					long val = el.getAsJsonObject().get("Entry_No").getAsLong();
					
					if (val > highestEntry)
						highestEntry = val;
					
					try {
						postToActivityStream("", el.getAsJsonObject());
					} catch (KeyManagementException e) {
						e.printStackTrace();
					} catch (NoSuchAlgorithmException e) {
						e.printStackTrace();
					}
				}
				
				//generalConfigService.updateConfigEntry("GeneralLogOffset", Long.toString(highestEntry));
				document = documents.getNextEntry().getDocument();
			}
		} catch (NotesException e) {
			e.printStackTrace();
		} catch (NullPointerException ex) {
			ex.printStackTrace();
		}
	}
	
	private boolean postToActivityStream(String message, JsonObject obj) throws IOException, KeyManagementException, NoSuchAlgorithmException
	{
		ProfileService profileService = new ProfileService("connectionsSSO");
		try {
			Profile profile = profileService.getProfile("mriedle@uni-koblenz.de");
			String id = profile.getId();
			String name = profile.getName();
			
			URL url = new URL("https://c55.bas.uni-koblenz.de/connections/opensocial/basic/rest/activitystreams/@me");
		    String post = "{ \n" +
		        "'generator': { \n" +
		        "    'id':'SoNBO-Explorer' \n" +
		        "}, \n" +
		        "'actor': { \n" +
		        "   'id': '@me' \n" +
		        "}, \n" +
		        "'title':'Rechnung bezahlt', \n" +
		        "'to': [ \n" +
		        "   { \n" +
		        "       'objectType':'person', \n" +
		        "       'id':'urn:lsid:lconn.ibm.com:profiles.person:D81F82CD-D8CD-C763-C125-822F003482DC' \n" +
		        "   } \n" +
		        "], \n" +
		        "'object': { \n" +
		        "   'summary':'@{{urn:lsid:lconn.ibm.com:profiles.person:D81F82CD-D8CD-C763-C125-822F003482DC|@Mathias Riedle}} die Rechnung [No] wurde von [User_ID] geändert', \n" +
		        "   'id':'c' \n" +
		        "}, \n" +
		        "'verb':'mention' \n" +
		        "}";
		    
		    ArrayList<String> tokenList = Utilities.getTokenList(post);
		    Map<String, String> replaceAttributesMap = new HashMap<String, String>();
            for (String replaceAttributeKey : tokenList) {
            	if (obj.has(replaceAttributeKey)) {
            		String replaceAttributeValue = obj.get(replaceAttributeKey).getAsString();
            		replaceAttributesMap.put(replaceAttributeKey, replaceAttributeValue);
            	} else {
            		replaceAttributesMap.put(replaceAttributeKey, "No value found");
            	}
            }
            String replacedQuery = Utilities.replaceTokens(post, replaceAttributesMap);
		    //Utilities.remotePrint(replacedQuery);
		    

		    HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
		    SSLContext ctx = SSLContext.getInstance("SSL");
		    ctx.init(new KeyManager[0], new TrustManager[] {new DefaultTrustManager()}, new SecureRandom());
		    connection.setHostnameVerifier(new HostnameVerifier() {
				public boolean verify(String arg0, SSLSession arg1) {
					Utilities.remotePrint("Verify hostname");
					return true;
				}
		    });
		    connection.setSSLSocketFactory(ctx.getSocketFactory());
		    connection.setRequestMethod("POST");
		    
		    String authEncoded = Base64.encode("mriedle:ConNeXt".getBytes("UTF-8"));
		    Utilities.remotePrint("Auth encoded: " + authEncoded);
		    connection.setRequestProperty("Authorization", "Basic " + authEncoded);
		    
		    connection.setDoOutput(true);
		    connection.setRequestProperty("Content-Type", "application/json");
		    

	        OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());

        	out.write(replacedQuery);	
	        out.close();

	        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	        String decodedString;
	        while ((decodedString = in.readLine()) != null) {
	            System.out.println(decodedString);
	        }
	        in.close();
	        
		} catch (ClientServicesException e) {
			Utilities.remotePrint("ClientServicesException");
			e.printStackTrace();
		}
		return false;
	}
	
    private static class DefaultTrustManager implements X509TrustManager {

        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        	Utilities.remotePrint("checkClientTrusted");
        }

        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        	Utilities.remotePrint("checkServerTrusted");
        }

        public X509Certificate[] getAcceptedIssuers() {
        	Utilities.remotePrint("getAcceptedIssuers");
        	Utilities.remotePrint(new X509Certificate[0].toString());
        	return new X509Certificate[0];
        }
    }
}
