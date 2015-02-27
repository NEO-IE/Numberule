package util;

public class Country extends Word {
	
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
