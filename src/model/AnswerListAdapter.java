package model;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pku.museum.R;

import entity.AnswerEntity;

public class AnswerListAdapter extends BaseAdapter
{
	private Context context;
	private List< AnswerEntity > list;
	
	public AnswerListAdapter( Context context, List< AnswerEntity > list )
	{
		this.context = context;
		this.list = list;
	}
	
	public int getCount() 
	{
		return list.size();
	}

	public Object getItem( int position )
	{
		return list.get( position );
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
			convertView = LayoutInflater.from( context ).inflate( R.layout.answer_list_item, null );
			holder = new ViewHolder();
			
			holder.nameView = ( TextView ) convertView.findViewById( R.id.answer_list_item_name );
			holder.infoView = ( TextView ) convertView.findViewById( R.id.answer_list_item_info );
			holder.answerView = ( TextView ) convertView.findViewById( R.id.answer_list_item_answer );
			
			convertView.setTag( holder );
		}
		else
		{
			holder = ( ViewHolder ) convertView.getTag();
		}
		
		AnswerEntity entity = list.get( position );
		holder.nameView.setText( entity.getName() );
		if( entity.getSign().length() > 11 )
		{
			holder.infoView.setText( entity.getSign().substring( 0, 11 ) + "..." );
		}
		else
		{
			holder.infoView.setText( entity.getSign() );
		}
		holder.answerView.setText( entity.getAnswer() );
		return convertView;
	}
	
	static class ViewHolder
	{
		TextView nameView;
		TextView infoView;
		TextView answerView;
	}
}
