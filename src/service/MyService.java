package service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Binder;
import android.os.IBinder;

public class MyService extends Service
{
	private IBinder binder = new MyService.MyBinder();
	
	/**
	 *  MediaPlayer
	 */
	private MediaPlayer player = null;
	private String audioUrl;

	public void onCreate()
	{
		super.onCreate();
		
		player = new MediaPlayer();
		player.setOnCompletionListener
		(
			new OnCompletionListener()
			{
				public void onCompletion( MediaPlayer mp ) 
				{
					
				}
			}
		);
	}
	
	public IBinder onBind( Intent intent ) 
	{
		return binder;
	}
	
	public int onStartCommand( Intent intent, int flags, int startId )
	{
		return START_STICKY;
	}
	
	public void onDestroy()
	{
		super.onDestroy();
		if( player != null )
		{
			player.stop();
			player.release();
		}
	}
	/**
	 *  Prepare to play
	 */
	public void preparePlay()
	{
		try 
		{
			player.setDataSource( audioUrl );
			player.prepare();
		} 
		catch( Exception ex ) {}
	}
	/**
	 *  Start to play
	 */
	public void startPlay()
	{
		if( player != null )
		{
			try
			{
				player.start();
			}
			catch( Exception ex ) {}
		}
	}
	/**
	 *  Pause play
	 */
	public void pausePlay()
	{
		if( player != null )
		{
			try
			{
				player.pause();
			}
			catch( Exception ex ) {}
		}
	}
	/** 
	 *  Stop play
	 */
	public void stopPlay()
	{
		if( player != null )
		{
			try
			{
				player.stop();
			}
			catch( Exception ex ) {}
		}
	}
	/**
	 *  Seek to play
	 */
	public void seekPlay( double progress )
	{
		try
		{
			player.seekTo( ( int ) progress * 1000 );
		}
		catch( Exception ex ) {}
	}
	/**
	 *  Get progress
	 */
	public double getProgress()
	{
		return player.getCurrentPosition() / 1000.0;
	}
	/**
	 * Aduio Url 
	 * @return
	 */
	public String getAudioUrl() 
	{
		return audioUrl;
	}
	public void setAudioUrl( String audioUrl )
	{
		this.audioUrl = audioUrl;
	}
	/**
	 *  Binder
	 * @author shuhang
	 *
	 */
	public class MyBinder extends Binder
	{
		MyService getService()
		{
			return MyService.this;
		}
	}
}
