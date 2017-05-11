package de.hska.vs2;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

public class FakeTwitterController {
	
	private RedisRepository repository;

	public String index() {
		return "index";
	}
	
	@RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
	public void process(final HttpServletRequest request, final HttpServletResponse response,
            final ServletContext servletContext, final ITemplateEngine templateEngine, @PathVariable("id") String id) {
		
		WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		User user = new User(SecurityInfo.getUid());
		Post[] posts;
		User[] follower;
		User[] following;
		
		if (user.getId() == id) {
			ctx.setVariable("currentUser.name", user.getName());
			ctx.setVariable("followingCount", user.getCountFollowing());
			ctx.setVariable("followerCount", user.getCountFollower());
			String[] timeline = repository.getGlobalTimeline(user.getId());
			for (int i = 0; i < timeline.length(); i++) {
				posts[i] = new Post(timeline[i]) ;
			}
			ctx.setVariable("globalPostList", posts);
			 timeline = repository.getPersonalTimeline(user.getId());
			for (int i = 0; i < timeline.length(); i++) {
				posts[i] = new Post(timeline[i]) ;
			}
			ctx.setVariable("lokalPostList", posts);
			String[] follow = repository.getFollower(user.getId());
			for (int i = 0; i < timeline.length(); i++) {
				follower[i] = new User(follow[i]) ;
			}
			ctx.setVariable("followerList", follower);
			 follow = repository.getFollower(user.getId());
			for (int i = 0; i < timeline.length(); i++) {
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
			for (int i = 0; i < timeline.length(); i++) {
				posts[i] = new Post(timeline[i]) ;
			}
			ctx.setVariable("lokalPostList", posts);
			 follow = repository.getFollower(user.getId());
			for (int i = 0; i < follow.length(); i++) {
				follower[i] = new User(follow[i]) ;
			}
			ctx.setVariable("followerList", follower);
			boolean isFollowing = false;
			for(int i = 0; i < follow.length(); i++){
				if(follow[i].getId() == id){
					isFollowing = true;
				}
			}
			ctx.setVariable("isFollowing", isFollowing);
			 follow = repository.getFollower(user.getId());
			for (int i = 0; i < follow.length(); i++) {
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
    public void follow(@PathVariable("id") int id) {
        repository.addFollower(id, SecurityInfo.getUid());
    }
	
	@RequestMapping(value = "/users/{id}", method = RequestMethod.POST, params = "action=unfollow")
    public void unfollow(@PathVariable("id") int id) {
        repository.deleteFollower(id, SecurityInfo.getUid());
    }
	
	@RequestMapping(value = "/users/{id}", method = RequestMethod.POST)
    public void search(@RequestParam("search") String search) {
		//String[] users = repository.search(user);
		WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		User[] result;
		/*for (int i = 0; i < users; i++) {
			result[i] = new User(users[i]);
		}*/

		String[] allUsers = repository.getAllUsers();
		int k = 0;
		for (int i = 0; i < allUsers.length(); i++) {
			User user = new User(allUser[i]);
			if (user.getName().contains(search)) {
				result[k] = new User(allUsers[i]);
				k++;
			}
		}
		ctx.setVariable("searchResult",result);
    }
	
}
