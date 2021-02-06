package kueres.base;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BaseEntity<E extends BaseEntity<E>> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	public static final String ID = "id";
	public long getId() { return this.id; }
	
	public abstract void applyPatch(E details);
	
}
