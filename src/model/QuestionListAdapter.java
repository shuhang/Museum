package model;

import java.util.List;

import com.pku.museum.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import entity.QuestionEntity;

public class QuestionListAdapter extends BaseAdapter
{
	private Context context;
	private List< QuestionEntity > list;
	
	public QuestionListAdapter( Context context, List< QuestionEntity > list )
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
			convertView = LayoutInflater.from( context ).inflate( R.layout.question_list_item, null );
			holder = new ViewHolder();
			
			holder.titleView = ( TextView ) convertView.findViewById( R.id.question_list_item_title );
			holder.voiceView = ( TextView ) convertView.findViewById( R.id.question_list_item_text1 );
			holder.answerView = ( TextView ) convertView.findViewById( R.id.question_list_item_text2 );
			holder.lookView = ( TextView ) convertView.findViewById( R.id.question_list_item_text3 );
			
			convertView.setTag( holder );
		}
		else
		{
			holder = ( ViewHolder ) convertView.getTag();
		}
		
		QuestionEntity entity = list.get( position );
		holder.titleView.setText( entity.getTitle() );
		holder.voiceView.setText( entity.getVoiceCount() + "段语音" );
		holder.answerView.setText( entity.getAnswerCount() + "个回答" );
		holder.lookView.setText( entity.getLookCount() + "人关注" );
		return convertView;
	}
	
	static class ViewHolder
	{
		TextView titleView;
		TextView voiceView;
		TextView answerView;
		TextView lookView;
	}
}
