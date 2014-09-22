package com.thomas.demo;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ScrollView;

/**
 * Bolg :http://blog.csdn.net/aaawqqq?viewmode=contents
 * 
 * @author baozi
 * 
 */

public class MyScrollView extends ScrollView
{
	private static final String TAG = MyScrollView.class.getSimpleName();
	// 拖动的距离 size = 4 的意思 只允许拖动屏幕的1/4
	private static final int size = 4;
	private View inner;
	private float y;
	private Rect normal = new Rect();
	private EditText edtxt = null;
	private int delt;
	private boolean isNormal = true;
	private boolean isDrag = false;

	public MyScrollView(Context context)
	{
		super(context);
	}

	public MyScrollView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate()
	{
		super.onFinishInflate();
		if (getChildCount() > 0)
		{
			inner = getChildAt(0);
			edtxt = (EditText) findViewById(R.id.edtxt);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev)
	{
		if (inner == null)
		{
			return super.onTouchEvent(ev);
		}
		else
		{
			commOnTouchEvent(ev);
		}
		return super.onTouchEvent(ev);
	}

	public void commOnTouchEvent(MotionEvent ev)
	{
		int action = ev.getAction();
		delt = edtxt.getHeight();
		switch (action)
		{
		case MotionEvent.ACTION_DOWN:
			y = ev.getY();
			break;

		case MotionEvent.ACTION_UP:
			if (isNeedAnimation())
			{
				animation();
			}
			break;

		case MotionEvent.ACTION_MOVE:
			final float preY = y;
			float nowY = ev.getY();
			/**
			 * size=4 表示 拖动的距离为屏幕的高度的1/4
			 */
			int deltaY = (int) (preY - nowY) / size;

			y = nowY;
			// 当滚动到最上或者最下时就不会再滚动，这时移动布局
			if (isNeedMove())
			{
				if (normal.isEmpty())
				{
					// 保存正常的布局位置
					if (isNormal)
					{
						Log.e(TAG, "Normal innerH:" + inner.getHeight());
						normal.set(inner.getLeft(), inner.getTop(),
								inner.getRight(), inner.getBottom());
					}
					else
					{
						Log.e(TAG, "innerH:" + inner.getHeight());
						normal.set(inner.getLeft(), -delt, inner.getRight(),
								inner.getHeight() - delt);
					}

					return;
				}
				int yy = inner.getTop() - deltaY;

				// 移动布局
				if (isDrag)
				{
					inner.layout(inner.getLeft(), yy, inner.getRight(),
							inner.getBottom() - deltaY);
				}
			}
			break;
		default:
			break;
		}
	}

	// 开启动画移动

	public void animation()
	{
		// 开启移动动画

		TranslateAnimation ta = null;
		// if (isNormal)
		// {
		// ta = new TranslateAnimation(0, 0, inner.getTop(), normal.top);
		// }
		// else
		// {
		// ta = new TranslateAnimation(0, 0, getScrollY(), normal.top);
		// }
		Log.e(TAG, "fromY: " + getScrollY() + " To:" + normal.top);
		ta = new TranslateAnimation(0, 0, getScrollY(), normal.top);
		ta.setDuration(200);
		inner.startAnimation(ta);
		// 设置回到正常的布局位置
		Log.e(TAG, "last top: " + normal.top);
		inner.layout(normal.left, normal.top, normal.right, normal.bottom);
		normal.setEmpty();
	}

	// 是否需要开启动画
	public boolean isNeedAnimation()
	{
		return !normal.isEmpty();
	}

	// 是否需要移动布局
	public boolean isNeedMove()
	{
		int offset = inner.getMeasuredHeight() - getHeight();
		int scrollY = getScrollY();
		Log.e(TAG, "scrollY: " + scrollY + "  delt:" + delt + "  delt/2:"
				+ delt / 2);
		if ((scrollY > (delt / 2)) && (scrollY < delt))
		{
			isNormal = false;
		}
		else
		{
			isNormal = true;
		}

		// if (scrollY == 0 || scrollY == offset)
		if (scrollY < delt || scrollY == offset)
		{
			Log.e(TAG, "need move");
			if (scrollY == 0)
			{
				isDrag = true;
			}
			else
			{
				isDrag = false;
			}
			return true;
		}

		return false;
	}
}
