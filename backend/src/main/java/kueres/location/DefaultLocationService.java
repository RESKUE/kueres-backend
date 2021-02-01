package kueres.location;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class DefaultLocationService implements LocationService {

	@Override
	public double[] addressToCoordinates(String address) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String coordinatesToAddress(double[] coordinates) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addPOI(long id, String name, double[] coordinates) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removePOI(long id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Long> findInRadius(double radius, double[] center) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double calculateDistance(double[] pointA, double[] pointB) {
		// TODO Auto-generated method stub
		return 0;
	}

}
