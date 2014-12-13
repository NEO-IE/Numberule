package util;

public class Number extends Word {
	String unit;
	boolean hasUnit;
	public Number(Integer idx, String str) {
		super(idx, str);
		hasUnit = false;
	}
	
	public void setUnit(String unit) {
		this.unit = unit;
		hasUnit = true;
	}
	
	public String getUnit() {
		return unit;	
	}
	

}
