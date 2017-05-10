package de.hska.vs2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Sascha on 09.05.2017.
 *
 * Contains all methods to get and set data from redis.
 */
@Repository
public class RedisRepository {
    @Autowired
    private StringRedisTemplate template;

    //TODO: ist nur C&P aus den Vorlesungsfolien, muss ggfs. noch an eigene Bedürfnisse angepasst werden

    public boolean auth (String uname, String pass) {
        String uid = template.opsForValue().get("uname:" + uname + ":uid");
        BoundHashOperations<String, String, String> userOps = template.boundHashOps("uid:" + uid + ":user");
        return userOps.get("pass").equals(pass);
    }

    public String addAuth (String uname, long timeout, TimeUnit tUnit) {
        String uid = template.opsForValue().get("uname:" + uname + ":uid");
        String auth = UUID.randomUUID().toString();
        template.boundHashOps("uid:" + uid + ":auth").put("auth", auth);
        template.expire("uid:" + uid + ":auth", timeout, tUnit);
        template.opsForValue().set("auth:" + auth + ":uid", uid, timeout, tUnit);
        return auth;
    }

    public void deleteAuth (String uname) {
        String uid = template.opsForValue().get("uname:" + uname + ":uid");
        String authKey = "uid:" + uid + ":auth";
        String auth = (String) template.boundHashOps(authKey).get("auth");
        List<String> keysToDelete = Arrays.asList(authKey, "auth:"+auth+":uid");
        template.delete(keysToDelete);
    }

    public String getUserName (String uid) {
        return template.opsForValue().get("uid:" + uid + ":uname");
    }

    public String getUserId (String uname) {
        return template.opsForValue().get("uname:" + uname + ":uid");
    }

    public String getUserPassword (String uid) {
        // sollte nicht nötig sein?
        return "";
    }

    public void changePassword (String uid, String pass) {
        // change password of uid to pass
    }

    public long getFollowerCount( String uid) {
        return template.opsForSet().size("uid:" + uid + "follower");
    }

    public long getFollowingCount (String uid) {
        return template.opsForSet().size("uid:" + uid + "following");
    }

    public String[] getFollower (String uid) {
        Set<String> followers = template.opsForSet().members("uid:" + uid + "follower");
        return followers.toArray(new String[followers.size()]);
    }

    public String[] getFollowing (String uid) {
        Set<String> following = template.opsForSet().members("uid:" + uid + "following");
        return following.toArray(new String[following.size()]);
    }

    public String[] getGlobalTimeline (String uid, long start, long end) {
        List<String> timeline = template.opsForList().range("uid:" + uid + "global_timeline", start, end);
        return timeline.toArray(new String[timeline.size()]);
    }

    public String[] getGlobalTimeline (String uid) {
        long length = template.opsForList().size("uid:" + uid + "global_timeline");
        List<String> timeline = template.opsForList().range("uid:" + uid + "global_timeline", 0, length);
        return timeline.toArray(new String[timeline.size()]);
    }

    public String[] getPersonalTimeline (String uid, long start, long end) {
        List<String> timeline = template.opsForList().range("uid:" + uid + "personal_timeline", start, end);
        return timeline.toArray(new String[timeline.size()]);
    }

    public String[] getPersonalTimeline (String uid) {
        long length = template.opsForList().size("uid:" + uid + "personal_timeline");
        List<String> timeline = template.opsForList().range("uid:" + uid + "personal_timeline", 0, length);
        return timeline.toArray(new String[timeline.size()]);
    }

    public String getPostContent (String pid) {
        return template.opsForValue().get("pid:" + pid + ":content");
    }

    public String getPostUser (String pid) {
        return template.opsForValue().get("pid:" + pid + ":uid");
    }

    public Date getPostTimestamp (String pid) throws Exception {
        String timestamp = template.opsForValue().get("pid:" + pid + ":timestamp");
        DateFormat format = new SimpleDateFormat("dd.MM.yyyy-HH:mm", Locale.GERMAN);
        return format.parse(timestamp);
    }

    public void addUser (User user) {
        // write data from user object into redis
    }

    public void addPost (Post post) {
        // write data from post object into redis
    }

    public void addFollowing (String user, String follower) {
        // add follower to users list and user to followers list
        // add posts to timeline?
    }

    //TODO: delete-Funktionen? (User, Posts, Follower ...)

}