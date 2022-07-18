package com.github.zhangsken.logutils;
 
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.Toast;
import com.github.zhangsken.liblogutils.LogViewFragment;
import android.app.FragmentTransaction;
import com.github.zhangsken.liblogutils.LogView;
import android.util.Log;
import android.widget.LinearLayout;

public class MainActivity extends Activity { 

    LogViewFragment mLogViewFragment;
    
    LogView mLogView;
     
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLogView = findViewById(R.id.activitymainLogView1);
        mLogView.setLevel(2);
        mLogView.setBackground(getDrawable(R.drawable.blank10x10));
        mLogView.startLog();
        
        LinearLayout ll1 = (LinearLayout) findViewById(R.id.activitymainLinearLayout1);
        ll1.setBackground(getDrawable(R.drawable.ic_launcher));
        LinearLayout ll2 = (LinearLayout) findViewById(R.id.activitymainLinearLayout2);
        ll2.setBackground(getDrawable(R.drawable.blank10x10));
        LinearLayout ll3 = (LinearLayout) findViewById(R.id.activitymainLinearLayout3);
        ll3.setBackground(getDrawable(R.drawable.blank10x10));
    }
    
	public void onTestLogViewActivity(View v) {
        if(isInMultiWindowMode()) {
            Intent i = new Intent(MainActivity.this, com.github.zhangsken.liblogutils.LogViewActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        } else {
            Toast.makeText(getApplication(), "Set current window to MultiWindowMode first.", Toast.LENGTH_SHORT).show();
        }
    }

    public void onTestLogViewFragment(View v) {
       /* if(mLogViewFragment  == null) {
            mLogViewFragment = new LogViewFragment();
            FragmentTransaction tx = getFragmentManager().beginTransaction();
            tx.add(R.id.activitymainFrameLayout1, mLogViewFragment, LogViewFragment.TAG);
            tx.commit();
        }*/
        
    }
    
    public void onLog(View v) {
        TestClassA.writeTestTAG();
        Log.v("Test", "tv");
        Log.v("Test2", "tv");
        Log.v("Test3", "tv");
        Log.d("Test", "td");
        Log.d("Test2", "td");
        Log.d("Test3", "td");
        Log.i("Test", "ti");
        Log.i("Test2", "ti");
        Log.i("Test3", "ti");
    }
    
    public void onCleanLog(View v) {
        mLogView.cleanLog();
    }
} 
