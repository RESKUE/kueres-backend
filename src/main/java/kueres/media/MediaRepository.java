package kueres.media;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 
 * The repository for MediaEntities.
 *
 * @author Tim Engbrocks, tim.engbrocks@student.kit.edu
 * @version 1.0
 * @since Feb 25, 2021
 *
 */

@Repository
public interface MediaRepository extends JpaRepository<MediaEntity, Long> {

}
