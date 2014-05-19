
package org.neo4j.twitter_graph;

import org.neo4j.graphdb.GraphDatabaseService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.neo4j.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.config.Neo4jConfiguration;
import org.springframework.data.neo4j.rest.SpringRestGraphDatabase;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.IOException;

@EnableNeo4jRepositories(basePackages = "org.neo4j.twitter_graph.repositories")
@EnableTransactionManagement
@Import(RepositoryRestMvcConfiguration.class)
@EnableScheduling
@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = {"org.neo4j.twitter_graph.services"})
public class TwitterApplication extends Neo4jConfiguration {

    public static final String URL = "http://localhost:7474/db/data/";

    public TwitterApplication() {
	     setBasePackage("org.neo4j.twitter_graph.domain");
    }

    @Bean
	public GraphDatabaseService graphDatabaseService() {
        return new SpringRestGraphDatabase(URL);
	}

    @Bean
    public TwitterTemplate twitterTemplate() {
        return new TwitterTemplate(System.getenv("TWITTER_BEARER"));
    }

	public static void main(String[] args) throws IOException {
        SpringApplication.run(TwitterApplication.class, args);
	}
}
