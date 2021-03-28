package kueres.location;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.geojson.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import de.fraunhofer.iosb.ilt.sta.model.Id;
import de.fraunhofer.iosb.ilt.sta.model.Location;
import de.fraunhofer.iosb.ilt.sta.model.Thing;
import de.fraunhofer.iosb.ilt.sta.model.ext.EntityList;
import de.fraunhofer.iosb.ilt.sta.service.SensorThingsService;
import kueres.utility.Utility;

/**
 * 
 * The default implementation of the location service.
 *
 * @author Tim Engbrocks, tim.engbrocks@student.kit.edu
 * @version 1.0
 * @since Feb 24, 2021
 *
 */

@Service
public class DefaultLocationService implements LocationService {
	
	/**
	 * The URL to a Nominatim server used for address/coordinate conversion.
	 */
	@Value("${kueres.nominatim-url}")
	private String nominatimUrl;

	/**
	 * The URL to the FROST service used for all functions related to points of interest.
	 */
	@Value("${kueres.frost-url}")
	private String frostUrl;
	
	private int polygonSteps = 8;
	private double radiusEarth = 6371000;

	public double[] addressToCoordinates(String address) {

		Utility.LOG.trace("DefaultLocationService.addressToCoordinates called");
		
		String transformedAddress = address.replace(" ", "+");
		String queryUrl = this.nominatimUrl + "/search?q=" + transformedAddress + "&format=json";

		try {

			URL url = new URL(queryUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);

			int status = connection.getResponseCode();

			Reader streamReader = null;
			if (status > 299) {
				streamReader = new InputStreamReader(connection.getErrorStream());
			} else {
				streamReader = new InputStreamReader(connection.getInputStream());
			}
			BufferedReader in = new BufferedReader(streamReader);
			String inputLine;
			StringBuffer content = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				content.append(inputLine);
			}
			in.close();
			connection.disconnect();

			String response = content.toString();
			
			ObjectMapper mapper = new ObjectMapper();
			JsonNode array = mapper.readTree(response);
			if (array.isArray() && array.size() > 0) {
				JsonNode firstResult = array.get(0);

				if (firstResult.has("lat") && firstResult.has("lon")) {

					JsonNode lon = firstResult.get("lon");
					JsonNode lat = firstResult.get("lat");
					double[] coordinates = new double[2];
					coordinates[0] = Double.parseDouble(lon.asText());
					coordinates[1] = Double.parseDouble(lat.asText());
					return coordinates;

				}

			}

			return null;

		} catch (IOException e) {
			Utility.LOG.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		
	}

	public String coordinatesToAddress(double[] coordinates) {

		Utility.LOG.trace("DefaultLocationService.coordinatesToAddress called");
		
		if (coordinates == null || coordinates.length != 2) {
			Utility.LOG.info("Coordinates not in correct format");
			return "";
		}

		String queryUrl = this.nominatimUrl + "/reverse?lon=" + coordinates[0] + "&lat=" + coordinates[1]
				+ "&format=json";
		
		try {

			URL url = new URL(queryUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);

			int status = connection.getResponseCode();

			Reader streamReader = null;
			if (status > 299) {
				streamReader = new InputStreamReader(connection.getErrorStream());
			} else {
				streamReader = new InputStreamReader(connection.getInputStream());
			}
			BufferedReader in = new BufferedReader(streamReader);
			String inputLine;
			StringBuffer content = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				content.append(inputLine);
			}
			in.close();
			connection.disconnect();

			String response = content.toString();
			
			ObjectMapper mapper = new ObjectMapper();
			JsonNode node = mapper.readTree(response);
			if (node.has("display_name")) {
				return node.get("display_name").asText();
			}

		} catch (IOException e) {
			Utility.LOG.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}

		return null;
		
	}

	
	public String addPOI(String name, double[] coordinates) {

		Utility.LOG.trace("DefaultLocationService.addPOI called");
		
		try {

			URL frostEndpoint = new URL(this.frostUrl);
			SensorThingsService sts = new SensorThingsService(frostEndpoint);
			
			Location poiLocation = new Location();
			poiLocation.setName(name);
			poiLocation.setDescription(name);
			poiLocation.setEncodingType("application/vnd.geo+json");
			poiLocation.setLocation(new Point(coordinates[0], coordinates[1]));
			
			Thing poi = new Thing();
			poi.setName(name);
			poi.setDescription(name);
			poi.getLocations().add(poiLocation);
			sts.things().create(poi);
			return poi.getId().getJson();

		} catch (IOException | ServiceFailureException e) {
			Utility.LOG.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}

	}

	public void removePOI(String id) {

		Utility.LOG.info("DefaultLocationService.removePOI called");
		
		try {

			URL frostEndpoint = new URL(this.frostUrl);
			SensorThingsService sts = new SensorThingsService(frostEndpoint);
			
			Thing thing = sts.things().find(Id.tryToParse(id));
			sts.delete(thing);
			
			Location location = sts.locations().find(Id.tryToParse(id));
			sts.locations().delete(location);
			
		} catch (IOException | ServiceFailureException e) {
			Utility.LOG.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}

	}

	public List<String> findInRadius(double radius, double[] center) {
		
		Utility.LOG.trace("DefaultLocationService.findInRadius called");
		
		try {

			URL frostEndpoint = new URL(this.frostUrl);
			SensorThingsService sts = new SensorThingsService(frostEndpoint);
			
			double sphereAngle = (360 * radius) / (Math.PI * this.radiusEarth);
			double convertedRadius = Math.sin(sphereAngle / 2) * this.radiusEarth;
			
			double[] firstPoint = getPoint(center, convertedRadius, 0);
			String polygon = firstPoint[0] + " " + firstPoint[1];
			for (double angle = (2 * Math.PI) / this.polygonSteps; angle < (2 * Math.PI); angle += (2 * Math.PI) / this.polygonSteps) {
				double[] point = getPoint(center, convertedRadius, angle);
				polygon += "," + String.format("%f", point[0]) + " " + String.format("%f", point[1]);
			}
			polygon += "," + firstPoint[0] + " " + firstPoint[1];
			
			EntityList<Location> pois = sts.locations().query().filter("st_within(location, geography'POLYGON ((" + polygon + "))')").list();
			
			List<String> ids = new ArrayList<String>();
			for (Location poi : pois) {
				ids.add(poi.getId().getJson());
			}

			return ids;
			
		} catch (IOException | ServiceFailureException e) {
			Utility.LOG.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		
	}

	public double calculateDistance(double[] pointA, double[] pointB) {

		Utility.LOG.trace("DefaultLocationService.calculateDistance called");
		
		// Using haversine formula with R=6371km
		double deltaLatRadians = (pointB[0] - pointA[0]) * (Math.PI / 180);
		double deltaLonRadians = (pointB[1] - pointA[1]) * (Math.PI / 180);
		double latARadians = pointA[0] * (Math.PI / 180);
		double latBRadians = pointB[0] * (Math.PI / 180);

		double a1 = Math.pow(Math.sin(deltaLatRadians / 2), 2);
		double a2 = Math.cos(latARadians) * Math.cos(latBRadians) * Math.pow(Math.sin(deltaLonRadians / 2), 2);
		double a = a1 + a2;

		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

		double d = this.radiusEarth * c;

		return d;
		
	}
	
	private double[] getPoint(double[] center, double radius, double angle) {
		
		Utility.LOG.trace("DefaultLocationService.getPoint called");
		
		double x = center[0] + (radius * Math.sin(angle));
		double y = center[1] + (radius * Math.cos(angle));
		
		return new double[] {x, y};
		
	}

}