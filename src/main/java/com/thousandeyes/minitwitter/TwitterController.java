package com.thousandeyes.minitwitter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.thousandeyes.minitwitter.aop.UserContextTLS;
import com.thousandeyes.minitwitter.dtos.ErrorResult;
import com.thousandeyes.minitwitter.dtos.ListResult;
import com.thousandeyes.minitwitter.dtos.SingleResult;
import com.thousandeyes.minitwitter.dtos.entities.Message;
import com.thousandeyes.minitwitter.dtos.entities.User;
import com.thousandeyes.minitwitter.repositories.DataService;


/**
 * @author Miguel
 * TwitterController handles MiniTwitter requests in REST.
 * Note that every call must be specify the http parameter "key".
 */
@Controller
public class TwitterController {
	
	private static final Logger logger = LoggerFactory.getLogger(TwitterController.class);
	
	@Autowired
	DataService dserv;
	
	/*
	 * For all Exceptions except IllegalAccessException, wraps the message in a ErrorResult and returns.
	 * For authorization, the IllegalAccessException is handled and the error 401 is returned. 
	 */
	@ExceptionHandler(Exception.class)
	public ErrorResult handleException(Exception ex, HttpServletRequest request, HttpServletResponse response) {
		logger.error("handleException: " + ex.toString());
		//ex.getCause().printStackTrace();
		
		if(ex instanceof IllegalAccessException) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return null;
		}
			
		response.setStatus(HttpServletResponse.SC_CONFLICT);		
		return new ErrorResult(ex.getMessage(), ex.toString());
	}

	/*
	 * Administration method.
	 */
    @RequestMapping(value = "/user/create", method = RequestMethod.GET)
    @ModelAttribute
    public @ResponseBody SingleResult<String> createUserWithKey(@RequestParam String username, @RequestParam(required=false) String name, @RequestHeader("Accept") String acceptHeader) throws Exception {
    	logger.info("createUserWithKey");
        if(!UserContextTLS.isAdmin())
    		throw new IllegalAccessException("Unauthorized access!");
        
    	return  new SingleResult<String>(dserv.createUserWithKey(new User(username,name)));
    }
    
    /*
	 * Administration method.
	 */
    @RequestMapping(value = "/user/delete-all", method = RequestMethod.GET)
    @ModelAttribute
    public @ResponseBody SingleResult<Integer> deleteAllUsers(@RequestHeader("Accept") String acceptHeader) throws Exception {
    	logger.info("createUserWithKey");
        if(!UserContextTLS.isAdmin())
    		throw new IllegalAccessException("Unauthorized access!");
        
    	return new SingleResult<Integer>(dserv.deleteAll());
    }
	
    /*
     * Returns a specific user from the parameter id or else username or else key.
     * If no paramaters are specified, it returns the current logged user.
     */
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    @ModelAttribute
    public @ResponseBody User getUser(@RequestParam(required=false) Integer id, @RequestParam(required=false) String username, @RequestParam(required=false) String userkey, @RequestHeader("Accept") String acceptHeader, HttpServletResponse response) throws Exception {
    	response.setStatus(HttpServletResponse.SC_ACCEPTED);
    	logger.info("getUser");
    	if(id==null && username==null && userkey==null) {
    		logger.info("getUserNULL");
    		return dserv.getUserByExample(null);
    	}
    	
    	User user = new User(username,"");
    	user.setId(id);
    	user.setKey(userkey);
    	return dserv.getUserByExample(user);
    }
    
    /*
     * Returns all users. If logged user is admin, he can see the keys of each user.
     */
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    @ModelAttribute
    public @ResponseBody ListResult<User> getUsers(@RequestHeader("Accept") String acceptHeader) throws Exception {
    	logger.info("getUsers");
    	return this.dserv.getUsers();
    }
    
    /*
     * Get the list of people a user is following as well as followers of the user.
     * If parameter is not specified, the username of the current logged user is considered.
     */
    @RequestMapping(value = "/users/followed-by-and-following", method = RequestMethod.GET)
    @ModelAttribute
    public @ResponseBody ListResult<User> getUsersFollowedByAndFollowing(@RequestParam(required=false) String username, @RequestHeader("Accept") String acceptHeader) throws Exception {
    	logger.info("getUsersFollowedByAndFollowing");
    	return this.dserv.getUsersFollowedByAndFollowing(username);
    }
    
    /*
     * Start following a user. Cannot follow self.
     */
    @RequestMapping(value = "/user/follow", method = RequestMethod.GET)
    @ModelAttribute
    public @ResponseBody SingleResult<Integer> followUser(@RequestParam String username, @RequestHeader("Accept") String acceptHeader) throws Exception {
    	logger.info("followUser");
    	return new SingleResult<Integer>(dserv.followUser(username));
    }
    
    /*
     * Unfollow a user.
     */
    @RequestMapping(value = "/user/unfollow", method = RequestMethod.GET)
    @ModelAttribute
    public @ResponseBody SingleResult<Integer> unfollowUser(@RequestParam String username, @RequestHeader("Accept") String acceptHeader) throws Exception {
    	logger.info("unfollowUser");
    	return new SingleResult<Integer>(dserv.unfollowUser(username));
    }
    
    /*
     * Read all tweets for a given user (include self-tweets and people being followed by user).
     * An extra "search=" argument can be used to further filter tweets based on keyword.
     * If parameter is not specified, the username of the current logged user is considered.
     */
    @RequestMapping(value = "/messages", method = RequestMethod.GET)
    @ModelAttribute
    public @ResponseBody ListResult<Message> getMessages(@RequestParam(required=false) String username, @RequestParam(required=false) String search, @RequestHeader("Accept") String acceptHeader) throws Exception {
    	logger.info("getMessages");
    	return dserv.getMessagesFiltered(username, search);
    }
    
    /*
	 * Administration method
	 */
    @RequestMapping(value = "/messages-all", method = RequestMethod.GET)
    @ModelAttribute
    public @ResponseBody ListResult<Message> getAllMessages(@RequestParam(required=false) String username, @RequestParam(required=false) String search, @RequestHeader("Accept") String acceptHeader) throws Exception {
    	logger.info("getAllMessages");
        if(!UserContextTLS.isAdmin())
    		throw new IllegalAccessException("Unauthorized access!");
        
    	return dserv.getAllMessages();
    }
    
    /*
     * Write a tweet. Only the first "@username" will be considered, within the text parameter, 
     * to address the message to a user.
     */
    @RequestMapping(value = "/write-message", method = RequestMethod.GET)
    @ModelAttribute
    public @ResponseBody SingleResult<Integer> writeMessage(@RequestParam String text, @RequestHeader("Accept") String acceptHeader) throws Exception {
    	logger.info("writeMessage");
    	return new SingleResult<Integer>(dserv.writeMessage(text));
    }
    
    
}
