package uniko.iwvi.fgbas.magoetz.sbo.util;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	
	/*
	 * Thanks to
	 * https://stackoverflow.com/questions/959731/how-to-replace-a-set-of-tokens-in-a-java-string
	 */
	public String replaceTokens(String text, Map<String, String> replacements) {
		Pattern pattern = Pattern.compile("\\[(.+?)\\]");
		Matcher matcher = pattern.matcher(text);
		StringBuffer buffer = new StringBuffer();

		while (matcher.find()) {
			String replacement = replacements.get(matcher.group(1));
			if (replacement != null) {
				// matcher.appendReplacement(buffer, replacement);
				// see comment
				matcher.appendReplacement(buffer, "");
				buffer.append("\"" + replacement  + "\"");
			}
		}
		matcher.appendTail(buffer);
		return buffer.toString();
	}
	
	/*
	 * returns List of tokens identified in a text string delimited by [token]
	 */
	public static ArrayList<String> getTokens(String text) {
		ArrayList<String> tokenList = new ArrayList<String>();
		Pattern pattern = Pattern.compile("\\[(.+?)\\]");
		Matcher matcher = pattern.matcher(text);
		StringBuffer buffer = new StringBuffer();
		while(matcher.find()) {
			 String match = matcher.group(1);
			if (match != null) {
				matcher.appendReplacement(buffer, "");
				tokenList.add(match);
			}
		}
		return tokenList;
	}
}
