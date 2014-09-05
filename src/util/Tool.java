package util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

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
}