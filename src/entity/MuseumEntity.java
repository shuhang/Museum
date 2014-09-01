package entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import util.Tool;

public class MuseumEntity 
{
	public static int Id;
	public static String Name;
	public static String Description;
	public static String ImageUrl;
	public static String GuideInfo;
	public static List< String > guideList;
	public static List< String > placeList;
	public static List< GuideEntity > GuideList;
	public static HashMap< String, List< ProEntity > > ProMap;
	
	public static void ParseJson( String value )
	{
		try
		{
			//Museum
			JSONObject museumObject = new JSONObject( value );
			//Information
			Id = museumObject.getInt( "id" );
			Name = museumObject.getString( "name" );
			Description = museumObject.getString( "description" );
			ImageUrl = museumObject.getString( "picture" );
			GuideInfo = museumObject.getString( "guide_info" );
			//GuideList
			JSONArray guideArray = museumObject.getJSONArray( "guides" );
			final int guideCount = guideArray.length();
			GuideList = new ArrayList< GuideEntity >( guideCount );
		
			for( int i = 0; i < guideCount; i ++ )
			{			
				JSONObject guideObject = guideArray.getJSONObject( i );
				//Guide
				GuideEntity guideEntity = new GuideEntity();
				guideEntity.setId( guideObject.getInt( "id" ) );
				guideEntity.setName( guideObject.getString( "name" ) );
				guideEntity.setDescription( guideObject.getString( "description" ) );
				guideEntity.setQuote( guideObject.getString( "quote" ) );
				guideEntity.setGuideType( guideObject.getString( "guide_type" ) );
				guideEntity.setImageUrl( guideObject.getString( "picture" ) );
				guideEntity.setTime( guideObject.getString( "total_audio_length" ) );
				JSONArray keywordArray = guideObject.getJSONArray( "keywords_" );
				final int keywordCount = keywordArray.length();
				String[] keywords = new String[ keywordCount ];
				for( int j = 0; j < keywordCount; j ++ )
				{
					keywords[ j ] = keywordArray.getString( j );
				}
				guideEntity.setKeyWords( keywords );
				//PlaceList
				JSONArray placeArray = guideObject.getJSONArray( "introductions" );
				final int placeCount = placeArray.length();
				List< PlaceEntity > placeList = new ArrayList< PlaceEntity >( placeCount );
				for( int j = 0; j < placeCount; j ++ )
				{
					JSONObject placeObject = placeArray.getJSONObject( j );
					//Place
					PlaceEntity placeEntity = new PlaceEntity();
					placeEntity.setId( placeObject.getInt( "id" ) );
					placeEntity.setName( placeObject.getString( "name" ) );
					placeEntity.setAudioUrl( placeObject.getString( "audio" ) );
					placeEntity.setTime( placeObject.getString( "length" ) );
					placeEntity.setLength( Tool.getSecondByString( placeEntity.getTime() ) );
					//PartList
					JSONArray partArray = placeObject.getJSONArray( "segments" );
					final int partCount = partArray.length();
					List< PartEntity > partList = new ArrayList< PartEntity >( partCount );
					for( int k = 0; k < partCount; k ++ )
					{
						JSONObject partObject = partArray.getJSONObject( k );
						//Part
						PartEntity partEntity = new PartEntity();
						partEntity.setId( partObject.getInt( "id" ) );
						partEntity.setName( partObject.getString( "name" ) );
						partEntity.setStart( partObject.getInt( "start" ) );
						partEntity.setPosition( partObject.getString( "position_" ) );
						partEntity.setImageUrl( partObject.getJSONArray( "pictures_" ).getString( 0 ) );
						JSONArray tagArray = partObject.getJSONArray( "tags_" );
						final int tagCount = tagArray.length();
						String[] tags = new String[ tagCount ];
						for( int m = 0; m < tagCount; m ++ )
						{
							tags[ m ] = tagArray.getString( m );
						}
						partEntity.setTags( tags );
						
						partList.add( partEntity );
					}
					placeEntity.setPartList( partList );
					
					placeList.add( placeEntity );
				}
				guideEntity.setPlaceList( placeList );
				
				GuideList.add( guideEntity );
			}
			
			//Pro
			ProMap = new HashMap< String, List< ProEntity > >();
			JSONArray tagArray = museumObject.getJSONArray( "tags" );
			final int tagCount = tagArray.length();
			for( int i = 0; i < tagCount; i ++ )
			{
				JSONObject tagObject = tagArray.getJSONObject( i );
				JSONArray proArray = tagObject.getJSONArray( "explainations" );
				final int proCount = proArray.length();
				if( proCount > 0 )
				{
					List< ProEntity > proList = new ArrayList< ProEntity >( proCount );
					for( int j = 0; j < proCount; j ++ )
					{
						JSONObject proObject = proArray.getJSONObject( j );
						
						ProEntity proEntity = new ProEntity();
						proEntity.setId( proObject.getInt( "id" ) );
						proEntity.setName( proObject.getString( "name" ) );
						proEntity.setAudioUrl( proObject.getString( "audio" ) );
						proEntity.setTime( proObject.getString( "length" ) );
						proEntity.setLength( Tool.getSecondByString( proEntity.getTime() ) );
						proEntity.setType( proObject.getString( "_type" ) );
						JSONObject object = proObject.getJSONObject( "speaker" );
						proEntity.setExpertId( object.getInt( "id" ) );
						proEntity.setExpertName( object.getString( "name" ) );
						proEntity.setExpertTitle( object.getString( "title" ) );
						proEntity.setImageUrl( object.getString( "picture" ) );
						
						proList.add( proEntity );
					}
					
					ProMap.put( tagObject.getString( "name" ), proList );
				}
			}

			guideList = new ArrayList< String >( guideCount + 1 );
			for( int i = 0; i < guideCount; i ++ )
			{
				guideList.add( GuideList.get( i ).getName() );
			}
			guideList.add( "不要导游" );
			
			final int placeCount = GuideList.get( 0 ).getPlaceList().size();
			placeList = new ArrayList< String >( placeCount );
			for( int i = 0; i < placeCount; i ++ )
			{
				placeList.add( GuideList.get( 0 ).getPlaceList().get( i ).getName() );
			}
		}
		catch( Exception ex ) {}
	}
}
