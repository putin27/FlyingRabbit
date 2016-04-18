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


public class BulletEffect {	//武器效果物件		
	float x,y,realx,realy,degree,temx,temy;
	//輸入的效果位置(中心)、縮放過後的效果位置(xy是左上角，但是顯示的圖片中心會是xy一開始輸入的xy)、縮放後的寬高	
	int dis,effectType,rand,sp;//距離、模式、隨機值、特殊值
	int	existTime=0;//從出現開始的時間	
	Bitmap picture;	//此效果的圖片
	Paint paint= new Paint();//創建paint(可以用來控制圖片的透明度)	
	boolean exist=false;//是否存在

	public void creatEffect(float x,float y,int dis,Bitmap picture,int effectType,float degree)//產生效果
	{
		//數入值
		this.x=x;
		this.y=y;	
		temx=x;
		temy=y;
		this.dis=dis;
		this.effectType=effectType;
		this.picture=picture;
		this.degree=degree;
		sp=(int) degree;
		//設為存在，重置狀態
		exist=true;		
		existTime=0;		
		paint.setAlpha(255);
		paint.setAntiAlias(true);
		
		//給予隨機數
		Random ran = new Random();
		rand=ran.nextInt(360);
		
	}	
	public Bitmap changeSize(Bitmap picture,int baseDis,float degree)//要縮放的圖,縮放的基本倍率,旋轉角度
	{
		int width = picture.getWidth(); //獲得圖片的寬高
        int height = picture.getHeight();
        
        float scaleWidth = (float) baseDis/dis;//放大為 基本倍率/距離倍(敵人的基本倍率是200，準星是300)
        float scaleHeight = (float) baseDis/dis;
        
        Matrix matrix = new Matrix();//取得想要缩放的matrix參數
        matrix.setScale(scaleWidth, scaleHeight);
        matrix.postRotate(degree);
        Bitmap newbit = Bitmap.createBitmap(picture, 0, 0, width, height, matrix,true); //得到縮放後的新圖片
        realx =x-newbit.getWidth()/2; //獲得新的圖片的左上角x、y
        realy =y-newbit.getHeight()/2;
		return newbit;		
	}
	public Bitmap changeShape(Bitmap picture,float upSize,float downSize,float heightSize)//要縮放的圖,縮放的基本倍率,旋轉角度
	{
		int width = picture.getWidth(); //獲得圖片的寬高
        int height = picture.getHeight();
		float[] src = new float[] 
		{ 
				0, 0, // 左上
				width, 0,// 右上
				width, height,// 右下
				0, height // 左下
		};
		float[] dst = new float[] { 
				-((upSize-1)*width)/2, 0, // 左上
				width+((upSize-1)*width)/2, 0,// 右上
				width+((downSize-1)*width)/2, height*heightSize,// 右下
				-((downSize-1)*width)/2, height*heightSize // 左下
		};
		Matrix matrix = new Matrix();
		matrix.setPolyToPoly(src, 0, dst, 0, src.length >> 1); 
        Bitmap newbit = Bitmap.createBitmap(picture, 0, 0, width, height, matrix,true); //得到縮放後的新圖片
        realx =x-newbit.getWidth()/2; //獲得新的圖片的左上角x、y
        realy =y-newbit.getHeight()/2;
		return newbit;	
	}
	
	public void drawSelf(Canvas canvas,float downX,float downY)//重新繪製自己，裡面會先move一次
	{
		move(downX,downY);//按照路徑移動
		
		switch(effectType)//行動模式的判斷
		{
			case 1://手槍、步槍
			{
				Bitmap handgun1;
				if(existTime<5)
				{
					handgun1 = Bitmap.createBitmap(picture, 0+240*existTime, 0, 240, 240,null,true); 
				}
				else
				{
					handgun1 = Bitmap.createBitmap(picture, 240*4, 0, 240, 240,null,true);
				}
				Bitmap newbit=changeSize(handgun1,100,rand);//按照距離轉換大小
		    	canvas.drawBitmap(newbit, realx, realy, paint);//繪出圖像
				break;
			}
			case 2://刀
			{
				Bitmap blade;
				
				if(existTime<3)
				{
					blade=Bitmap.createBitmap(picture, 0, 0, 351, 11,null,true);
				}
				else if(existTime<10)
				{
					blade=Bitmap.createBitmap(picture, 0, 0+11*((existTime-3)/2), 351, 11,null,true);
				}
				else
				{
					blade=Bitmap.createBitmap(picture, 0, 33, 351, 11,null,true);
				}
				Bitmap newbit=changeSize(blade,300,degree);//按照距離轉換大小
		    	canvas.drawBitmap(newbit, realx, realy, paint);//繪出圖像
		    	break;
			}
			case 3://蓄力型1
			{
				Bitmap charge;
				if(existTime<30)
				{
					charge = Bitmap.createBitmap(picture, 0, 0, 1, 1,null,true);
				}
				else if(existTime<60)
				{
					charge = Bitmap.createBitmap(picture, 120*((existTime/2)%5), 0, 120, 120,null,true);
				}
				else
				{
					charge = Bitmap.createBitmap(picture, 120*(5+((existTime/2)%5)), 0, 120, 120,null,true); 				
				}
				degree=degree-2;
				Bitmap newbit=changeSize(charge,500,degree);//按照距離轉換大小
		    	canvas.drawBitmap(newbit, realx, realy, paint);//繪出圖像
				break;
			}
			case 4://蓄力型2 ，裡面的degree不是角度，是蓄力的等級
			{
				Bitmap charge;
				
				if(degree==1)
				{
					if(existTime<3)
						charge = Bitmap.createBitmap(picture, 960-192-192*existTime, 192, 192, 192,null,true);
					else
						charge = Bitmap.createBitmap(picture, 960-192-192*2, 192, 192, 192,null,true);
				}
				else if(degree==2)
				{
					if(existTime<6)
						charge = Bitmap.createBitmap(picture, 960-192-192*(existTime/2), 192, 192, 192,null,true);
					else
						charge = Bitmap.createBitmap(picture, 960-192-192*2, 192, 192, 192,null,true);
					
					
				}
				else
				{
					if(existTime<8)
						charge = Bitmap.createBitmap(picture, 960-192-192*(existTime/2), 192, 192, 192,null,true);
					else
						charge = Bitmap.createBitmap(picture, 960-192-192*3, 192, 192, 192,null,true);
				}
				
				Bitmap newbit=changeSize(charge,(int) (200*degree),0);//按照距離轉換大小
		    	canvas.drawBitmap(newbit, realx, realy, paint);//繪出圖像
				break;
			}
			case 5://爆炸1
			{
				Bitmap boom1;
				if(existTime<15)
				boom1 = Bitmap.createBitmap(picture, 0+240*(existTime/2), 0, 240, 240,null,true); 
				else
				boom1 = Bitmap.createBitmap(picture, 0+240*7, 0, 240, 240,null,true); 
					
				Bitmap newbit=changeSize(boom1,300,degree);//按照距離轉換大小
		    	canvas.drawBitmap(newbit, realx, realy, paint);//繪出圖像
				break;
			}
			case 6://SP1爆炸
			{
				Bitmap boom1;
				if(existTime<30)
				boom1 = Bitmap.createBitmap(picture, 0, +240*(existTime/4),640,240,null,true); 
				else
				boom1 = Bitmap.createBitmap(picture, 0, 240*8,640,240,null,true); 
					
				Bitmap newbit=changeSize(boom1,400,degree);//按照距離轉換大小
		    	canvas.drawBitmap(newbit, realx, realy, paint);//繪出圖像
				break;
			}
			case 7://SP1 炸彈
			{
				Bitmap boom2;
				boom2 = Bitmap.createBitmap(picture, 0, 0, 94, 106,null,true); 				
				Bitmap newbit=changeSize(boom2,100,degree);//按照距離轉換大小
		    	canvas.drawBitmap(newbit, realx, realy, paint);//繪出圖像
				break;
			}
			case 8://SP2光束
			{
				Bitmap beam;
				beam = Bitmap.createBitmap(picture, 0+192*(existTime%5), 0+384*(existTime%10/5), 192, 384,null,true); 
				
				Bitmap newbit=changeShape(beam,(float) 1,(float) 3,(float) 1);//按照距離轉換大小
		    	canvas.drawBitmap(newbit, realx, realy, paint);//繪出圖像
				break;
			}
			case 9://SP2擊中效果
			{
				Bitmap charge;
				charge = Bitmap.createBitmap(picture, 960-192-192*existTime, 192, 192, 192,null,true);
				if(dis<200)
				{
					dis=200;
				}
				Bitmap newbit=changeSize(charge,400,0);//按照距離轉換大小
		    	canvas.drawBitmap(newbit, realx, realy, paint);//繪出圖像
				break;
			}
			case 10://BOOM2
			{
				Bitmap boom1;
				if(existTime<10)
				boom1 = Bitmap.createBitmap(picture, 0+240*(existTime/2), 0, 240, 240,null,true); 
				else
				boom1 = Bitmap.createBitmap(picture, 0+240*5, 0, 240, 240,null,true); 
					
				Bitmap newbit=changeSize(boom1,300,degree);//按照距離轉換大小
		    	canvas.drawBitmap(newbit, realx, realy, paint);//繪出圖像
				break;
			}
		}
		
		
	}
	public void move(float downX,float downY)//按照路徑移動位置
	{		
		existTime++;//累加存在時間
		
		switch(effectType)//行動模式的判斷
		{
			case 1://手槍、步槍
			{
				if(existTime>1&&existTime<8)
				{
					paint.setAlpha(255-(existTime-2)*30);;
				}
				else if(existTime>8)
				{
					exist=false;
				}
				break;
			}			
			case 2://刀
			{
				if(existTime>6&&existTime<12)
				{
					paint.setAlpha(255-(existTime-6)*40);
				}
				else if(existTime>12)
				{
					exist=false;
				}
				break;
			}
			case 3://蓄力
			{
				x=downX;
				y=downY;
				break;
			}
			case 4://蓄力2
			{	
				if(existTime>1&&existTime<7+degree*2)
				{
					paint.setAlpha((int) (255-(existTime-2)*10*(5-degree)));;
				}
				else if(existTime>7+degree*2)
				{
					exist=false;
				}
				
				break;
			}
			case 5://爆炸1
			{				
				if(existTime>16)
				{
					exist=false;
				}
				break;
			}
			case 6://SP1爆炸
			{
				if(existTime>12&&existTime<30)
				{
					paint.setAlpha(255-(existTime-12)*10);
				}
				else if(existTime>30)
				{
					exist=false;
				}
				break;
			}
			case 7://SP1 炸彈
			{
				if(existTime>1&&existTime<15)
				{					
					if(sp==1)
					{
						x=x+(120-temx)/13+rand%10-6;
						y=y+(30-temy)/12+existTime+rand%3-2;
					}
					else if(sp==2)
					{
						x=x+(263-temx)/13+rand%9-5;
						y=y+(30-temy)/12+existTime+rand%4-2;
					}
					else if(sp==3)
					{
						x=x+(444-temx)/13+rand%8-4;
						y=y+(30-temy)/12+existTime+rand%5-3;
					}
					dis=dis+20;
					degree=degree+rand%90-45;
				}
				else if(existTime>15)
				{
					exist=false;
				}
				break;
			}
			case 8://SP2光束
			{
				x=downX+3;
				y=downY-100;
				if(existTime>180)
				{
					paint.setAlpha(255-(existTime-180)*10);;
				}
				
				if(existTime>200)
				{
					exist=false;
				}
				break;
			}
			case 9://SP2
			{	
				paint.setAlpha((int) (255-40*existTime));
				if(existTime>3)
				{
					exist=false;
				}				
				break;
			}
			case 10://爆炸2
			{	
				if(existTime>10)
				{
					paint.setAlpha((int) (255-20*(existTime-10)));
				}
				if(dis>20)
				{
					dis=dis-5;
				}
				
				if(existTime>20)
				{
					exist=false;
				}
				break;
			}
		}		
	}
}

