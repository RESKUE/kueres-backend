package kueres.location;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import kueres.utility.Utility;

@Service
public class DefaultLocationService implements LocationService {

	//https://nominatim.openstreetmap.org/search?q=135+pilkington+avenue,+birmingham&format=json&polygon=1&addressdetails=1
	
	private String nominatimUrl = "https://nominatim.openstreetmap.org";
	
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
		
		String queryUrl = this.nominatimUrl + "/reverse?lat=" + coordinates[0] + "&lon=" + coordinates[1] + "&format=json";
		
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
		
		//Using haversine formula with R=6371km
		double R = 6371000;
		
		double deltaLatRadians = (pointB[0] - pointA[0]) * (Math.PI/180);
		double deltaLonRadians = (pointB[1] - pointA[1]) * (Math.PI/180);
		double latARadians = pointA[0] * (Math.PI/180);
		double latBRadians = pointB[0] * (Math.PI/180);
		
		double a1 = Math.pow(Math.sin(deltaLatRadians / 2), 2);
		double a2 = Math.cos(latARadians) * Math.cos(latBRadians) * Math.pow(Math.sin(deltaLonRadians / 2), 2);
		double a = a1 + a2;
		
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		
		double d = R * c;
		
		return d;
	}
	
}
