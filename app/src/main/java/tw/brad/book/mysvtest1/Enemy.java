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


public class Enemy {	//敵人物件		
	float x,y,realx,realy,realWidth,realHeight,temx,temy,movex,movey;
	//輸入的敵人位置(中心)、縮放過後的敵人位置(xy是左上角，但是顯示的圖片中心會是xy一開始輸入的xy)、縮放後的寬高、temxy給切斷用、movexy移動路徑用
	
	int maxhp,hp=0,dis,moveType,deadType,rand,rand2,rand3,hptem;//敵人生命、距離、行動模式、死亡模式、隨機數
	float degree,temDegree;//角度
	int	existTime=0,deadTime=0,injuredTime=0,lockedTime=0;//從出現開始的時間、從死亡開始的時間、被鎖定的時間
	int attack=0,injured=0,bladeIn=0,sp=0,n_sp=0;//造成傷害的狀態、被傷害的狀態、被切入的狀態、特殊攻擊
	Bitmap picture1,picture2,picture3,picture_lock;	//此敵人的圖片、受到傷害的圖片、死亡圖片、鎖定
	Paint paint= new Paint();	//創建paint(可以用來控制圖片的透明度)	
	Paint paintLock= new Paint();	//準星的paint
	boolean exist=false;//是否存在

	public void creatEnemy(float x,float y,int hp,int dis,float degree,
			Bitmap picture,Bitmap picture_injured,Bitmap picture_dead,Bitmap picture_lock,
			int moveType,int deadType)//產生敵人
	{
		//數入值
		this.x=x;
		this.y=y;
		this.hp=hp;
		this.maxhp=hp;
		this.dis=dis;
		this.moveType=moveType;
		this.picture1=picture;
		this.picture2=picture_injured;
		this.picture3=picture_dead;
		this.picture_lock=picture_lock;
		this.degree=degree;
		this.deadType=deadType;
		
		//設為存在，重置狀態
		exist=true;
		injured=0;
		existTime=0;
		deadTime=0;
		injuredTime=0;
		bladeIn=0;
		paint.setAlpha(255);
		realWidth=0;
		realHeight=0;
		//給予隨機數
		Random ran = new Random();
		rand=ran.nextInt(10000);
		rand2=ran.nextInt(440)+50;
		rand3=ran.nextInt(130)+50;
	}
	public void disableEnemy()//銷毀敵人
	{
		exist=false;
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
        if(baseDis==200)//敵人圖像的倍率，這樣就不會儲存到準星的real座標
        {
        	realx =x-newbit.getWidth()/2; //獲得新的圖片的左上角x、y
            realy =y-newbit.getHeight()/2;
            realWidth=newbit.getWidth();//把新的圖片長寬存起來作為範圍確認
            realHeight=newbit.getHeight();
        }        
		return newbit;		
	}
	public Bitmap changeSize2(Bitmap picture,int baseDis,float degree)//要縮放的圖,縮放的基本倍率,旋轉角度
	{
		int width = picture.getWidth(); //獲得圖片的寬高
        int height = picture.getHeight();
        
        float scaleWidth = (float) baseDis/dis;//放大為 基本倍率/距離倍(敵人的基本倍率是200，準星是300)
        float scaleHeight = (float) baseDis/dis;
        
        Matrix matrix = new Matrix();//取得想要缩放的matrix參數
        matrix.setScale(scaleWidth, scaleHeight);
        matrix.postRotate(degree);	
        Bitmap newbit = Bitmap.createBitmap(picture, 0, 0, width, height, matrix,true); //得到縮放後的新圖片
        if(baseDis==200)//敵人圖像的倍率，這樣就不會儲存到準星的real座標
        {
        	realx =temx-newbit.getWidth()/2; //獲得新的圖片的左上角x、y
            realy =temy-newbit.getHeight()/2;
            realWidth=newbit.getWidth();//把新的圖片長寬存起來作為範圍確認
            realHeight=newbit.getHeight();
        }        
		return newbit;		
	}
	public void drawSelf(Canvas canvas)//重新繪製自己，裡面會先move一次
	{
		int width = picture1.getWidth(); //獲得圖片的寬高
        int height = picture1.getHeight();
        
        
		if(hp<=0)
		{			
			deadMove();//照死亡路徑移動			
		}
		else
		{
			move();//按照路徑移動			
		}
		
		int lockY =0;//準星的高度
        //調整準心的透明度
		if(lockedTime<4)
		{
			lockY=4*(4-lockedTime);
			paintLock.setAlpha(0+lockedTime*70);
		}
		else if(lockedTime%31-4<16)
		{
			paintLock.setAlpha(210-(lockedTime%31-4)*4);
		}
		else
		{
			paintLock.setAlpha(210-(34-lockedTime%31-4)*4);
		}
		//距離過近時調整準心的位置
		if(dis<300)
		{
			if(lockedTime%8<5)
			{
				lockY=(lockedTime%8)*2;
			}
			else
			{
				lockY=(8-lockedTime%8)*2;
			}			
		}
		
		
        if(hp<=0)//判斷是否死亡
        {
        	injured=0;//重置被傷害狀態
        	//Bitmap newbit=changeSize(picture3,200,degree);//按照距離轉換大小
        	if(deadType==4)
        	{	
        		if(rand%2==0)//左右分開
        		{
        			Bitmap newbit1 = Bitmap.createBitmap(picture1, 0, 0, width/2, height, null,true); //得到縮放後的新圖片
            		Bitmap newbit2 = Bitmap.createBitmap(picture1, width/2, 0, width/2, height, null,true); //得到縮放後的新圖片
                    
            		Bitmap part1=changeSize(newbit1,200,degree);//按照距離轉換大小  
            		canvas.drawBitmap(part1, realx, realy, paint);//繪出圖像
            		
            		Bitmap part2=changeSize2(newbit2,200,degree);//按照距離轉換大小  
            		canvas.drawBitmap(part2, realx, realy, paint);//繪出圖像
        		}
        		else//上下分開
        		{
        			Bitmap newbit1 = Bitmap.createBitmap(picture1, 0, 0, width, height/2, null,true); //得到縮放後的新圖片
            		Bitmap newbit2 = Bitmap.createBitmap(picture1, 0, height/2, width, height/2, null,true); //得到縮放後的新圖片
                    
            		Bitmap part1=changeSize(newbit1,200,degree);//按照距離轉換大小  
            		canvas.drawBitmap(part1, realx, realy, paint);//繪出圖像
            		
            		Bitmap part2=changeSize2(newbit2,200,temDegree);//按照距離轉換大小  
            		canvas.drawBitmap(part2, realx, realy, paint);//繪出圖像
        		}
        	}      
        	else if(deadType==1)
        	{
        		Bitmap newbit=changeSize(picture3,200,degree);//按照距離轉換大小  
        		canvas.drawBitmap(newbit, realx, realy, paint);//繪出圖像
        	}
        	else if(deadType==5)
        	{
        		Bitmap newbit=changeSize(picture2,200,degree);//按照距離轉換大小  
        		canvas.drawBitmap(newbit, realx, realy, paint);//繪出圖像
        	}
        	else
        	{
        		Bitmap newbit=changeSize(picture1,200,degree);//按照距離轉換大小  
        		canvas.drawBitmap(newbit, realx, realy, paint);//繪出圖像
        	}
        }
        else if(injured>0)//判斷是否受到傷害
        {      
        	injuredTime=3;
        	Bitmap newbit=changeSize(picture2,200,degree);//按照距離轉換大小
        	hp=hp-injured;//HP減去所受傷害
        	injured=0;
        	if(hp<=0&&bladeIn==1&&deadType==2)//如果是被刀砍死
    		{
        		if(deadType==2)
        		{
        			deadType=4;//死亡動作改變
        		}
        		
    			if(rand%2==0)//左右分開
    			{
    				temx=x+width/4;
        			temy=y;
        			x=x-width/4;
    			}
    			else//上下分開
    			{
    				temx=x;
        			temy=y+height/4;
        			y=y-height/4;
    			}
    			
    		}
        	canvas.drawBitmap(newbit, realx, realy, paint);//繪出圖像 
        	
        	paintLock.setAlpha(255);
        	Bitmap newlock=changeSize(picture_lock,300,0);//按照距離轉換大小
        	canvas.drawBitmap(newlock, x-newlock.getWidth()/2, y+realHeight/2+lockY, paintLock);//繪出圖像        	
        }
        else//正常狀態
        {
        	injured=0;//重置被傷害狀態      
        	if(injuredTime>0)
        	{
        		injuredTime--;
        		Bitmap newbit=changeSize(picture2,200,degree);//按照距離轉換大小
        		canvas.drawBitmap(newbit, realx, realy, paint);//繪出圖像
        	}
        	else
        	{
        		Bitmap newbit=changeSize(picture1,200,degree);//按照距離轉換大小  
        		canvas.drawBitmap(newbit, realx, realy, paint);//繪出圖像
        	}        	        	
        		
        	        	
        	Bitmap newlock=changeSize(picture_lock,300,0);//按照距離轉換大小
        	canvas.drawBitmap(newlock, x-newlock.getWidth()/2, y+realHeight/2+lockY, paintLock);//繪出準星圖像
        	
        }	
	}
	public void move()//按照路徑移動位置
	{		
		existTime++;//累加存在時間
		//Log.i("info","moveType:"+moveType);
		switch(moveType)//行動模式的判斷
		{
			case 1://往玩家面前衝，撞到則消失
			{
				if(dis>500)
				{
					paint.setAlpha(255-(4*dis-2000));
					if((255-(4*dis-2000))<0)
					{
						paint.setAlpha(0);
					}
				}					
				if(dis<200)//離玩家太近，對玩家造成傷害，並且死亡
				{
					deadTime++;
					hp=0;
					attack=1;
				}
				else//正常狀態，向前移動
				{						
					dis=dis-2;
				}
				break;
			}			
			case 2://往玩家面前衝，撞到則消失(帶旋轉)
			{
				if(dis>500)
				{
					paint.setAlpha(255-(4*dis-2000));
					if((255-(4*dis-2000))<0)
					{
						paint.setAlpha(0);
					}
				}					
				if(dis<100)//離玩家太近，對玩家造成傷害，並且死亡
				{
					deadTime++;
					hp=0;
					attack=1;
				}
				else//正常狀態，向前移動
				{						
					dis=dis-2;
					degree=degree+10;
				}
				break;
			}
			case 3://向前移動到定點，然後轉換行動模式
			{
				if(dis>500)
				{
					paint.setAlpha(255-(4*dis-2000));
					if((255-(4*dis-2000))<0)
					{
						paint.setAlpha(0);
					}
				}	
				if(act(1,1,x,y,400)==1)//到達定點後轉為左右移動丟蛋
				{
					moveType=5;
				}
				break;
			}	
			case 4://向指定位置加速度移動+旋轉，到達指定位置則變為Type1
			{				
				if(act(1,1,movex,movey,dis)==1)//到達定點後轉為前進
				{
					moveType=1;
				}
				break;
			}
			case 5://左右移動，丟蛋
			{	
				if(existTime>1200)//時間經過消失
				{
					if(act(1,1,x,-70,dis)==1)
					{
						exist=false;
					}
				}
				else if(act(1,(float) 0.5,rand2,y,dis)==1)//到達定點後左右移動
				{		
					Random ran = new Random();
					rand2=ran.nextInt(440)+50;					
				}	
				if(existTime%100==0)
				{
					sp=1;//丟蛋
				}
				
				break;
			}
			case 6://往玩家面前衝，撞到則消失(帶隨機移動)
			{
				if(dis>500)
				{
					paint.setAlpha(255-(4*dis-2000));
					if((255-(4*dis-2000))<0)
					{
						paint.setAlpha(0);
					}
				}					
				if(dis<200)//離玩家太近，對玩家造成傷害，並且死亡
				{
					deadTime++;
					hp=0;
					attack=1;
				}
				else if(act(1,(float) 1,rand2,rand3,dis)==1)//到達定點後隨機移動
				{		
					Random ran = new Random();
					rand2=ran.nextInt(440)+50;	
					rand3=ran.nextInt(130)+50;
				}	
										
				dis=dis-2;
				
				break;
			}
			case 7://向指定位置加速度移動，到達指定位置則變為Type1
			{	
				n_sp=1;
				if(dis>500)
				{
					paint.setAlpha(255-(4*dis-2000));
					if((255-(4*dis-2000))<0)
					{
						paint.setAlpha(0);
					}
				}	
				if(act(1,3,x,y,400)==1)//到達定點後轉為前進
				{
					moveType=1;
				}
				break;
			}
			//BOSS 1
			case 100://向前移動到定點，然後轉換行動模式
			{
				if(dis>500)
				{
					paint.setAlpha(255-(4*dis-2000));
					if((255-(4*dis-2000))<0)
					{
						paint.setAlpha(0);
					}
				}	
				if(act(1,1,x,y,400)==1)//到達定點後轉為隨機移動
				{
					moveType=101;
				}
				break;
			}	
			case 101://隨機移動
			{
				if(dis>500)
				{
					paint.setAlpha(255-(4*dis-2000));
					if((255-(4*dis-2000))<0)
					{
						paint.setAlpha(0);
					}
				}	
				if(dis>500)
				{
					paint.setAlpha(255-(4*dis-2000));
					if((255-(4*dis-2000))<0)
					{
						paint.setAlpha(0);
					}
				}		
				if(act(1,(float) 0.7,rand2,rand3,dis)==1)//到達定點後隨機移動
				{		
					Random ran = new Random();
					rand2=ran.nextInt(440)+50;	
					rand3=ran.nextInt(130)+50;
				}	
				if(existTime%400==390)			
				{
					moveType=102;
				}
				else if(existTime%600==380)			
				{
					moveType=105;
					sp=2;//向下攻擊
					
				}
				break;
			}
			case 102://往定點移動後向前衝
			{
				if(dis>500)
				{
					paint.setAlpha(255-(4*dis-2000));
					if((255-(4*dis-2000))<0)
					{
						paint.setAlpha(0);
					}
				}	
				if(dis>500)
				{
					paint.setAlpha(255-(4*dis-2000));
					if((255-(4*dis-2000))<0)
					{
						paint.setAlpha(0);
					}
				}		
				if(act(1,(float) 1,rand2,rand3,600)==1)//到達定點後向前衝
				{		
					hptem=hp;
					moveType=103;				
				}	
				break;
			}
			case 103://向前衝後回到原位
			{
				if(dis>500)
				{
					paint.setAlpha(255-(4*dis-2000));
					if((255-(4*dis-2000))<0)
					{
						paint.setAlpha(0);
					}
				}	
				if(act(1,2,x,y,200)==1)
				{
					attack=1;
					moveType=104;
				}
				//Log.i("info","hptem:"+hptem+" hp:"+hp);
				if(hp<hptem-20)
				{
					degree=degree-50;
					moveType=104;
				}
				break;
			}
			case 104://回到原位 隨機移動
			{
				if(dis>500)
				{
					paint.setAlpha(255-(4*dis-2000));
					if((255-(4*dis-2000))<0)
					{
						paint.setAlpha(0);
					}
				}	
				if(act(1,3,x,y,400)==1)
				{					
					moveType=101;
				}
				break;
			}
			case 105://往上飛
			{
				if(dis>500)
				{
					paint.setAlpha(255-(4*dis-2000));
					if((255-(4*dis-2000))<0)
					{
						paint.setAlpha(0);
					}
				}	
				if(act(1,(float) 0.5,x,-200,200)==1)
				{					
					moveType=106;					
				}
				break;
			}
			case 106://往下飛
			{
				if(dis>500)
				{
					paint.setAlpha(255-(4*dis-2000));
					if((255-(4*dis-2000))<0)
					{
						paint.setAlpha(0);
					}
				}	
				if(act(2,3,x,280,200)==1)
				{					
					moveType=104;					
				}
				break;
			}
		}		
	}
	public int act(int actType,float a_speed,float a_x,float a_y,int a_dis)//按照路徑移動位置
	{
		switch(actType)
		{		
			case 1://定點移動(加速度)
			{		
				Log.i("info","x:"+x+" a_x:"+a_x+"y:"+y+" a_y:"+a_y+" dis:"+dis+" a_dis:"+a_dis);
				if(x>a_x-1&&x<a_x+1&&y>a_y-1&&y<a_y+1&&dis>a_dis-2&&dis<a_dis+2)
				{
					return 1;
				}
				//dis移動				
				if(dis>=a_dis+6)
				{
					dis=(int) (dis-2*a_speed);
				}
				else if(dis<=a_dis-6)
				{
					dis=(int) (dis+2*a_speed);
				}
				else if(dis>=a_dis+2)
				{
					dis=(int) (dis-2);
				}
				else if(dis<=a_dis-2)
				{
					dis=(int) (dis+2);
				}				
				//X移動
				if((a_x-x)*(a_x-x)+(a_y-y)*(a_y-y)<1600)//離定點很近時，角度慢慢變為正面
				{						
					degree=degree-degree/12;
				}				
				if((a_x-x)*(a_x-x)<1600)//離定點很近時，移動變慢
				{
					x=x+(a_x-x)/12*a_speed;
				}
				else
				{
					if(a_x-x<0)//判定左右移動方向
					{
						x=x-10*a_speed;//移動
						if(degree*degree<2)//如果第一次移動，旋轉角度
						{
							degree=degree-30;
						}
					}							
					else
					{
						x=x+10*a_speed;
						if(degree*degree<2)
						{
							degree=degree+30;
						}
					}						
				}
				
				if((a_y-y)*(a_y-y)<1600)
				{
					y=y+(a_y-y)/12;
				}
				else
				{
					
					if(a_y-y<0)
						y=y-10;
					else
						y=y+10;
				}		
				break;
			}		
			case 2://定點移動
			{		
				Log.i("info","x:"+x+" a_x:"+a_x+"y:"+y+" a_y:"+a_y+" dis:"+dis+" a_dis:"+a_dis);
				if(x>a_x-1&&x<a_x+1&&y>a_y-1&&y<a_y+1&&dis>a_dis-2&&dis<a_dis+2)
				{
					return 1;
				}
				//dis移動				
				if(dis>=a_dis+6)
				{
					dis=(int) (dis-2*a_speed);
				}
				else if(dis<=a_dis-6)
				{
					dis=(int) (dis+2*a_speed);
				}
				else if(dis>=a_dis+2)
				{
					dis=(int) (dis-2);
				}
				else if(dis<=a_dis-2)
				{
					dis=(int) (dis+2);
				}				
				//X移動
				if((a_x-x)*(a_x-x)+(a_y-y)*(a_y-y)<1600)//離定點很近時，角度慢慢變為正面
				{						
					degree=degree-degree/12;
				}				
				if((a_x-x)*(a_x-x)<1000)//離定點很近時，移動變慢
				{
					x=a_x;
				}
				else
				{
					if(a_x-x<0)//判定左右移動方向
					{
						x=x-10*a_speed;//移動
						if(degree*degree<2)//如果第一次移動，旋轉角度
						{
							degree=degree-30;
						}
					}							
					else
					{
						x=x+10*a_speed;
						if(degree*degree<2)
						{
							degree=degree+30;
						}
					}						
				}
				
				if((a_y-y)*(a_y-y)<1000)
				{
					y=a_y;
				}
				else
				{
					
					if(a_y-y<0)
						y=y-10;
					else
						y=y+10;
				}		
				break;
			}			
		}
		return 0;
	}
	public void deadMove()//按照路徑移動位置(死亡時)
	{		
		existTime++;//累加存在時間
		
		switch(deadType)//行動模式的判斷
		{
			case 1://爆炸
			{
				paint.setAlpha(255-6*deadTime);//透明化
				deadTime++;//累計死亡時間
				if(deadTime==40)//死亡後一段時間，消失
				{
					exist=false;
				}
				break;
			}			
			case 2://掉落
			{
				int way;
				if(rand%2==1)
				{
					way=1;
				}
				else
				{
					way=-1;
				}
				x=x+way*(rand%8+2);
				y=y-(rand%8+2-deadTime*2);
				dis=dis+20;
				degree=degree+way*(rand%25+5);
				paint.setAlpha(255-12*deadTime);//透明化
				deadTime++;//累計死亡時間
				if(deadTime==20)//死亡後一段時間，消失
				{
					exist=false;
				}
				break;
			}
			case 3://BOSS爆炸
			{
				paint.setAlpha(255-6*deadTime);//透明化
				deadTime++;//累計死亡時間
				if(deadTime==40)//死亡後一段時間，消失
				{
					exist=false;
				}
				break;				
			}	
			case 4://切斷
			{
				x=x-(rand%8+2);
				y=y-(rand%8+2-deadTime*2);
				dis=dis-2;
				degree=degree-(rand%25+5);
				
				temx=temx+((rand/10)%8+2);
				temy=temy-((rand/10)%8+2-deadTime*2);
				dis=dis-2;
				temDegree=temDegree+((rand/10)%25+5);
				
				
				paint.setAlpha(255-12*deadTime);//透明化
				deadTime++;//累計死亡時間
				if(deadTime==40)//死亡後一段時間，消失
				{
					exist=false;
				}
				break;
			}
			case 5://滑落
			{
				paint.setAlpha(255-6*deadTime);//透明化
				deadTime++;//累計死亡時間
				y=y+2;
				if(deadTime==40)//死亡後一段時間，消失
				{
					exist=false;
				}
				break;
			}	
		}		
	}
	public boolean isInrange(float downX,float downY,float range)//判斷觸碰是否在範圍內
	{
		if(downX>realx-range&&downX<realx+realWidth+range&&
				downY>realy-range&&downY<realy+realHeight+range)
		{
			return true;
		}
		return false;
	}
}

