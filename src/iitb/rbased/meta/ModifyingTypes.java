package iitb.rbased.meta;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

/**
 * This class stores a list of typed dependencies that can be considered
 * to be type modifying
 * @author aman
 *
 */
public class ModifyingTypes {
	static HashSet<String> relDepTypes;
	static HashSet<String> keywordDepTypes;
	final static String RelationModTypes = "/mnt/a99/d0/aman/scala/workspace/StanfordCoreNLPInterface/data/modtypes";
	final static String KeywordModTypes = "/mnt/a99/d0/aman/scala/workspace/StanfordCoreNLPInterface/data/keyword_mod_types";
	static
	{
		relDepTypes = new HashSet<>();
		try
		{
		BufferedReader br = new BufferedReader(new FileReader(new File(RelationModTypes)));
		String type_line = null;
		while(null != (type_line = br.readLine())) {
			relDepTypes.add(type_line);
		}
		br.close();
		} catch(IOException ioe) {
			System.out.println(ioe);
		}
		
		keywordDepTypes = new HashSet<>();
		try
		{
		BufferedReader br = new BufferedReader(new FileReader(new File(KeywordModTypes)));
		String type_line = null;
		while(null != (type_line = br.readLine())) {
			keywordDepTypes.add(type_line);
		}
		br.close();
		} catch(IOException ioe) {
			System.out.println(ioe);
		}
		
		
	}
	public static boolean isRelDep(String dep) {
		return relDepTypes.contains(dep);
	}
	public static boolean isKeywordDep(String dep) {
	
		return keywordDepTypes.contains(dep);
	}
		
}
