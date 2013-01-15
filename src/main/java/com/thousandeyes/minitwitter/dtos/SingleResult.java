package com.thousandeyes.minitwitter.dtos;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Miguel
 *
 */
@XmlRootElement
public class SingleResult<T> {

	private T result;
	
	public SingleResult() {
	}

	public SingleResult(T result) {
		super();
		this.result = result;
	}

	public T getResult() {
		return result;
	}

	public void setResult(T result) {
		this.result = result;
	}

}
