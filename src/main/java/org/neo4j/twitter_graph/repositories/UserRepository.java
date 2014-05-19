package org.neo4j.twitter_graph.repositories;

import org.neo4j.twitter_graph.domain.User;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Set;

@RepositoryRestResource(collectionResourceRel = "users", path = "users")
public interface UserRepository extends GraphRepository<User> {

    @Query( "MATCH (me:User {user:{name}})-[:POSTED]->(tweet)-[:MENTIONS]->(user)" +
            " WHERE me <> user " +
            " RETURN distinct user")
    Set<User> suggestFriends(@Param("name") String user);

    User findByUser(@Param("0") String user);

}
