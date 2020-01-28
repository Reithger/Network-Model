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
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	public String getAddress() {
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
	
//---  Mechanics   ----------------------------------------------------------------------------
	
	public String toString() {
		return getAddress();
	}
	
}
