package kueres.query;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

public class SortBuilder {

	public static Sort buildSort(String[] params) {
		return Sort.by(assembleOrders(params));
	}
	
	private static List<Order> assembleOrders(String[] params) {
		List<Order> orders = new ArrayList<Order>();
		for (String parameter : params) {
			String[] typeAndDirection = parameter.split(";");
			if (typeAndDirection.length == 2) {
				Sort.Direction direction = getDirection(typeAndDirection[1]);
				if (direction != null) {
					orders.add(new Order(direction, typeAndDirection[0]));
				}
				
			}
			
		}
		return orders;
	}
	
	private static Sort.Direction getDirection(String direction) {
		if (direction.equals("desc")) {
			return Sort.Direction.DESC;
		} else if (direction.equals("asc")) {
			return Sort.Direction.ASC;
		}
		return null;
	}
	
}
