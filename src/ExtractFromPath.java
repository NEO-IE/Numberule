import java.util.ArrayList;

import util.Word;

//sg

//The path should have one of the keywords, and should not have any 
//modifying words
public class ExtractFromPath {
	public static final Integer AGL = 0;
	public static final Integer FDI 	= 1;
	public static final Integer GOODS = 2;
	public static final Integer ELEC = 3;
	public static final Integer CO2 = 4;
	public static final Integer INF = 5;
	public static final Integer INTERNET = 6;
	public static final Integer GDP = 7;
	public static final Integer LIFE = 8;
	public static final Integer POP = 8;
	public static final Integer DIESEL = 9;
	
	public static final Integer NUM_RELATIONS = 10;
	static String relName[] = {"AGL", "FDI", "GOODS", "ELEC", "CO2", "INF", "INTERNET", "GDP", "LIFE", "POP", "DIESEL"};
	static String KEYWORDS[][] = {
		{"area", "land", "land area"},
		{"foreign", "FDI", "direct", "investments"},
		{"goods"},
		{"Electricity", "kilowatthors", "Terawatt"},
		{"Carbon", "Carbon Emission", "CO2"},
		{"Inflation", "Price Rise"},
		{"Internet", "users"},
		{"Gross domestic", "GDP"},
		{"life", "life expectancy"},
		{"population", "people", "inhabitants", "natives"},
		{"diesel"},
	};
	static String modifiers[] = {"change", "up", "down", "males", "females", "male", "female", "growth", "increase", "decrease", "decreased", "increased", "changed"};
	/**
	 * Checks whether the given dependency path is an extraction for the relation defined by the given 
	 * keywords
	 * @param path
	 * @return
	 */
	static boolean isExtraction(ArrayList<Word> path, String keywords[]) {
		boolean keywordPresent = false;
		boolean modifierPresent = false;

		for(String kw : keywords) {
			keywordPresent = keywordPresent || path.contains(kw.toLowerCase());
			if(keywordPresent) {
				break;
			}
		}
		if(!keywordPresent) return false;
		for(String mod : modifiers) {
			modifierPresent = modifierPresent || path.contains(mod);
			if(modifierPresent) {
				break;
			}
		}
		//System.out.println("kw : " + keywordPresent + ", mod: " + modifierPresent);
		return keywordPresent && !modifierPresent;
	}
	
	/*
	 * iterates over all the relations and checks whether the given path is a possible 
	 * extraction
	 */
	
	public static ArrayList<String> getExtractions(ArrayList<Word> path) {
		//System.out.println(path);
		//for fast searching, we will first create a map 
		ArrayList<String> res = new ArrayList<String>();
		for(int i = 0; i < NUM_RELATIONS; i++) {
			if(isExtraction(path, KEYWORDS[i])) {
				res.add(relName[i]);
			}
		}
		return res;
	}
}