package kueres.location;

import java.util.List;

import de.fraunhofer.iosb.ilt.sta.model.Id;

public interface LocationService {

	public double[] addressToCoordinates(String address);
	public String coordinatesToAddress(double[] coordinates);
	
	public void addPOI(long id, String name, double[] coordinates);
	public void removePOI(long id);
	
	public List<Long> findInRadius(double radius, double[] center);
	
	public double calculateDistance(double[] pointA, double[] pointB);
	
}
