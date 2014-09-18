package view;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import model.AnswerListAdapter;
import util.Information;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.pku.museum.R;

import entity.AnswerEntity;
import entity.ProEntity;
import entity.QuestionEntity;

public class QuestionInfoActivity extends Activity
{
	private QuestionEntity entity;

	private ListView listView;
	private AnswerListAdapter adapter;
	private List< AnswerEntity > list;
	
	private TextView textAnswer;
	private TextView textLook;
	
	private Handler handler;
	
	private boolean isPlaying = false;
	private double progress = 0;
	private int length;
	private MediaPlayer player = null;
	private Timer timer = null;
	private ImageButton playButton;
	private String url;
	private SeekBar seekBar;
	
	@SuppressLint("HandlerLeak")
	protected void onCreate( Bundle savedInstanceState ) 
	{
		super.onCreate( savedInstanceState );

		entity = ( QuestionEntity ) getIntent().getExtras().getSerializable( "question" );
		Information.AnswerCount = entity.getAnswerCount();
		Information.LookCount = entity.getLookCount();
		
		handler = new Handler()
		{
			public void handleMessage( Message message )
			{
				switch( message.what )
				{
				case 0 :
					adapter.notifyDataSetChanged();
					break;
				case 1 :
					seekBar.setProgress( ( int ) progress );
					break;
				case 2 :
					stopPlay();
					break;
				}
			}
		};
		
		init();
	}
	
	private void init()
	{
		setContentView( R.layout.question_info );
		
		listView = ( ListView ) findViewById( R.id.answer_list );
		View headerView = ( View ) LayoutInflater.from( this ).inflate( R.layout.answer_list_header, null );
		
		TextView title = ( TextView ) headerView.findViewById( R.id.answer_list_header_title );
		title.setText( entity.getTitle() );
		TextView info = ( TextView ) headerView.findViewById( R.id.answer_list_header_info );
		info.setText( entity.getInfo() );
		
		TextView text1 = ( TextView ) headerView.findViewById( R.id.answer_list_header_text1 );
		text1.setText( entity.getVoiceCount() + "段语音" );
		textAnswer = ( TextView ) headerView.findViewById( R.id.answer_list_header_text2 );
		textAnswer.setText( entity.getAnswerCount() + "个回答" );
		textLook = ( TextView ) headerView.findViewById( R.id.answer_list_header_text3 );
		textLook.setText( entity.getLookCount() + "人关注" );
		
		Button buttonAnswer = ( Button ) headerView.findViewById( R.id.answer_list_header_button1 );
		buttonAnswer.setOnClickListener
		(
			new OnClickListener()
			{
				public void onClick( View view )
				{
					stopPlay();
					
					Intent intent = new Intent( QuestionInfoActivity.this, AnswerQuestionActivity.class );
					intent.putExtra( "type", 0 );
					intent.putExtra( "id", entity.getId() );
					startActivityForResult( intent, 0 );
				}
			}
		);
		
		Button buttonLook = ( Button ) headerView.findViewById( R.id.answer_list_header_button2 );
		buttonLook.setOnClickListener
		(
			new OnClickListener()
			{
				public void onClick( View view )
				{
					stopPlay();
					
					Intent intent = new Intent( QuestionInfoActivity.this, AnswerQuestionActivity.class );
					intent.putExtra( "type", 1 );
					intent.putExtra( "id", entity.getId() );
					startActivityForResult( intent, 1 );
				}
			}
		);
		
		RelativeLayout layout = ( RelativeLayout ) headerView.findViewById( R.id.answer_list_header_pro_layout );
		if( entity.getVoiceCount() == 0 )
		{
			layout.setVisibility( View.GONE );
		}
		else
		{
			ProEntity pro = entity.getProEntity();
			
			TextView pro1 = ( TextView ) headerView.findViewById( R.id.answer_list_header_pro_title );
			pro1.setText( pro.getName() );
			TextView pro2 = ( TextView ) headerView.findViewById( R.id.answer_list_header_pro_name );
			pro2.setText( pro.getExpertName() );
			TextView pro3 = ( TextView ) headerView.findViewById( R.id.answer_list_header_pro_job );
			pro3.setText( pro.getExpertTitle() );
			TextView pro4 = ( TextView ) headerView.findViewById( R.id.answer_list_header_pro_time );
			pro4.setText( pro.getTime() );
			
			ImageView image = ( ImageView ) headerView.findViewById( R.id.answer_list_header_pro_head );
			image.setImageBitmap( BitmapFactory.decodeFile( Information.RootPath + pro.getImageUrl() ) );
			
			playButton = ( ImageButton ) headerView.findViewById( R.id.answer_list_header_pro_play_button );
			playButton.setOnClickListener
			(
				new OnClickListener()
				{
					public void onClick( View view )
					{
						if( isPlaying == true )
						{
							pausePlay();
						}
						else
						{
							startPlay();
						}
					}
				}
			);
			
			url = Information.RootPath + pro.getAudioUrl();
			length = pro.getLength();
			seekBar = ( SeekBar ) headerView.findViewById( R.id.answer_list_header_pro_seekbar );		
			seekBar.setClickable( true );
			seekBar.setMax( length );
			seekBar.setOnSeekBarChangeListener
			(
				new OnSeekBarChangeListener()
				{
					public void onProgressChanged( SeekBar seekBar, int progress, boolean fromUser ) {}
					public void onStartTrackingTouch( SeekBar seekBar ) {}
					public void onStopTrackingTouch( SeekBar seekBar ) 
					{
						if( isPlaying )
						{
							progress = seekBar.getProgress();
							if( player != null )
							{
								try
								{
									player.seekTo( ( int ) progress * 1000 );
								}
								catch( Exception ex ) {}
							}
						}
					}
				}
			);
			seekBar.setProgressDrawable( this.getResources().getDrawable( R.drawable.seekbar_gray_bg ) );
			
			Drawable drawable = this.getResources().getDrawable( R.drawable.gray_progress );
			drawable.setBounds( 0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight() );
			seekBar.setThumb( drawable );
		}
		
		listView.addHeaderView( headerView );
		
		list = entity.getAnswerList();
		adapter = new AnswerListAdapter( this, list );
		listView.setAdapter( adapter );
		
		listView.setOnItemClickListener
		(
			new OnItemClickListener()
			{
				public void onItemClick( AdapterView<?> parent, View view, int position, long id ) 
				{
					stopPlay();
					
					AnswerEntity answer = list.get( position - 1 );
					Bundle bundle = new Bundle();
					bundle.putString( "title", entity.getTitle() );
					bundle.putString( "name", answer.getName() );
					bundle.putString( "info", answer.getSign() );
					bundle.putString( "content", answer.getAnswer() );
					
					Intent intent = new Intent( QuestionInfoActivity.this, AnswerInfoActivity.class );
					intent.putExtras( bundle );
					startActivity( intent );
				}
			}
		);
	}
	
	private void startPlay()
	{
		isPlaying = true;
		playButton.setImageResource( R.drawable.pro_stop );
		
		seekBar.setProgressDrawable( getResources().getDrawable( R.drawable.seekbar_orange ) );
		
		Drawable drawable = getResources().getDrawable( R.drawable.orange_progress );
		drawable.setBounds( 0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight() );
		seekBar.setThumb( drawable );
		
		if( player == null )
		{
			try
			{
				progress = 0;
				
				player = new MediaPlayer();
				player.setDataSource( url );
				player.prepare();
				
				player.setOnErrorListener
				(
					new OnErrorListener()
					{
						public boolean onError( MediaPlayer arg0, int arg1, int arg2 ) 
						{
							try
							{
								arg0.reset();
								arg0.setDataSource( url );
								arg0.prepare();
								arg0.start();
							}
							catch( Exception ex ) {}
							return true;
						}
					}
				);
	
				timer = new Timer();
				timer.schedule
				(
					new TimerTask()
					{
						public void run()
						{
							if( isPlaying )
							{
								if( progress < length )
								{
									progress += 0.1;
									handler.sendEmptyMessage( 1 );
								}
								else
								{
									handler.sendEmptyMessage( 2 );
									timer.cancel();
								}
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
		}
		player.start();
	}
	
	private void pausePlay()
	{
		isPlaying = false;
		playButton.setImageResource( R.drawable.pro_play );
		try
		{
			if( player != null )
			{
				player.pause();
			}
		}
		catch( Exception ex ){}
	}
	
	private void stopPlay()
	{
		isPlaying = false;
		playButton.setImageResource( R.drawable.pro_play );
		seekBar.setProgress( 0 );
		seekBar.setProgressDrawable( getResources().getDrawable( R.drawable.seekbar_gray_bg ) );
		
		Drawable drawable = getResources().getDrawable( R.drawable.gray_progress );
		drawable.setBounds( 0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight() );
		seekBar.setThumb( drawable );
		try
		{
			if( player != null )
			{
				player.stop();
				player.release();
				player = null;
			}
		}
		catch( Exception ex ){}
	}
	
	protected void onActivityResult( int requestCode, int resultCode, Intent data ) 
	{
		if( resultCode == 100 )
		{
			int count = entity.getAnswerCount() + 1;
			textAnswer.setText( count + "人回答" );
			Information.AnswerCount = count;
			Information.LookCount = entity.getLookCount();
			entity.setAnswerCount( count );
			
			AnswerEntity answer = new AnswerEntity();
			answer.setName( data.getStringExtra( "input1" ) );
			answer.setSign( data.getStringExtra( "input2" ) );
			answer.setAnswer( data.getStringExtra( "input3" ) );
			list.add( answer );
			handler.sendEmptyMessage( 0 );
		}
		else if( resultCode == 101 )
		{
			int count = entity.getLookCount() + 1;
			textLook.setText( count + "人关注" );
			Information.LookCount = count;
			Information.AnswerCount = entity.getAnswerCount();
			entity.setLookCount( count );
		}
	}
	
	public boolean onKeyDown( int keyCode, KeyEvent event )
	{
		if( keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 )
		{
			try
			{
				if( player != null )
				{
					player.stop();
					player.release();
					player = null;
				}
			}
			catch( Exception ex ){}
			setResult( 100 );
			finish();
			return true;
		}
		return false;
	}
}
