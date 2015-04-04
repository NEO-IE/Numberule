package iitb.rbased.util;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class Number extends Word {
	String unit;
	boolean hasUnit;
	float flat_val;
	
	public Number(Integer idx, String str) {
		super(idx, str);
		hasUnit = false;
		unit = "";
	}
	
	public void setFlatVal(float flat_val){
		this.flat_val = flat_val;
	}
	
	public float getFlatVal(){
		return flat_val;
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
	
	public static Double getDoubleValue(String num){
		java.lang.Number number = null;
		for (Locale l : Locale.getAvailableLocales()) {
			NumberFormat format = NumberFormat.getInstance(l);
			try {
				number = format.parse(num);
			} catch (ParseException e) {
				continue;
			}
			break;
		}
		if(null == number){
			return null;
		}
		Double numVal = number.doubleValue();
		return numVal;
	}

}
