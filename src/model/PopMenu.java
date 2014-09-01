package model;

import java.util.ArrayList;
import java.util.List;

import util.Information;
import view.GuideActivity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pku.museum.R;

public class PopMenu 
{
	private ArrayList< String > itemList;
	private Context context;
	private PopupWindow popupWindow ;
	private ListView listView;
	private TextView widthText;
	/**
	 *  0 : place
	 *  1 : guide
	 */
	private int symbol;
	@SuppressWarnings("deprecation")
	public PopMenu( Context context, int symbol ) 
	{
		this.context = context;
		this.symbol = symbol;
		
		itemList = new ArrayList< String >( 5 );
		View view = LayoutInflater.from( context ).inflate( R.layout.pop_menu, null );
		
		widthText = ( TextView ) view.findViewById( R.id.pop_menu_width );
        
        listView = ( ListView ) view.findViewById( R.id.popmenu_list );
        listView.setAdapter( new PopAdapter() );
        listView.setFocusableInTouchMode( true );
        listView.setFocusable( true );
        
        int width = ( int ) ( Information.ScreenWidth * 0.6 );
        popupWindow = new PopupWindow( view, width, ( int )( width * 1.3 ) );
        //popupWindow = new PopupWindow( view, context.getResources().getDimensionPixelSize( R.dimen.popmenu_width ), LayoutParams.WRAP_CONTENT );
        // �����Ϊ�˵��������Back��Ҳ��ʹ����ʧ�����Ҳ�����Ӱ����ı�����������ģ�
        popupWindow.setBackgroundDrawable( new BitmapDrawable() );
	}
	public void setOnItemClickListener( OnItemClickListener listener ) 
	{
		listView.setOnItemClickListener( listener );
	}
	//����б��ȡ����б���Ա�����б���
	public void addItems( List< String > items ) 
	{
		String maxString = "";
		for( String s : items )
		{
			itemList.add( s );
			if( s.length() > maxString.length() )
			{
				maxString = new String( s );
			}
		}
		widthText.setText( maxString + "�溽" );
	}
	//������Ӳ˵���
	public void addItem( String item )
	{
		itemList.add( item );
	}
	//����ʽ ���� pop�˵� parent ���½�
	public void showAsDropDown( View parent ) 
	{
		popupWindow.showAsDropDown( parent, 0, context.getResources().getDimensionPixelSize( R.dimen.popmenu_yoff ) );
		// ʹ��ۼ�
        popupWindow.setFocusable( true );
        // ����������������ʧ
        popupWindow.setOutsideTouchable( true );
        //ˢ��״̬
        popupWindow.update();
	}
	//���ز˵�
	public void dismiss() 
	{
		popupWindow.dismiss();
	}
	// ������
	private class PopAdapter extends BaseAdapter
	{
		public int getCount() 
		{
			return itemList.size();
		} 
		
		public Object getItem( int position )
		{
			return itemList.get( position );
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
				convertView = LayoutInflater.from( context ).inflate( R.layout.popmenu_item, null );
				
				holder = new ViewHolder();
				holder.groupItem = ( TextView ) convertView.findViewById( R.id.popmenu_item_text );
				holder.layout = ( RelativeLayout ) convertView.findViewById( R.id.popmenu_layout );
	
				convertView.setTag( holder );
			} 
			else 
			{
				holder = ( ViewHolder ) convertView.getTag();
			}
			holder.groupItem.setText( itemList.get( position ) );
			
			if( ( symbol == 0 && position == GuideActivity.placeIndex ) || ( symbol == 1 && position == GuideActivity.guideIndex ) )
			{
				holder.layout.setBackgroundResource( R.drawable.bg_pop_pressed );
			}
			else
			{
				holder.layout.setBackgroundColor( Color.TRANSPARENT );
			}

			return convertView;
		}

		private final class ViewHolder 
		{
			TextView groupItem;
			RelativeLayout layout;
		}
	}
}