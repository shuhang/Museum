package model;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import view.GuideActivity;
import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MySeekBar 
{
	private MediaPlayer proPlayer = null;
	private double progress = -2;
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
			
			if( timer != null )
			{
				timer.cancel();
			}
			timer = new Timer();
			timer.schedule
			( 
				new TimerTask()
				{
					public void run()
					{
						if( progress < GuideActivity.proPlayLength && GuideActivity.isProPlaying )
						{
							progress += 0.1;
							GuideActivity.handler.sendEmptyMessage( 8 );
						}
						else if( GuideActivity.isProPlaying )
						{
							GuideActivity.handler.sendEmptyMessage( 4 );
						}
					}
				}
				, 100, 100 
			);
		}
		catch( Exception ex ) 
		{
			ex.printStackTrace();
		}
		
		addListener();
	}
	
	private void addListener()
	{	
		seekBarMap.get( GuideActivity.proPlayIndex ).setOnSeekBarChangeListener
		(
			new OnSeekBarChangeListener()
			{
				public void onProgressChanged( SeekBar seekBar, int progress, boolean fromUser ) {}
				public void onStartTrackingTouch( SeekBar seekBar ) {}
				public void onStopTrackingTouch( SeekBar seekBar ) 
				{
					progress = seekBar.getProgress();
					if( proPlayer != null )
					{
						try
						{
							proPlayer.seekTo( ( int ) progress * 1000 );
						}
						catch( Exception ex ) {}
						if( GuideActivity.isProPlaying == false )
						{
							pausePlay();
						}
					}
				}
			}
		);
	}
	
	public void updateSeekBar()
	{
		if( GuideActivity.isProPlaying && GuideActivity.proPlayIndex != -1 )
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
	
	public void seekTo( double timeSecond )
	{
		try
		{
			proPlayer.seekTo( ( int )( timeSecond * 1000 ) );
			
			if( GuideActivity.isProPlaying == false )
			{
				proPlayer.pause();
			}
		}
		catch( Exception ex ) {}
	}
	
	public void cancelTimer()
	{
		if( timer != null )
		{
			timer.cancel();
		}
	}
}