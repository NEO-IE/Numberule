//sg
/**
 * This class wraps a relation. 
 * The extractor will return an object of this class, and then we'll need to augment the 
 * argument or the relation involved.
 */
package util;

public class Relation {
	public Relation(Word arg1, Word arg2, Word keyword, String relName) {
		this.arg1 = arg1;
		this.arg2 = arg2;
		this.relName = relName;
		this.keyword = keyword;
	}
	Word arg1;
	Word arg2;
	Word keyword;
	String relName;
	@Override
	public String toString() {
		return relName + "(" + arg1.val + ", " + arg2.val + ")";
	}
}
