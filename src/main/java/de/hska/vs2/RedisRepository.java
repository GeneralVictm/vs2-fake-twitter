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

    public boolean auth(String uname, String pass) {
        String uid = template.opsForValue().get("uname:" + uname + ":uid");
        BoundHashOperations<String, String, String> userOps = template.boundHashOps("uid:" + uid + ":user");
        return userOps.get("pass").equals(pass);
    }

    public String addAuth(String uname, long timeout, TimeUnit tUnit) {
        String uid = template.opsForValue().get("uname:" + uname + ":uid");
        String auth = UUID.randomUUID().toString();
        template.boundHashOps("uid:" + uid + ":auth").put("auth", auth);
        template.expire("uid:" + uid + ":auth", timeout, tUnit);
        template.opsForValue().set("auth:" + auth + ":uid", uid, timeout, tUnit);
        return auth;
    }

    public void deleteAuth(String uname) {
        String uid = template.opsForValue().get("uname:" + uname + ":uid");
        String authKey = "uid:" + uid + ":auth";
        String auth = (String) template.boundHashOps(authKey).get("auth");
        List<String> keysToDelete = Arrays.asList(authKey, "auth:" + auth + ":uid");
        template.delete(keysToDelete);
    }

    public String getUserName(String uid) {
        //TODO aus dem User Hash lesen!
        return (String) template.opsForHash().get("uid:" + uid + ":user", "uname");
    }

    public String getUserId(String uname) {

        return template.opsForValue().get("uname:" + uname + ":uid");
    }

    public String getUserPassword(String uid) {
        // sollte nicht nötig sein?
        return "";
    }

    public void changePassword(String uid, String pass) {
        // change password of uid to pass
    }

    public boolean isUsernameFree(String uname) {
        return !template.opsForSet().isMember("user", uname);
    }

    public long getFollowerCount(String uid) {
        return template.opsForSet().size("uid:" + uid + "follower");
    }

    public long getFollowingCount(String uid) {
        return template.opsForSet().size("uid:" + uid + "following");
    }

    public String[] getFollower(String uid) {
        Set<String> followers = template.opsForSet().members("uid:" + uid + "follower");
        return followers.toArray(new String[followers.size()]);
    }

    public String[] getFollowing(String uid) {
        Set<String> following = template.opsForSet().members("uid:" + uid + "following");
        return following.toArray(new String[following.size()]);
    }

    public String[] getAllUsers() {//---------------------------------------------------------------------------------------------------
        Set<String> users = template.opsForSet().members("uid:" + uid + ":user");
        return users.toArray(new String[users.size()]);
    }


    public String[] getGlobalTimeline(long start, long end) {
        List<String> timeline = template.opsForList().range("global_timeline", start, end);
        return timeline.toArray(new String[timeline.size()]);
    }

    public String[] getGlobalTimeline() {
        long length = template.opsForList().size("global_timeline");
        List<String> timeline = template.opsForList().range("global_timeline", 0, length);
        return timeline.toArray(new String[timeline.size()]);
    }

    public String[] getPersonalTimeline(String uid, long start, long end) {
        List<String> timeline = template.opsForList().range("uid:" + uid + "personal_timeline", start, end);
        return timeline.toArray(new String[timeline.size()]);
    }

    public String[] getPersonalTimeline(String uid) {
        long length = template.opsForList().size("uid:" + uid + "personal_timeline");
        List<String> timeline = template.opsForList().range("uid:" + uid + "personal_timeline", 0, length);
        return timeline.toArray(new String[timeline.size()]);
    }

    public String getPostContent(String pid) {
        return template.opsForValue().get("pid:" + pid + ":content");
    }

    public String getPostUser(String pid) {
        return template.opsForValue().get("pid:" + pid + ":uid");
    }

    public Date getPostTimestamp(String pid) throws Exception {
        String timestamp = template.opsForValue().get("pid:" + pid + ":timestamp");
        DateFormat format = new SimpleDateFormat("dd.MM.yyyy-HH:mm", Locale.GERMAN);
        return format.parse(timestamp);
    }

    public User addUser(User user) {
        // write data from user object into redis
        String uid = UUID.randomUUID().toString();
        String key = "uid:" + uid + ":user";        //kann man :user nicht weg lassen?!
        template.opsForHash().put(key, "uid", uid);
        //template.opsForHash().put(key, "firstName", user.getFirstName());
        //template.opsForHash().put(key, "lastName", user.getLastName());
        template.opsForHash().put(key, "uname", user.getName());
        template.opsForHash().put(key, "pass", user.getPass());

        template.opsForValue().set("uname:" + user.getName() + "uid", uid);
        template.opsForSet().add("user", key);
        user.setUid(uid);
        return user;
    }

    public Post addPost(Post post) {
        // write data from post object into redis TODO: and add new post to timelines
        String pid = UUID.randomUUID().toString();
        template.opsForValue().set("pid:" + pid + "content", post.getContent());
        template.opsForValue().set("pid:" + pid + "uid", post.getUser().getId());

        DateFormat format = new SimpleDateFormat("dd.MM.yyyy-HH:mm", Locale.GERMAN);
        String timestamp = format.format(post.getTimestamp());
        template.opsForValue().set("pid:" + pid + "timestamp", timestamp);

        // ADD TIMELINES-----------------------------------------------------------------------------------------------------------

        String key = "personalTimeline:" + post.getUser().getId();
        template.opsForHash().put(key, "post" + pid, pid);          //hier post + pid, damit mehrere Posts mit eindeutigem Key sind.

        String[] followers = getFollower(post.getUser().getId());

        for (int i = 0; i < followers.length; i++) {
            String followerID = followers[i];
            key = "GlobalTimeline:" + followerID;
            template.opsForHash().put(key, "post" + pid, pid);
        }

        post.setPid(pid);
        return post;
    }


    public void addFollowing(String uid, String followedID) {
        // add uid to followed follower-list and followedID to user's followed-list
        // add posts to timeline?
        //Set<String> following = template.opsForSet().members("uid:" + uid + "following");
        //Set<String> following = template.opsForSet().members("uid:" + uid + "follower");
        String key = "uid:" + uid + "following";
        template.opsForSet().add(key, followedID);

        key = "uid:" + followedID + "follower";
        template.opsForSet().add(key, uid);

    }

    public void deleteFollowing(String uid, String followedID) {
        // add uid to followed follower-list and followedID to user's followed-list
        // add posts to timeline?
        //Set<String> following = template.opsForSet().members("uid:" + uid + "following");
        //Set<String> following = template.opsForSet().members("uid:" + uid + "follower");
        String key = "uid:" + uid + "following";
        template.opsForSet().remove(key, followedID);

        key = "uid:" + followedID + "follower";
        template.opsForSet().remove(key, uid);

    }

    public User deleteUser(User user) {
        // write data from user object into redis
        String uid = user.getId();
        String key = "uid:" + uid + ":user";
        template.opsForHash().delete(key, "uid");
        //template.opsForHash().put(key, "firstName", user.getFirstName());
        //template.opsForHash().put(key, "lastName", user.getLastName());
        template.opsForHash().delete(key, "uname");
        template.opsForHash().delete(key, "pass");

        key = "uname:" + user.getName() + "uid";
        template.delete(key);
        //template.opsForValue().set("uname:" + user.getName() + "uid", uid);
        template.opsForSet().remove("user", key);
        user.setUid(uid);
        return user;
    }

    public Post deletePost(Post post) {
        // write data from post object into redis TODO: and add new post to timelines
        String pid = post.getPid();

        String key = "pid:" + pid + "content";
        template.delete(key);
        key = "pid:" + pid + "uid";
        template.delete(key);
        //template.opsForValue().set("pid:" + pid + "content", post.getContent());
        //template.opsForValue().set("pid:" + pid + "uid", post.getUser().getId());


        String timestamp = format.format(post.getTimestamp());
        key = "pid:" + pid + "timestamp";
        template.delete(key);
        //template.opsForValue().set("pid:" + pid + "timestamp", timestamp);

        // DEL FROM TIMELINES-----------------------------------------------------------------------------------------------------------

         key = "personalTimeline:" + post.getUser().getId();
        template.opsForHash().delete(key, "post" + pid);          //hier post + pid, damit mehrere Posts mit eindeutigem Key sind.

        String[] followers = getFollower(post.getUser().getId());

        for (int i = 0; i < followers.length; i++) {
            String followerID = followers[i];
             key = "GlobalTimeline:" + followerID;
            template.opsForHash().delete(key, "post" + pid);
        }

        post.setPid(pid);
        return post;
    }



}