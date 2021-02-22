package kueres.location;

import java.util.List;

public interface LocationService {

	public double[] addressToCoordinates(String address);
	public String coordinatesToAddress(double[] coordinates);
	
	public String addPOI(String name, double[] coordinates);
	public void removePOI(String id);
	
	public List<String> findInRadius(double radius, double[] center);
	
	public double calculateDistance(double[] pointA, double[] pointB);
	
}
