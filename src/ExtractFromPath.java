import java.io.IOException;
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
	public static final Integer FDI = 1;
	public static final Integer GOODS = 2;
	public static final Integer ELEC = 3;
	public static final Integer CO2 = 4;
	public static final Integer INF = 5;
	public static final Integer INTERNET = 6;
	public static final Integer GDP = 7;
	public static final Integer LIFE = 8;
	public static final Integer POP = 8;
	public static final Integer DIESEL = 9;

	/*
	 * 
	 * public static final Integer NUM_RELATIONS = 11; static String relName[] =
	 * {"AGL", "FDI", "GOODS", "ELEC", "CO2", "INF", "INTERNET", "GDP", "LIFE",
	 * "POP", "DIESEL"}; static String KEYWORDS[][] = { {"area", "land",
	 * "land area"}, {"foreign", "FDI", "direct", "investments"}, {"goods"},
	 * {"Electricity", "kilowatthors", "Terawatt"}, {"Carbon",
	 * "Carbon Emission", "CO2"}, {"Inflation", "Price Rise"}, {"Internet",
	 * "users"}, {"Gross domestic", "GDP"}, {"life", "life expectancy"},
	 * {"population", "people", "inhabitants", "natives"}, {"diesel"}, }; static
	 * String modifiers[] = {"change", "up", "down", "males", "females", "male",
	 * "female", "growth", "increase", "decrease", "decreased", "increased",
	 * "changed"};
	 */

	/**
	 * Checks whether the given dependency path is an extraction for the
	 * relation defined by the given keywords. Returns the keyword if one is
	 * present in the sentence, otherwise returns null
	 * 
	 * @param path
	 * @return
	 */

	static Word hasKeyword(ArrayList<Word> path, ArrayList<String> keywords,
			String[] modifiers) {

		boolean keywordPresent = false;
		boolean modifierPresent = false;
		Word keywordTemp = null;
		for (String kw : keywords) {
			keywordTemp = hasKeyword(path, kw.toLowerCase());
			keywordPresent = (keywordTemp != null);
			if (keywordPresent) {
				break;
			}
		}
		if (!keywordPresent)
			return null;
		// System.out.println("kw : " + keywordPresent + ", mod: " +
		// modifierPresent);
		return keywordPresent && !modifierPresent ? keywordTemp : null;
	}

	/**
	 * Returns the keyword (if any that is present on the path), if there is no
	 * keyword, returns null
	 * 
	 * @param wordsOnPath
	 * @param keyword
	 * @return
	 */
	static Word hasKeyword(ArrayList<Word> wordsOnPath, String keyword) {
		for (Word w : wordsOnPath) {
			if (w.val.equals(keyword)) {
				return w;
			}
		}
		return null;
	}

	/**
	 * Returns all the relations that can exist between the argPair, path is the
	 * list of words that appear in the dependency graph between the argPair
	 * 
	 * @param argPair
	 * @param path
	 * @return
	 */
	public static ArrayList<Relation> getExtractions(
			Pair<Country, Number> argPair, ArrayList<Word> path) {

		KeywordData kwd = null;
		try {
			kwd = new KeywordData();
		} catch (IOException e) {
			e.printStackTrace();
		}

		ArrayList<Relation> res = new ArrayList<Relation>();
		boolean modifierPresent = false;
		// if modifiers are present, cannot be extraction
		for (String mod : kwd.modifiers) {
			modifierPresent = modifierPresent || path.contains(mod);
			if (modifierPresent) {
				return res; // return empty result
			}
		}
		Word keyword = null;
		for (int i = 0; i < kwd.NUM_RELATIONS; i++) {

			if (null != (keyword = isExtraction(path, kwd.KEYWORDS.get(i),
					kwd.modifiers))) {
				assert (keyword != null);
				res.add(new Relation(argPair.first, argPair.second, keyword,
						kwd.relName.get(i)));
			}
		}
		return res;
	}
}
