package view;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import model.MuseumListAdapter;
import util.Information;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.pku.museum.R;

import entity.MuseumEntity;

public class MainActivity extends Activity 
{
	private ListView museumListView;
	private MuseumListAdapter museumListAdapter;
	private List< MuseumEntity > museumList = new ArrayList< MuseumEntity >();
	
	protected void onCreate( Bundle savedInstanceState ) 
	{
		super.onCreate( savedInstanceState );
		
		DisplayMetrics dm = getResources().getDisplayMetrics();
		Information.ScreenDensity = dm.density;
		Information.ScreenWidth = dm.widthPixels;
		Information.ScreenHeight = dm.heightPixels;
//		Resources r = this.getResources();
//		float px = TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, 14, r.getDisplayMetrics() );
//		System.out.println( "AAAAAAAAAAAAAAAAAAA:" + px );
		
		init();
	}
	
	private void init()
	{
		setContentView( R.layout.activity_main );
		
		parseJson();

		MuseumEntity entity = new MuseumEntity();
		museumList.add( entity );
		
		museumListAdapter = new MuseumListAdapter( this, museumList );
		
		museumListView = ( ListView ) findViewById( R.id.museum_list );
		museumListView.setAdapter( museumListAdapter );
		
		museumListView.setOnItemClickListener
		(
			new OnItemClickListener()
			{
				public void onItemClick( AdapterView<?> parent, View view, int position, long id ) 
				{
					Intent intent = new Intent( MainActivity.this, ChooseGuideActivity.class );
					startActivity( intent );
				}
			}
		);
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
}
