//sg
/**
 * This class wraps a relation. 
 * The extractor will return an object of this class, and then we'll need to augment the 
 * argument or the relation involved.
 */
package util;

/**
 * Represents a relation in the sentence
 * as we know, the relation is made up of 3 things
 * The two arguments, which are words. We make the assumption that the arguments are single words
 * 
 * 
 * @author aman
 *
 */
public class Relation {
	public Relation(Word arg1, Number arg2, Word keyword, String relName) {
		this.arg1 = arg1;
		this.arg2 = arg2;
		this.relName = relName;
		this.keyword = keyword;
	}
	Word arg1;
	Number arg2;
	Word keyword;
	String relName;
	@Override
	public String toString() {
		return relName + "(" + arg1.val + ", " + keyword.val + ", " + arg2.val + " " + arg2.unit + ")";
	}
	public Word getArg1() {
		return arg1;
	}
	public Number getArg2() {
		return arg2;
	}
	public Word getKeyword() {
		return keyword;
	}
	public String getRelName() {
		return relName;
	}
}
