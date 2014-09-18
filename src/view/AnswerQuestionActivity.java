package view;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

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

public class AnswerQuestionActivity extends Activity
{
	private EditText text1;
	private EditText text2;
	private EditText text3;
	private EditText text4;
	
	private String input1;
	private String input2;
	private String input3;
	private String input4;
	
	private EditText textEmail;
	
	private Handler handler;
	
	private Dialog dialog = null;
	
	private int id;
	private HttpURLConnection connection = null;
	
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
				case 0 : //回答成功
					showMessage( "回答成功" );
					Intent intent = new Intent();
					intent.putExtra( "input1", input1 );
					intent.putExtra( "input2", input2 );
					intent.putExtra( "input3", input3 );
					setResult( 100, intent );
					finish();
					break;
				case 1 : //回答失败
					showMessage( "回答失败" );
					break;
				case 2 : //关注成功
					showMessage( "提交成功" );
					setResult( 101 );
					finish();
					break;
				case 3 : //关注失败
					showMessage( "提交失败" );
					break;
				}
			}
		};
		
		id = getIntent().getIntExtra( "id", 0 );
		
		if( getIntent().getIntExtra( "type", 0 ) == 0 )
		{
			showAnswerView();
		}
		else
		{
			showLookView();
		}
	}
	
	private void showAnswerView()
	{
		setContentView( R.layout.answer_question );
		
		text1 = ( EditText ) findViewById( R.id.answer_question_input1 );
		text2 = ( EditText ) findViewById( R.id.answer_question_input2 );
		text3 = ( EditText ) findViewById( R.id.answer_question_input3 );
		text4 = ( EditText ) findViewById( R.id.answer_question_input4 );
		
		Button buttonAdd = ( Button ) findViewById( R.id.answer_question_button );
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
	
	private void showLookView()
	{
		setContentView( R.layout.look_question );
		
		textEmail = ( EditText ) findViewById( R.id.look_question_input );
		
		Button buttonAdd = ( Button ) findViewById( R.id.look_question_button );
		buttonAdd.setOnClickListener
		(
			new OnClickListener()
			{
				public void onClick( View view )
				{
					final String input = textEmail.getText().toString();
					if( !Tool.isEmail( input ) || input.length() > 30 )
					{
						showMessage( "邮箱格式不正确" );
					}
					else
					{
						lookQuestion( input );
					}
				}
			}
		);
	}
	
	private void lookQuestion( final String email )
	{
		if( Tool.isNetworkConnected( this ) )
		{
			dialog = new Dialog( this, R.style.my_dialog );
			LayoutInflater inflater = LayoutInflater.from( this );  
			View view = inflater.inflate( R.layout.dialog_progress_view, null );
			TextView textView = ( TextView ) view.findViewById( R.id.dialog_textview );
			textView.setText( "正在提交" );
			
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
	    				doLookQuestion( email );
	    			}
	    		}
	    	).start();
		}
		else
		{
			showMessage( "没有网络，请检查网络连接" );
		}
	}
	
	private void doLookQuestion( final String email )
	{
		final String urlString = "http://112.124.15.159:3000/questions/" + id + "/question_followers";
		try
		{		
			final String paramsString = "question_follower[question_id]=" + id
					 + "&question_follower[follower_email]=" + URLEncoder.encode( email, "utf-8" );

			URL url = new URL( urlString );
			connection = ( HttpURLConnection ) url.openConnection();
			connection.setRequestMethod( "POST" );
			connection.setConnectTimeout( 10000 );
			connection.setReadTimeout( 30000 );
			connection.setDoOutput( true );
			connection.setDoInput( true );
			
			connection.getOutputStream().write( paramsString.getBytes() );	
			
			final int responseCode = connection.getResponseCode();
			if( responseCode == 200 )
			{
				handler.sendEmptyMessage( 2 );
			}
			else
			{				
				handler.sendEmptyMessage( 3 );
			}
		}
		catch( Exception ex )
		{
			handler.sendEmptyMessage( 3 );
		}
		finally
		{
			if( connection != null )
			{
				connection.disconnect();
			}
		}
	}
	
	private void judgeInput()
	{
		input1 = text1.getText().toString();
		final int length = input1.replaceAll( " ", "" ).length();
		if( length == 0 || length > 300 )
		{
			showMessage( "回答输入不正确" );
			return;
		}
		input2 = text2.getText().toString();
		if( input2.length() == 0 || input2.length() > 30 )
		{
			showMessage( "昵称输入不正确" );
			return;
		}
		input3 = text3.getText().toString();
		if( input3.length() == 0 || input3.length() > 30 )
		{
			showMessage( "个人介绍输入不正确" );
			return;
		}
		input4 = text4.getText().toString();
		if( !input4.equals( "" ) && ( !Tool.isEmail( input4 ) || input4.length() > 30 ) )
		{
			showMessage( "邮箱格式不正确" );
			return;
		}
		answerQuestion();
	}
	
	private void answerQuestion()
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
	    				doAnswerQuestion();
	    			}
	    		}
	    	).start();
		}
		else
		{
			showMessage( "没有网络，请检查网络连接" );
		}
	}
	
	private void doAnswerQuestion()
	{
		final String urlString = "http://112.124.15.159:3000/questions/" + id + "/answers.json";
		try
		{		
			String paramsString = "answer[answer]=" + URLEncoder.encode( input1, "utf-8" ) 
					 + "&answer[answerer]=" + URLEncoder.encode( input2, "utf-8" ) 
					 + "&answer[self_introduction]=" + URLEncoder.encode( input3, "utf-8" );
			if( !input4.equals( "" ) )
			{
				paramsString += "&follower_email=" + URLEncoder.encode( input4, "utf-8" );
			}

			URL url = new URL( urlString );
			connection = ( HttpURLConnection ) url.openConnection();
			connection.setRequestMethod( "POST" );
			connection.setConnectTimeout( 10000 );
			connection.setReadTimeout( 30000 );
			connection.setDoOutput( true );
			connection.setDoInput( true );
			
			connection.getOutputStream().write( paramsString.getBytes() );	
			
			final int responseCode = connection.getResponseCode();
			if( responseCode == 200 )
			{
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
}