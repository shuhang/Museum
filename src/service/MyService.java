package service;

import java.util.List;

import model.MySeekBar;

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
	private MediaPlayer guidePlayer = null;
	private MediaPlayer proPlayer = null;
	private String guideAudioUrl;
	private String proAudioUrl;
	/**
	 * 
	 */
	public void onCreate()
	{
		super.onCreate();
		
		guidePlayer = new MediaPlayer();
		proPlayer = new MediaPlayer();
		guidePlayer.setOnCompletionListener
		(
			new OnCompletionListener()
			{
				public void onCompletion( MediaPlayer mp ) 
				{
					playNextGuide();
				}
			}
		);
		proPlayer.setOnCompletionListener
		(
			new OnCompletionListener()
			{
				public void onCompletion( MediaPlayer mp ) 
				{
					playNextPro();
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
		killGuidePlay();
		killProPlay();
	}
	/**
	 * 
	 */
	public void updateGuideAudioUrl( String audioUrl )
	{
//		if( count < 1 )
//			audioUrl = Information.Sdcard + "/first.mp3";
//		else
//			audioUrl = Information.Sdcard + "/first1.mp3";
//		count ++;
		this.guideAudioUrl = audioUrl;
	}
	/**
	 * 
	 */
	public void updateProAudioUrl( String audioUrl )
	{
		this.proAudioUrl = audioUrl;
	}
	/**
	 * 
	 */
	public void killGuidePlay()
	{
		if( guidePlayer != null )
		{
			try
			{
				guidePlayer.stop();
				guidePlayer.release();
			}
			catch( Exception ex ) {}
		}
	}
	/**
	 * 
	 */
	public void killProPlay()
	{
		if( proPlayer != null )
		{
			try
			{
				proPlayer.stop();
				proPlayer.release();
			}
			catch( Exception ex ) {}
		}
	}
	/**
	 *  Play Next
	 */
	public void playNextGuide()
	{
		GuideActivity.isGuidePlaying = true;
		
		if( GuideActivity.placeIndex < MuseumEntity.placeList.size() - 1 )
		{			
			GuideActivity.placeIndex ++;
			updatePlace();
			
			if( GuideActivity.isShowing )
			{
				GuideActivity.handler.sendEmptyMessage( 9 );
			}
		}
		else
		{
			GuideActivity.placeIndex --;
			playNextGuide();
			GuideActivity.isGuidePlaying = false;
			pauseGuidePlay();
		}
		if( GuideActivity.isShowing )
		{
			GuideActivity.handler.sendEmptyMessage( 5 );
		}
	}
	/**
	 * 
	 */
	public void playNextPro()
	{
		judgeNextPro();
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
				playPro( j );
				return;
			}
		GuideActivity.proPlayIndex = -1;
		MySeekBar.getInstance().cancelTimer();
		if( GuideActivity.isShowing )
		{
			GuideActivity.handler.sendEmptyMessage( 1 );
		}
		GuideActivity.isGuidePlaying = true;
		GuideActivity.handler.sendEmptyMessage( 12 );
	}
	/**
	 * 
	 */
	public void playPro( int index )
	{
		if( GuideActivity.isGuidePlaying == true )
		{
			GuideActivity.isGuidePlaying = false;
			pauseGuidePlay();
			if( GuideActivity.isShowing )
			{
				GuideActivity.handler.sendEmptyMessage( 11 );
			}
		}
		GuideActivity.proPlayIndex = index;
		GuideActivity.proPlayMap.put( GuideActivity.proPlayIndex, false );
		GuideActivity.isProPlaying = true;
		GuideActivity.myService.playNewPro( Information.RootPath + MuseumEntity.ProMap.get( GuideActivity.partEntity.getTags()[ 0 ] ).get( GuideActivity.proPlayIndex ).getAudioUrl(), 0 );
		GuideActivity.proPlayLength = MuseumEntity.ProMap.get( GuideActivity.partEntity.getTags()[ 0 ] ).get( GuideActivity.proPlayIndex ).getLength();
		MySeekBar.getInstance().seekBarMap.get( GuideActivity.proPlayIndex ).setMax( GuideActivity.proPlayLength );
		MySeekBar.getInstance().startTimer();
		if( GuideActivity.isShowing )
		{
			GuideActivity.handler.sendEmptyMessage( 1 );
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
		GuideActivity.lock = true;
		GuideActivity.guideProgress = 0;
		GuideActivity.partIndex = 0;
		GuideActivity.proPlayIndex = -1;
		GuideActivity.placeEntity = GuideActivity.guideEntity.getPlaceList().get( GuideActivity.placeIndex );
		GuideActivity.partEntity = GuideActivity.placeEntity.getPartList().get( GuideActivity.partIndex );
		
		if( GuideActivity.partEntity.getTags().length > 0 && MuseumEntity.ProMap.get( GuideActivity.partEntity.getTags()[ 0 ] ) != null )
		{
			updateProPlayMap( MuseumEntity.ProMap.get( GuideActivity.partEntity.getTags()[ 0 ] ).size() );
		}
		playNewGuide( Information.RootPath + GuideActivity.placeEntity.getAudioUrl(), 0 );
		if( GuideActivity.isShowing )
		{
			GuideActivity.handler.sendEmptyMessage( 9 );
		}
		GuideActivity.lock = false;
	}
	/**
	 * 
	 */
	public void playNewGuide( String url, int progress )
	{
		guidePlayer.reset();
		updateGuideAudioUrl( url );
		prepareGuidePlay();
		seekGuidePlay( progress );
		startGuidePlay();
	}
	/**
	 * 
	 */
	public void playNewPro( String url, int progress )
	{
		proPlayer.reset();
		updateProAudioUrl( url );
		prepareProPlay();
		seekProPlay( progress );
		startProPlay();
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
	 *  Prepare to play
	 */
	public void prepareGuidePlay()
	{
		try 
		{
			guidePlayer.setDataSource( guideAudioUrl );
			guidePlayer.prepare();
		} 
		catch( Exception ex ) 
		{
			ex.printStackTrace();
		}
	}
	/**
	 *  Prepare to play
	 */
	public void prepareProPlay()
	{
		try 
		{
			proPlayer.setDataSource( proAudioUrl );
			proPlayer.prepare();
		} 
		catch( Exception ex ) 
		{
			ex.printStackTrace();
		}
	}
	/**
	 *  Start to play
	 */
	public void startGuidePlay()
	{
		if( guidePlayer != null )
		{
			try
			{
				guidePlayer.start();
			}
			catch( Exception ex ) 
			{
				ex.printStackTrace();
			}
		}
	}
	/**
	 *  Start to play
	 */
	public void startProPlay()
	{
		if( proPlayer != null )
		{
			try
			{
				proPlayer.start();
			}
			catch( Exception ex ) 
			{
				ex.printStackTrace();
			}
		}
	}
	/**
	 *  Pause play
	 */
	public void pauseGuidePlay()
	{
		if( guidePlayer != null )
		{
			try
			{
				guidePlayer.pause();
			}
			catch( Exception ex ) 
			{
				ex.printStackTrace();
			}
		}
	}
	/**
	 *  Pause play
	 */
	public void pauseProPlay()
	{
		if( proPlayer != null )
		{
			try
			{
				proPlayer.pause();
			}
			catch( Exception ex ) 
			{
				ex.printStackTrace();
			}
		}
	}
	/** 
	 *  Stop play
	 */
	public void stopGuidePlay()
	{
		if( guidePlayer != null )
		{
			try
			{
				guidePlayer.stop();
			}
			catch( Exception ex ) 
			{
				ex.printStackTrace();
			}
		}
	}
	/** 
	 *  Stop play
	 */
	public void stopProPlay()
	{
		if( proPlayer != null )
		{
			try
			{
				proPlayer.stop();
			}
			catch( Exception ex ) 
			{
				ex.printStackTrace();
			}
		}
	}
	/**
	 *  Seek to play
	 */
	public void seekGuidePlay( int progress )
	{
		try
		{
			guidePlayer.seekTo( progress * 1000 );
		}
		catch( Exception ex ) 
		{
			ex.printStackTrace();
		}
	}
	/**
	 *  Seek to play
	 */
	public void seekProPlay( int progress )
	{
		try
		{
			proPlayer.seekTo( progress * 1000 );
		}
		catch( Exception ex ) 
		{
			ex.printStackTrace();
		}
	}
	/**
	 *  Get progress
	 */
	public double getGuideProgress()
	{
		return guidePlayer.getCurrentPosition() / 1000.0;
	}
	/**
	 *  Get progress
	 */
	public double getProProgress()
	{
		return proPlayer.getCurrentPosition() / 1000.0;
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
