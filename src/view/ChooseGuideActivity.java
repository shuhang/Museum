package view;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import model.GuideListAdapter;
import util.Information;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;

import com.pku.museum.R;

import entity.MuseumEntity;

public class ChooseGuideActivity extends Activity
{
	private ListView guideListView;
	private GuideListAdapter guideListAdapter;

	protected void onCreate( Bundle savedInstanceState ) 
	{
		super.onCreate( savedInstanceState );
		
		DisplayMetrics dm = getResources().getDisplayMetrics();
		Information.ScreenDensity = dm.density;
		Information.ScreenWidth = dm.widthPixels;
		Information.ScreenHeight = dm.heightPixels;
		
		parseJson();
		
		init();
	}
	
	private void parseJson()
    {
    	try
    	{
    		InputStream stream = getAssets().open( "json.txt" );
    		StringBuilder builder = new StringBuilder( "" );
    		String line = null;
    		BufferedReader reader = new BufferedReader( new InputStreamReader( stream ) );
    		while( ( line = reader.readLine() ) != null )
    		{
    			builder.append( line + "\n" );
    		}
    		MuseumEntity.ParseJson( builder.toString() );
    	}
    	catch( Exception ex ) {}
    }
	
	private void init()
	{
		requestWindowFeature( Window.FEATURE_NO_TITLE );
		setContentView( R.layout.choose_guide );

		guideListView = ( ListView ) findViewById( R.id.guide_list );
		
		View headerView = getLayoutInflater().inflate( R.layout.choose_guide_list_header, null );
		guideListView.addHeaderView( headerView );
		
		View footerView = getLayoutInflater().inflate( R.layout.choose_guide_list_footer, null );
		guideListView.addFooterView( footerView );
		Button button = ( Button ) findViewById( R.id.choose_guide_list_footer_button );
		button.setOnClickListener
		(
			new OnClickListener()
			{
				public void onClick( View view )
				{
					Intent intent = new Intent( ChooseGuideActivity.this, GuideActivity.class );
					intent.putExtra( "guideIndex", MuseumEntity.GuideList.size() );
					intent.putExtra( "guideMode", -1 );
					startActivity( intent );
				}
			}
		);
		
		guideListAdapter = new GuideListAdapter( this, MuseumEntity.GuideList );
		guideListView.setAdapter( guideListAdapter );
	}
}
