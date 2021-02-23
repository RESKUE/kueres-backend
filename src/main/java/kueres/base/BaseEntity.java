package kueres.base;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

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
	public abstract void applyPatch(E details);
	
}
