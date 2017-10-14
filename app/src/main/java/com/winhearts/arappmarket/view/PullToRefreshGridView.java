package com.winhearts.arappmarket.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.winhearts.arappmarket.utils.adapter.PullToRefreshAdapter;

/**
 * 到底加载GridView
 * @author wangmn
 * 
 */
public class PullToRefreshGridView extends GridView {
	public static final String TAG=PullToRefreshGridView.class.getName();
	private int pageSize = 9;// 每页的item个数
	private int totolCount = 0;// 总的item个数
	private int pageNum=0;
	private int pageRows=1;
	private int oldCount=0;
	private ListAdapter pullAdapter;
	private OnKeyEndListener onKeyEndListener;
	private long lastDownTimeMillis=0;
	private long lastUpTimeMillis=0;
	private PullStatus status=PullStatus.NONE;
	private TextView pageIndicator;
	private View footerView;
	
	private String firstType;
	private String secondType;
	private String orderStr;
	
	private Handler handler;
	//拉取状态
	enum PullStatus
	{
		NONE,PULLING,COMPLETE
	}
	
	enum Direction
	{
		UP,DOWN
	}

	public interface OnKeyEndListener
	{
		void onKeyUpEnd();
		void onKeyRightEnd();
		void onKeyLeftEnd();
		void onRefresh(String code, String secondType, String orderStr);
	}
	
	public PullToRefreshGridView(Context context) {
		super(context);
		handler=new Handler();
	}

	public PullToRefreshGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		handler=new Handler();
	}

	public PullToRefreshGridView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		handler=new Handler();
	}
	
	public void setOnKeyEndListener(OnKeyEndListener onKeyEndListener)
	{
		this.onKeyEndListener=onKeyEndListener;
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		if(adapter==null||!(adapter instanceof PullToRefreshAdapter))
		{
			 throw new IllegalArgumentException("adapter must be PullToRefreshAdapter");
		}
		super.setAdapter(adapter);
		PullToRefreshAdapter pullToRefreshAdapter=(PullToRefreshAdapter)adapter;
		pullToRefreshAdapter.setGridView(this);
		pullAdapter=getAdapter();
		status=PullStatus.NONE;
		changeIndictorView();
	}
	
	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
		if(pageSize!=0)
		{
			pageNum=totolCount/pageSize+(totolCount%pageSize==0?0:1);
		}
	}

	public int getTotolCount() {
		return totolCount;
	}

	public void setTotolCount(int totolCount) {
		this.totolCount = totolCount;
		if(totolCount!=0)
		{
			pageNum=totolCount/pageSize+(totolCount%pageSize==0?0:1);
		}
	}

	public void setPageIndicator(TextView pageIndicator) {
		this.pageIndicator = pageIndicator;
	}

	public void setFooterView(View footerView) {
		this.footerView = footerView;
	}
	
	private void changeIndictorView()
	{
		int pageindex=getCurrentPageIndex();
		if(footerView!=null)
		{
			if(totolCount==0)
			{
				footerView.setVisibility(INVISIBLE);
			}else {
				if(pullAdapter.getCount()<totolCount||pageindex<pageNum)
				{
					footerView.setVisibility(VISIBLE);
				}else {
					footerView.setVisibility(INVISIBLE);
				}
			}
		}
		
		if(pageIndicator!=null)
		{
			if(totolCount==0)
			{
				pageIndicator.setVisibility(INVISIBLE);
			}else {
				pageIndicator.setVisibility(VISIBLE);
				pageIndicator.setText(pageindex+"/"+pageNum);
			}
		}
	}
	
	private int getCurrentPageIndex()
	{
		int pos=getSelectedItemPosition()+1;
		int pageindex=pos/pageSize+(pos%pageSize==0?0:1);
		return pageindex==0?1:pageindex;
	}

	public synchronized void onRefreshComplete()
	{
		Log.d(TAG,"onRefreshComplete status:"+status);
		//改变焦点
		if(pullAdapter.getCount()==oldCount||oldCount==0)
		{
			changeIndictorView();
			return;
		}else if(status==PullStatus.PULLING)
		{
			status=PullStatus.COMPLETE;
			/*handler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
*/					scrollPage(Direction.DOWN);
				/*}
			}, 100);
			*/
			return;
		}
		changeIndictorView();
	}
	
	@SuppressLint("NewApi")
	private void scrollPage(Direction direction)
	{
		int currentPos=getSelectedItemPosition();
		int colNum=getColums();
		int currentRow=(currentPos+1)/colNum+((currentPos+1)%colNum==0?0:1);
		if(direction==Direction.DOWN)
		{//向下翻页
			int position=currentRow*colNum;
			Log.d(TAG, "scroll to position:"+position+",currPos:"+currentPos);
			
			int selectPosition=currentPos+colNum;
			if(selectPosition>=pullAdapter.getCount())
			{
				selectPosition=pullAdapter.getCount()-1;
				
			}
			setSelection(selectPosition);
			smoothScrollToPositionFromTop(position, 0,400);
			Log.d(TAG,"scroll call finish");
		}else {//向上翻页
			int position=(currentRow-1)*colNum-pageSize;
			Log.d(TAG, "scroll to position:"+position+",currPos:"+currentPos);
			setSelection(currentPos-colNum);
			smoothScrollToPositionFromTop(position, 0,400);
			Log.d(TAG,"scroll call finish");
		}
		changeIndictorView();
	}
	
	@SuppressLint("NewApi")
	public int getColums()
	{
		return getNumColumns();
	}
	
	public int calculateRows()
	{
		int colums=getColums();
		if(colums!=0)
			pageRows=pageSize/colums+(pageSize%colums==0?0:1);
		return pageRows;
	}
	
	private boolean isFirstLine()
	{
		int currentPos=getSelectedItemPosition();
		int colNum=getColums();
		int pagerows=calculateRows();
		int currentRow=(currentPos+1)/colNum+((currentPos+1)%colNum==0?0:1);
		Log.d(TAG,"currentRow:"+currentRow+",currentPos:"+currentPos+",pageRows:"+pagerows+",colNum:"+colNum);
		return currentRow % pagerows == 1;
	}
	
	private boolean isFirstPage()
	{
		int currentPos=getSelectedItemPosition();
		int colNum=getColums();
		Log.d(TAG, "currentPos:"+currentPos+",colNum:"+colNum);
		return currentPos < colNum;
	}
	
	/**
	 * 是否是当前页的最后一行
	 * @return
	 */
	private boolean isLastLine()
	{
		int currentPos=getSelectedItemPosition();
		int colNum=getColums();
		int count=getCount();
		int pagerows=calculateRows();
		int currentRow=(currentPos+1)/colNum+((currentPos+1)%colNum==0?0:1);
		int lastLine=count/colNum+(count%colNum==0?0:1);
		Log.d(TAG, "currentPos:"+currentPos+",colNum:"+colNum+",count:"+count+",pageRows:"+pagerows+",currentRow:"+currentRow+",lastLine:"+lastLine);
		return currentRow % pagerows == 0 || currentRow == lastLine;
	}
	
	private boolean isKeyRightEnd()
	{
		int currentPos=getSelectedItemPosition();
		int colNum=getColums();
		Log.d(TAG,"currentPos:"+currentPos+",colNum:"+colNum);
		return (currentPos + 1) % colNum == 0;
	}
	
	private boolean isKeyLeftEnd()
	{
		int currentPos=getSelectedItemPosition();
		int colNum=getColums();
		Log.d(TAG,"currentPos:"+currentPos+",colNum:"+colNum);
		return (currentPos + 1) % colNum == 1;
	}
	
	private boolean loadMoreData()
	{
		int currentPos=getSelectedItemPosition();
		int colNum=getColums();
		int count=getCount();
		int currentRow=(currentPos+1)/colNum+((currentPos+1)%colNum==0?0:1);
		int lastLine=count/colNum+(count%colNum==0?0:1);
		Log.d(TAG,"curret row:"+currentRow+",lastLine:"+lastLine+",count:"+getCount()+",colNum:"+colNum);
		return currentRow >= lastLine;
	}
	
	private boolean isLastPage()
	{
		int currentPos=getSelectedItemPosition();
		int colNum=getColums();
		int totalcount=getTotolCount();
		int currentRow=(currentPos+1)/colNum+((currentPos+1)%colNum==0?0:1);
		int lastLine=totalcount/colNum+(totalcount%colNum==0?0:1);
		Log.d(TAG,"currentRow:"+currentRow+",lastLine:"+lastLine+",totalCount:"+totalcount);
		return currentRow == lastLine;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		int action = event.getAction();
		long currentTimeMillis=System.currentTimeMillis();
		if (action == KeyEvent.ACTION_DOWN) {
			int keyCode = event.getKeyCode();
			switch (keyCode) {
			case KeyEvent.KEYCODE_DPAD_DOWN:{
				/**
				 * 非最后一行的最后一列
				 */
				Log.d(TAG,"isLastLine:"+isLastLine()+",isLastPage:"+isLastPage());
				if(isLastLine())
				{
					Log.d(TAG,"capture last line");
					if(currentTimeMillis-lastDownTimeMillis<1000)
					{
						lastDownTimeMillis=currentTimeMillis;
						return true;
					}
					if(!isLastPage()){
						//请求加载更多的数据
						if(status!=PullStatus.PULLING)
						{
							if(loadMoreData())
							{
								Log.d(TAG,"request more data");
								oldCount=pullAdapter.getCount();
								if(onKeyEndListener!=null)
								{
									Log.d(TAG,"onRefresh");
									status=PullStatus.PULLING;
									onKeyEndListener.onRefresh(firstType, secondType, orderStr);
								}
							}else {
								scrollPage(Direction.DOWN);
							}
						}
					}
					return true;
				}
				
				break;
			}
			case KeyEvent.KEYCODE_DPAD_RIGHT:{
				if(isKeyRightEnd()&&onKeyEndListener!=null)
				{
					Log.d(TAG,"onKeyRightEnd");
					onKeyEndListener.onKeyRightEnd();
				}
				break;
			}
			
			case KeyEvent.KEYCODE_DPAD_LEFT:{
				if(isKeyLeftEnd()&&onKeyEndListener!=null)
				{
					Log.d(TAG,"onKeyLeftEnd");
					onKeyEndListener.onKeyLeftEnd();
					return true;
				}
				break;
			}
			
			case KeyEvent.KEYCODE_DPAD_UP:{
				Log.d(TAG,"isFirstLine:"+isFirstLine()+",isFirstPage:"+isFirstPage());
				if(isFirstLine())
				{
					if(currentTimeMillis-lastUpTimeMillis<1000)
					{
						lastUpTimeMillis=currentTimeMillis;
						return true;
					}
					
					if(!isFirstPage())
					{
						scrollPage(Direction.UP);
						return true;
					}else
					{
						if(onKeyEndListener!=null)
						{
							onKeyEndListener.onKeyUpEnd();
							return true;
						}
					}

				}
				break;
			}
			default:
				break;
			}
		}
		return super.dispatchKeyEvent(event);
	}

	public String getFirstType() {
		return firstType;
	}

	public void setFirstType(String code) {
		this.firstType = code;
	}	
	
	public String getSecondType() {
		return secondType;
	}

	public void setSecondType(String code) {
		this.secondType = code;
	}	
	
	public String getOrder() {
		return orderStr;
	}

	public void setOrder(String code) {
		this.orderStr = code;
	}	
	/*@Override
	public void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		Log.d(TAG,"position onFocusChange,gainFocus:"+gainFocus);
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }*/
}
