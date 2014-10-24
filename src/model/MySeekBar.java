package model;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import view.GuideActivity;
import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.widget.SeekBar;

public class MySeekBar 
{
	public static MediaPlayer proPlayer = null;
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
					if( GuideActivity.isProPlaying && GuideActivity.isShowing )
					{
						GuideActivity.handler.sendEmptyMessage( 8 );
					}
				}
			}
			, 100, 100 
		);
	}
	
	public void updateSeekBar()
	{
		if( GuideActivity.proPlayIndex != -1 && seekBarMap.get( GuideActivity.proPlayIndex ) != null )
		{
			seekBarMap.get( GuideActivity.proPlayIndex ).setProgress( ( int ) GuideActivity.myService.getProProgress() );
		}
	}
}