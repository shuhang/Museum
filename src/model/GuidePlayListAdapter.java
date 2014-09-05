package model;

import java.util.List;

import util.Information;
import util.Tool;
import view.GuideActivity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.pku.museum.R;

import entity.ProEntity;

public class GuidePlayListAdapter extends BaseAdapter
{
    private Context context;
    private List< ProEntity > proList;

    public GuidePlayListAdapter( Context context, List< ProEntity > proList )
    {
    	this.context = context;
    	this.proList = proList;
    }

	public int getCount() 
	{
		return proList.size();
	}

	public Object getItem( int position ) 
	{
		return proList.get( position );
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
			convertView = LayoutInflater.from( context ).inflate( R.layout.guide_play_pro_list_item, null );
			
			holder = new ViewHolder();
			holder.headView = ( ImageView ) convertView.findViewById( R.id.guide_play_pro_list_item_head );
			
			holder.nameText = ( TextView ) convertView.findViewById( R.id.guide_play_pro_list_item_name );
			holder.jobText = ( TextView ) convertView.findViewById( R.id.guide_play_pro_list_item_job );
			holder.timeText = ( TextView ) convertView.findViewById( R.id.guide_play_pro_list_item_time );
			holder.titleText = ( TextView ) convertView.findViewById( R.id.guide_play_pro_list_item_title );
			holder.praiseText = ( TextView ) convertView.findViewById( R.id.guide_play_pro_list_item_praise_count );
			
			holder.playButton = ( ImageButton ) convertView.findViewById( R.id.guide_play_pro_list_item_play_button );
			holder.praiseButton = ( ImageButton ) convertView.findViewById( R.id.guide_play_pro_list_item_praise_button );

			holder.waitButton = ( Button ) convertView.findViewById( R.id.guide_play_pro_list_item_wait_button );
			
			holder.line = ( View ) convertView.findViewById( R.id.guide_play_pro_list_item_line );
			
			convertView.setTag( holder );
		}
		else
		{
			holder = ( ViewHolder ) convertView.getTag();
		}
		
		if( position == 0 )
		{
			holder.line.setVisibility( View.INVISIBLE );
		}
		else
		{
			holder.line.setVisibility( View.VISIBLE );
		}
		
		final int index = position;
		final ProEntity entity = proList.get( position );
		int width = ( int ) ( 68 * Information.ScreenDensity );
		holder.headView.setImageBitmap( Tool.resizeImageToSmall( Information.RootPath + entity.getImageUrl(), width, width ) );
		holder.nameText.setText( entity.getExpertName() );
		holder.jobText.setText( entity.getExpertTitle() );
		holder.timeText.setText( entity.getTime() );
		holder.titleText.setText( entity.getName() );
		holder.praiseText.setText( "0" );
		
		holder.praiseButton.setImageResource( R.drawable.praise_green );
		
		holder.playButton.setImageResource( R.drawable.pro_play );
		if( GuideActivity.isProPlaying == true && GuideActivity.proPlayIndex == index )
		{
			holder.playButton.setImageResource( R.drawable.pro_stop );
		}
		holder.playButton.setOnClickListener
		(
			new OnClickListener()
			{
				public void onClick( View view )
				{
					if( index == GuideActivity.proPlayIndex )
					{
						if( GuideActivity.isProPlaying == true )
						{
							GuideActivity.isProPlaying = false;
							GuideActivity.handler.sendEmptyMessage( 3 );
						}
						else
						{
							GuideActivity.isProPlaying = true;
							
							Message message = GuideActivity.handler.obtainMessage();
							message.what = 1;
							message.arg1 = index;
							GuideActivity.handler.sendMessage( message );
						}
					}
					else
					{
						GuideActivity.isProPlaying = true;
						
						Message message = GuideActivity.handler.obtainMessage();
						message.what = 1;
						message.arg1 = index;
						GuideActivity.handler.sendMessage( message );
					}
				}
			}
		);
		
		SeekBar seekBar = ( SeekBar ) convertView.findViewById( R.id.guide_play_pro_list_item_seekbar );
		seekBar.setProgressDrawable( context.getResources().getDrawable( R.drawable.pro_seekbar_gray ) );
		seekBar.setThumb( context.getResources().getDrawable( R.drawable.gray_progress ) );
		if( index != GuideActivity.proPlayIndex )
		{
			seekBar.setProgress( 0 );
		}
		MySeekBar.getInstance().seekBarMap.put( index, seekBar );
		
		if( GuideActivity.proPlayIndex == index )
		{
			holder.waitButton.setVisibility( View.INVISIBLE );
		}
		else
		{
			holder.waitButton.setVisibility( View.VISIBLE );
			
			if( GuideActivity.proPlayMap.get( index ) == true )
			{
				holder.waitButton.setBackgroundResource( R.drawable.round_button_orange );
				holder.waitButton.setText( "等待播放" );
				Drawable temp = context.getResources().getDrawable( R.drawable.pro_wait );
				temp.setBounds( 0, 0, ( int )( 20 * Information.ScreenDensity ), ( int )( 20 * Information.ScreenDensity ) );
				holder.waitButton.setCompoundDrawables( temp, null, null, null );
			}
			else
			{
				holder.waitButton.setBackgroundResource( R.drawable.round_button_gray );
				holder.waitButton.setText( "添加播放" );
				Drawable temp = context.getResources().getDrawable( R.drawable.pro_add );
				temp.setBounds( 0, 0, ( int )( 20 * Information.ScreenDensity ), ( int )( 20 * Information.ScreenDensity ) );
				holder.waitButton.setCompoundDrawables( temp, null, null, null );
			}
			
			holder.waitButton.setOnClickListener
			(
				new OnClickListener()
				{
					public void onClick( View view )
					{
						Button button = ( Button ) view;
						if( GuideActivity.proPlayMap.get( index ) == true )
						{
							GuideActivity.proPlayMap.put( index, false );
							button.setBackgroundResource( R.drawable.round_button_gray );
							button.setText( "添加播放" );
							Drawable temp = context.getResources().getDrawable( R.drawable.pro_add );
							temp.setBounds( 0, 0, ( int )( 20 * Information.ScreenDensity ), ( int )( 20 * Information.ScreenDensity ) );
							button.setCompoundDrawables( temp, null, null, null );
						}
						else
						{
							GuideActivity.proPlayMap.put( index, true );
							button.setBackgroundResource( R.drawable.round_button_orange );
							button.setText( "等待播放" );
							Drawable temp = context.getResources().getDrawable( R.drawable.pro_wait );
							temp.setBounds( 0, 0, ( int )( 20 * Information.ScreenDensity ), ( int )( 20 * Information.ScreenDensity ) );
							button.setCompoundDrawables( temp, null, null, null );
						}
						GuideActivity.handler.sendEmptyMessage( 2 );
					}
				}
			);
		}
		
		return convertView;
	}
	
	static class ViewHolder
	{
		ImageView headView;
		TextView nameText;
		TextView jobText;
		TextView titleText;
		TextView timeText;
		TextView praiseText;
		ImageButton praiseButton;
		ImageButton playButton;
		Button waitButton;
		View line;
	}
}
