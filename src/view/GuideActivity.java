package view;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import model.GuidePlayListAdapter;
import model.MySeekBar;
import model.PopMenu;
import util.Information;
import util.Tool;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.pku.museum.R;

import entity.GuideEntity;
import entity.MuseumEntity;
import entity.PartEntity;
import entity.PlaceEntity;
import entity.ProEntity;

public class GuideActivity extends Activity
{
	public static Handler handler;
	public static int placeIndex = 0;
	public static int guideIndex = 0;
	
	private boolean isGuide = true;
	
	private int guideMode;
	
	private PopMenu placePopMenu;
	private PopMenu guidePopMenu;
	private SeekBar seekBar;
	private RelativeLayout seekBarLayout;
	
	private ListView listView;
	private GuidePlayListAdapter adapter;
	
	private TextView headerText1;
	private TextView headerText2;
	private TextView headerText3;
	private ImageView headerImage;

	private Button buttonChoosePlace;
	private Button buttonChooseGuide;
	private Button buttonPre;
	private Button buttonNext;
	private ImageButton buttonGuidePlay;
	
	private int partIndex;
	private GuideEntity guideEntity;
	private PlaceEntity placeEntity;
	private PartEntity partEntity;
	
	private TextView textTimeNow;
	private TextView textTimeAll;
	
	//Guide Play
	private MediaPlayer guidePlayer = null;
	private double guideProgress = 0;
	private boolean isGuidePlaying = false;
	private Timer timer = null;
	//ProPlay
	public static SparseBooleanArray proPlayMap = new SparseBooleanArray();
	public static int proPlayIndex = -1;
	public static int proPlayLength = 1;
	public static boolean isProPlaying = false;
	public static String audioUrl;
	//PartText
	private HashMap< TextView, Integer > partTextIndexMap;
	private SparseArray< TextView > partTextMap;
	//Bitmap
	private Bitmap smallBitmap = null;
	private Bitmap bigBitmap = null;
	private Dialog dialog;
	@SuppressLint({ "HandlerLeak" })
	protected void onCreate( Bundle savedInstanceState ) 
	{
		super.onCreate( savedInstanceState );
		
		placeIndex = 0;
		partIndex = 0;
		guideIndex = getIntent().getIntExtra( "guideIndex", 0 );
		guideMode = getIntent().getIntExtra( "guideMode", -1 );
		if( guideIndex == MuseumEntity.guideList.size() - 1 )
		{
			isGuide = false;
			guideEntity = MuseumEntity.GuideList.get( 0 );
		}
		else
		{
			isGuide = true;
			guideEntity = MuseumEntity.GuideList.get( guideIndex );
		}
		placeEntity = guideEntity.getPlaceList().get( placeIndex );
		partEntity = placeEntity.getPartList().get( partIndex );
		
		handler = new Handler()
		{
			public void handleMessage( Message message )
			{
				switch( message.what )
				{
				case 0 : //更新界面
					if( isGuide )
					{
						textTimeNow.setText( Tool.getStringBySecond( guideProgress ) );
						updateHeaderView();
						updatePreNextButton();
						updatePartListView();
					}
					break;
				case 1 : //专家列表开始播放
					if( GuideActivity.proPlayIndex != message.arg1 )
					{
						MySeekBar.getInstance().stopPlay();
						GuideActivity.proPlayIndex = message.arg1;
						proPlayMap.put( proPlayIndex, false );

						updateWaitText();

						if( isGuidePlaying == true )
						{
							pauseGuide();
						}
						
						isProPlaying = true;
						audioUrl = Information.RootPath + MuseumEntity.ProMap.get( partEntity.getTags()[ 0 ] ).get( proPlayIndex ).getAudioUrl();
						proPlayLength = MuseumEntity.ProMap.get( partEntity.getTags()[ 0 ] ).get( proPlayIndex ).getLength();
						MySeekBar.getInstance().seekBarMap.get( proPlayIndex ).setMax( proPlayLength );
						MySeekBar.getInstance().init();				
					}
					adapter.notifyDataSetChanged();
					MySeekBar.getInstance().startPlay();
					break;
				case 2 : //等待播放按钮改变
					updateWaitText();
					break;
				case 3 : //暂停专家播放
					adapter.notifyDataSetChanged();
					MySeekBar.getInstance().pausePlay();
					break;
				case 4 : //专家播放完毕
					isProPlaying = false;
					playNextPro();
					break;
				case 5 : //导游播放完毕
					if( isGuide )
					{
						showNextPlace();
					}
					break;
				case 6 : //导游播放按钮响应，停止专家播放
					proPlayIndex = -1;
					isProPlaying = false;
					MySeekBar.getInstance().stopPlay();
					adapter.notifyDataSetChanged();
					break;
				case 7 : //更新导游播放进度条
					seekBar.setProgress( ( int ) guideProgress );
					textTimeNow.setText( Tool.getStringBySecond( guideProgress ) );
					break;
				case 8 : //更新专家播放进度条
					MySeekBar.getInstance().updateSeekBar();
					break;
				}
			}
		};

		init();
	}
	/**
	 *  界面初始化
	 */
	private void init()
	{
		setContentView( R.layout.guide_play );
		
		RelativeLayout layout2 = ( RelativeLayout ) findViewById( R.id.guide_play_bottom_layout2 );
		layout2.setVisibility( View.VISIBLE );
		
		if( isGuide == false )
		{
			showNoGuide();
		}
		
		Button buttonAsk = ( Button ) findViewById( R.id.guide_title_ask );
		buttonAsk.setOnClickListener
		(
			new OnClickListener()
			{
				public void onClick( View view )
				{
					pauseAll();
					
					Intent intent = new Intent( GuideActivity.this, QuestionActivity.class );
					startActivity( intent );
				}
			}
		);
		
		buttonChoosePlace = ( Button ) findViewById( R.id.guide_title_down );
		buttonChoosePlace.setText( MuseumEntity.placeList.get( placeIndex ) );
		buttonChoosePlace.setOnClickListener
		(
			new OnClickListener()
			{
				public void onClick( View view )
				{
					showPlacePopMenu( view );
				}
			}
		);
		
		buttonChooseGuide = ( Button ) findViewById( R.id.guide_title_guide );
		buttonChooseGuide.setOnClickListener
		(
			new OnClickListener()
			{
				public void onClick( View view )
				{
					showGuidePopMenu( view );
				}
			}
		);
		
		buttonPre = ( Button ) findViewById( R.id.guide_play_button_pre );
		if( placeIndex != 0 ) 
		{
			buttonPre.setText( "   上一展厅" );
		}
		buttonPre.setOnClickListener
		(
			new OnClickListener()
			{
				public void onClick( View view )
				{
					if( partIndex != 0 )
					{
						showPrePart();
					}
					else if( placeIndex != 0 )
					{
						showPrePlace();
					}
				}
			}
		);
		
		textTimeNow = ( TextView ) findViewById( R.id.guide_play_nowtime );
		textTimeAll = ( TextView ) findViewById( R.id.guide_play_totaltime );
		
		buttonNext = ( Button ) findViewById( R.id.guide_play_button_next );
		if( placeEntity.getPartList().size() > 1 )
		{
			buttonNext.setText( placeEntity.getPartList().get( 1 ).getName() + "   " );
		}
		else if( placeIndex != MuseumEntity.placeList.size() - 1 )
		{			
			buttonNext.setText( "下一展厅   " );
		}
		buttonNext.setOnClickListener
		(
			new OnClickListener()
			{
				public void onClick( View view )
				{
					if( partIndex != placeEntity.getPartList().size() - 1 )
					{
						showNextPart();
					}
					else if( placeIndex != MuseumEntity.placeList.size() - 1 )
					{
						showNextPlace();						
					}
				}
			}
		);
		
		buttonGuidePlay = ( ImageButton ) findViewById( R.id.guide_play_button_center );
		buttonGuidePlay.setOnClickListener
		(
			new OnClickListener()
			{
				public void onClick( View view )
				{
					if( isGuidePlaying == true )
					{
						pauseGuide();
					}
					else
					{
						startGuide();
					}
						
					handler.sendEmptyMessage( 6 );
				}
			}
		);
		
		seekBarLayout = ( RelativeLayout ) findViewById( R.id.guide_play_seekbar_layout );

		seekBar = ( SeekBar ) findViewById( R.id.guide_play_seekbar );
		seekBar.setClickable( true );
		seekBar.setMax( placeEntity.getLength() );
		seekBar.setOnSeekBarChangeListener
		(
			new OnSeekBarChangeListener()
			{
				public void onProgressChanged( SeekBar seekBar, int progress, boolean fromUser ) {}
				public void onStartTrackingTouch( SeekBar seekBar ) {}
				public void onStopTrackingTouch( SeekBar seekBar ) 
				{
					guideProgress = seekBar.getProgress();
					judgeBetweenPoint();
					try
					{						
						guidePlayer.seekTo( ( int ) guideProgress * 1000 );
						if( isGuidePlaying == false )
						{
							guidePlayer.pause();
						}
					}
					catch( Exception ex ) {}
				}
			}
		);
		//ProList
		listView = ( ListView ) findViewById( R.id.guide_play_pro_list );
		
		View headerView = getLayoutInflater().inflate( R.layout.guide_play_pro_list_header, null );
		headerText1 = ( TextView ) headerView.findViewById( R.id.guide_play_pro_list_header_text1 );
		headerText2 = ( TextView ) headerView.findViewById( R.id.guide_play_pro_list_header_text2 );
		headerText3 = ( TextView ) headerView.findViewById( R.id.guide_play_pro_list_header_text3 );
		headerImage = ( ImageView ) headerView.findViewById( R.id.guide_play_pro_list_header_image );
		headerImage.setOnClickListener
		(
			new OnClickListener()
			{
				public void onClick( View v )
				{
					dialog = new Dialog( GuideActivity.this, R.style.big_image );
					LayoutInflater inflater = LayoutInflater.from( GuideActivity.this );  
					View view = inflater.inflate( R.layout.show_big_image, null );
					ImageView image = ( ImageView ) view.findViewById( R.id.big_image );
					bigBitmap = Tool.resizeImageToBig( Information.RootPath + partEntity.getImageUrl() );
					image.setImageBitmap( bigBitmap );
					image.setOnClickListener
					(
						new OnClickListener()
						{
							public void onClick( View v )
							{
								dialog.dismiss();
							}
						}
					);
					
					WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
					dialog.getWindow().setAttributes( layoutParams );
					dialog.setContentView( view );
					dialog.show();
					dialog.setOnDismissListener
					(
						new OnDismissListener()
						{
							public void onDismiss( DialogInterface arg0 )
							{
								if( bigBitmap != null && !bigBitmap.isRecycled() )
								{
									bigBitmap.recycle();
									bigBitmap = null;
								}
							}
						}
					);
				}
			}
		);
		
		updateHeaderView();
		listView.addHeaderView( headerView );
		
		View footerView = getLayoutInflater().inflate( R.layout.guide_play_pro_list_footer, null );
		listView.addFooterView( footerView );
		
		updatePartListView();
		
		if( isGuide )
		{
			addPoint();
			
			startGuidePlay();
		}
	}
	/**
	 * 判断是否有专家等待播放，若有就播放，若没有就继续导游播放
	 */
	private void playNextPro()
	{
		boolean symbol = true;
		final int count = proPlayMap.size();
		for( int i = 0; i < count; i ++ )
			if( proPlayMap.get( i ) )
			{
				symbol = false;
				Message newMessage = handler.obtainMessage();
				newMessage.arg1 = i;
				newMessage.what = 1;
				handler.sendMessage( newMessage );
				break;
			}
		if( symbol == true )
		{
			proPlayIndex = -1;
			adapter.notifyDataSetChanged();
			
			if( isGuide )
			{
				startGuide();
			}
		}
	}
	/**
	 * 继续导游播放
	 */
	private void startGuide()
	{
		try
		{
			isGuidePlaying = true;
			buttonGuidePlay.setImageResource( R.drawable.guide_stop );
			guidePlayer.start();
		}
		catch( Exception ex ) {}
	}
	/**
	 * 暂停导游播放
	 */
	private void pauseGuide()
	{
		try
		{
			isGuidePlaying = false;
			buttonGuidePlay.setImageResource( R.drawable.guide_play );
			guidePlayer.pause();
		}
		catch( Exception ex ) {}
	}
	/**
	 * 点击等待播放按钮后，更新header界面
	 */
	private void updateWaitText()
	{
		int count = 0;
		final int mapSize = proPlayMap.size();
		for( int i = 0; i < mapSize; i ++ )
			if( proPlayMap.get( i ) == true )
			{
				count ++;
			}
		headerText3.setText( count + " 段等待播放" );
	}
	/**
	 * 初始化导游播放，然后开始
	 */
	private void startGuidePlay()
	{
		try
		{
			guidePlayer = new MediaPlayer();
			guidePlayer.setDataSource( Information.RootPath + placeEntity.getAudioUrl() );
			guidePlayer.prepare();
			guidePlayer.start();
			
			guidePlayer.setOnErrorListener
			(
				new OnErrorListener()
				{
					public boolean onError( MediaPlayer arg0, int arg1, int arg2 ) 
					{
						try
						{
							arg0.reset();
							arg0.setDataSource( Information.RootPath + placeEntity.getAudioUrl() );
							arg0.prepare();
							arg0.start();
						}
						catch( Exception ex ) {}
						return true;
					}
				}
			);
			
			guidePlayer.setOnCompletionListener
			(
				new OnCompletionListener()
				{
					public void onCompletion( MediaPlayer mp ) 
					{
						handler.sendEmptyMessage( 5 );
						stopTimer();
					}
				}
			);
			
			guideProgress = 0;
			isGuidePlaying = true;
			buttonGuidePlay.setImageResource( R.drawable.guide_stop );
			
			textTimeNow.setText( "00:00" );
			textTimeAll.setText( Tool.getStringBySecond( placeEntity.getLength() ) );
			
			stopTimer();
			startTimer();
		}
		catch( Exception ex ) {}
	}
	/**
	 * 
	 */
	private void stopTimer()
	{
		if( timer != null )
		{
			timer.cancel();
			timer = null;
		}
	}
	/**
	 * 
	 */
	private void startTimer()
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
	/**
	 * 
	 */
	private void seekbarAction()
	{
		if( isGuide && isGuidePlaying )
		{
			guideProgress += guidePlayer.getCurrentPosition() * 1.0 / 1000;
			handler.sendEmptyMessage( 7 );
			judgeAtPoint();
		}
	}
	/**
	 *  检查是否到达特殊时间点并响应
	 */
	private void judgeAtPoint()
	{
		final int count = placeEntity.getPartList().size();
		for( int i = partIndex + 1; i < count; i ++ )
		{
			if( placeEntity.getPartList().get( i ).getStart() - guideProgress < 0.2 && placeEntity.getPartList().get( i ).getStart() - guideProgress >= 0.1 )
			{
				judgeNextPro();
				break;
			}
			else if( placeEntity.getPartList().get( i ).getStart() - guideProgress <= 0 )
			{
				partIndex = i;
				partEntity = placeEntity.getPartList().get( partIndex );
				handler.sendEmptyMessage( 0 );
				break;
			}
		}
		if( placeEntity.getLength() - guideProgress < 0.2 && placeEntity.getLength() - guideProgress >= 0.1 )
		{
			judgeNextPro();
		}
	}
	/**
	 * 导游播放要到达节点之前，先判断是否有专家等待播放
	 */
	private void judgeNextPro()
	{
		final int sum = proPlayMap.size();
		for( int j = 0; j < sum; j ++ )
			if( proPlayMap.get( j ) )
			{
				Message newMessage = handler.obtainMessage();
				newMessage.arg1 = j;
				newMessage.what = 1;
				handler.sendMessage( newMessage );
				break;
			}
	}
	/**
	 *  检查是否介于时间点之间并响应
	 */
	private void judgeBetweenPoint()
	{
		boolean symbol = false;
		final int count = placeEntity.getPartList().size();
		for( int i = 0; i < count - 1; i ++ )
		{
			if( placeEntity.getPartList().get( i ).getStart() <= guideProgress && placeEntity.getPartList().get( i + 1 ).getStart() > guideProgress )
			{
				if( partIndex != i )
				{
					symbol = true;
					partIndex = i;
					partEntity = placeEntity.getPartList().get( partIndex );
					handler.sendEmptyMessage( 0 );
				}
				return;
			}
		}
		if( symbol == false && partIndex != count - 1 )
		{
			partIndex = count - 1;
			partEntity = placeEntity.getPartList().get( partIndex );
			handler.sendEmptyMessage( 0 );
		}
	}
	/**
	 * 添加时间点
	 */
	private void addPoint()
	{
		final int count = placeEntity.getPartList().size();
		for( int i = 0; i < count; i ++ )
		{	
			PartEntity partEntity = placeEntity.getPartList().get( i );
			
			ImageView imageView = new ImageView( this );
			imageView.setImageResource( R.drawable.point );
			LayoutParams params = new LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT );
			params.addRule( RelativeLayout.CENTER_VERTICAL );
			double value = ( partEntity.getStart() * 1.0 * 654 / placeEntity.getLength() + 28 ) * Information.ScreenWidth / 720;
			if( i == 0 )
			{
				value = partEntity.getStart() * 1.0 * 654 / placeEntity.getLength() * Information.ScreenWidth / 720 + 14 * Information.ScreenDensity;
			}
			params.leftMargin = ( new BigDecimal( value ).setScale( 0, BigDecimal.ROUND_HALF_UP ) ).intValue();
			imageView.setLayoutParams( params );
			seekBarLayout.addView( imageView );
			
			TextView textView = new TextView( this );
			textView.setText( "" + ( i + 1 ) );
			textView.setTextSize( 10 );
			textView.setTextColor( this.getResources().getColor( R.color.bg_milk_green ) );
			LayoutParams params1 = new LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT );
			params1.addRule( RelativeLayout.ALIGN_PARENT_TOP );
			value = ( partEntity.getStart() * 1.0 / placeEntity.getLength() * 654  + 27 ) * Information.ScreenWidth / 720;
			if( i == 0 )
			{
				value = partEntity.getStart() * 1.0 / placeEntity.getLength() * 654 * Information.ScreenWidth / 720 + 13.5 * Information.ScreenDensity;
			}
			params1.leftMargin = ( new BigDecimal( value ).setScale( 0, BigDecimal.ROUND_HALF_UP ) ).intValue();
			textView.setLayoutParams( params1 );
			seekBarLayout.addView( textView );
		}
	}
	/**
	 * 显示上一个展厅
	 */
	private void showPrePart()
	{
		partIndex --;
		partEntity = placeEntity.getPartList().get( partIndex );
		
		updatePartView();
	}
	/**
	 * 显示下一个展厅
	 */
	private void showNextPart()
	{
		partIndex ++;
		partEntity = placeEntity.getPartList().get( partIndex );
		
		updatePartView();
	}
	/**
	 * 更新当前展厅所有界面
	 */
	private void updatePartView()
	{
		seekBar.setProgress( partEntity.getStart() );
		guideProgress = partEntity.getStart();
		textTimeNow.setText( Tool.getStringBySecond( guideProgress ) );
		
		try
		{
			guidePlayer.seekTo( ( int ) guideProgress * 1000 );
			if( isGuidePlaying == false )
			{
				guidePlayer.pause();
			}
		}
		catch( Exception ex ) {}
		
		updateHeaderView();
		updatePreNextButton();
		updatePartListView();
	}
	/**
	 * 更新前进后退按钮
	 */
	private void updatePreNextButton()
	{
		buttonPre.setText( "" );
		buttonNext.setText( "" );
		if( partIndex > 0 )
		{
			buttonPre.setText( "   " + placeEntity.getPartList().get( partIndex - 1 ).getName() );
		}
		else if( placeIndex != 0 )
		{
			buttonPre.setText( "   上一展厅" );
		}
		if( partIndex < placeEntity.getPartList().size() - 1 )
		{
			buttonNext.setText( placeEntity.getPartList().get( partIndex + 1 ).getName() + "   " );
		}
		else if( placeIndex != MuseumEntity.placeList.size() - 1 )
		{
			buttonNext.setText( "下一展厅   " );
		}
	}
	/**
	 * 更新界面的header
	 */
	private void updateHeaderView()
	{
		String position = "";
		if( partEntity.getPosition() != null )
		{
			position = partEntity.getPosition();
		}
		headerText1.setText( position + " " + partEntity.getName() );
		int talkCount = 0;
		if( partEntity.getTags().length > 0 && MuseumEntity.ProMap.get( partEntity.getTags()[ 0 ] ) != null )
		{
			talkCount = MuseumEntity.ProMap.get( partEntity.getTags()[ 0 ] ).size();
		}
		headerText2.setText( talkCount + " 段详解" );
		
		headerText3.setText( "0 段等待播放" );
		
		int width = ( int ) ( Information.ScreenWidth * 0.375 );
		int height = width * 2 / 3;
		smallBitmap = Tool.cutImage( Tool.resizeBitmap( Information.RootPath + partEntity.getImageUrl(), width, height ), width, height );
		headerImage.setImageBitmap( smallBitmap );
	}
	/**
	 * 更新界面的专家列表
	 */
	private void updatePartListView()
	{
		isProPlaying = false;
		MySeekBar.getInstance().cancelTimer();
		proPlayIndex = -1;
		MySeekBar.getInstance().seekBarMap.clear();
		MySeekBar.getInstance().stopPlay();
		if( partEntity.getTags().length > 0 && MuseumEntity.ProMap.get( partEntity.getTags()[ 0 ] ) != null )
		{
			adapter = new GuidePlayListAdapter( this, MuseumEntity.ProMap.get( partEntity.getTags()[ 0 ] ) );
			updateProPlayMap( MuseumEntity.ProMap.get( partEntity.getTags()[ 0 ] ).size() );
		}
		else
		{
			adapter = new GuidePlayListAdapter( this, new ArrayList< ProEntity >() );
		}
		listView.setAdapter( adapter );
	}
	/**
	 * 显示选择展厅的弹出框
	 * @param view
	 */
	private void showPlacePopMenu( View view )
	{
		placePopMenu = new PopMenu( this, 0 );
		placePopMenu.addItems( MuseumEntity.placeList );
		placePopMenu.showAsDropDown( view );
		
		placePopMenu.setOnItemClickListener
		(
			new OnItemClickListener()
			{
				public void onItemClick( AdapterView<?> arg0, View arg1, int position, long arg3 )
				{
					if( placeIndex != position )
					{
						placeIndex = position;
						updatePlace();
						placePopMenu.dismiss();
					}
				}
			}
		);
	}
	/**
	 * 展示自助游界面
	 */
	private void showNoGuide()
	{
		try
		{
			MySeekBar.getInstance().stopPlay();
			if( guidePlayer != null )
			{
				guidePlayer.stop();
				guidePlayer.release();
			}
		}
		catch( Exception ex ) {}
		
		LinearLayout layout1 = ( LinearLayout ) findViewById( R.id.guide_play_bottom_layout1 );
		layout1.setVisibility( View.VISIBLE );
		
		Button buttonUp = ( Button ) findViewById( R.id.guide_play_button_up );
		buttonUp.setOnClickListener
		(
			new OnClickListener()
			{
				public void onClick( View v )
				{
					if( placeIndex > 0 )
					{
						placeIndex --;
						updatePlace();
					}
				}
			}
		);
		Button buttonDown = ( Button ) findViewById( R.id.guide_play_button_down );
		buttonDown.setOnClickListener
		(
			new OnClickListener()
			{
				public void onClick( View v )
				{
					if( placeIndex < guideEntity.getPlaceList().size() - 1 )
					{
						placeIndex ++;
						updatePlace();
					}
				}
			}
		);
		
		RelativeLayout layout2 = ( RelativeLayout ) findViewById( R.id.guide_play_bottom_layout2 );
		layout2.setVisibility( View.GONE );
		
		updatePartTable();
	}
	/**
	 * 更新自助游界面下方的展厅table
	 */
	private void updatePartTable()
	{
		final int count = placeEntity.getPartList().size();
		partTextMap = new SparseArray< TextView >( count );
		partTextIndexMap = new HashMap< TextView, Integer >( count );
		
		TableLayout table = ( TableLayout ) findViewById( R.id.guide_play_table );
		final int sum = count / 6;
		for( int i = 0; i < sum; i ++ )
		{	
			TableRow row = ( TableRow ) LayoutInflater.from( this ).inflate( R.layout.guide_play_table_row, null );
			TextView text1 = ( TextView ) row.findViewById( R.id.guide_play_table_row_text1 );
			text1.setText( placeEntity.getPartList().get( i * 6 ).getPosition() );
			partTextIndexMap.put( text1, i * 6 );
			partTextMap.put( i * 6, text1 );
			TextView text2 = ( TextView ) row.findViewById( R.id.guide_play_table_row_text2 );
			text2.setText( placeEntity.getPartList().get( i * 6 + 1 ).getPosition() );
			text2.setVisibility( View.VISIBLE );
			partTextIndexMap.put( text2, i * 6 + 1 );
			partTextMap.put( i * 6 + 1, text2 );
			TextView text3 = ( TextView ) row.findViewById( R.id.guide_play_table_row_text3 );
			text3.setText( placeEntity.getPartList().get( i * 6 + 2 ).getPosition() );
			text3.setVisibility( View.VISIBLE );
			partTextIndexMap.put( text3, i * 6 + 2 );
			partTextMap.put( i * 6 + 2, text3 );
			TextView text4 = ( TextView ) row.findViewById( R.id.guide_play_table_row_text4 );
			text4.setText( placeEntity.getPartList().get( i * 6 + 3 ).getPosition() );
			text4.setVisibility( View.VISIBLE );
			partTextIndexMap.put( text4, i * 6 + 3 );
			partTextMap.put( i * 6 + 3, text4 );
			TextView text5 = ( TextView ) row.findViewById( R.id.guide_play_table_row_text5 );
			text5.setText( placeEntity.getPartList().get( i * 6 + 4 ).getPosition() );
			text5.setVisibility( View.VISIBLE );
			partTextIndexMap.put( text5, i * 6 + 4 );
			partTextMap.put( i * 6 + 4, text5 );
			TextView text6 = ( TextView ) row.findViewById( R.id.guide_play_table_row_text6 );
			text6.setText( placeEntity.getPartList().get( i * 6 + 5 ).getPosition() );
			text6.setVisibility( View.VISIBLE );
			partTextIndexMap.put( text6, i * 6 + 5 );
			partTextMap.put( i * 6 + 5, text6 );
			
			table.addView( row );
		}
		final int rest = count - sum * 6;
		if( rest > 0 )
		{
			TableRow row = ( TableRow ) LayoutInflater.from( this ).inflate( R.layout.guide_play_table_row, null );
			
			TextView text1 = ( TextView ) row.findViewById( R.id.guide_play_table_row_text1 );
			text1.setText( placeEntity.getPartList().get( sum * 6 ).getPosition() );
			partTextIndexMap.put( text1, sum * 6 );
			partTextMap.put( sum * 6, text1 );
			
			if( rest > 1 )
			{
				TextView text2 = ( TextView ) row.findViewById( R.id.guide_play_table_row_text2 );
				text2.setText( placeEntity.getPartList().get( sum * 6 + 1 ).getPosition() );
				text2.setVisibility( View.VISIBLE );
				partTextIndexMap.put( text2, sum * 6 + 1 );
				partTextMap.put( sum * 6 + 1, text2 );
				
				if( rest > 2 )
				{
					TextView text3 = ( TextView ) row.findViewById( R.id.guide_play_table_row_text3 );
					text3.setText( placeEntity.getPartList().get( sum * 6 + 2 ).getPosition() );
					text3.setVisibility( View.VISIBLE );
					partTextIndexMap.put( text3, sum * 6 + 2 );
					partTextMap.put( sum * 6 + 2, text3 );
					
					if( rest > 3 )
					{
						TextView text4 = ( TextView ) row.findViewById( R.id.guide_play_table_row_text4 );
						text4.setText( placeEntity.getPartList().get( sum * 6 + 3 ).getPosition() );
						text4.setVisibility( View.VISIBLE );
						partTextIndexMap.put( text4, sum * 6 + 3 );
						partTextMap.put( sum * 6 + 3, text4 );
						
						if( rest > 4 )
						{
							TextView text5 = ( TextView ) row.findViewById( R.id.guide_play_table_row_text5 );
							text5.setText( placeEntity.getPartList().get( sum * 6 + 4 ).getPosition() );
							text5.setVisibility( View.VISIBLE );
							partTextIndexMap.put( text5, sum * 6 + 4 );
							partTextMap.put( sum * 6 + 4, text5 );
						}
					}
				}
			}
			
			table.addView( row );
		}
		
		TextView text = partTextMap.get( partIndex );
		text.setTextColor( Color.MAGENTA );
		text.setBackgroundColor( Color.WHITE );
		
		setPartTextListener();
	}
	/**
	 * 给展厅table添加选中事件
	 */
	private void setPartTextListener()
	{
		final int count = placeEntity.getPartList().size();
		for( int i = 0; i < count; i ++ )
		{
			TextView text = partTextMap.get( i );
			text.setOnClickListener
			(
				new OnClickListener()
				{
					public void onClick( View v )
					{				
						TextView temp = ( TextView ) v;
						int index = partTextIndexMap.get( temp );
						if( partIndex != index )
						{
							TextView last = partTextMap.get( partIndex );
							last.setTextColor( Color.WHITE );
							last.setBackgroundColor( 0x00000000 );

							temp.setTextColor( Color.MAGENTA );
							temp.setBackgroundColor( Color.WHITE );
							partIndex = index;
							
							partEntity = placeEntity.getPartList().get( partIndex );
							
							updatePartView();
						}
					}
				}
			);
		}
	}
	/**
	 * 显示导游选择弹出框
	 * @param view
	 */
	private void showGuidePopMenu( View view )
	{
		guidePopMenu = new PopMenu( this, 1 );
		guidePopMenu.addItems( MuseumEntity.guideList );
		guidePopMenu.showAsDropDown( view );
		
		guidePopMenu.setOnItemClickListener
		(
			new OnItemClickListener()
			{
				public void onItemClick( AdapterView<?> arg0, View arg1, int position, long arg3 )
				{
					if( guideIndex != position )
					{
						guideIndex = position;
						if( guideIndex == MuseumEntity.guideList.size() - 1 )
						{
							isGuide = false;
							showNoGuide();
						}
						else
						{
							isGuide = true;
							updateGuide();					
						}
						guidePopMenu.dismiss();
					}
				}
			}
		);
	}
	/**
	 * 选择不同的导游后，开始更新
	 */
	private void updateGuide()
	{
		placeIndex = 0;
		proPlayIndex = -1;
		MySeekBar.getInstance().stopPlay();
		partIndex = 0;
		guideProgress = 0;
		guideEntity = MuseumEntity.GuideList.get( guideIndex );
		placeEntity = guideEntity.getPlaceList().get( placeIndex );
		partEntity = placeEntity.getPartList().get( partIndex );
		try
		{
			guidePlayer.stop();
			guidePlayer.release();
		}
		catch( Exception ex ) {}
		
		if( partEntity.getTags().length > 0 && MuseumEntity.ProMap.get( partEntity.getTags()[ 0 ] ) != null )
		{
			updateProPlayMap( MuseumEntity.ProMap.get( partEntity.getTags()[ 0 ] ).size() );
		}
		
		init();
	}
	/**
	 */
	private void updatePlace()
	{
		buttonChoosePlace.setText( MuseumEntity.placeList.get( placeIndex ) );

		guideProgress = 0;
		partIndex = 0;
		proPlayIndex = -1;
		MySeekBar.getInstance().stopPlay();
		placeEntity = guideEntity.getPlaceList().get( placeIndex );
		partEntity = placeEntity.getPartList().get( partIndex );
		
		try
		{
			guidePlayer.stop();
			guidePlayer.release();
		}
		catch( Exception ex ) {}
		
		if( partEntity.getTags().length > 0 && MuseumEntity.ProMap.get( partEntity.getTags()[ 0 ] ) != null )
		{
			updateProPlayMap( MuseumEntity.ProMap.get( partEntity.getTags()[ 0 ] ).size() );
		}
		
		init();
	}
	/**
	 * 播放上一展厅
	 */
	private void showPrePlace()
	{
		placeIndex --;
		updatePlace();
	}
	/**
	 * 当前展厅播放完毕，继续播放下一展厅
	 */
	private void showNextPlace()
	{
		if( placeIndex < MuseumEntity.placeList.size() - 1 )
		{
			placeIndex ++;
			updatePlace();
		}
		else
		{
			seekBar.setProgress( 0 );
			isGuidePlaying = false;
			buttonGuidePlay.setImageResource( R.drawable.pro_play );
		}
	}
	/**
	 * 更新数据
	 * @param count
	 */
	private void updateProPlayMap( final int count )
	{
		List< ProEntity > proList = MuseumEntity.ProMap.get( partEntity.getTags()[ 0 ] );
		proPlayMap.clear();
		for( int i = 0; i < count; i ++ )
		{
			ProEntity entity = proList.get( i );
			if( guideMode == 1 && Tool.judgeInProList1( entity.getName() ) )
			{
				proPlayMap.put( i, true );
			}
			else if( guideMode == 2 && Tool.judgeInProList2( entity.getName() ) )
			{
				proPlayMap.put( i, true );
			}
			else
			{
				proPlayMap.put( i, false );
			}
		}
	}
	/**
	 * 暂停所有播放
	 */
	private void pauseAll()
	{
		if( isGuidePlaying == true )
		{
			pauseGuide();
		}
		else if( isProPlaying == true )
		{
			handler.sendEmptyMessage( 6 );
		}
	}
	/**
	 * 监听返回事件，终止所有播放
	 */
	public boolean onKeyDown( int keyCode, KeyEvent event )
	{
		if( keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 )
		{
			try
			{
				if( timer != null )
				{
					timer.cancel();
				}
				MySeekBar.getInstance().cancelTimer();
				if( guidePlayer != null )
				{
					guidePlayer.stop();
					guidePlayer.release();
				}
			}
			catch( Exception ex ) {}
			MySeekBar.getInstance().stopPlay();
			finish();
			return true;
		}
		return false;
	}
	/**
	 * 
	 */
	protected void onPause()
	{
		super.onPause();
		if( isGuidePlaying )
		{
			stopTimer();
		}
		else if( isProPlaying )
		{
			MySeekBar.getInstance().stopTimer();
		}
	}
	/**
	 * 
	 */
	protected void onResume()
	{
		super.onResume();
		
	}
}