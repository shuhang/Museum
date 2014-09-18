package entity;

import java.io.Serializable;

public class AnswerEntity implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int id;
	private String name;
	private String sign;
	private String answer;
	
	public int getId()
	{
		return id;
	}
	public void setId( int id )
	{
		this.id = id;
	}
	public String getName()
	{
		return name;
	}
	public void setName( String name ) 
	{
		this.name = name;
	}
	public String getSign() 
	{
		return sign;
	}
	public void setSign( String sign )
	{
		this.sign = sign;
	}
	public String getAnswer()
	{
		return answer;
	}
	public void setAnswer( String answer ) 
	{
		this.answer = answer;
	}
}
