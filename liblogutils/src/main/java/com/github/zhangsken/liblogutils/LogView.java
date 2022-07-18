package com.github.zhangsken.liblogutils;
import android.view.View;
import android.content.Context;
import android.util.AttributeSet;
import android.content.res.TypedArray;
import android.util.Log;
import android.os.Handler;
import java.lang.ref.WeakReference;
import android.os.Message;
import android.widget.TextView;
import android.widget.LinearLayout;
import java.util.ArrayList;
import android.widget.ScrollView;
import java.util.Set;
import java.util.Iterator;
import android.content.pm.PackageManager.NameNotFoundException;
import java.io.IOException;
import android.content.pm.PackageManager;
import android.widget.Toast;

public class LogView extends LinearLayout {
    public static final String TAG = LogView.class.getSimpleName();
    ArrayList<String> mlistTAG;
    int mnLevel;
    int mnLastCount;
    ScrollView msvLog;
    TextView mtvMSG;

    RealTimeLoadLogThread mRealTimeLoadLogThread = null;
    LogViewHandler mHandler;

    /**
     * 在java代码里new的时候会用到
     * @param context
     */
    public LogView(Context context) {
        super(context);
    }

    /**
     * 在xml布局文件中使用时自动调用
     * @param context
     */
    public LogView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHandler = new LogViewHandler(this);
        mtvMSG = new TextView(context);
        msvLog = new ScrollView(context);
        msvLog.addView(mtvMSG);
        addView(msvLog);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LogView);
        mlistTAG = new ArrayList<String>();
        String mszTagListString = typedArray.getString(R.styleable.LogView_tag);

        if (mszTagListString.equals("[DEFAULT]")) {
            // 设置app:tag设置为"[DEFAULT]"就添加应用包内默认的基本类
            try {
                Set<String> setString = ClassUtils.getFileNameByPackageName(getContext().getApplicationContext(), getContext().getApplicationContext().getPackageName());
                //Log.d(TAG, "setString.size() is " + Integer.toString(setString.size()));
                Iterator it = setString.iterator();
                while (it.hasNext()) {
                    Object obj = it.next();
                    if (obj instanceof String) {
                        //Log.d(TAG, "obj is " + obj);
                        String szTemp = ((String)obj).replaceAll(".*[\\.]+", "");
                        szTemp = szTemp.replaceAll("\\$+.*", "");
                        //Log.d(TAG, "szTemp is " + szTemp);      
                        if (!checkTAGExist(szTemp)) {
                            mlistTAG.add(szTemp);
                        }

                    }
                }
                String szTAG = "";
                for (int i = 0; i < mlistTAG.size(); i++) {
                    szTAG += mlistTAG.get(i) + " ";
                }
                Toast.makeText(getContext(), "szTAG is " + szTAG, Toast.LENGTH_SHORT).show();
            } catch (PackageManager.NameNotFoundException e) {
                Log.d(TAG, "LogView PackageManager.NameNotFoundException : " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "LogView IOException : " + e.getMessage());
            } catch (InterruptedException e) {
                Log.d(TAG, "LogView InterruptedException : " + e.getMessage());
            }
        } else {
            for (String sz : mszTagListString.split(",")) {
                mlistTAG.add(sz);
            }
        }




        mnLevel = typedArray.getInteger(R.styleable.LogView_level, 0);
        mnLastCount = typedArray.getInteger(R.styleable.LogView_lastcount, 0);
        mtvMSG.setTextColor(typedArray.getColor(R.styleable.LogView_textColor, 0xFF000000));
        mtvMSG.setTextIsSelectable(typedArray.getBoolean(R.styleable.LogView_textIsSelectable, false));
        typedArray.recycle();

        mtvMSG.setBackground(context.getDrawable(R.drawable.blank10x10));
        msvLog.setBackground(context.getDrawable(R.drawable.blank10x10));

        //mtvMSG.setText("Hello, World!");
    }

    // 检查mlistTAG变量中是否包括了szTemp一样的TAG。
    //
    boolean checkTAGExist(String szTemp) {
        for (int i = 0; i < mlistTAG.size(); i++) {
            if (mlistTAG.get(i).equals(szTemp)) {
                return true;
            }

        }
        return false;
    }

    // 设置日志显示等级
    //
    public void setLevel(int n) {
        mnLevel = n;
    }

    /**
     * 不会自动调用，如果有默认style时，在第二个构造函数中调用
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public LogView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    /**
     * 只有在API版本>21时才会用到
     * 不会自动调用，如果有默认style时，在第二个构造函数中调用
     * @param context
     * @param attrs
     * @param defStyleAttr
     * @param defStyleRes
     */
    public LogView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void appenMSG(String sz) {
        mtvMSG.append(sz + "\n");
        scrollTextView();
    }

    public void startLog() {
        if (mRealTimeLoadLogThread == null) { 

            mRealTimeLoadLogThread = new RealTimeLoadLogThread(mHandler, mlistTAG, mnLevel, mnLastCount);
            mRealTimeLoadLogThread.start();
        } else {
            Log.d(TAG, "Start log failed.");
        }

    }

    public void cleanLog() {
        mtvMSG.setText("");
        LogViewCleanLogThread logViewCleanLogThread = new LogViewCleanLogThread(mHandler);
        logViewCleanLogThread.start();

    }

    void scrollTextView() {
        final ScrollView sv = msvLog;
        sv.post(new Runnable() {
                @Override
                public void run() {
                    sv.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
    }


}
