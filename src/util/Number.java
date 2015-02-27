package util;

public class Number extends Word {
	String unit;
	boolean hasUnit;
	
	public Number(Integer idx, String str) {
		super(idx, str);
		hasUnit = false;
		unit = "";
	}
	
	public Number(Integer idx, String str, int startOff, int endOff) {
		super(idx, str, startOff, endOff);
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
	
	@Override
	public String toString(){
		return super.getVal() + " " + unit;
	}
	

}
