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


public class Player {	//玩家物件		
	float x,y,realx,realy,realWidth,realHeight;
	//輸入的玩家位置(中心)、縮放過後的玩家位置(xy是左上角，但是顯示的圖片中心會是xy一開始輸入的xy)、縮放後的長寬
	int hp,dis,existTime=0,deadTime=0,injured;//玩家生命、距離、從出現開始的時間、從死亡開始的時間、受傷狀態
	Bitmap picture1,picture2,picture3;	//此玩家的圖片、受到傷害的圖片、死亡圖片
	Paint paint= new Paint();	//創建paint(可以用來控制圖片的透明度)	
	boolean exist=false;//是否存在、是否受到傷害

	public void creatPlayer(float x,float y,int hp,int dis,Bitmap picture,Bitmap picture_injured,Bitmap picture_dead)//產生敵人
	{
		this.x=x;
		this.y=y;
		this.hp=hp;
		this.dis=dis;
		this.picture1=picture;
		this.picture2=picture_injured;
		this.picture3=picture_dead;
		exist=true;
		injured=0;
		existTime=0;
		deadTime=0;
	}
	public void disablePlayer()//銷毀玩家
	{
		exist=false;
	}
	public Bitmap changeSize(Bitmap picture)
	{
		int width = picture.getWidth(); //獲得圖片的寬高
        int height = picture.getHeight();
        
        float scaleWidth = (float) 200/dis;//放大為200除以距離倍
        float scaleHeight = (float) 200/dis;
        
        Matrix matrix = new Matrix();//取得想要缩放的matrix參數
        matrix.setScale(scaleWidth, scaleHeight);
        	
        Bitmap newbit = Bitmap.createBitmap(picture, 0, 0, width, height, matrix,true); //得到縮放後的新圖片
        realx =x-newbit.getWidth()/2; //獲得新的圖片的左上角x、y
        realy =y-newbit.getHeight()/2;
        realWidth=newbit.getWidth();//把新的圖片長寬存起來作為範圍確認
        realHeight=newbit.getHeight();
		return newbit;		
	}
	public void drawSelf(Canvas canvas)//重新繪製自己
	{		
		int width = picture1.getWidth(); //獲得圖片的寬高
        int height = picture1.getHeight();
        
                
        if(hp<1)//判斷是否死亡
        {
        	injured=0;
        	Bitmap newbit=changeSize(picture3);//按照距離轉換大小
        	canvas.drawBitmap(newbit, realx, realy, paint);//繪出圖像
        }
        else if(injured>0)//判斷是否受到傷害
        {
        	injured=injured-1;
        	Bitmap newbit=changeSize(picture2);//按照距離轉換大小
        	//hp=hp-5;
        	canvas.drawBitmap(newbit, realx, realy, null);//繪出圖像
        }
        else{
        	Bitmap newbit=changeSize(picture1);//按照距離轉換大小
        	canvas.drawBitmap(newbit, realx, realy, null);//繪出圖像	
        }	
	}
	public void move(float downX,float downY)//按照路徑移動位置
	{		
		if(x>downX+50)
		{
			x=x-15;
		}
		else if(x<downX-50&&x<480-15)
		{
			x=x+15;
		}
		else if(x>downX+30)
		{
			x=x-5;
		}
		else if(x<downX-30&&x<480-5)
		{
			x=x+5;
		}
		else if(x>downX+10)
		{
			x=x-2;
		}
		else if(x<downX-10&&x<480-2)
		{
			x=x+2;
		}
		existTime++;//累加存在時間
	}
}

