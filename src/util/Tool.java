package util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import entity.AnswerEntity;
import entity.ProEntity;
import entity.QuestionEntity;

public class Tool 
{
	/**
	 * 将字符串转化为秒
	 * @param value
	 * @return
	 */
	public static int getSecondByString( String value )
	{
		int sum = 0, index1 = 0;
		if( value.contains( "'" ) )
		{
			index1 = value.indexOf( "'" );
			sum += Integer.parseInt( value.substring( 0, index1 ) ) * 60;
		}
		int index2 = value.indexOf( "\"" );
		sum += Integer.parseInt( value.substring( index1 + 1, index2 ) );
		return sum;
	}
	/**
	 * 将秒转化为字符串
	 * @param time
	 * @return
	 */
	public static String getStringBySecond( double time )
	{
		int value = ( int ) time;
		int minute = value / 60;
		String text1 = "", text2 = "";
		if( minute == 0 ) text1 = "00";
		else
		{
			if( minute < 10 ) text1 = "0" + minute;
			else text1 = "" + minute;
		}
		int second = value - 60 * minute;
		if( second == 0 ) text2 = "00";
		else
		{
			if( second < 10 ) text2 = "0" + second;
			else text2 = "" + second;
		}
		
		return text1 + ":" + text2;
	}
	/**
	 * 缩放图片到指定大小
	 * @param path
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap resizeImageToSmall( String path, int width, int height )
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;//不加载bitmap到内存中
        BitmapFactory.decodeFile( path, options );
        int oldWidth = options.outWidth;
        int oldHeight = options.outHeight;

        options.inSampleSize = oldWidth / width;
        if( oldWidth * 1.0 / width * height > oldHeight )
        {
        	options.inSampleSize = oldHeight / height;
        }
        
        options.inDither = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile( path, options );
    }
	/**
	 * 
	 * @param path
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap resizeBitmap( String path, int width, int height )
	{
		Bitmap oldBitmap = BitmapFactory.decodeFile( path );
		int oldWidth = oldBitmap.getWidth();
    	int oldHeight = oldBitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale( ( float ) width / oldWidth, ( float ) height / oldHeight );
        return Bitmap.createBitmap( oldBitmap, 0, 0, oldWidth, oldHeight, matrix, false );
	}
	/**
	 * 缩放图片到指定大小
	 * @param path
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap resizeImageToBig( String path )
    {
		Bitmap oldBitmap = BitmapFactory.decodeFile( path );
		int width = oldBitmap.getWidth();
    	int height = oldBitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ( ( float ) Information.ScreenWidth / width );
        matrix.postScale( scaleWidth, scaleWidth );
        return Bitmap.createBitmap( oldBitmap, 0, 0, width, height, matrix, false );
    }
	/**
	 * 截取指定大小图片
	 * @param path
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap cutImage( String path, int width, int height )
	{
		Bitmap resizeBitmap = resizeImageToSmall( path, width, height );
		final int oldWidth = resizeBitmap.getWidth();
		final int oldHeight = resizeBitmap.getHeight();
		return Bitmap.createBitmap( resizeBitmap, Math.abs( ( oldWidth - width ) / 2 ), Math.abs( ( oldHeight - height ) / 2 ), width, height );
	}
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static boolean judgeInProList1( String value )
	{
		final int count = Information.ProList1.length;
		for( int i = 0; i < count; i ++ )
		{
			if( Information.ProList1[ i ].equals( value ) ) 
			{
				return true;
			}
		}
		return false;
	}
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static boolean judgeInProList2( String value )
	{
		final int count = Information.ProList2.length;
		for( int i = 0; i < count; i ++ )
		{
			if( Information.ProList2[ i ].equals( value ) ) 
			{
				return true;
			}
		}
		return false;
	}
	/**
	 * 判断邮箱格式释放正确
	 * @param email
	 * @return
	 */
	public static boolean isEmail( String email ) 
	{
		String value = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern pattern = Pattern.compile( value );
		Matcher matcher = pattern.matcher( email );
		return matcher.matches();
	}
	/**
	 *  判断是否联网
	 * @param context
	 * @return
	 */
	public static boolean isNetworkConnected( Context context ) 
	{
		if( context != null )
		{  
			ConnectivityManager mConnectivityManager = ( ConnectivityManager ) context.getSystemService( Context.CONNECTIVITY_SERVICE );  
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();  
			if( mNetworkInfo != null ) 
			{  
				return mNetworkInfo.isAvailable();  
			}  
		}  
		return false;  
	}
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static List< QuestionEntity > parseQuestion( final String value )
	{
		List< QuestionEntity > list = new ArrayList< QuestionEntity >();
		try
		{
			JSONArray array = new JSONArray( value );
			final int count = array.length();
			for( int i = 0; i < count; i ++ )
			{
				JSONObject object = array.getJSONObject( i );
				QuestionEntity entity = new QuestionEntity();
				
				entity.setId( object.getInt( "id" ) );
				entity.setTitle( object.getString( "question" ) );
				entity.setInfo( object.getString( "description" ) );
				if( object.has( "explaination" ) )
				{
					JSONObject proObject = object.getJSONObject( "explaination" );
					ProEntity proEntity = new ProEntity();
					proEntity.setId( proObject.getInt( "id" ) );
					proEntity.setAudioUrl( proObject.getString( "audio" ) );
					proEntity.setName( proObject.getString( "name" ) );
					proEntity.setTime( proObject.getString( "length" ) );
					proEntity.setLength( Tool.getSecondByString( proObject.getString( "length" ) ) );
					
					JSONObject speaker = proObject.getJSONObject( "speaker" );
					proEntity.setExpertId( speaker.getInt( "id" ) );
					proEntity.setExpertName( speaker.getString( "name" ) );
					proEntity.setExpertTitle( speaker.getString( "title" ) );
					proEntity.setImageUrl( speaker.getString( "picture" ) );
					
					entity.setProEntity( proEntity );
					entity.setVoiceCount( 1 );
				}
				else
				{
					entity.setProEntity( null );
					entity.setVoiceCount( 0 );
				}
				
				entity.setLookCount( object.getJSONArray( "question_followers" ).length() );
				
				JSONArray answerArray = object.getJSONArray( "answers" );
				final int sum = answerArray.length();
				entity.setAnswerCount( sum );
				List< AnswerEntity > answerList = new ArrayList< AnswerEntity >( sum );
				for( int j = 0; j < sum; j ++ )
				{
					JSONObject answer = answerArray.getJSONObject( j );
					AnswerEntity answerEntity = new AnswerEntity();
					answerEntity.setId( answer.getInt( "id" ) );
					answerEntity.setAnswer( answer.getString( "answer" ) );
					answerEntity.setName( answer.getString( "answerer" ) );
					answerEntity.setSign( answer.getString( "self_introduction" ));
					
					answerList.add( answerEntity );
				}
				entity.setAnswerList( answerList );
				
				list.add( entity );
			}
		}
		catch( Exception ex ) {}
		return list;
	}
}