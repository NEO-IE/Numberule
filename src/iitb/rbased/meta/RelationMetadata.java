package iitb.rbased.meta;


import java.util.HashMap;
import java.util.Set;

public class RelationMetadata {
	private static HashMap<String, String> unitMap = null;
	private static HashMap<String, String> relMap = null;
	static {
			unitMap = new HashMap<String, String>();
			unitMap.put("AGL", "square metre");
			
			unitMap.put("FDI", "united states dollar");
			unitMap.put("GOODS", "united states dollar");
			unitMap.put("GDP", "united states dollar");
			
			unitMap.put("ELEC", "joule");
			
			unitMap.put("CO2", "kilogram");
			
			//unitMap.put("DIESEL", "united states dollar per litre");
			
			unitMap.put("INF", "percent");
			unitMap.put("INTERNET", "percent");
			
			unitMap.put("LIFE", "second");
			
			unitMap.put("POP", "");
			
			relMap = new HashMap<String, String>();
			relMap.put("AG.LND.TOTL.K2", "AGL");
			relMap.put("BN.KLT.DINV.CD", "FDI");
			relMap.put("BX.GSR.MRCH.CD", "GOODS");
			relMap.put("EG.ELC.PROD.KH", "ELEC");
			relMap.put("EN.ATM.CO2E.KT", "CO2");
			//relMap.put("EP.PMP.DESL.CD", "DIESEL");
			relMap.put("FP.CPI.TOTL.ZG", "INF");
			relMap.put("IT.NET.USER.P2", "INTERNET");
			relMap.put("NY.GDP.MKTP.CD", "GDP");
			relMap.put("SP.DYN.LE00.IN", "LIFE");
			relMap.put("SP.POP.TOTL", "POP");
	}
	public static String getUnit(String rel) {
			return unitMap.get(rel);
	}
	public static Set<String> getRelations(){
		return unitMap.keySet();
	}
	public static String getShortenedRelation(String rel){
		return relMap.get(rel);
	}
	public static Set<String> getWorldBankRels(){
		return relMap.keySet();
	}
	
}
