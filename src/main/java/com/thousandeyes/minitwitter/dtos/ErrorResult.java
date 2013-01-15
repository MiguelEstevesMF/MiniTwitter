package com.thousandeyes.minitwitter.dtos;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Miguel
 *
 */
@XmlRootElement
public class ErrorResult {

	private String message;
	private String details;
	public ErrorResult() {
		super();
	}
	public ErrorResult(String message, String details) {
		super();
		this.message = message;
		this.setDetails(details);
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getDetails() {
		return details;
	}
	public void setDetails(String details) {
		this.details = details;
	}

	
}
