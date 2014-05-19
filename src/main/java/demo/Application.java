package demo;

import demo.domain.Message;
import demo.domain.Tag;
import demo.domain.User;
import org.apache.commons.logging.LogFactory;
import org.neo4j.graphdb.GraphDatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.config.Neo4jConfiguration;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.rest.SpringRestGraphDatabase;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.social.twitter.api.SearchOperations;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;


import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Configuration
@ComponentScan
@EnableTransactionManagement
@Import(RepositoryRestMvcConfiguration.class)
@EnableScheduling
@EnableNeo4jRepositories
@EnableAutoConfiguration
public class Application extends Neo4jConfiguration {

    public Application() {
        setBasePackage(User.class.getPackage().getName());
    }


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    GraphDatabaseService graphDatabaseService(Environment environment) {
        return new SpringRestGraphDatabase(environment.getProperty("neo4j.host"));
    }

    @Bean
    TwitterTemplate twitterTemplate(Environment environment) {
        String bearerToken = environment.getProperty("twitter.bearerToken");
        return new TwitterTemplate(bearerToken);
    }
}


@Service
class Importer {

    SearchOperations searchOperations;

    @Autowired
    void setTwitterTemplate(TwitterTemplate twitterTemplate) {
        this.searchOperations = twitterTemplate.searchOperations();
    }

    @Autowired
    UserRepository userRepository;

    @Autowired
    TweetRepository tweetRepository;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    void setEnvironment(Environment environment) {
        this.query = environment.getProperty("twitter.query");
    }

    String query;

    @Scheduled(initialDelay = 1000 * 10, fixedRate = 10 * 1000)
    void importSearchResults() {

        searchOperations.search(this.query, 200).getTweets().stream().forEach((source) -> {
            User user = userRepository.save(new User(source.getUser()));
            Message tweet = new Message(source.getId(), user, source.getText());
            source.getEntities().getMentions().forEach(mention -> tweet.addMention(userRepository.save(new User(mention.getId(), mention.getName(), mention.getScreenName()))));
            source.getEntities().getHashTags().forEach(tag -> tweet.addTag(tagRepository.save(new Tag(tag.getText()))));
            tweetRepository.save(tweet);
            LogFactory.getLog(getClass()).info("imported tweet " + tweet.toString() + ".");
        });
    }

}

@RepositoryRestResource(collectionResourceRel = "tweets", path = "tweets")
interface TweetRepository extends GraphRepository<Message> {

    Message findByTweetId(Long id);

    @Query("MATCH (User)-[:POSTED]->(tweet)-[:TAGGED]->(t:Tag) RETURN  t.tag  as tag, count(*) as frequency order by frequency  desc limit 10 ")
    List<Map<String, Object>> trendingTweets();

    Collection<Message> findByTagsTag(String tag);
}

@RepositoryRestResource(collectionResourceRel = "users", path = "users")
interface UserRepository extends GraphRepository<User> {

    @Query("MATCH (me:User {user:{name}})-[:POSTED]->(tweet)-[:MENTIONS]->(user) WHERE me <> user  RETURN distinct user")
    Set<User> suggestFriends(@Param("name") String user);

    User findByUser(@Param("0") String user);
}


@RepositoryRestResource(collectionResourceRel = "tags", path = "tags")
interface TagRepository extends GraphRepository<Tag> {
}