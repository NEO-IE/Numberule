package meta;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class KeywordData {
	public String keyWordFile = "/mnt/a99/d0/ashishm/workspace/depbased/data/keywords.json";
	public String modifiers[];
	public ArrayList<String> relName;
	public ArrayList<ArrayList<String>> KEYWORDS;
	
	public Integer NUM_RELATIONS;
	
	
	public KeywordData() throws IOException{
		modifiers = new String[]
				{"change", "up", "down", "males", "females", "male", "female",
				"growth", "increase", "decrease", "decreased", "increased", 
				"changed", "grown", "grew", "surge", "surged", "rose"};
		String keywordJson = FileUtils.readFileToString(new File(keyWordFile));
		
		//System.out.println(keywordJson);
		
		JSONParser parser=new JSONParser();
		KEYWORDS = new ArrayList<ArrayList<String>>();
		
		try {
			JSONObject arr = (JSONObject) parser.parse(keywordJson);
			NUM_RELATIONS = arr.keySet().size();
			
			relName = new ArrayList<String>(arr.keySet());
			
			for(String rel: relName){
				ArrayList<String> value = (ArrayList<String>) arr.get(rel);
				KEYWORDS.add(value);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String args[]) throws IOException{
		new KeywordData();
	}
}
