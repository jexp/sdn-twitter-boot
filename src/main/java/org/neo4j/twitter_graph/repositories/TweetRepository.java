package org.neo4j.twitter_graph.repositories;

import org.neo4j.twitter_graph.domain.Tweet;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Collection;

/**
 * @author mh
 * @since 24.07.12
 */
@RepositoryRestResource(collectionResourceRel = "tweets", path = "tweets")
public interface TweetRepository extends GraphRepository<Tweet> {
    Tweet findByTweetId(Long id);
    Collection<Tweet> findByTagsTag(String tag);
}
