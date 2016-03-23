package com.example.adminstrator.wheellist;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import java.io.File;
import java.util.ArrayList;

public class WheelView extends View{

	private Scroller mScroller;
	private VelocityTracker mVelocityTracker;
	private int mTouchSlop;
	private float mLastMoveY;
	private int mScrollOffset;
	
	private ArrayList<String> mItems;
	private Paint mPaint;
	
	private int mDisplayItems = 5;
	private int mSpliteLineHeight = 0;
	private int mLineHeight = -1;
	private int mSelectItem = -1;
	private int mCenterItemPos = -1;
	private int mMinFlingVelocity = -1;
	
	private int mTextSize = 36;
	private int mTextColor;
	private Rect mTextBounds;
	
	private String mVoicePath;
	
	private int mToCenterDistance;
	
	private boolean mIsTouch = false;
	
	//private SoundPool mSoundPool;
	private MediaPlayer mPlayer;
	private int soundId;
	private boolean startedPlay = false;
	
	private Context mCtx;
	private static final String TAG = "BerWheelView";
	
	public WheelView(Context context) {
		super(context);
		this.mCtx = context;
		// TODO Auto-generated constructor stub
		mScroller = new Scroller(context);
		mTouchSlop = ViewConfiguration.getTouchSlop();
		mMinFlingVelocity = ViewConfiguration.getMinimumFlingVelocity();
		mItems = new ArrayList<String>();
		mPaint = new Paint();

		mTextColor = Color.BLACK;
		
		mPaint.setTextSize(mTextSize);
		mPaint.setColor(mTextColor);
		mPaint.setStrokeWidth(mSpliteLineHeight);
		
		mTextBounds = new Rect();
		
		this.setBackgroundColor(Color.GRAY);
		
	}

	@Override
	public void computeScroll() {
		// TODO Auto-generated method stub
		super.computeScroll();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub

		
		int action = event.getAction();
		switch(action){
		case MotionEvent.ACTION_DOWN:
			mIsTouch = true;
			mScroller.forceFinished(true);
			mLastMoveY = event.getY();
			stopSound();
			break;
		case MotionEvent.ACTION_MOVE:
			if(null == mVelocityTracker){				
				mVelocityTracker = VelocityTracker.obtain();
			}
		
			mVelocityTracker.addMovement(event);
			
			mScrollOffset = (int)(event.getY() - mLastMoveY);
			if(Math.abs(mScrollOffset) > mTouchSlop){
				startSound();
				newPosition(mScrollOffset, mLineHeight);
				this.invalidate();
				mLastMoveY = event.getY();
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			//fling
			mIsTouch = false;
			mVelocityTracker.computeCurrentVelocity(1000);
			float velocity = mVelocityTracker.getYVelocity();
			mVelocityTracker.clear();
			if(Math.abs(velocity) > mMinFlingVelocity){
				mScroller.fling(0, (int)event.getY(), 0, (int)(-velocity), 0, 0, -2000, 2000);
				this.invalidate();
				return true;
			}
			//end fling
			scrollToCenter();
			
			
			break;
		}
		return true;//super.onTouchEvent(event);
	}

	private void scrollToCenter(){
		mToCenterDistance = mLineHeight/2-mCenterItemPos;
		if(mToCenterDistance != 0){	
			mLastMoveY = 0;
			mScroller.startScroll(0, 0, 0, mLineHeight/2-mCenterItemPos, 2000);
			this.invalidate();
		}
	}
	
	public void setItems(String[] items){
		if(null==items || items.length==0){
			return;
		}
		
		this.mItems.clear();
		
		for(String item : items){
			this.mItems.add(item);
		}
		
		this.mSelectItem = this.mItems.size() / 2;
		
		this.invalidate();
	}
	
	public void setItemHeight(int height){
		this.mLineHeight = height;
	}
	
	public void setShowItems(int count){
		this.mDisplayItems = count;
	}
	
	public boolean setScrollVoice(String path){
		if(null == path){
			return false;
		}
		File file = new File(path);
		if(!file.exists()){
			return false;
		}
		
		this.mVoicePath = path;
		
//		mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
//		soundId = mSoundPool.load(this.mVoicePath, 1);
//		startSound();
		
		if(null == mPlayer){
			mPlayer = new MediaPlayer();
		}
		
		mPlayer.reset();
		try {
			mPlayer.setDataSource(this.mVoicePath);
			mPlayer.setLooping(true);
			mPlayer.prepare();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			if(null != mPlayer){
				mPlayer.release();
				mPlayer = null;
			}
			e.printStackTrace();
		} 
		
		
		
		
		return true;
	}
	
	private void startSound(){
//		if(null != mSoundPool && (!startedPlay)){
//			startedPlay = true;
//			//mSoundPool.stop(soundId);
//			mSoundPool.play(soundId, 1.0f, 1.0f, 1, -1, 1.0f);
//		}
//		
//		if(null != mSoundPool){
//			mSoundPool.autoResume();
//		}
		
		if(null != mPlayer && (!startedPlay)){
			startedPlay = true;
			mPlayer.start();
		}
		
		if(null != mPlayer){
			mPlayer.start();
		}
			
	}
	
	private void stopSound(){
//		//startedPlay = false;
//		if(null != mSoundPool){
//			//mSoundPool.stop(soundId);
//			mSoundPool.autoPause();
//		}
		
		if(null != mPlayer && mPlayer.isPlaying()){
			//mPlayer.stop();
			mPlayer.pause();
			mPlayer.seekTo(0);
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		int itemSize = this.mItems.size();
		if(0 == itemSize){
			return;
		}
		
		
		
		int viewH = this.getHeight();
		int viewW = this.getWidth();
		if(mLineHeight < 0){			
			mLineHeight = (viewH+mSpliteLineHeight)/this.mDisplayItems - mSpliteLineHeight;
		}
		
		if(mCenterItemPos < 0){
			mCenterItemPos = mLineHeight/2;
		}
		
		//newPosition(mScrollOffset, mLineHeight);
		
		canvas.save();
		mPaint.setColor(Color.YELLOW);
		//canvas.drawLine(0, viewH / 2, viewW, viewH / 2, mPaint);
		canvas.drawRect(0, (viewH-mLineHeight)/2, viewW, (viewH+mLineHeight)/2, mPaint);
		mPaint.setColor(Color.BLACK);
		//draw center
		int itemTop = viewH/2-mCenterItemPos;
		int selectIndex = mSelectItem;
		drawText(canvas, 0, itemTop, viewW, itemTop + mLineHeight, mItems.get(selectIndex));
		
		//draw up
		drawUp(canvas, itemTop, viewW);
		
		// draw down
		drawDown(canvas, itemTop + mLineHeight, viewW, viewH);
		

		
		canvas.restore();
		//canvas.translate(0, -this.mScrollOffset);
		if(!mScroller.isFinished()){
			mScroller.computeScrollOffset();
			int curY = mScroller.getCurrY();
			mScrollOffset = (int)(mLastMoveY - curY);
			//Log.e("BerWheelView", "scrollerY = "+curY);
			newPosition(mScrollOffset, mLineHeight);
			this.invalidate();
			mLastMoveY = curY;
			
		}else if(mScroller.isFinished() && (!mIsTouch)){
			scrollToCenter();
		}
		
		if(Math.abs(mToCenterDistance - mLastMoveY) <= 10){
			stopSound();
		}
	}
	
	private void newPosition(int distance, int lineHeight){
		//int scrolledLine = (Math.abs(offset) + lineHeight);
		int itemSize = mItems.size();
		if(distance > 0){
			//向下滑动,移到上边线就认为已经移到了下一个item
			//Log.e("BerWheelView", "newPosition mSelectItem="+mSelectItem);
			int passItems = (distance+lineHeight-mCenterItemPos+mSpliteLineHeight)/(lineHeight+mSpliteLineHeight);
			mSelectItem = getItemIndex(-passItems);
			//Log.e("BerWheelView", "newPosition mSelectItem="+mSelectItem+ "--passItem="+passItems+"--["+distance+","+mCenterItemPos+"]");
			if(distance-mCenterItemPos< 0){
				//不超过上边线
				mCenterItemPos = mCenterItemPos - distance;
			}else if(distance-mCenterItemPos == 0){
				//刚好移到上边线，
				mCenterItemPos = lineHeight + mSpliteLineHeight;
			}
			else{
				//
				mCenterItemPos = lineHeight-(distance-mCenterItemPos-mSpliteLineHeight)%(lineHeight+mSpliteLineHeight);
			}
		}else{
			//向上滑动
			//Log.e("BerWheelView", "newPosition mSelectItem="+mSelectItem);
			int passItems = (-distance+mCenterItemPos)/(lineHeight+mSpliteLineHeight);
			mSelectItem = getItemIndex(passItems);
			//Log.e("BerWheelView", "newPosition mSelectItem="+mSelectItem+ "--passItem="+passItems+"--["+distance+","+mCenterItemPos+"]");
			if(-distance - (lineHeight+mSpliteLineHeight-mCenterItemPos) < 0){
				mCenterItemPos = mCenterItemPos - distance;
			}else{
				mCenterItemPos = (-distance - (lineHeight-mCenterItemPos) -mSpliteLineHeight)%(lineHeight+mSpliteLineHeight);
			}
		}
	}
	
	private void drawText(Canvas canvas, int left, int top, int right, int bottom, String txt){
		mPaint.getTextBounds(txt, 0, txt.length(), mTextBounds);
		mPaint.setTextSize(mTextSize);
		int x = left + (right-left - mTextBounds.width())/2;
		int txtH = mTextBounds.bottom - mTextBounds.top;
		int y = top+(bottom-top-txtH)/2 - mTextBounds.top;
		canvas.drawText(txt, x, y, mPaint);
	}

	
	private void drawUp(Canvas canvas, int startPos, int width){
		int index = 0;
		while(startPos > 0){
			if(mSpliteLineHeight > 0){				
				canvas.drawLine(0, startPos-mSpliteLineHeight, width, startPos-mSpliteLineHeight, mPaint);
				startPos -= mSpliteLineHeight;
			}
			index--;
			drawText(canvas, 0, startPos-mLineHeight, width, startPos, mItems.get(getItemIndex(index)));
			startPos-= mLineHeight;
		}
	}
	
	private void drawDown(Canvas canvas, int startPos, int width, int bottom){
		int index = 0;
		while(startPos < bottom){
			if(mSpliteLineHeight > 0){				
				canvas.drawLine(0, startPos+mSpliteLineHeight, width, startPos+mSpliteLineHeight, mPaint);
				startPos += mSpliteLineHeight;
			}
			index++;
			drawText(canvas, 0, startPos+mLineHeight, width, startPos, mItems.get(getItemIndex(index)));
			startPos+= mLineHeight;
		}
	}
	
	private int getItemIndex(int offset){
		int itemSize = mItems.size();
		int index = 0;
		
		index = (mSelectItem + offset%itemSize +itemSize)%itemSize;

		return index;
	}
	
	public int getSelectItem(){
		return mSelectItem;
	}
	
	public boolean setSelectItem(int index){
		int size = mItems.size();
		if(size == 0 || index<0 || index>=size){
			return false;
		}
		
		mSelectItem = index;
		mCenterItemPos = -1;
		this.invalidate();
		return true;
	}
	
	public void destroy(){
		if(null != mPlayer){
			mPlayer.stop();
			mPlayer.release();
			mPlayer = null;
		}
	}
}
