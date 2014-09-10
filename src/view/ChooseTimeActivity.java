package view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.pku.museum.R;

public class ChooseTimeActivity extends Activity
{
	private int guideIndex;
	private int guideMode;
	private boolean isChoose;

	protected void onCreate( Bundle savedInstanceState ) 
	{
		super.onCreate( savedInstanceState );
		requestWindowFeature( Window.FEATURE_NO_TITLE );
		
		guideIndex = getIntent().getIntExtra( "guideIndex", 0 );
		
		showChooseTimeView();
	}
	
	@SuppressLint("UseSparseArrays")
	private void showChooseTimeView()
	{
		isChoose = true;
		setContentView( R.layout.choose_time );

		Button button1 = ( Button ) findViewById( R.id.choose_time_button1 );
		button1.setOnClickListener
		(
			new OnClickListener()
			{
				public void onClick( View view )
				{
					guideMode = 0;
					showStartView();
				}
			}
		);
		button1.setText( "  · 我听导游讲就行，不需要专家了" );
		
		Button button2 = ( Button ) findViewById( R.id.choose_time_button2 );
		button2.setOnClickListener
		(
			new OnClickListener()
			{
				public void onClick( View view )
				{
					guideMode = 1;
					showStartView();
				}
			}
		);
		String value = "推荐·挺有特色的，插播15分钟专家讲解吧";
		Spannable span = new SpannableString( value );
		span.setSpan( new AbsoluteSizeSpan( 10 ), 0, 2, Spannable.SPAN_INCLUSIVE_INCLUSIVE );
		span.setSpan( new AbsoluteSizeSpan( 15 ), 2, value.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE );
		button2.setText( span );
		
		Button button3 = ( Button ) findViewById( R.id.choose_time_button3 );
		button3.setOnClickListener
		(
			new OnClickListener()
			{
				public void onClick( View view )
				{
					guideMode = 2;
					showStartView();
				}
			}
		);
		button3.setText( "  · 非常感兴趣，插播30分钟专家讲解" );
	}
	
	private void showStartView()
	{
		isChoose = false;
		setContentView( R.layout.start_guide );
		
		Button button = ( Button ) findViewById( R.id.start_guide_button );
		button.setOnClickListener
		(
			new OnClickListener()
			{
				public void onClick( View view )
				{
					Intent intent = new Intent( ChooseTimeActivity.this, GuideActivity.class );
					intent.putExtra( "guideIndex", guideIndex );
					intent.putExtra( "guideMode", guideMode );
					startActivity( intent );
				}
			}
		);
	}
	
	public boolean onKeyDown( int keyCode, KeyEvent event )
	{
		if( keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 )
		{
			if( isChoose == true )
			{
				finish();
			}
			else
			{
				showChooseTimeView();
			}
			return true;
		}
		return false;
	}
}