package view;

import model.GuideListAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
		
		init();
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
