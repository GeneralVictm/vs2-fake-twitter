package de.hska.vs2;

import org.springframework.beans.factory.annotation.Autowired;

import java.rmi.server.UID;
import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;

/**
 * Created by Alex on 10.05.2017.
 */
public class User {

    @Autowired
    private RedisRepository repository;


    private String uname;
    private String uid;
    private long countFollower;
    private long countFollowing;
    private String[] follower;
    private String[] following;
    private String password;

    public String getName() {
        return uname;
    }

    public String getId() {
        return uid;
    }

    public long getCountFollower() {
        return countFollower;
    }

    public long getCountFollowing() {
        return countFollowing;
    }

    public String[] getFollower() {
        return follower;
    }

    public String[] getFollowing() {
        return following;
    }

    public String getPass() {
        return password;
    }

    public User(String uid){
        this.uname = repository.getUserName(uid);
        uid = uid;
        this.countFollower = repository.getFollowerCount(uid);
        this.countFollowing = repository.getFollowingCount(uid);
        this.follower = repository.getFollower(uid);
        this.following = repository.getFollowing(uid);
        this.password = repository.getUserPassword(uid);

    }

}
