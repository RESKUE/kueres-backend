package kueres.location;

import java.util.List;

/**
 * 
 * An interface for all functions provided by the location service.
 * A custom implementation for this interface can be provided
 * by implementing this interface and marking the implementation as @PrimaryBean
 *
 * @author Tim Engbrocks, tim.engbrocks@student.kit.edu
 * @version 1.0
 * @since Feb 24, 2021
 *
 */

public interface LocationService {

	/**
	 * Convert an address to coordinates.
	 * @param address - the address that should be converted
	 * @return the corresponding coordinates: {longitude, latitude}.
	 */
	public double[] addressToCoordinates(String address);
	
	/**
	 * Convert coordinates to an address.
	 * @param coordinates - the coordinates that should be converted: {longitude, latitude}
	 * @return the corresponding address.
	 */
	public String coordinatesToAddress(double[] coordinates);
	
	/**
	 * Add a point of interest (POI) to the location service.
	 * @param name - the name of the POI
	 * @param coordinates - the location of the POI: {longitude, latitude}
	 * @return an identifier of the POI.
	 */
	public String addPOI(String name, double[] coordinates);
	
	/**
	 * Remove a point of interest (POI) from the location service.
	 * @param id - the POIs identifier
	 */
	public void removePOI(String id);
	
	/**
	 * Find all points of interest (POIs) in a circle.
	 * @param radius - the radius of the circle.
	 * @param center - the center point of the circle: {longitude, latitude}
	 * @return the identifiers of all POIs in that circle.
	 */
	public List<String> findInRadius(double radius, double[] center);
	
	/**
	 * Calculate the distance in meters between two points.
	 * @param pointA - {longitude, latitude}
	 * @param pointB - {longitude, latitude}
	 * @return the distance between pointA and pointB in meters.
	 */
	public double calculateDistance(double[] pointA, double[] pointB);
	
}
