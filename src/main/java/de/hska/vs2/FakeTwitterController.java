
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

public class FakeTwitterController {

	public String index() {
		return "index";
	}
	
	@RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
	public void process(final HttpServletRequest request, final HttpServletResponse response,
            final ServletContext servletContext, final ITemplateEngine templateEngine, @PathVariable("id") int id) {
		
		WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		User user = new User(SecurityInfo.getUid());
		Post[] posts;
		User[] follower;
		User[] following;
		
		if (user.getId() == id) {
			ctx.setVariable("currentUser.name", user.getName());
			ctx.setVariable("followingCount", user.getCountFollowing());
			ctx.setVariable("followerCount", user.getCountFollower());
			String[] timeline = RedisRepository.getGlobalTimeline(user.getId());
			for (int i = 0; i < timeline.length; i++) {
				posts[i] = new Post(timeline[i]) ;
			}
			ctx.setVariable("globalPostList", posts);
			String[] timeline = RedisRepository.getPersonalTimeline(user.getId());
			for (int i = 0; i < timeline.length; i++) {
				posts[i] = new Post(timeline[i]) ;
			}
			ctx.setVariable("lokalPostList", posts);
			String[] follow = RedisRepository.getFollower(user.getId());
			for (int i = 0; i < timeline.length; i++) {
				follower[i] = new User(follow[i]) ;
			}
			ctx.setVariable("followerList", follower);
			String[] follow = RedisRepository.getFollower(user.getId());
			for (int i = 0; i < timeline.length; i++) {
				following[i] = new User(follow[i]) ;
			}
			ctx.setVariable("followingList", following);
			templateEngine.process("fake-twitter", ctx, response.getWriter());
		} else {
			User user2 = new User(id);
			ctx.setVariable("currentUser.name", user2.getName());
			ctx.setVariable("followingCount", user2.getCountFollowing());
			ctx.setVariable("followerCount", user2.getCountFollower());
			String[] timeline = RedisRepository.getPersonalTimeline(id);
			for (int i = 0; i < timeline.length; i++) {
				posts[i] = new Post(timeline[i]) ;
			}
			ctx.setVariable("lokalPostList", posts);
			String[] follow = RedisRepository.getFollower(user.getId());
			for (int i = 0; i < timeline.length; i++) {
				follower[i] = new User(follow[i]) ;
			}
			ctx.setVariable("followerList", follower);
			String[] follow = RedisRepository.getFollower(user.getId());
			for (int i = 0; i < timeline.length; i++) {
				following[i] = new User(follow[i]) ;
			}
			ctx.setVariable("followingList", following);
			templateEngine.process("otherProfile", ctx, response.getWriter());
		}
		
	}
	
	@RequestMapping(value = "/users", method = RequestMethod.POST)
    public String newPost() {
        
    }
	
	@RequestMapping(value = "/users", method = RequestMethod.POST)
    public String follow() {
        
    }
	
	@RequestMapping(value = "/users", method = RequestMethod.POST)
    public String unfollow() {
        
    }
	
	
}
