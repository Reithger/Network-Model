package network;

import java.util.ArrayList;
import java.util.Arrays;

public class Address {
	
//---  Instance Variables   -------------------------------------------------------------------

	private ArrayList<String> points;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public Address() {
		points = new ArrayList<String>();
	}
	
	public Address(String in) {
		points = new ArrayList<String>();
		String[] bre = in.split("\\.");
		for(String s : bre) {
			points.add(s);
		}
	}
	
//---  Operations   ---------------------------------------------------------------------------

	public Address prefixMatch(Address other) {
		StringBuilder sb = new StringBuilder();
		sb.append(points.get(0));
		for(int i = 1; i < points.size(); i++) {
			if(points.get(i).equals(other.getAddressPiece(i))) {
				sb.append("." + points.get(i));
			}
			else {
				break;
			}
		}
		return new Address(sb.toString());
	}
	
	public Address tear() {
		String[] ad = getAddress().split("\\.");
		if(ad.length == 1) {
			return new Address();
		}
		String reb = "";
		for(int i = 0; i < ad.length - 2; i++) {
			reb += ad[i] + ".";
		}
		reb += ad[ad.length - 2];
		return new Address(reb);
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	public String getAddress() {
		if(points.size() == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < points.size() - 1; i++) {
			sb.append(points.get(i) + ".");
		}
		sb.append(points.get(points.size()-1));
		return sb.toString();
	}
	
	public String getAddressPiece(int position) {
		if(position < points.size()) {
			return points.get(position);	
		}
		return null;
	}
	
	public int getLength() {
		return points.size();
	}
	
//---  Mechanics   ----------------------------------------------------------------------------
	
	public boolean equals(Address a) {
		System.out.println(a.getAddress() + " " + getAddress());
		return getAddress().equals(a.getAddress());
	}
	
	public String toString() {
		return getAddress();
	}
	
}
