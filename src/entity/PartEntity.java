package entity;

public class PartEntity 
{
	private int id;
	private String name;
	private int start;
	private String position;
	private String imageUrl;
	private String[] tags;
	
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
	public int getStart() 
	{
		return start;
	}
	public void setStart( int start ) 
	{
		this.start = start;
	}
	public String getPosition() 
	{
		return position;
	}
	public void setPosition( String position ) 
	{
		this.position = position;
	}
	public String getImageUrl() 
	{
		return imageUrl;
	}
	public void setImageUrl( String imageUrl ) 
	{
		this.imageUrl = imageUrl;
	}
	public String[] getTags() 
	{
		return tags;
	}
	public void setTags( String[] tags )
	{
		this.tags = tags;
	}
}
