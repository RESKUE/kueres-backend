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

import kueres.utility.Utility;

@Service
public class DefaultLocationService implements LocationService {

	//http://nominatim.openstreetmap.org/search?q=135+pilkington+avenue,+birmingham&format=json&polygon=1&addressdetails=1
	
	private String nominatimUrl = "http://nominatim.openstreetmap.org";
	
	@Override
	public double[] addressToCoordinates(String address) {
		
		String transformedAddress = address.replace(" ", "+");
		String queryUrl = this.nominatimUrl + "/search?q=" + transformedAddress + "&format=json&polygon=1&addressdetails=1";
		
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
			
			Utility.LOG.info("response: {}", content.toString());
			
			return new double[0];
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
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
