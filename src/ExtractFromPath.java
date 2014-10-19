import java.util.ArrayList;

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
	public static final Integer DIESEL = 8;
	
	static String keywords[][] = {
		{"area", "land", "land area"},
		{"Foreign", "FDI"},
		{"goods"},
		{"Electricity"},
		{"Carbon", "Carbon Emission", "CO2"},
		{"Inflation", "Price Rise"},
		{"Internet"},
		{"Gross domestic", "GDP"},
		{"life", "life expectancy"},
		{"population", "people"},
		{"diesel"},
	};
	static String modifiers[] = {"change", "increase", "decrease", "decreased", "increased", "changed"};
	static boolean isExtraction(ArrayList<String> path) {
		boolean keywordPresent = false;
		boolean modifierPresent = false;

		for(String kw : keywords) {
			keywordPresent = keywordPresent || path.contains(kw);
		}
		if(!keywordPresent) return false;
		for(String mod : modifiers) {
			modifierPresent = modifierPresent || path.contains(mod);
		}
		System.out.println("kw : " + keywordPresent + ", mod: " + modifierPresent);
		return keywordPresent && !modifierPresent;
	}
}