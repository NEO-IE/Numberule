package util;
/**
 * The need of this class arises from the fact that there we cannot treat words as just words 
 * There is a lot of meta information that is associated with the word and the corresponding index
 * @author aman
 *
 */
public class Word {
	public String val; //the string content of the word
	public Integer idx; //the index of the word in that sentence
	public Word(Integer idx, String str) {
		this.idx = idx;
		this.val = str;
	}
	@Override
	public String toString() {
		return val + ", " + idx.toString();
	}
}
