package com.thousandeyes.minitwitter.repositories;

import java.rmi.AlreadyBoundException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.thousandeyes.minitwitter.aop.UserContextTLS;
import com.thousandeyes.minitwitter.dtos.ListResult;
import com.thousandeyes.minitwitter.dtos.entities.Message;
import com.thousandeyes.minitwitter.dtos.entities.User;


/**
 * @author Miguel
 *
 */
@Repository
public class DataService {

	private static final Logger logger = LoggerFactory.getLogger(DataService.class);
	
	private static NamedParameterJdbcTemplate JdbcTemplate;
	
    @Autowired
    public void setDataSource(DataSource dataSource) {
        JdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        logger.info("instantiate JdbcTemplate");
    }
    
    public User getAuthenticatedUser(String key) throws Exception {
    	try {
    		User user = new User();
    		user.setKey(key);
    		return this.getUserByExample(user);
    	} catch (NoSuchElementException ex) {
			throw new IllegalAccessException("Unauthorized access!");
    	}
    }
    
    public String createUserWithKey(User user) throws Exception {
    	if(this.createUser(user) != null)
    		return this.createKey(user.getUsername());
    	else
    		return null;
    }
    
    private Integer createUser(User user) throws AlreadyBoundException {
    	try {
    		Map<String,Object> namedParameters = new HashMap<String, Object>();
    		namedParameters.put("name", user.getName());
    		namedParameters.put("username", user.getUsername());
    		return JdbcTemplate.update(
    	        "insert into users (name, username) values (:name, :username)", namedParameters);
    	} catch(DuplicateKeyException ex) {
    		throw new AlreadyBoundException("That username has already been taken!");
    	}
    }
    
    private String createKey(String username) throws Exception {
    	try {
    		String key = java.util.UUID.randomUUID().toString().replaceAll("-", "").substring(16);
    		Map<String,Object> namedParameters = new HashMap<String, Object>();
    		namedParameters.put("username", username);
    		namedParameters.put("key", key);
    		JdbcTemplate.update(
    				"update `users` set `key` = :key where `username` = :username", namedParameters);
    		return key;
    	} catch(DuplicateKeyException ex) {
    			throw new Exception("We are sorry to inform you that we are not accepting more account registrations at the moment.");
    	}
    }
    
    public User getUserByExample(User user) throws Exception {
    	User result;
    	try{
        	String criteria="";
        	String keyColumn="";
        	Map<String,Object> namedParameters;
        	if (user != null) {
        		if(user.getId() != null) {
        			criteria = "`id` = :id";
        			namedParameters = Collections.singletonMap("id", (Object) user.getId());
        		} else if(user.getUsername() != null) {
        			criteria = "`username` = :username";
        			namedParameters = Collections.singletonMap("username", (Object) user.getUsername());
        		} else if(user.getKey() != null) {
        			criteria = "`key` = :key";
        			namedParameters = Collections.singletonMap("key", (Object) user.getKey());
        		} else {
        			throw new Exception("Malformed user by example!");
        		}
        	} else {
        		criteria = "`id`=:id";
        		namedParameters = Collections.singletonMap("id", (Object) UserContextTLS.instance.get().getId());
        		keyColumn=",`key`";
        	}
    		result = (User) JdbcTemplate.query(
    				"select `id`,`name`,`username`"+ keyColumn +" from `users` where "+ criteria +" ", 
    				namedParameters, new BeanPropertyRowMapper(User.class)).get(0);
    		return result;
    	} catch (IndexOutOfBoundsException ex) {
    		throw new NoSuchElementException("Error getting user from database: user not found!");
    	}
    }
    
    public ListResult<User> getUsers() throws Exception {
    	ListResult<User> result;
    	try{
    		String IdAndkeyColumn="";
    		if(UserContextTLS.isAdmin())
    			IdAndkeyColumn=", `id`, `key` ";
    		Map<String, Integer> namedParameters = Collections.emptyMap();
    		result = new ListResult(JdbcTemplate.query(
        		"select `name`,`username`"+ IdAndkeyColumn +" from `users`", 
        		namedParameters, new BeanPropertyRowMapper(User.class)));
    		return result;
    	} catch (IndexOutOfBoundsException ex) {
    		throw new NoSuchElementException("Error getting users from database: users not found!");
    	}
    }
    
    public ListResult<Message> getMessagesFiltered(String username, String filter) throws Exception {
    	ListResult<Message> result;
    	String sqlFilter ="";
    	try{
    		Integer userId;
    		if(username!=null) {
    			User user = new User(username,null);
    			userId = this.getUserByExample(user).getId();
    		} else {
    			username = UserContextTLS.instance.get().getUsername();
    			userId = UserContextTLS.instance.get().getId();
    		}
    		Map<String,Object> namedParameters = new HashMap<String, Object>();
    		namedParameters.put("userId", userId);
    		namedParameters.put("username", username);
    		if(filter != null) {
    			namedParameters.put("filter", "%"+filter+"%");
    			sqlFilter =  "m.text LIKE :filter AND";
    		}
    		result = new ListResult(JdbcTemplate.query(
        		"SELECT :username as `authorUsername`, `text`, `insert_date` FROM messages m WHERE m.author_id=:userId AND "+ sqlFilter +" (m.aimed_at=m.author_id OR " +
        		"m.aimed_at IN (SELECT id FROM users u LEFT JOIN users_mapping um ON u.id=um.following_id WHERE um.user_id=:userId))" +
        		" ORDER BY insert_date DESC", 
        		namedParameters, new BeanPropertyRowMapper(Message.class)));
    		return result;
    	} catch (IndexOutOfBoundsException ex) {
    		throw new NoSuchElementException("Error getting users from database: users not found!");
    	}
    }
    
    
    public ListResult<User> getUsersFollowedByAndFollowing(String username) throws Exception {
    	ListResult<User> result;
    	try{
    		Integer userId;
    		if(username!=null) {
    			User user = new User(username,null);
    			userId = this.getUserByExample(user).getId();
    		} else {
    			userId = UserContextTLS.instance.get().getId();
    		}
    		String IdAndkeyColumn="";
    		if(UserContextTLS.isAdmin())
    			IdAndkeyColumn=", v.id, v.key ";
    		Map<String, Integer> namedParameters = Collections.singletonMap("userId", userId);
    		result = new ListResult(JdbcTemplate.query( 
        		"SELECT v.name, v.username"+ IdAndkeyColumn +", GROUP_CONCAT(IF(NOT v.following_id=:userId, 'following', 'followed')) AS follow " +
        		"FROM (SELECT *  FROM users u LEFT JOIN users_mapping um ON u.id=um.user_id WHERE um.following_id=:userId "+
        				"UNION SELECT *  FROM users u LEFT JOIN users_mapping um ON u.id=um.following_id WHERE um.user_id=:userId"+
        		") AS v GROUP BY v.id", namedParameters, new BeanPropertyRowMapper(User.class)));
    		return result;
    	} catch (IndexOutOfBoundsException ex) {
    		throw new NoSuchElementException("Error getting users from database: users not found!");
    	}
    }
    
    public Integer followUser(String followingUsername) throws Exception {
    	try {
    		Integer userId = UserContextTLS.instance.get().getId();
    		if(UserContextTLS.instance.get().getUsername().equals(followingUsername))
    			throw new IllegalArgumentException("Can not follow self!");
    		Map<String,Object> namedParameters = new HashMap<String, Object>();
    		namedParameters.put("userId", userId);
    		namedParameters.put("followingUsername", followingUsername);
    		return JdbcTemplate.update(
    	        "INSERT INTO users_mapping (user_id, following_id) VALUES (:userId, (SELECT id FROM users u WHERE u.username=:followingUsername))", namedParameters);
    	} catch(DuplicateKeyException ex) {
    		throw new AlreadyBoundException("You are already following that user!");
    	} catch(DataIntegrityViolationException ex){
    		throw new NoSuchElementException("User does not exist!");
    	}    	
    }
    
    public Integer unfollowUser(String followingUsername) throws Exception {
    	try {
    		Integer userId = UserContextTLS.instance.get().getId();
    		if(UserContextTLS.instance.get().getUsername().equals(followingUsername))
    			throw new IllegalArgumentException("Can not be followed by self!");
    		Map<String,Object> namedParameters = new HashMap<String, Object>();
    		namedParameters.put("userId", userId);
    		namedParameters.put("followingUsername", followingUsername);
    		Integer res = JdbcTemplate.update(
    	        "DELETE FROM users_mapping WHERE user_id = :userId AND following_id = (SELECT id FROM users u WHERE u.username=:followingUsername)", namedParameters);
    		if(res==0)
    			throw new IllegalArgumentException("You were not following that user!");
    		return res;
    	} catch(Exception ex) {
    		throw ex;
    	}
    }
    


    public Integer writeMessage(String text) throws Exception {
       	try {
       		Integer userId = UserContextTLS.instance.get().getId();
       		
        	String aimedAt=null;
        	Pattern pattern = Pattern.compile("(.*)@([^ $]+)(.*)");
        	Matcher matcher = pattern.matcher(text);
        	if (matcher.find( )) {
        		try{
        			User aimedAtUser = this.getUserByExample(new User(matcher.group(2),""));
        			aimedAt=String.valueOf(aimedAtUser.getId());
        		} catch (NoSuchElementException ex) {}
        	}
        	
    		Map<String,Object> namedParameters = new HashMap<String, Object>();
    		namedParameters.put("text", text);
    		namedParameters.put("insert_date", new Date(System.currentTimeMillis()));
    		namedParameters.put("author_id", userId);
    		namedParameters.put("aimed_at", aimedAt);
    		return JdbcTemplate.update(
    	        "insert into messages (text, insert_date, author_id, aimed_at) values (:text, :insert_date, :author_id, :aimed_at)", namedParameters);
    	} catch(Exception ex) {
    		throw ex;
    	}
    }
    
    public ListResult<Message> getAllMessages() throws Exception {
    	ListResult<Message> result;
    	try{
    		Map<String, String> namedParameters = Collections.emptyMap();
    		result = new ListResult(JdbcTemplate.query(
        		"SELECT m.id, m.text, m.insert_date, u.username as authorUsername FROM messages m LEFT JOIN users u ON m.author_id=u.id ORDER BY m.insert_date DESC", 
        		namedParameters, new BeanPropertyRowMapper(Message.class)));
    		return result;
    	} catch (IndexOutOfBoundsException ex) {
    		throw new NoSuchElementException("Error getting users from database: users not found!");
    	}
    }
    
    public Integer deleteAll() throws Exception {
       	try {
    		Map namedParameters = Collections.emptyMap();;
    		JdbcTemplate.update(
    	        "delete from users", namedParameters);
    		JdbcTemplate.update(
        	        "ALTER TABLE users AUTO_INCREMENT = 1", namedParameters);
    		JdbcTemplate.update(
        	        "ALTER TABLE messages AUTO_INCREMENT = 1", namedParameters);
    		
    		namedParameters = new HashMap<String, String>();
    		namedParameters.put("key", "admin");
    		namedParameters.put("username", "admin");
    		return JdbcTemplate.update(
    	        "insert into users (`key`, username) values (:key, :username)", namedParameters);
    	} catch(Exception ex) {
    		throw ex;
    	}
    }
    
}
