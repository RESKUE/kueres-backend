package kueres.location;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.net.MalformedURLException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import kueres.KueresTestInitializer;
import kueres.KueresTestTerminator;

@SpringBootTest
@ContextConfiguration(initializers = KueresTestInitializer.class)
@Import(KueresTestTerminator.class)
@TestPropertySource(locations="classpath:test.properties")
@TestInstance(Lifecycle.PER_CLASS)
public class LocationTest {
	
	@Autowired
	private DefaultLocationService service;
	
	@Test
	public void addressCoordinatesConversion() {
		
		String addressWhitehouse = "White House, 1600, Pennsylvania Avenue Northwest, Washington, District of Columbia, 20500, United States";
		double[] coordinatesWhitehouse = new double[] {38.897699700000004,-77.03655315};
		
		String convertedAddress = this.service.coordinatesToAddress(coordinatesWhitehouse);
		double[] convertedCoordinates = this.service.addressToCoordinates(addressWhitehouse);
		
		assertThat(convertedAddress).isNotNull();
		assertThat(convertedAddress).isNotEmpty();
		assertThat(convertedAddress).isEqualTo(addressWhitehouse);
		
		assertThat(convertedCoordinates).isNotNull();
		assertThat(convertedCoordinates).isNotEmpty();
		assertThat(convertedCoordinates[0]).isCloseTo(coordinatesWhitehouse[0], within(0.001));
		assertThat(convertedCoordinates[1]).isCloseTo(coordinatesWhitehouse[1], within(0.001));
		
	}
	
	@Test
	public void pois() throws MalformedURLException, ServiceFailureException {
		
		String name = "test";
		double[] point = new double[] {0, 0};
		
		List<String> ids = this.service.findInRadius(1, point);
		assertThat(ids).isEmpty();
		
		String id = this.service.addPOI(name, point);
		assertThat(id).isNotNull();
		
		List<String> afterAdd = this.service.findInRadius(1, point);
		assertThat(afterAdd.size()).isEqualTo(1);
		assertThat(afterAdd.get(0)).isEqualTo(id);
		
		this.service.removePOI(id);
		
		List<String> afterRemove = this.service.findInRadius(1, point);
		assertThat(afterRemove).isEmpty();
		
	}
	
	@Test
	public void calculateDistance() {
		
		double[] northPole = new double[] {90, 135};
		double[] southPole = new double[] {-90, -45};
		double distanceNorthToSouthPole = 20020.26 * 1000;
		double distance = this.service.calculateDistance(northPole, southPole);
		double error = 1 - (distance / distanceNorthToSouthPole);
		
		assertThat(error).isCloseTo(0.0, within(0.001));
		
	}
	
}
