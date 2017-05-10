package de.hska.vs2;

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
    private int countFollower;
    private int countFollowing;
    private String[] follower;
    private String[] following;
    private String password;




    public User(String uid){
        this.uname = repository.getName(uid);
        uid = uid;
        this.countFollower = repository.getCountFollowers(uid);
        this.countFollowing = repository.getCountFollowing(uid);
        this.follower = repository.getFollower(uid);
        this.following = repository.getFollowing(uid);
        this.password = repository.getPassword(uid);

    }

}
