package util;

import edu.stanford.nlp.ling.CoreLabel;

public class Country extends Word {
	
	public CoreLabel token;
	
	public Country(CoreLabel token){
		super(-1, token.toString()); //dummy insertion
		this.token = token;
	}
	
	public Country(Integer idx, String str) {
		super(idx, str);
	}
	public Country(Integer idx, String str, int startOff, int endOff) {
		super(idx, str, startOff, endOff);
	}
	public Country(Word w) {
		super(w);
	}
}
