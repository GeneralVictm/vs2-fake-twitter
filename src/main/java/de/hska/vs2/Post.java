package de.hska.vs2;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * Created by Alex on 10.05.2017.
 */
public class Post {
    @Autowired
    private RedisRepository repository;
    private String pid;
    private User user;
    private Date timestamp;
    private String content;

    public String getPid() {
        return pid;
    }

    public User getUser() {
        return user;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getContent() {
        return content;
    }

    public boolean setPid(String pid) {
        if (this.pid == null) {
            this.pid = pid;
            return true;
        }
        return false;
    }

    public Post (String uid, Date timestamp, String content){
        this.pid = null;
        this.user = new User(uid);
        this.timestamp = timestamp;
        this.content = content;
    }

    public Post (String pid) throws Exception {
        this.pid = pid;
        this.user = new User(repository.getPostUser(pid));
        this.timestamp = repository.getPostTimestamp(pid);
        this.content = repository.getPostContent(pid);
    }
}
