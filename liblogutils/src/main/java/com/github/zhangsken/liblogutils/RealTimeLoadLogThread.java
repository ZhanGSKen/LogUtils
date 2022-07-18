package com.github.zhangsken.liblogutils;

import android.util.Log;
import android.os.Message;
import java.lang.ref.WeakReference;
import android.os.Handler;
import java.io.BufferedReader;
import android.os.Looper;
import java.io.IOException;
import com.github.zhangsken.liblogutils.LogViewHandler;
import java.text.SimpleDateFormat;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.apache.commons.lang.StringUtils;

public class RealTimeLoadLogThread extends Thread { // 将Thread声明为static，非静态内部类或者匿名类默认持有外部类对象的引用，容易造成内存泄露
    public final static String TAG = "RealTimeLoadLogThread";
    static String[] szarrayLogViewLevel = {"V", "D", "I", "W", "E","A", "F", "S"};
    ArrayList<String> mszlistTAG;
    int mnLevel;
    int mnLastCount;

    //volatile修饰符用来保证其它线程读取的总是该变量的最新的值
    public volatile boolean exit = false;
    // 延迟显示控制变量,
    // 用来解决数据量过大的问题
    volatile boolean isOpenLogShow = false;
    int nOpenLogShowDelaySecond = 1;

    WeakReference<Handler> handlerWeakReference;
    public RealTimeLoadLogThread(Handler handler, ArrayList<String> szlistTAG, int nLevel, int nLastCount) { // 采用弱引用的方式持有Activity中Handler对象的引用，避免内存泄露
        Log.d(TAG, "call RealTimeLoadLogThread(Handler handler)");
        handlerWeakReference = new WeakReference<Handler>(handler);
        mszlistTAG = szlistTAG;
        mnLevel = nLevel;
        mnLastCount = nLastCount;
    }

    void sendMSG(String sz) {
        Message message1 = Message.obtain();
        message1.what = LogViewHandler.WHAT_APPEN_MSG_TEXT;
        message1.obj = sz;
        Handler handler1 = handlerWeakReference.get();
        if (!exit && handler1 != null) {
            handler1.sendMessage(message1);
        }
    }

    @Override
    public void run() {
        java.lang.Process mLogcatProc = null;
        BufferedReader reader = null;
        try {
            sendMSG("Log time start at : " + getDateNowString());

            //获取logcat日志信息
            //mLogcatProc = Runtime.getRuntime().exec("logcat -T '" + getDateNowString() + "' " + "" + ":D *:S");
            mLogcatProc = Runtime.getRuntime().exec(getLocatString());
            reader = new BufferedReader(new InputStreamReader(mLogcatProc.getInputStream()));
            String szLineNew;
            // 以下代码包函Looper
            Looper.prepare();
            while (!exit && (szLineNew = reader.readLine()) != null) {
                sendMSG(szLineNew);
            }
            Looper.loop();

            throw new RuntimeException("AppLogFragment thread loop unexpectedly exited!");
            // 此行以下代码不再执行到了
            // int i = 0; //不执行此行

        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }

    }


    // 获取当前时间的格式化字符串
    public String getDateNowString() {
        // 日期类转化成字符串类的工具
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss.mmm");
        // 读取当前时间
        long nTimeNow = System.currentTimeMillis();
        return mSimpleDateFormat.format(nTimeNow);
    }
    
    public String getLocatString() {
        String szResult = "logcat -T "+Integer.toString(mnLastCount)+" -v time";
        String szLogLevel_LocatFlag = getLogLevel_LocatFlag(mnLevel);
        //Log.d(TAG, "szLogLevel_LocatFlag is "+szLogLevel_LocatFlag);

        ArrayList<String> arrayListStringTAG = new ArrayList<String>();
        for (int i = 0; i < mszlistTAG.size(); i++) {
            if(!mszlistTAG.get(i).trim().equals("")) {
                arrayListStringTAG.add(mszlistTAG.get(i) + szLogLevel_LocatFlag);
            }
        }
        if (arrayListStringTAG.size() > 0) {
            szResult += " -s " + StringUtils.join(arrayListStringTAG, ",");
        } else if (!szLogLevel_LocatFlag.equals("")) {
            szResult += " -s  *" + szLogLevel_LocatFlag;
        }

        //Log.d(TAG, "szResult is " + szResult);
        //此串正解
        //szResult = "logcat -T 100 -v time -s " + TAG + ":D";
        //szResult = "logcat -v time -s Test:I,LogViewFragment:I";
        //szResult = "logcat -v time -s Test:D";
        //szResult = "logcat -t '2021-12-30 17:10:03.879' -v time -s *:S";
        //szResult = "logcat -v time -s Test:D ";
        //szResult = "logcat -T 100 -v time -s Test:D";
        return szResult;
    }
    
    public String getLogLevel_LocatFlag(int nLevel) {
        return ":" + szarrayLogViewLevel[nLevel];
    }
}
