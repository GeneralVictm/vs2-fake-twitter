package de.hska.vs2;

import java.util.Date;

/**
 * Created by Alex on 10.05.2017.
 */
public class Post {
    @Autowired
    private RedisRepository repository;
    private String pid;
    private String uid;
    private Date timestamp;
    private String content;


    public String getUid() {
        return uid;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getContent() {
        return content;
    }


    public Post (String uid, Date timestamp, String content){
        this.uid = uid;
        this.timestamp = new Date();
        this.content = content;

    }

    public Post (String pid) throws Exception {
        this.pid = pid;
        this.uid = repository.getPostUser(pid);
        this.timestamp = repository.getPostTimestamp(pid);
        this.content = repository.getPostContent(pid);

    }
}
