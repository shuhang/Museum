package view;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONObject;

import util.Information;
import util.Tool;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pku.museum.R;

import entity.AnswerEntity;
import entity.QuestionEntity;

public class AddQuestionActivity extends Activity
{
	private EditText textTitle;
	private EditText textInfo;
	private EditText textEmail;
	
	private Handler handler;
	
	private Dialog dialog;
	private HttpURLConnection connection;
	private int id;
	private String title;
	private String info;
	
	@SuppressLint("HandlerLeak")
	protected void onCreate( Bundle savedInstanceState ) 
	{
		super.onCreate( savedInstanceState );
		
		handler = new Handler()
		{
			public void handleMessage( Message message )
			{
				if( dialog != null )
				{
					dialog.dismiss();
				}
				switch( message.what )
				{
				case 0 : //提交成功
					showMessage( "提交成功" );
					QuestionEntity entity = new QuestionEntity();
					entity.setId( id );
					entity.setAnswerCount( 0 );
					entity.setAnswerList( new ArrayList< AnswerEntity >() );
					entity.setInfo( info );
					entity.setTitle( title );
					entity.setLookCount( 1 );
					entity.setVoiceCount( 0 );
					Bundle bundle = new Bundle();
					bundle.putSerializable( "question", entity );
					Intent intent = new Intent( AddQuestionActivity.this, QuestionInfoActivity.class );
					intent.putExtras( bundle );
					startActivityForResult( intent, 0 );
					break;
				case 1 : //提交失败
					showMessage( "提交失败" );
					break;
				}
			}
		};
		
		init();
	}
	
	private void init()
	{
		setContentView( R.layout.add_question );
		
		textTitle = ( EditText ) findViewById( R.id.add_question_input1 );
		textInfo = ( EditText ) findViewById( R.id.add_question_input2 );
		textEmail = ( EditText ) findViewById( R.id.add_question_input3 );
		
		Button buttonAdd = ( Button ) findViewById( R.id.add_question_button );
		buttonAdd.setOnClickListener
		(
			new OnClickListener()
			{
				public void onClick( View view )
				{
					judgeInput();
				}
			}
		);
	}
	
	private void judgeInput()
	{
		title = textTitle.getText().toString();
		final int titleLength = title.replaceAll( " ", "" ).length();
		if( titleLength == 0 || titleLength > 100 )
		{
			showMessage( "问题输入不正确" );
			return;
		}
		info = textInfo.getText().toString();
		if( info.length() > 200 )
		{
			showMessage( "补充描述输入不正确" );
			return;
		}
		final String email = textEmail.getText().toString();
		if( !Tool.isEmail( email ) || email.length() > 30 )
		{
			showMessage( "邮箱格式不正确" );
			return;
		}
		addQuestion( title, info, email );
	}
	
	private void addQuestion( final String title, final String info, final String email )
	{
		if( Tool.isNetworkConnected( this ) )
		{
			dialog = new Dialog( this, R.style.my_dialog );
			LayoutInflater inflater = LayoutInflater.from( this );  
			View view = inflater.inflate( R.layout.dialog_progress_view, null );
			TextView textView = ( TextView ) view.findViewById( R.id.dialog_textview );
			textView.setText( "正在发布" );
			
			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.alpha = 0.8f;
			dialog.getWindow().setAttributes( layoutParams );
			dialog.setContentView( view );
			dialog.setCancelable( false );
			dialog.setOnKeyListener
			(
				new OnKeyListener()
				{
					public boolean onKey( DialogInterface dialog, int keyCode, KeyEvent event ) 
					{
						if( keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 )
						{
							if( connection != null )
							{
								connection.disconnect();
							}
							dialog.dismiss();
							return true;
						}
						return false;
					}
				}
			);
			dialog.show();
			
			new Thread
	    	(
	    		new Thread()
	    		{
	    			public void run()
	    			{
	    				doAddQuestion( title, info, email );
	    			}
	    		}
	    	).start();
		}
		else
		{
			showMessage( "没有网络，请检查网络连接" );
		}
	}
	
	private void doAddQuestion( final String title, final String info, final String email )
	{
		final String urlString = Information.Server_Url + "/questions.json";
		try
		{		
			final String paramsString = "question[question]=" + URLEncoder.encode( title, "utf-8" ) 
					 + "&question[description]=" + URLEncoder.encode( info, "utf-8" ) + "&follower_email=" + URLEncoder.encode( email, "utf-8" );

			URL url = new URL( urlString );
			connection = ( HttpURLConnection ) url.openConnection();
			connection.setRequestMethod( "POST" );
			connection.setConnectTimeout( 10000 );
			connection.setReadTimeout( 30000 );
			connection.setDoOutput( true );
			connection.setDoInput( true );
			
			connection.getOutputStream().write( paramsString.getBytes() );	
			
			final int responseCode = connection.getResponseCode();
			if( responseCode == 201 )
			{	
				BufferedReader reader = new BufferedReader( new InputStreamReader( connection.getInputStream() ) );
				String temp1 = null;
				StringBuilder value = new StringBuilder();
				while( ( temp1 = reader.readLine() ) != null )
				{
					value.append( temp1 );
				}
				JSONObject object = new JSONObject( value.toString() );
				id = object.getInt( "id" );
				handler.sendEmptyMessage( 0 );
			}
			else
			{				
				handler.sendEmptyMessage( 1 );
			}
		}
		catch( Exception ex )
		{
			handler.sendEmptyMessage( 1 );
		}
		finally
		{
			if( connection != null )
			{
				connection.disconnect();
			}
		}
	}
	
	private void showMessage( String message )
	{
		Toast.makeText( this, message, Toast.LENGTH_SHORT ).show();
	}
	
	protected void onActivityResult( int requestCode, int resultCode, Intent data ) 
	{
		if( resultCode == 100 )
		{
			finish();
		}
	}
}
