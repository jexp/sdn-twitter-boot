package org.neo4j.twitter_graph.repositories;

import org.neo4j.twitter_graph.domain.Tag;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * @author mh
 * @since 24.07.12
 */
@RepositoryRestResource(collectionResourceRel = "tags", path = "tags")
public interface TagRepository extends GraphRepository<Tag> {
}
