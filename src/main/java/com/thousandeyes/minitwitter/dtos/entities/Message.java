package com.thousandeyes.minitwitter.dtos.entities;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.thousandeyes.minitwitter.dtos.marshalling.CustomJsonDateSerializer;

/**
 * @author Miguel
 *
 */
@XmlRootElement
@XmlType(propOrder = {"id", "insert_date", "authorUsername", "text"})
@JsonPropertyOrder({"id", "insert_date", "authorUsername", "text"})
@JsonSerialize(include=Inclusion.NON_NULL)
public class Message {
	
	static final long serialVersionUID =1L;
	
	private Integer id;
	private String text;
	
	@JsonSerialize(using = CustomJsonDateSerializer.class)
	private Date insert_date;
	private String authorUsername;
	
	public Message() {
		super();
	}
	public Message(String text, Date insert_date, String authorUsername) {
		super();
		this.text = text;
		this.insert_date = insert_date;
		this.authorUsername = authorUsername;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	@XmlElement(name = "timestamp", required = true)
	public Date getInsert_date() {
		return insert_date;
	}
	public void setInsert_date(Date insert_date) {
		this.insert_date = insert_date;
	}
	
	@XmlElement(name="author")
	@JsonProperty(value="author")
	public String getAuthorUsername() {
		return authorUsername;
	}
	public void setAuthorUsername(String authorUsername) {
		this.authorUsername = authorUsername;
	}
	
	
}
