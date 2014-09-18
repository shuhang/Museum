package entity;

import java.io.Serializable;

public class ProEntity implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int id;
	private String name;
	private String audioUrl;
	private String time;
	private String type;
	private int expertId;
	private String expertName;
	private String expertTitle;
	private String imageUrl;
	private int length;
	
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
	public String getAudioUrl() 
	{
		return audioUrl;
	}
	public void setAudioUrl( String audioUrl )
	{
		this.audioUrl = audioUrl;
	}
	public String getTime() 
	{
		return time;
	}
	public void setTime( String time )
	{
		this.time = time;
	}
	public String getType() 
	{
		return type;
	}
	public void setType( String type ) 
	{
		this.type = type;
	}
	public int getExpertId() 
	{
		return expertId;
	}
	public void setExpertId( int expertId ) 
	{
		this.expertId = expertId;
	}
	public String getExpertName() 
	{
		return expertName;
	}
	public void setExpertName( String expertName )
	{
		this.expertName = expertName;
	}
	public String getExpertTitle() 
	{
		return expertTitle;
	}
	public void setExpertTitle( String expertTitle ) 
	{
		this.expertTitle = expertTitle;
	}
	public String getImageUrl() 
	{
		return imageUrl;
	}
	public void setImageUrl( String imageUrl )
	{
		this.imageUrl = imageUrl;
	}
	public int getLength() 
	{
		return length;
	}
	public void setLength( int length )
	{
		this.length = length;
	}
}
