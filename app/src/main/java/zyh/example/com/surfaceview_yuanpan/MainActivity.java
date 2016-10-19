package zyh.example.com.surfaceview_yuanpan;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import zyh.example.com.surfaceview_yuanpan.view.YuanpanView;

public class MainActivity extends AppCompatActivity {

    public YuanpanView mYuanpanView;
    public ImageView mImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mYuanpanView=(YuanpanView)findViewById(R.id.pan);
        mImageView=(ImageView)findViewById(R.id.choujiang);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mYuanpanView.isStart()) {
                    mImageView.setImageResource(R.drawable.stop);
                    mYuanpanView.panStart(1);
                }
                else {
                    if (!mYuanpanView.isshouldEnd())
                    {
                        mImageView.setImageResource(R.drawable.start);
                        mYuanpanView.panEnd();
                    }
                }
            }
        });

    }
}
