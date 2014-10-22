package model;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import view.GuideActivity;
import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.widget.SeekBar;

public class MySeekBar 
{
	public static MediaPlayer proPlayer = null;
	public static double progress = -2;
	private Timer timer = null;
	
	@SuppressLint("UseSparseArrays")
	public HashMap< Integer, SeekBar > seekBarMap = new HashMap< Integer, SeekBar >();
	
	public static MySeekBar instance = null;
	
	public static MySeekBar getInstance()
	{
		if( instance == null )
		{
			instance = new MySeekBar();
		}
		return instance;
	}
	
	public void init()
	{
		try
		{
			progress = 0;
			
			proPlayer = new MediaPlayer();
			proPlayer.setDataSource( GuideActivity.audioUrl );
			proPlayer.prepare();
			
			proPlayer.setOnErrorListener
			(
				new OnErrorListener()
				{
					public boolean onError( MediaPlayer arg0, int arg1, int arg2 ) 
					{
						try
						{
							arg0.reset();
							arg0.setDataSource( GuideActivity.audioUrl );
							arg0.prepare();
							arg0.start();
						}
						catch( Exception ex ) {}
						return true;
					}
				}
			);
			
			proPlayer.setOnCompletionListener
			(
				new OnCompletionListener()
				{
					public void onCompletion( MediaPlayer mp ) 
					{
						GuideActivity.handler.sendEmptyMessage( 4 );
						stopTimer();
					}
				}
			);
			
			stopTimer();
			startTimer();
		}
		catch( Exception ex ) 
		{
			ex.printStackTrace();
		}
	}
	
	public void stopTimer()
	{
		if( timer != null )
		{
			timer.cancel();
			timer = null;
		}
	}
	
	public void startTimer()
	{
		if( timer == null )
		{
			timer = new Timer();
		}
		timer.schedule
		( 
			new TimerTask()
			{
				public void run()
				{
					seekbarAction();
				}
			}
			, 100, 100 
		);
	}
	
	private void seekbarAction()
	{
		if( GuideActivity.isProPlaying )
		{
			progress = proPlayer.getCurrentPosition() * 1.0 / 1000;
			GuideActivity.handler.sendEmptyMessage( 8 );
		}
	}
	
	public void updateSeekBar()
	{
		if( GuideActivity.isProPlaying && seekBarMap.get( GuideActivity.proPlayIndex ) != null )
		{
			seekBarMap.get( GuideActivity.proPlayIndex ).setProgress( ( int ) progress );
		}
	}
	
	public void startPlay()
	{
		try
		{
			proPlayer.start();
		}
		catch( Exception ex ) {}
	}
	
	public void pausePlay()
	{
		try
		{
			proPlayer.pause();
		}
		catch( Exception ex ) {}
	}
	
	public void stopPlay()
	{
		try
		{
			if( proPlayer != null )
			{
				proPlayer.stop();
				proPlayer.release();
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}
	
	public void cancelTimer()
	{
		if( timer != null )
		{
			timer.cancel();
		}
	}
}