import java.util.ArrayList;

import util.Country;
import util.Pair;
import util.Relation;
import util.Word;
import util.Number;

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
	
	public static final Integer NUM_RELATIONS = 11;
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
	 * keywords. If a keyword is present, also sets the value of keyword to it
	 * @param path
	 * @return
	 */
	static boolean isExtraction(ArrayList<Word> path, String keywords[], Word keyword) {
		boolean keywordPresent = false;
		boolean modifierPresent = false;

		for(String kw : keywords) {
			keyword = hasKeyword(path, kw.toLowerCase());
			keywordPresent = (keyword != null);
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
	
	/**
	 * Returns the keyword (if any that is present on the path), if there is no keyword, returns null
	 * @param wordsOnPath
	 * @param keyword
	 * @return
	 */
	static Word hasKeyword(ArrayList<Word> wordsOnPath, String keyword) {
		for(Word w : wordsOnPath) {
			if(w.val.equals(keyword)) {
				return w;
			}
		}
		return null;
	}

	
	/**
	 * Returns all the relations that can exist between the argPair, path is the list of words
	 * that appear in the dependency graph between the argPair
	 * @param argPair
	 * @param path
	 * @return
	 */
	public static ArrayList<Relation> getExtractions(Pair<Country, Number> argPair, ArrayList<Word> path) {
		//System.out.println(path);
		//for fast searching, we will first create a map 
		ArrayList<Relation> res = new ArrayList<Relation>();
		Word keyword = null;
		for(int i = 0; i < NUM_RELATIONS; i++) {
			if(isExtraction(path, KEYWORDS[i], keyword)) {
				assert(keyword != null);
				res.add(new Relation(argPair.first, argPair.second, keyword, relName[i]));
			}
		}
		return res;
	}
}	