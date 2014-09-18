package view;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.pku.museum.R;

public class AnswerInfoActivity extends Activity
{
	private String value1;
	private String value2;
	private String value3;
	private String value4;

	protected void onCreate( Bundle savedInstanceState ) 
	{
		super.onCreate( savedInstanceState );
		
		Bundle bundle = getIntent().getExtras();
		value1 = bundle.getString( "name" );
		value2 = bundle.getString( "info" );
		value3 = bundle.getString( "content" );
		value4 = bundle.getString( "title" );
		if( value2.length() > 11 )
		{
			value2 = value2.substring( 0, 11 ) + "...";
		}
		if( value4.length() > 12 )
		{
			value4 = value4.substring( 0, 12 ) + "...";
		}
		
		init();
	}
	
	private void init()
	{
		setContentView( R.layout.answer_info );
		
		TextView text1 = ( TextView ) findViewById( R.id.answer_info_text1 );
		text1.setText( value1 );
		TextView text2 = ( TextView ) findViewById( R.id.answer_info_text2 );
		text2.setText( value2 );
		TextView text3 = ( TextView ) findViewById( R.id.answer_info_content );
		text3.setText( value3 );
		TextView text4 = ( TextView ) findViewById( R.id.answer_info_title );
		text4.setText( value4 );
	}
}
