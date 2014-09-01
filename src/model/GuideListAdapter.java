package model;

import java.util.List;

import util.Information;
import view.ChooseTimeActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.pku.museum.R;

import entity.GuideEntity;

public class GuideListAdapter extends BaseAdapter
{
    private Context context;
    private List< GuideEntity > guideList;
    
    public GuideListAdapter( Context context, List< GuideEntity > guideList )
    {
    	this.context = context;
    	this.guideList = guideList;
    }
	
	public int getCount() 
	{
		return guideList.size();
	}

	public Object getItem( int position ) 
	{
		return guideList.get( position );
	}

	public long getItemId( int position ) 
	{
		return position;
	}

	public View getView( int position, View convertView, ViewGroup parent ) 
	{
		ViewHolder holder;
		if( convertView == null )
		{
			convertView = LayoutInflater.from( context ).inflate( R.layout.guide_list_item, null );
			
			holder = new ViewHolder();
			holder.headView = ( ImageView ) convertView.findViewById( R.id.guide_head );
			
			holder.starView1 = ( ImageView ) convertView.findViewById( R.id.guide_star1 );
			holder.starView2 = ( ImageView ) convertView.findViewById( R.id.guide_star2 );
			holder.starView3 = ( ImageView ) convertView.findViewById( R.id.guide_star3 );
			holder.starView4 = ( ImageView ) convertView.findViewById( R.id.guide_star4 );
			holder.starView5 = ( ImageView ) convertView.findViewById( R.id.guide_star5 );
			
			holder.nameText = ( TextView ) convertView.findViewById( R.id.guide_name );
			holder.jobText = ( TextView ) convertView.findViewById( R.id.guide_job );
			holder.timeText = ( TextView ) convertView.findViewById( R.id.guide_time );
			holder.commentText = ( TextView ) convertView.findViewById( R.id.guide_comment );
			holder.topicText = ( TextView ) convertView.findViewById( R.id.guide_word );

			holder.textTag1 = ( TextView ) convertView.findViewById( R.id.guide_tag1 );
			holder.textTag2 = ( TextView ) convertView.findViewById( R.id.guide_tag2 );
			holder.textTag3 = ( TextView ) convertView.findViewById( R.id.guide_tag3 );
			
			holder.button = ( Button ) convertView.findViewById( R.id.guide_choose_button );
			
			convertView.setTag( holder );
		}
		else
		{
			holder = ( ViewHolder ) convertView.getTag();
		}
		
		final int index = position;
		final GuideEntity entity = guideList.get( position );
		
		holder.headView.setImageBitmap( BitmapFactory.decodeFile( Information.RootPath + entity.getImageUrl() ) );
		
		holder.nameText.setText( entity.getName() );
		holder.jobText.setText( entity.getDescription() );
		holder.timeText.setText( entity.getTime() );
		holder.topicText.setText( entity.getQuote() );
		holder.commentText.setText( "0 个评论" );
		
		String[] describe = entity.getKeyWords();
		holder.textTag1.setText( " " + describe[ 0 ] + " " );
		holder.textTag2.setText( "" );
		holder.textTag3.setText( "" );
		
		if( describe.length > 1 )
		{
			holder.textTag2.setText( " " + describe[ 1 ] + " " );
		}
		if( describe.length > 2 )
		{
			holder.textTag3.setText( " " + describe[ 2 ] + " " );
		}
		
		holder.starView1.setImageResource( R.drawable.star_full );
		holder.starView2.setImageResource( R.drawable.star_full );
		holder.starView3.setImageResource( R.drawable.star_full );
		holder.starView4.setImageResource( R.drawable.star_full );
		holder.starView5.setImageResource( R.drawable.star_full );
		double star = 4.5;
		if( star < 4.9 )
		{
			if( star > 4.1 ) holder.starView5.setImageResource( R.drawable.star_half );
			else
			{
				holder.starView5.setImageResource( R.drawable.star_empty );
				if( star > 3.1 ) holder.starView4.setImageResource( R.drawable.star_half );
				else
				{
					holder.starView4.setImageResource( R.drawable.star_empty );
					if( star > 2.1 ) holder.starView3.setImageResource( R.drawable.star_half );
					else
					{
						holder.starView3.setImageResource( R.drawable.star_empty );
						if( star > 1.1 ) holder.starView2.setImageResource( R.drawable.star_half );
						else
						{
							holder.starView2.setImageResource( R.drawable.star_empty );
							if( star > 0.1 ) holder.starView1.setImageResource( R.drawable.star_half );
							else holder.starView1.setImageResource( R.drawable.star_empty );
						}
					}
				}
			}
		}
		
		holder.button.setOnClickListener
		(
			new OnClickListener()
			{
				public void onClick( View view )
				{
					Intent intent = new Intent( context, ChooseTimeActivity.class );
					intent.putExtra( "guideIndex", index );
					context.startActivity( intent );
				}
			}
		);
		
		return convertView;
	}
	
	static class ViewHolder
	{
		ImageView headView;
		ImageView starView1;
		ImageView starView2;
		ImageView starView3;
		ImageView starView4;
		ImageView starView5;
		TextView nameText;
		TextView jobText;
		TextView topicText;
		TextView timeText;
		TextView commentText;
		TextView textTag1;
		TextView textTag2;
		TextView textTag3;
		Button button;
	}
}
