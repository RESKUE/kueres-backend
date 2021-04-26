package kueres.base;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kueres.utility.Utility;

/**
 * 
 * The BaseEntity provides each entity with a unique identifier.
 * The BaseEntity is used as generic superclass for the rest of the kueres.base package.
 *
 * @author Tim Engbrocks, tim.engbrocks@student.kit.edu
 * @version 1.0.0
 * @since Apr 26, 2021
 *
 */

@MappedSuperclass
public abstract class BaseEntity<E extends BaseEntity<E>> {

	/**
	 * Create an entity from a JSON string.
	 * @param json - the JSON string
	 * @param fields - all fields of the entity
	 * @param typeClass - the type of entity that should be created
	 * @return the created entity.
	 * @throws JsonMappingException if the JSON string can not be processed
	 * @throws InstantiationException if the JSON string can not be processed
	 * @throws IllegalAccessException if the JSON string can not be processed
	 * @throws IllegalArgumentException if the JSON string can not be processed
	 * @throws InvocationTargetException if the JSON string can not be processed
	 * @throws NoSuchMethodException if the JSON string can not be processed
	 * @throws SecurityException if the JSON string can not be processed
	 * @throws JsonProcessingException if the JSON string can not be processed
	 */
	public static <T extends BaseEntity<T>> T createEntityFromJSON(String json, String[] fields, Class<T> typeClass) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, JsonMappingException, JsonProcessingException {
		
		T entity = typeClass.getDeclaredConstructor().newInstance();
		
		T parsed = new ObjectMapper().readValue(json, typeClass);
		
		for (String field : fields) {
			if (json.contains("\"" + field + "\":")) {
				PropertyDescriptor descriptor = org.springframework.beans.BeanUtils.getPropertyDescriptor(typeClass, field);
				descriptor.getWriteMethod().invoke(entity, descriptor.getReadMethod().invoke(parsed));
			}
		}
		
		return entity;
		
	}
	
	/**
	 * Test if a JSON string contains a field by its name.
	 * @param json - the JSON string
	 * @param fieldName - the fields name
	 * @return if the JSON string contains the field.
	 */
	public static boolean containsFields(String json, String fieldName) {
		return json.contains("\"" + fieldName + "\":");
	}
	
	/**
	 * A list of all updateable fields that the entity has.
	 */
	@JsonIgnore
	public abstract String[] getUpdateableFields();
	
	/**
	 * The identifier of a BaseEntity.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id = -1L;
	/**
	 * The path mapping for this field.
	 */
	public static final String ID = "id";
	public Long getId() { return this.id; }
	
	/**
	 * Update the fields of instance of this class.
	 * Fields that are not populated in the updated data will not be changed.
	 * @param json - the updated data in JSON format.
	 * @throws JsonMappingException if the JSON string can not be processed
	 * @throws InstantiationException if the JSON string can not be processed
	 * @throws IllegalAccessException if the JSON string can not be processed
	 * @throws IllegalArgumentException if the JSON string can not be processed
	 * @throws InvocationTargetException if the JSON string can not be processed
	 * @throws NoSuchMethodException if the JSON string can not be processed
	 * @throws SecurityException if the JSON string can not be processed
	 * @throws JsonProcessingException if the JSON string can not be processed
	 */
	public abstract void applyPatch(String json) throws JsonMappingException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, JsonProcessingException;
	
	/**
	 * Deserialize the services' BaseEntity from a JSON string.
	 * @param json - the JSON string
	 * @return The deserialized entity.
	 * @throws JsonMappingException when the entity could not be deserialized.
	 * @throws JsonProcessingException when the entity could not be deserialized.
	 */
	@SuppressWarnings("unchecked")
	public E getEntityFromJSON(String json) throws JsonMappingException, JsonProcessingException  {
		
		Utility.LOG.trace("BaseEntity.getEntityFromJSON called.");
		
		return new ObjectMapper().readValue(
				json, 
				(Class<E>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
		
	}
	
}
