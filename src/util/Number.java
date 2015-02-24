package util;

import edu.stanford.nlp.ling.CoreLabel;


public class Number extends Word {
	String unit;
	boolean hasUnit;
	CoreLabel token;
	
	public Number(CoreLabel token){
		super(-1, token.toString()); //dummy word.
		this.token = token;
	}
	
	public Number(Integer idx, String str) {
		super(idx, str);
		hasUnit = false;
		unit = "";
	}
	
	public void setUnit(String unit) {
		this.unit = unit;
		hasUnit = true;
	}
	
	public String getUnit() {
		return unit;	
	}
	
	public Number(Number n) {
		super(n.getIdx(), n.getVal());
		unit = new String(n.getUnit());
		hasUnit = n.hasUnit;
	}
	
	

}
