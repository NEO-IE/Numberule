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
		this.country = (util.Country)arg1;
		this.num = arg2;
		this.relName = relName;
		this.keyword = keyword;
	}
	
	public Relation(Relation r) {
		country = new Country(r.getCountry());
		
	}
	
	Country country;
	Number num;
	Word keyword;
	String relName;
	@Override
	public String toString() {
		return relName + "(" + country.getVal() + ", " + keyword.getVal() + ", " + num.getVal() + " " + num.unit + ")";
	}
	public Word getCountry() {
		return country;
	}
	public Number getNumber() {
		return num;
	}
	public Word getKeyword() {
		return keyword;
	}
	public String getRelName() {
		return relName;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((country == null) ? 0 : country.hashCode());
		result = prime * result + ((keyword == null) ? 0 : keyword.hashCode());
		result = prime * result + ((relName == null) ? 0 : relName.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Relation other = (Relation) obj;
		if (country == null) {
			if (other.country != null)
				return false;
		} else if (!country.equals(other.country))
			return false;
		if (keyword == null) {
			if (other.keyword != null)
				return false;
		} else if (!keyword.equals(other.keyword))
			return false;
		if (relName == null) {
			if (other.relName != null)
				return false;
		} else if (!relName.equals(other.relName))
			return false;
		return true;
	}
}
