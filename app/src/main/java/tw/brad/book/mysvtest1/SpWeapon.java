package tw.brad.book.mysvtest1;

import java.util.HashMap;
import java.util.LinkedList;
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


public class SpWeapon {	//武器物件		
	float x1,y1,x2,y2;
	int atk1,atk2,exist1,exist2;//攻擊力
	float bullet=100,reloadSpeed=(float) 0.1;
	Bitmap picture[]=new Bitmap[6];	//SP武器圖
	Paint paint= new Paint();	//創建paint(可以用來控制圖片的透明度)		
	
	public SpWeapon(Bitmap picture[])//產生武器
	{
        //改變大小
		Matrix matrix = new Matrix();//取得想要缩放的matrix參數
        matrix.setScale((float)0.5, (float)0.5);        	
        for(int i=0;i<6;i++)
        {
        	this.picture[i] = Bitmap.createBitmap(picture[i], 0, 0, 92, 92, matrix,true); //得到縮放後的新圖片		
        }			
		//依照是否被選擇，決定位置
		x1=10;y1=180;
		x2=10;y2=225;	
		paint.setAntiAlias(true);
	}		
	public void drawSelf(Canvas canvas)//重新繪製自己
	{	
		
		canvas.drawBitmap(picture[2], x1, y1, paint);//繪出黑圖像	
		canvas.drawBitmap(picture[5], x2, y2, paint);
		int t=(int) (46*bullet/100);
		if(t<1)
		{
			t=1;
		}
		if(t>46)
		{
			t=46;
		}		
		Bitmap newbit = Bitmap.createBitmap(picture[1], 0, 0, 46, t, null,true); 
		canvas.drawBitmap(newbit, x1, y1, paint);//繪出白圖像	
		newbit = Bitmap.createBitmap(picture[4], 0, 0, 46,t, null,true); 
		canvas.drawBitmap(newbit, x2, y2, paint);
		
		
		if(bullet==100)
		{
			canvas.drawBitmap(picture[0], x1, y1, paint);//繪出紅框圖像
			canvas.drawBitmap(picture[3], x2, y2, paint);
		}
	}	
	public int isInrange(float downX,float downY)//判斷觸碰是否在範圍內
	{
		if(downX>x1&&downX<x1+46&&downY>y1&&downY<y1+46)
		{
			return 1;
		}
		if(downX>x2&&downX<x2+46&&downY>y2&&downY<y2+46)
		{
			return 2;
		}
		return 0;
	}
	public void attack(int a)
	{
		if(a==1)
		{
			exist1=20;
		}
		else if(a==2)
		{
			exist2=200;
		}
	}
}

