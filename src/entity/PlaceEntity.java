package entity;

import java.util.List;

public class PlaceEntity 
{
	private int id;
	private int length;
	private String name;
	private String time;
	private String audioUrl;
	private List< PartEntity > partList;
	
	public int getId() 
	{
		return id;
	}
	public void setId( int id ) 
	{
		this.id = id;
	}
	public int getLength() 
	{
		return length;
	}
	public void setLength( int length )
	{
		this.length = length;
	}
	public String getName() 
	{
		return name;
	}
	public void setName( String name ) 
	{
		this.name = name;
	}
	public String getTime() 
	{
		return time;
	}
	public void setTime( String time ) 
	{
		this.time = time;
	}
	public String getAudioUrl() 
	{
		return audioUrl;
	}
	public void setAudioUrl( String audioUrl ) 
	{
		this.audioUrl = audioUrl;
	}
	public List<PartEntity> getPartList() 
	{
		return partList;
	}
	public void setPartList( List<PartEntity> partList ) 
	{
		this.partList = partList;
	}
}
