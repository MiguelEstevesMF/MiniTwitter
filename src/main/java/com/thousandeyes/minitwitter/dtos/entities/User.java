package com.thousandeyes.minitwitter.dtos.entities;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

/**
 * @author Miguel
 *
 */
@XmlRootElement
@XmlType(propOrder = {"id", "username", "name", "key", "follow"})
@JsonPropertyOrder({"id", "username", "name", "key", "follow"})
@JsonSerialize(include=Inclusion.NON_NULL)
public class User {

	static final long serialVersionUID =1L;
	
	private Integer id;
	private String name;
	private String username;
	private String key;
	private String follow;
	
	public User() {
		super();
	}
	public User(String username, String name) {
		super();
		this.name = name;
		this.username = username;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	public String getFollow() {
		return follow;
	}
	public void setFollow(String follow) {
		this.follow = follow;
	}
}
