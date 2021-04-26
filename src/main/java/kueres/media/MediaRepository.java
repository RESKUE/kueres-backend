package kueres.media;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 
 * The repository for MediaEntities.
 *
 * @author Tim Engbrocks, tim.engbrocks@student.kit.edu
 * @version 1.0.0
 * @since Apr 26, 2021
 *
 */

@Repository
public interface MediaRepository extends JpaRepository<MediaEntity, Long> {

}
