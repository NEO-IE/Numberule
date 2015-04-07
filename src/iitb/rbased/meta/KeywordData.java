package iitb.rbased.meta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class KeywordData {
	public static String keyWordFile = "/mnt/a99/d0/aman/scala/workspace/rulebasedextractor/data/keywords.json";
	public static final String modifiers[] = new String[] { "change", "up", "down", "males", "females", "male",
			"female", "growth", "increase", "decrease", "decreased", "increased", "changed", "grown", "grew", "surge",
			"surged", "rose", "risen" };
	public static ArrayList<String> relName;
	public static ArrayList<ArrayList<String>> KEYWORDS;

	public static Integer NUM_RELATIONS;

	static {
		String keywordJson = null;
		try {
			keywordJson = FileUtils.readFileToString(new File(keyWordFile));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// System.out.println(keywordJson);

		JSONParser parser = new JSONParser();
		KEYWORDS = new ArrayList<ArrayList<String>>();

		try {
			JSONObject arr = (JSONObject) parser.parse(keywordJson);
			NUM_RELATIONS = arr.keySet().size();

			relName = new ArrayList<String>(arr.keySet());

			for (String rel : relName) {
				ArrayList<String> value = (ArrayList<String>) arr.get(rel);
				KEYWORDS.add(value);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private KeywordData() throws IOException {

	}

	public static void main(String args[]) throws IOException {
		new KeywordData();
	}
}
