package tw.brad.book.mysvtest1;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;


public class EnemySP {	//敵人特殊攻擊物件		
	int exist,type;
	float x,width;
	public void creatEnemySP(float x,float width,int exist,int type)//產生特殊攻擊
	{
		this.x=x;
		this.width=width;
		this.exist=exist;	
		this.type=type;
	}	
	public boolean isInrange(float X)//判斷觸碰是否在範圍內
	{
		if(X>x-width-50&&X<x+width+50)
		{
			return true;
		}
		return false;
	}
}

