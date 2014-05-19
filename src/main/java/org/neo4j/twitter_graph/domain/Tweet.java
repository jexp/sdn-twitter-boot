package org.neo4j.twitter_graph.domain;

import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@NodeEntity
public class Tweet {
    @GraphId Long id;

    @Indexed(unique=true) Long tweetId;

    String text;

    @Fetch @RelatedTo(type="POSTED", direction = Direction.INCOMING) User poster;
    @Fetch @RelatedTo(type="TAGGED")   Collection<Tag> tags=new HashSet<Tag>();
    @Fetch @RelatedTo(type="MENTIONS") Set<User> mentions=new HashSet<User>();
    @Fetch @RelatedTo(type="SOURCE")   Tweet source;

    public Tweet() {
    }

    public Tweet(long tweetId, User poster, String text) {
        this.tweetId = tweetId;
        this.poster = poster;
        this.text = text;
    }

    public void addMention(User mention) {
        this.mentions.add(mention);
    }
    public Long getId() {
        return id;
    }

    public Long getTweetId() {
        return tweetId;
    }

    public User getPoster() {
        return poster;
    }

    @Override
    public String toString() {
        return "Tweet " + tweetId + ": " + text + " by " + poster;
    }

    public Set<User> getMentions() {
        return mentions;
    }

    public Collection<Tag> getTags() {
        return tags;
    }

    public void addTag(Tag tag) {
        tags.add(tag);
    }

    public void setSource(Tweet source) {
        this.source = source;
    }

    public Tweet getSource() {
        return source;
    }

    public String getText() {
        return text;
    }
}
