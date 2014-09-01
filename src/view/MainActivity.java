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
import android.view.View;
import android.view.WindowManager;
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
	
	@SuppressWarnings("deprecation")
	protected void onCreate( Bundle savedInstanceState ) 
	{
		super.onCreate( savedInstanceState );
		
		WindowManager wm = this.getWindowManager();
		Information.ScreenWidth = wm.getDefaultDisplay().getWidth();
		Information.ScreenHeight = wm.getDefaultDisplay().getHeight();
		
//		Resources r = this.getResources();
//		float px = TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, 14, r.getDisplayMetrics() );
//		System.out.println( "AAAAAAAAAAAAAAAAAAA:" + px );
//		File file = new File( "" );
//		
		
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
