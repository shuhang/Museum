package view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.pku.museum.R;

public class ChooseTimeActivity extends Activity
{
	private RelativeLayout layout1;
	private RelativeLayout layout2;
	private RelativeLayout layout3;
	private RelativeLayout layout4;
	private RelativeLayout layout5;
	
	private int timeIndex = 2;
	private SparseIntArray map = new SparseIntArray();
	private int guideIndex;
	private boolean isChoose;
	
	private OnClickListener listener;
	
	protected void onCreate( Bundle savedInstanceState ) 
	{
		super.onCreate( savedInstanceState );
		requestWindowFeature( Window.FEATURE_NO_TITLE );
		
		guideIndex = getIntent().getIntExtra( "guideIndex", 0 );
		
		listener = new OnClickListener()
		{
			public void onClick( View v ) 
			{
				RelativeLayout layout = ( RelativeLayout ) v;
				timeIndex = map.get( layout.getId() );
				updateView();
			}
		};
		
		showChooseTimeView();
	}
	
	@SuppressLint("UseSparseArrays")
	private void showChooseTimeView()
	{
		timeIndex = 2;
		isChoose = true;
		setContentView( R.layout.choose_time );
		
		layout1 = ( RelativeLayout ) findViewById( R.id.choose_time_layout1 );
		layout2 = ( RelativeLayout ) findViewById( R.id.choose_time_layout2 );
		layout3 = ( RelativeLayout ) findViewById( R.id.choose_time_layout3 );
		layout4 = ( RelativeLayout ) findViewById( R.id.choose_time_layout4 );
		layout5 = ( RelativeLayout ) findViewById( R.id.choose_time_layout5 );
		map.put( layout1.getId(), 0 );
		map.put( layout2.getId(), 1 );
		map.put( layout3.getId(), 2 );
		map.put( layout4.getId(), 3 );
		map.put( layout5.getId(), 4 );
		layout1.setOnClickListener( listener );
		layout2.setOnClickListener( listener );
		layout3.setOnClickListener( listener );
		layout4.setOnClickListener( listener );
		layout5.setOnClickListener( listener );
		
		updateView();
		
		Button button = ( Button ) findViewById( R.id.choose_time_button );
		button.setOnClickListener
		(
			new OnClickListener()
			{
				public void onClick( View view )
				{
					showStartView();
				}
			}
		);
	}
	
	private void updateView()
	{
		updateLayout( layout1 );
		updateLayout( layout2 );
		updateLayout( layout3 );
		updateLayout( layout4 );
		updateLayout( layout5 );
	}
	
	private void updateLayout( RelativeLayout layout )
	{
		if( map.get( layout.getId() ) == timeIndex )
		{
			layout.setBackgroundColor( Color.MAGENTA );
		}
		else
		{
			layout.setBackgroundColor( Color.CYAN );
		}
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