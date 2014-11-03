package com.example.placebadges;

public class Badge {
	
	public String country;
	public String countryCode;
	public String place;
	
	public Badge(String country, String countryCode, String place){
		this.country = country;
		this.countryCode = countryCode;
		this.place = place;
	}
	
	public String toString(){
		return place + ", " + country + "(" + countryCode + ")";
	}
	
	public boolean equals(Object obj) {
		
		if (!(obj instanceof Badge))
			return false;
		
		if (obj == this)
			return true;
		
		Badge b = (Badge)obj;
		
		if(b.countryCode.equals(this.countryCode) && b.place.equals(this.place))
			return true;
		
		return false;

	}
}
