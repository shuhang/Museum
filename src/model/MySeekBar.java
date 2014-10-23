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
		startTimer();
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
					if( GuideActivity.isProPlaying && seekBarMap.get( GuideActivity.proPlayIndex ) != null )
					{
						progress = GuideActivity.myService.getProgress();
						if( GuideActivity.isShowing )
						{
							GuideActivity.handler.sendEmptyMessage( 8 );
						}
					}
				}
			}
			, 100, 100 
		);
	}
	
	public void updateSeekBar()
	{
		seekBarMap.get( GuideActivity.proPlayIndex ).setProgress( ( int ) progress );
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