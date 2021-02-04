package kueres.location;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.geojson.Point;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import de.fraunhofer.iosb.ilt.sta.model.Location;
import de.fraunhofer.iosb.ilt.sta.model.Thing;
import de.fraunhofer.iosb.ilt.sta.model.ext.EntityList;
import de.fraunhofer.iosb.ilt.sta.service.SensorThingsService;
import kueres.utility.Utility;

@Service
public class DefaultLocationService implements LocationService {

	private String nominatimUrl = "https://nominatim.openstreetmap.org";

	private String frostUrl = "http://localhost:5438/FROST-Server/v1.0/";
	
	private int polygonSteps = 8;
	private double radiusEarth = 6371000;

	@Override
	public double[] addressToCoordinates(String address) {

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

					JsonNode lat = firstResult.get("lat");
					JsonNode lon = firstResult.get("lon");
					double[] coordinates = new double[2];
					coordinates[0] = Double.parseDouble(lat.asText());
					coordinates[1] = Double.parseDouble(lon.asText());
					return coordinates;

				}

			}

			return null;

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public String coordinatesToAddress(double[] coordinates) {

		if (coordinates == null || coordinates.length != 2) {
			Utility.LOG.info("Coordinates not in correct format");
			return "";
		}

		String queryUrl = this.nominatimUrl + "/reverse?lat=" + coordinates[0] + "&lon=" + coordinates[1]
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

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public void addPOI(long id, String name, double[] coordinates) {

		try {

			URL frostEndpoint = new URL(this.frostUrl);
			SensorThingsService sts = new SensorThingsService(frostEndpoint);
			
			Location poiLocation = new Location();
			poiLocation.setName(String.valueOf(id));
			poiLocation.setDescription(name);
			poiLocation.setEncodingType("application/vnd.geo+json");
			poiLocation.setLocation(new Point(coordinates[0], coordinates[1]));
			sts.locations().create(poiLocation);
			
			Thing poi = new Thing();
			poi.setName(String.valueOf(id));
			poi.setDescription(name);
			poi.getLocations().add(poiLocation);
			sts.things().create(poi);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ServiceFailureException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void removePOI(long id) {

		try {

			URL frostEndpoint = new URL(this.frostUrl);
			SensorThingsService sts = new SensorThingsService(frostEndpoint);

			EntityList<Thing> pois = sts.things().query().filter("startswith(name,'" + id + "')").list();
			for (Thing poi : pois) {
				if (poi.getName().equals(String.valueOf(id))) {
					sts.things().delete(poi);
					return;
				}
			}
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ServiceFailureException e) {
			e.printStackTrace();
		}

	}

	@Override
	public List<Long> findInRadius(double radius, double[] center) {
		
		try {

			URL frostEndpoint = new URL(this.frostUrl);
			SensorThingsService sts = new SensorThingsService(frostEndpoint);
			
			double sphereAngle = (360 * radius) / (Math.PI * this.radiusEarth);
			double convertedRadius = Math.sin(sphereAngle / 2) * this.radiusEarth;
			
			double[] firstPoint = getPoint(center, convertedRadius, 0);
			String polygon = firstPoint[0] + " " + firstPoint[1];
			for (double angle = (2 * Math.PI) / this.polygonSteps; angle < (2 * Math.PI); angle += (2 * Math.PI) / this.polygonSteps) {
				double[] point = getPoint(center, convertedRadius, angle);
				polygon += "," + point[0] + " " + point[1];
			}
			polygon += "," + firstPoint[0] + " " + firstPoint[1];
			
			EntityList<Location> pois = sts.locations().query().filter("st_within(location, geography'POLYGON ((" + polygon + "))')").list();
			
			List<Long> ids = new ArrayList<Long>();
			for (Location poi : pois) {
				Utility.LOG.info("found: ({},{}) at {}", poi.getId(), poi.getName(), poi.getLocation());
				ids.add((long) poi.getId().getValue());
			}

			return ids;
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ServiceFailureException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public double calculateDistance(double[] pointA, double[] pointB) {

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
		
		double x = center[0] + (radius * Math.sin(angle));
		double y = center[1] + (radius * Math.cos(angle));
		
		return new double[] {x, y};
		
	}

}