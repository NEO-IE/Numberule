package meta;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
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
	static HashSet<String> modifyingTypesSet;
	final static String MODDING_TYPES_LIST_FILE = "data/modtypes";
	static
	{
		modifyingTypesSet = new HashSet<>();
		try
		{
		BufferedReader br = new BufferedReader(new FileReader(new File(MODDING_TYPES_LIST_FILE)));
		String type_line = null;
		while(null != (type_line = br.readLine())) {
			System.out.println(type_line);
			modifyingTypesSet.add(type_line);
		}
		br.close();
		} catch(IOException ioe) {
			System.out.println(ioe);
		}
	}
	public static boolean isModifier(String dependencyType) {
		// TODO Auto-generated method stub
		return modifyingTypesSet.contains(dependencyType);
	}
		
}
