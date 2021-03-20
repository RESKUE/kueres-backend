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
 * @version 1.0
 * @since Feb 22, 2021
 *
 */

@MappedSuperclass
public abstract class BaseEntity<E extends BaseEntity<E>> {

	public static <T extends BaseEntity<T>> T createEntityFromJSON(String json, String[] fields, Class<T> typeClass) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, JsonMappingException, JsonProcessingException {
		
		T entity = typeClass.getDeclaredConstructor().newInstance();
		
		T parsed = new ObjectMapper().readValue(json, typeClass);
		
		for (String field : fields) {
			if (json.contains("\"" + field + "\":")) {
				PropertyDescriptor descriptor = org.springframework.beans.BeanUtils.getPropertyDescriptor(typeClass, field);
//				descriptor.getWriteMethod().invoke(entity, descriptor.getPropertyType().cast(descriptor.getReadMethod().invoke(parsed)));
				descriptor.getWriteMethod().invoke(entity, descriptor.getReadMethod().invoke(parsed));
			}
		}
		
		return entity;
		
	}
	
	protected boolean containsFields(String json, String fieldName) {
		return json.contains("\"" + fieldName + "\":");
	}
	
	@JsonIgnore
	public abstract String[] getUpdateableFields();
	
	/**
	 * The identifier of a BaseEntity.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id = -1;
	/**
	 * The path mapping for this field.
	 */
	public static final String ID = "id";
	public long getId() { return this.id; }
	
	/**
	 * Update the fields of instance of this class.
	 * Fields that are not populated in the updated data will not be changed.
	 * @param details - the updated data.
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
