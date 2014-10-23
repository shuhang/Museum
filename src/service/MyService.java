package service;

import java.util.List;

import util.Information;
import util.Tool;
import view.GuideActivity;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Binder;
import android.os.IBinder;
import entity.MuseumEntity;
import entity.ProEntity;

public class MyService extends Service
{
	/**
	 *  MediaPlayer
	 */
	private MediaPlayer player = null;
	private String audioUrl;

	private int count = 0;
	/**
	 * 
	 */
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
					playNext();
				}
			}
		);
	}
	/**
	 * 
	 */
	public IBinder onBind( Intent intent ) 
	{
		return new MyService.MyBinder();
	}
	/**
	 * 
	 */
	public int onStartCommand( Intent intent, int flags, int startId )
	{
		return START_STICKY;
	}
	/**
	 * 
	 */
	public void onDestroy()
	{
		super.onDestroy();
		killPlay();
	}
	/**
	 * 
	 */
	public void updateAudioUrl()
	{
		if( count < 1 )
			audioUrl = Information.Sdcard + "/first.mp3";
		else
			audioUrl = Information.Sdcard + "/first1.mp3";
		count ++;
		//audioUrl = Information.RootPath + GuideActivity.placeEntity.getAudioUrl();
	}
	/**
	 * 
	 */
	public void killPlay()
	{
		if( player != null )
		{
			try
			{
				player.stop();
				player.release();
			}
			catch( Exception ex ) {}
		}
	}
	/**
	 *  Play Next
	 */
	public void playNext()
	{
		if( GuideActivity.isGuidePlaying )
		{
			if( GuideActivity.placeIndex < MuseumEntity.placeList.size() - 1 )
			{
				GuideActivity.lock = true;
				GuideActivity.placeIndex ++;
				updatePlace();
			}
			else
			{
				GuideActivity.isGuidePlaying = false;
			}
			if( GuideActivity.isShowing )
			{
				GuideActivity.handler.sendEmptyMessage( 5 );
				GuideActivity.handler.sendEmptyMessage( 9 );
			}
		}
		else if( GuideActivity.isProPlaying )
		{
			
		}
	}
	/**
	 *  Play Pre
	 */
	public void playPre()
	{
		if( GuideActivity.isGuidePlaying )
		{
			GuideActivity.placeIndex --;
			updatePlace();
		}
	}
	/**
	 *  更新展厅信息
	 */
	public void updatePlace()
	{
		GuideActivity.guideProgress = 0;
		GuideActivity.partIndex = 0;
		GuideActivity.proPlayIndex = -1;
		GuideActivity.placeEntity = GuideActivity.guideEntity.getPlaceList().get( GuideActivity.placeIndex );
		GuideActivity.partEntity = GuideActivity.placeEntity.getPartList().get( GuideActivity.partIndex );
		
		if( GuideActivity.partEntity.getTags().length > 0 && MuseumEntity.ProMap.get( GuideActivity.partEntity.getTags()[ 0 ] ) != null )
		{
			updateProPlayMap( MuseumEntity.ProMap.get( GuideActivity.partEntity.getTags()[ 0 ] ).size() );
		}
		player.reset();
		updateAudioUrl();
		preparePlay();
		seekPlay( 0 );
		startPlay();
		GuideActivity.lock = false;
	}
	/**
	 * 更新专家数据
	 * @param count
	 */
	private void updateProPlayMap( final int count )
	{
		List< ProEntity > proList = MuseumEntity.ProMap.get( GuideActivity.partEntity.getTags()[ 0 ] );
		GuideActivity.proPlayMap.clear();
		for( int i = 0; i < count; i ++ )
		{
			ProEntity entity = proList.get( i );
			if( GuideActivity.guideMode == 1 && Tool.judgeInProList1( entity.getName() ) )
			{
				GuideActivity.proPlayMap.put( i, true );
			}
			else if( GuideActivity.guideMode == 2 && Tool.judgeInProList2( entity.getName() ) )
			{
				GuideActivity.proPlayMap.put( i, true );
			}
			else
			{
				GuideActivity.proPlayMap.put( i, false );
			}
		}
	}
	/**
	 * 导游播放要到达节点之前，先判断是否有专家等待播放
	 */
	public void judgeNextPro()
	{
		final int sum = GuideActivity.proPlayMap.size();
		for( int j = 0; j < sum; j ++ )
			if( GuideActivity.proPlayMap.get( j ) )
			{
				
				break;
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
	public void seekPlay( int progress )
	{
		try
		{
			player.seekTo( progress * 1000 );
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
	 *  Binder
	 * @author shuhang
	 *
	 */
	public class MyBinder extends Binder
	{
		public MyService getService()
		{
			return MyService.this;
		}
	}
}
