package zyh.example.com.surfaceview_yuanpan.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import zyh.example.com.surfaceview_yuanpan.R;

/**
 * Created by zyh on 10/18/16.
 */
public class YuanpanView extends SurfaceView  implements SurfaceHolder.Callback,Runnable{
    private SurfaceHolder mSurfaceHolder;
    private Canvas mCanvas;
    private  Thread mThread;
    private  boolean isRunning;
    private  String text[]=new String[]{
        "一号大奖","二号大奖","三号大奖","四号大奖 "
    };
    private int colors[]=new int[]{
            0xFFFFC300,0xfff17e01,0xffffc300,0xfff17e01
    };
    private  int imgs[]=new int[]
            {
                    R.drawable.ipad,R.drawable.iphone,R.drawable.meizi,R.drawable.danfan
            };
    private  Bitmap[] mBitmaps;
    private  int itemcount =4;
    private RectF mRectF=new RectF();
    private  int radius;
    private Paint mPaint_yuanpan;
    private  Paint mTextPaint;
    private  float mSpeed;
    private  volatile  float mStartAngle=45;
    private  boolean isshouldEnd;
    private  int mCenter;
    private  int mPaddind;
    private  Bitmap mBitmap= BitmapFactory.decodeResource(getResources(),R.drawable.bg);
    private  float TextSzie= TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,20,getResources().getDisplayMetrics());





    public YuanpanView(Context context) {
        this(context ,null);
    }

    public YuanpanView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mSurfaceHolder =getHolder();
        mSurfaceHolder.addCallback(this);
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setKeepScreenOn(true);

    }
    protected  void onMeasure(int widthMeasureSpec,int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        int width=Math.min(getMeasuredWidth(),getMeasuredHeight());
        radius=width-getPaddingLeft()-getPaddingRight();
        mPaddind=getPaddingLeft();
        mCenter=width/2;
        Log.d("width",width+"");
        Log.d("getPaddingLeft()",getPaddingLeft()+"");
        setMeasuredDimension(width,width);

    }



    @Override
    public void run() {
        while(isRunning) {
            long start = System.currentTimeMillis();
            draw();
            long end = System.currentTimeMillis();
            try {
                if ((end - start) < 50)
                    Thread.sleep(50 - end + start);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
         mPaint_yuanpan=new Paint();
        mPaint_yuanpan.setAntiAlias(true);
         mPaint_yuanpan.setDither(true);
        mTextPaint=new Paint();
        mTextPaint.setColor(0xffffffff);
        mTextPaint.setTextSize(TextSzie);
        mRectF=new RectF(getPaddingLeft(),getPaddingLeft(),radius+getPaddingLeft(),radius+getPaddingLeft());
        mBitmaps=new Bitmap[itemcount];
        for (int i=0;i<itemcount;i++)
        {
            mBitmaps[i]=BitmapFactory.decodeResource(getResources(),imgs[i]);

        }
        isRunning=true;
        mThread=new Thread(this);
        mThread.start();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
   isRunning=false;
    }
    public  void draw()
    {
        try {
            mCanvas=mSurfaceHolder.lockCanvas();
            if (mCanvas!=null)
            {
                drawBg();
                float tmpAngle=mStartAngle;
                float sweepAnge=(float)(360/itemcount);
                for (int i=0;i<itemcount;i++)
                {
                    mPaint_yuanpan.setColor(colors[i]);
                    mCanvas.drawArc(mRectF,tmpAngle,sweepAnge,true,mPaint_yuanpan);
                    drawText(tmpAngle,sweepAnge,text[i]);
                    drawIcon(tmpAngle,i);
                    tmpAngle=tmpAngle+sweepAnge;

                }
                mStartAngle+=mSpeed;
                if (isshouldEnd)
                {
                    mSpeed-=1;
                }
                if (mSpeed<=0)
                {
                    mSpeed=0;
                    isshouldEnd=false;
                }

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            if (mCanvas!=null)
                mSurfaceHolder.unlockCanvasAndPost(mCanvas);
        }
    }
    public  void drawBg()
    {
        mCanvas.drawColor(0xffffffff);
        mCanvas.drawBitmap(mBitmap,null,new RectF(mPaddind/2,mPaddind/2,getMeasuredWidth()-mPaddind/2,getMeasuredHeight()
        -mPaddind/2),null);

    }
    public  void drawText(float mStartAngle,float sweepAngle,String string)
    {
        Path path=new Path();
        path.addArc(mRectF,mStartAngle,sweepAngle);
        float textWidth=mTextPaint.measureText(string);
        float hoffset=(float)(radius*Math.PI/itemcount/2-textWidth/2);
        float voffset=radius/2/6;
        mCanvas.drawTextOnPath(string,path,hoffset,voffset,mTextPaint);


    }
    public  void drawIcon(float mStartAngle,int i)
    {
        int imgwidth=radius/8;
        float angle=(float) ((mStartAngle+45)*(Math.PI/180));
        int x=(int)(mCenter+radius/4*Math.cos(angle));
        int  y=(int)(mCenter+radius/4*Math.sin(angle));
        RectF rectF=new RectF(x-imgwidth/2,y-imgwidth/2,x+imgwidth/2,y+imgwidth/2);
        mCanvas.drawBitmap(mBitmaps[i],null,rectF,null);

    }
    public  void panStart(int index)
    {
       float angle=(float)(360/itemcount);
        float from =315-(index+1)*angle;
        float to=from+angle;
        float targetfrom =4*360+from;
        float targetto=4*360+to;
        float v1=(float)(Math.sqrt(1+8*targetfrom)-1)/2;
        float v2=(float)(Math.sqrt((1+8*targetto))-1)/2;
        mSpeed=(float)(v1+Math.random()*(v2-v1));
        isshouldEnd=false;

    }
    public  boolean isStart()
    {
        return  mSpeed!=0;
    }
    public  boolean isshouldEnd()
    {
        return  isshouldEnd;
    }
    public  void panEnd(){
        mStartAngle=45;
        isshouldEnd=true;
    }
}
