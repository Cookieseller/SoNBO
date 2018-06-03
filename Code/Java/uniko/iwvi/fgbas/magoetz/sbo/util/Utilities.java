package uniko.iwvi.fgbas.magoetz.sbo.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import uniko.iwvi.fgbas.magoetz.sbo.SoNBOSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.ibm.xsp.designer.context.XSPContext;

public class Utilities {

	private static String redirectUrl = "";
	
    public void printJson(JsonObject json, String info) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonString = gson.toJson(json);
        System.out.println("------------");
        System.out.println(info);
        System.out.println(jsonString);
    }
    
    public static String ListToString(List<String> list) {
    	return StringUtils.join(list, ", ");
    }

    /*
     * Thanks to
     * https://stackoverflow.com/questions/959731/how-to-replace-a-set-of-tokens-in-a-java-string
     */
    public static String replaceTokens(String text, Map<String, String> replacements) {
        Pattern pattern = Pattern.compile("\\[\\[(.+?)\\]\\]");
        Matcher matcher = pattern.matcher(text);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String replacement = replacements.get(matcher.group(1));
            if (replacement != null) {
                // matcher.appendReplacement(buffer, replacement);
                // see comment
                matcher.appendReplacement(buffer, "");
                buffer.append(replacement);
            }
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    /*
     * Thanks to
     * https://stackoverflow.com/questions/959731/how-to-replace-a-set-of-tokens-in-a-java-string
     */
    public static String replaceTokens(String text, JsonObject data) {
    	ArrayList<String> tokenList      = Utilities.getTokenList(text);
    	Map<String, String> replacements = new HashMap<String, String>();

    	for (String replaceAttributeKey : tokenList) {
	    	if (data.has(replaceAttributeKey)) {
	    		String replaceAttributeValue = data.get(replaceAttributeKey).getAsString();
	    		replacements.put(replaceAttributeKey, replaceAttributeValue);
	    	} else {
	    		replacements.put(replaceAttributeKey, "No value found");
	    	}
	    }

        return Utilities.replaceTokens(text, replacements);
    }

    public static void remotePrint(String print) {
		try {
			StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
			URL url = new URL("http://78.47.252.114:2208");
	        URLConnection connection = url.openConnection();
	        connection.setDoOutput(true);
	        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

	        if (print == null) {
	        	print = "";
	        }
	        
	        OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
	        if (stackTraceElements.length >= 1) {
	        	out.write(stackTraceElements[3].getClassName() + "::" + stackTraceElements[3].getMethodName() + "=" + URLEncoder.encode(print, "UTF-8"));	
	        } else {
	        	out.write("Unknown Parent=" + URLEncoder.encode(print, "UTF-8"));	
	        }
	        
	        out.close();

	        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	        String decodedString;
	        while ((decodedString = in.readLine()) != null) {
	            System.out.println(decodedString);
	        }
	        in.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public static void redirectToAuthentication() {
    	FacesContext ctx = FacesContext.getCurrentInstance(); 
        SoNBOSession session = (SoNBOSession) ctx.getApplication().getVariableResolver().resolveVariable(ctx, "soNBOSession");

        XSPContext context = XSPContext.getXSPContext(ctx);
        redirectUrl = context.getUrl().getAddress();
        Utilities.remotePrint(redirectUrl);
        try {
        	ctx.getExternalContext().redirect("https://devil.bas.uni-koblenz.de/SoNBO/SNBO-NAV.nsf/auth.xsp");	
        } catch (IOException e) {
        	e.printStackTrace();
        }
	}
    
    /**
     * Returns List of tokens identified in a text string delimited by [token]
     */
    public static ArrayList<String> getTokenList(String text) {
        ArrayList<String> tokenList = new ArrayList<String>();
        Pattern pattern = Pattern.compile("\\[\\[(.+?)\\]\\]");
        Matcher matcher = pattern.matcher(text);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String match = matcher.group(1);
            if (match != null) {
                matcher.appendReplacement(buffer, "");
                tokenList.add(match);
            }
        }
        return tokenList;
    }
}
