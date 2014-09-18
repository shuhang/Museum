package view;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import model.QuestionListAdapter;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pku.museum.R;

import entity.QuestionEntity;

public class QuestionActivity extends Activity
{
	private Handler handler;
	
	private Dialog dialog;
	private HttpURLConnection connection = null;
	
	private ListView questionListView;
	private QuestionListAdapter adapter;
	private List< QuestionEntity > questionList = new ArrayList< QuestionEntity >();
	
	private EditText input;
	
	private int nowIndex;
	
	private boolean connectionSymbol;
	
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
				case 0 : //搜索成功
					adapter = new QuestionListAdapter( QuestionActivity.this, questionList );
					questionListView.setAdapter( adapter );
					break;
				case 1 : //搜索失败
					showMessage( "搜索失败" );
					break;
				case 2 :
					adapter.notifyDataSetChanged();
					break;
				case 3 :
					break;
				}
			}
		};
		
		init();
	}
	
	private void init()
	{
		setContentView( R.layout.question_list );
		questionListView = ( ListView ) findViewById( R.id.question_list );
		adapter = new QuestionListAdapter( this, questionList );
		questionListView.setAdapter( adapter );
		
		questionListView.setOnItemClickListener
		(
			new OnItemClickListener()
			{
				public void onItemClick( AdapterView<?> parent, View view, int position, long id ) 
				{
					nowIndex = position;
					
					Bundle bundle = new Bundle();
					bundle.putSerializable( "question", questionList.get( position ) );
					Intent intent = new Intent( QuestionActivity.this, QuestionInfoActivity.class );
					intent.putExtras( bundle );
					startActivityForResult( intent, 0 );
				}
			}
		);
		
		input = ( EditText ) findViewById( R.id.question_list_input );
		ImageButton buttonSearch = ( ImageButton ) findViewById( R.id.question_list_search_button );
		buttonSearch.setOnClickListener
		(
			new OnClickListener()
			{
				public void onClick( View view )
				{
					String keyWord = input.getText().toString();
					if( keyWord.replaceAll( " ", "" ).length() == 0 )
					{
						showMessage( "输入不能为空" );
					}
					else
					{
						searchQuestion( keyWord );
					}
				}
			}
		);
		
		Button buttonAdd = ( Button ) findViewById( R.id.question_list_add_button );
		buttonAdd.setOnClickListener
		(
			new OnClickListener()
			{
				public void onClick( View view )
				{
					addQuestion();
				}
			}
		);
	}
	
	private void doSearchQuestion( final String keyWord )
	{
		connectionSymbol = true;
		try
		{
			String urlString = Information.Server_Url + "/questions/query/" + URLEncoder.encode( keyWord, "utf-8" ) + ".json";		
			URL url = new URL( urlString );
			connection = ( HttpURLConnection ) url.openConnection();  
			connection.setRequestProperty( "Content-Type", "application/json" );
			connection.setRequestMethod( "GET" );
			connection.setConnectTimeout( 10000 );
			connection.setReadTimeout( 30000 );
			connection.connect();

			int responseCode = connection.getResponseCode();
			if( responseCode == 200 )
			{
				BufferedReader reader = new BufferedReader( new InputStreamReader( connection.getInputStream() ) );
				String temp1 = null;
				StringBuilder value = new StringBuilder();
				while( ( temp1 = reader.readLine() ) != null )
				{
					value.append( temp1 );
				}
				if( connectionSymbol == true )
				{
					questionList = Tool.parseQuestion( value.toString() );
					handler.sendEmptyMessage( 0 );
				}
				else
				{
					handler.sendEmptyMessage( 3 );
				}
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
	
	private void searchQuestion( final String keyWord )
	{
		if( Tool.isNetworkConnected( this ) )
		{
			dialog = new Dialog( this, R.style.my_dialog );
			LayoutInflater inflater = LayoutInflater.from( this );  
			View view = inflater.inflate( R.layout.dialog_progress_view, null );
			TextView textView = ( TextView ) view.findViewById( R.id.dialog_textview );
			textView.setText( "正在搜索" );
			
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
							connectionSymbol = false;
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
	    				doSearchQuestion( keyWord );
	    			}
	    		}
	    	).start();
		}
		else
		{
			showMessage( "没有网络，请检查网络连接" );
		}
	}
	
	private void addQuestion()
	{
		Intent intent = new Intent( this, AddQuestionActivity.class );
		startActivity( intent );
	}
	
	private void showMessage( String message )
	{
		Toast.makeText( this, message, Toast.LENGTH_SHORT ).show();
	}
	
	protected void onActivityResult( int requestCode, int resultCode, Intent data ) 
	{
		if( resultCode == 100 )
		{
			QuestionEntity entity = questionList.get( nowIndex );
			entity.setAnswerCount( Information.AnswerCount );
			entity.setLookCount( Information.LookCount );
			handler.sendEmptyMessage( 2 );
		}
	}
}
