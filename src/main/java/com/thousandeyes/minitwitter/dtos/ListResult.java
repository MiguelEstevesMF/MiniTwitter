package com.thousandeyes.minitwitter.dtos;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeInfo.As;
import org.codehaus.jackson.annotate.JsonTypeInfo.Id;

/**
 * @author Miguel
 *
 * @param <T>
 */
@XmlRootElement
public class ListResult<T> {
		
	protected List<T> list;

	public ListResult(){}

	public ListResult(List<T> list){
		this.list=list;
	}

	@XmlElement(name="item")
	@JsonProperty(value="item")
	@JsonTypeInfo(use=Id.NAME, include=As.WRAPPER_OBJECT)
	public List<T> getList(){
		return list;
	}

}
