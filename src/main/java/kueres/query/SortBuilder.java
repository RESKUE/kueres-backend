package kueres.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import kueres.utility.Utility;

/**
 * 
 * The SortBuilder constructs Spring Sort objects used for sorting repository requests.
 *
 * @author Tim Engbrocks, tim.engbrocks@student.kit.edu
 * @version 1.0
 * @since Feb 25, 2021
 *
 */

public class SortBuilder {

	/**
	 * Build a Pageable object from sort, page and size query parameters.
	 * @param sort - the sort query parameter
	 * @param page - the page query parameter
	 * @param size - the size query parameter
	 * @return A Pageable object representing the supplied query parameters.
	 */
	public static Pageable buildPageable(Optional<String[]> sort, Optional<Integer> page, Optional<Integer> size) {
		
		Utility.LOG.trace("SortBuilder.buildPageable called");
		
		Sort sorting = Sort.unsorted();		// default sort
		int pageNumber = 0;					// default page number, starts at 0
		int pageSize = 25;					// default page size, 25
		
		if (sort.isPresent()) {
			sorting = SortBuilder.buildSort(sort.get());
		}
		if (page.isPresent()) {
			pageNumber = page.get();
		}
		if (size.isPresent()) {
			pageSize = size.get();
		}
		
		return PageRequest.of(pageNumber, pageSize, sorting);
		
	}
	
	/**
	 * Build a Spring Sort Object from the sort query parameter.
	 * @param sort - the sort query parameter
	 * @return The Spring Sort Object representing the query parameter.
	 */
	public static Sort buildSort(String[] sort) {
		
		Utility.LOG.trace("SortBuilder.buildSort called");
		
		return Sort.by(assembleOrders(sort));
		
	}
	
	private static List<Order> assembleOrders(String[] params) {
		
		Utility.LOG.trace("SortBuilder.assembleOrders called");
		
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
		
		Utility.LOG.trace("SortBuilder.getDirection called");
		
		if (direction.equals("desc")) {
			return Sort.Direction.DESC;
		} else if (direction.equals("asc")) {
			return Sort.Direction.ASC;
		}
		return null;
		
	}
	
}
