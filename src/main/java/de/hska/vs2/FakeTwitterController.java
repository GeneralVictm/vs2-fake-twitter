
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
		
		ctx.setVariable("currentUser.name");
		ctx.setVariable("followingCount");
		ctx.setVariable("followerCount");
		ctx.setVariable("post.user.name");
		ctx.setVariable("post.timestamp");
		ctx.setVariable("post.content");
		ctx.setVariable("");
		
		templateEngine.process("fake-twitter", ctx, response.getWriter());
	}
}
