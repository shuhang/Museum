package model;

import java.util.List;

import util.Information;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pku.museum.R;

import entity.MuseumEntity;

public class MuseumListAdapter extends BaseAdapter
{
    private Context context;
    private List< MuseumEntity > museumList;

    public MuseumListAdapter( Context context, List< MuseumEntity > museumList )
    {
    	this.context = context;
    	this.museumList = museumList;
    }

	public int getCount() 
	{
		return museumList.size();
	}

	public Object getItem( int position ) 
	{
		return museumList.get( position );
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
			convertView = LayoutInflater.from( context ).inflate( R.layout.museum_list_item, null );
			
			holder = new ViewHolder();
			holder.imageView = ( ImageView ) convertView.findViewById( R.id.meseum_list_item_cover );
			holder.nameText = ( TextView ) convertView.findViewById( R.id.museum_list_item_name );
			holder.infoText = ( TextView ) convertView.findViewById( R.id.museum_list_item_info );
			
			convertView.setTag( holder );
		}
		else
		{
			holder = ( ViewHolder ) convertView.getTag();
		}

		holder.imageView.setImageBitmap( BitmapFactory.decodeFile( Information.RootPath + MuseumEntity.ImageUrl ) );
		holder.nameText.setText( MuseumEntity.Name );
		holder.infoText.setText( MuseumEntity.GuideInfo );

		return convertView;
	}
	
	static class ViewHolder
	{
		ImageView imageView;
		TextView nameText;
		TextView infoText;
	}
}
