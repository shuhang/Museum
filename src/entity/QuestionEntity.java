package entity;

import java.io.Serializable;
import java.util.List;

public class QuestionEntity implements Serializable
{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String title;
	private String info;
	private int voiceCount;
	private int answerCount;
	private int lookCount;
	private ProEntity proEntity;
	private List< AnswerEntity > answerList;
	
	public int getId() 
	{
		return id;
	}
	public void setId( int id ) 
	{
		this.id = id;
	}
	public String getInfo() 
	{
		return info;
	}
	public void setInfo( String info ) 
	{
		this.info = info;
	}
	public ProEntity getProEntity() 
	{
		return proEntity;
	}
	public void setProEntity( ProEntity proEntity ) 
	{
		this.proEntity = proEntity;
	}
	public List<AnswerEntity> getAnswerList() 
	{
		return answerList;
	}
	public void setAnswerList( List< AnswerEntity > answerList )
	{
		this.answerList = answerList;
	}
	public String getTitle() 
	{
		return title;
	}
	public void setTitle( String title ) 
	{
		this.title = title;
	}
	public int getVoiceCount() 
	{
		return voiceCount;
	}
	public void setVoiceCount( int voiceCount ) 
	{
		this.voiceCount = voiceCount;
	}
	public int getAnswerCount() 
	{
		return answerCount;
	}
	public void setAnswerCount( int answerCount ) 
	{
		this.answerCount = answerCount;
	}
	public int getLookCount() 
	{
		return lookCount;
	}
	public void setLookCount( int lookCount )
	{
		this.lookCount = lookCount;
	}
}
