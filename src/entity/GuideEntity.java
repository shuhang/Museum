package entity;

import java.util.List;

public class GuideEntity 
{
	private int id;
	private String name;
	private String description;
	private String quote;
	private String guideType;
	private String imageUrl;
	private String time;
	private String[] keyWords;
	private List< PlaceEntity > placeList;
	
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
	public String getDescription() 
	{
		return description;
	}
	public void setDescription( String description )
	{
		this.description = description;
	}
	public String getQuote()
	{
		return quote;
	}
	public void setQuote( String quote )
	{
		this.quote = quote;
	}
	public String getGuideType() 
	{
		return guideType;
	}
	public void setGuideType( String guideType )
	{
		this.guideType = guideType;
	}
	public String getImageUrl() 
	{
		return imageUrl;
	}
	public void setImageUrl( String imageUrl ) 
	{
		this.imageUrl = imageUrl;
	}
	public String getTime() 
	{
		return time;
	}
	public void setTime( String time ) 
	{
		this.time = time;
	}
	public String[] getKeyWords() 
	{
		return keyWords;
	}
	public void setKeyWords( String[] keyWords ) 
	{
		this.keyWords = keyWords;
	}
	public List<PlaceEntity> getPlaceList() 
	{
		return placeList;
	}
	public void setPlaceList( List<PlaceEntity> placeList ) 
	{
		this.placeList = placeList;
	}
}
