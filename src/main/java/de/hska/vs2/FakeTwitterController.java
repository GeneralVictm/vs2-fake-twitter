package de.hska.vs2;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;

public class FakeTwitterController {
	
	private RedisRepository repository;

	public String index() {
		return "index";
	}
	
	@RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
	public void process(final HttpServletRequest request, final HttpServletResponse response,
						final ServletContext servletContext, final ITemplateEngine templateEngine, @PathVariable("id") String id) throws Exception {
		
		WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		User user = new User(SecurityInfo.getUid());
		Post[] gposts;
		Post[] pposts;
		User[] follower;
		User[] following;
		
		if (user.getId().equals(id)) {
			ctx.setVariable("currentUser.name", user.getName());
			ctx.setVariable("followingCount", user.getCountFollowing());
			ctx.setVariable("followerCount", user.getCountFollower());
			String[] timeline = repository.getGlobalTimeline();
			gposts = new Post[timeline.length];
			for (int i = 0; i < timeline.length; i++) {
				gposts[i] = new Post(timeline[i]) ;
			}
			ctx.setVariable("globalPostList", gposts);
			timeline = repository.getPersonalTimeline(user.getId());
			pposts = new Post[timeline.length];
			for (int i = 0; i < timeline.length; i++) {
				pposts[i] = new Post(timeline[i]) ;
			}
			ctx.setVariable("lokalPostList", pposts);
			String[] follow = repository.getFollower(user.getId());
			follower = new User[follow.length];
			for (int i = 0; i < timeline.length; i++) {
				follower[i] = new User(follow[i]) ;
			}
			ctx.setVariable("followerList", follower);
			follow = repository.getFollower(user.getId());
			following = new User[follow.length];
			for (int i = 0; i < timeline.length; i++) {
				following[i] = new User(follow[i]);
			}
			ctx.setVariable("followingList", following);
			templateEngine.process("fake-twitter", ctx, response.getWriter());
		} else {
			User user2 = new User(id);
			ctx.setVariable("currentUser.name", user2.getName());
			ctx.setVariable("followingCount", user2.getCountFollowing());
			ctx.setVariable("followerCount", user2.getCountFollower());
			String[] timeline = repository.getPersonalTimeline(id);
			pposts = new Post[timeline.length];
			for (int i = 0; i < timeline.length; i++) {
				pposts[i] = new Post(timeline[i]) ;
			}
			ctx.setVariable("lokalPostList", pposts);
			String[] follow = repository.getFollower(user.getId());
			follower = new User[follow.length];
			for (int i = 0; i < follow.length; i++) {
				follower[i] = new User(follow[i]) ;
			}
			ctx.setVariable("followerList", follower);
			boolean isFollowing = false;
			for(int i = 0; i < follow.length; i++){
				if(follow[i].equals(id)){
					isFollowing = true;
				}
			}
			ctx.setVariable("isFollowing", isFollowing);
			follow = repository.getFollower(user.getId());
			following = new User[follow.length];
			for (int i = 0; i < follow.length; i++) {
				following[i] = new User(follow[i]) ;
			}
			ctx.setVariable("followingList", following);
			templateEngine.process("otherProfile", ctx, response.getWriter());
		}
		
	}
	
	@RequestMapping(value = "/users", method = RequestMethod.POST, params = "action=post")
    public void newPost(@RequestParam("postContent") String content) {
		User user = new User(SecurityInfo.getUid());
		Post post = new Post(user.getId(), new Date(), content);
		repository.addPost(post);
    }
	
	@RequestMapping(value = "/users/{id}", method = RequestMethod.POST, params = "action=follow")
    public void follow(@PathVariable("id") String id) {
		repository.addFollowing(id, SecurityInfo.getUid());
    }
	
	@RequestMapping(value = "/users/{id}", method = RequestMethod.POST, params = "action=unfollow")
    public void unfollow(@PathVariable("id") String id) {
		repository.deleteFollowing(id, SecurityInfo.getUid());
    }
	
	@RequestMapping(value = "/users/{id}", method = RequestMethod.POST)
    public void search(@RequestParam("search") String search, final HttpServletRequest request, final HttpServletResponse response,
					   final ServletContext servletContext) {
		WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ArrayList<User> result = new ArrayList<>();

		String[] allUsers = repository.getAllUsers();
		for (int i = 0; i < allUsers.length; i++) {
			User user = new User(allUsers[i]);
			if (user.getName().contains(search)) {
				result.add(new User(allUsers[i]));
			}
		}
		ctx.setVariable("searchResult",result.toArray());
    }
	
}
