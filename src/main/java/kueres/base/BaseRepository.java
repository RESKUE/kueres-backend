package kueres.base;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 
 * The BaseRepository provides repository functionality for a BaseEntity that is needed for the rest of the kueres.base package.
 *
 * @author Tim Engbrocks, tim.engbrocks@student.kit.edu
 * @version 1.0
 * @since Feb 22, 2021
 *
 */

public interface BaseRepository<E extends BaseEntity<E>> extends JpaRepository<E, Long>, JpaSpecificationExecutor<E>{
	
}
