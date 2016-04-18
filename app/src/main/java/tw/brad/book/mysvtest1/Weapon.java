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


public class Weapon {	//武器物件		
	float x,y;
	int atk,dis,weaponType,cd=0,shootCd;//攻擊力、距離、種類、冷卻狀態、冷卻時間
	float bullet=100,bulletCost,reloadSpeed;
	Boolean select,drag=false,otherDrag=false;//是否被選擇、被拖動、另一個裝備被拖動、在上下交換中
	Bitmap picture,picture_s;	//武器圖、被選擇時的圖
	Paint paint= new Paint();	//創建paint(可以用來控制圖片的透明度)		
	int num=0,lock=0;//裝備上下位置、鎖定時間
	public void creatWeapon(int weaponType,Boolean select,Bitmap picture,Bitmap picture_s,
			int atk,int dis,int shootCd,float bulletCost,float reloadSpeed,int lv)//產生武器
	{
        //改變大小
		Matrix matrix = new Matrix();//取得想要缩放的matrix參數
        matrix.setScale((float)0.5, (float)0.5);        	
        this.picture = Bitmap.createBitmap(picture, 0, 0, 80, 80, matrix,true); //得到縮放後的新圖片
		this.picture_s=Bitmap.createBitmap(picture_s, 0, 0, 80, 80, matrix,true);			
		//依照是否被選擇，決定位置
		this.select=select;
		if(select)
		{
			x=485;y=160;
		}
		else
		{
			x=485;y=201;
		}		
		//資料庫 將武器編號對應武器數值傳入各個變數中	
		this.atk=(int)atk+atk*lv/10;
		this.dis=dis;
		this.weaponType=weaponType;
		this.bulletCost=bulletCost;
		this.reloadSpeed=reloadSpeed;
		this.shootCd=shootCd;					
	}		
	public void drawSelf(Canvas canvas)//重新繪製自己
	{	
		if(select)
		{			
			canvas.drawBitmap(picture_s, x, y, paint);//繪出被選擇圖像
		}
		else
		{
			canvas.drawBitmap(picture, x, y, paint);//繪出一般圖像
		}
	}	
	public void move(float downX,float downY)//移動
	{			
		float centerX=x+20,centerY=y+20;//獲得武器的中心xy
		
		if(otherDrag)//其他武器被拖動中，動作是:當觸碰位置到自己的位置時，就上下替換			
		{
			if(num==1)//在上面時
			{
				if(downY<=200)//當觸碰滑到圖片下面
				{
					num=2;//替換
				}
			}
			else if(num==2)//在下面時
			{
				if(downY>200)//當觸碰滑到圖片上面
				{
					num=1;//替換
				}
			}
		}	
		
		if(drag)//被拖動中，動作為:朝著觸碰位置移動，越過基準線就替換
		{
			if(centerY>downY+2)
			{
				centerY=centerY-(centerY-downY)/3;//使用加速度
			}
			else if(centerY<downY-2)
			{
				centerY=centerY+2+(downY-centerY)/3;
			}
			//把中心xy輸入回左上xy
			x=centerX-20;
			y=centerY-20;
			
			if(num==1)//在上面時
			{
				if(downY>200)//當觸碰滑到界限下面
				{
					num=2;//替換
				}
			}
			else if(num==2)//在下面時
			{
				if(downY<=200)//當觸碰滑到界限上面
				{
					num=1;//替換
				}
			}
		}					
		else//兩個武器都沒被拖動中(都是放開狀態):回歸上或下的位置
		{
			if(num==2)
			{
				x=x+(485-x)/3;
				y=y+(201-y)/3;	
			}
			else 
			{
				x=x+(485-x)/3;
				y=y+(160-y)/3;
			}			
		}		
	}
	public boolean isInrange(float downX,float downY)//判斷觸碰是否在範圍內
	{
		if(downX>x&&downX<x+40&&downY>y&&downY<y+40)
		{
			return true;
		}
		return false;
	}
}

