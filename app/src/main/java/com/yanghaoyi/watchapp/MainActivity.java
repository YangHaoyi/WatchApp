package com.yanghaoyi.watchapp;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvWakeUp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }


    private void init(){
        initView();
        initEvent();
    }

    private void initView(){
        tvWakeUp = findViewById(R.id.tvWakeUp);
    }

    private void initEvent(){
        tvWakeUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvWakeUp:
                sendService();
                break;
            default:
                break;
        }
    }



    private void startService(){
        Intent intent = new Intent();
        //设置一个组件名称  同组件名来启动所需要启动Service
        intent.setComponent(new ComponentName("com.yanghaoyi.keepservice","com.yanghaoyi.keepservice.MyService"));
        startService(intent);
    }



    public boolean isRun(Context context){
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        boolean isAppRunning = false;
        String MY_PKG_NAME = "com.yanghaoyi.mainapp";
        //100表示取的最大的任务数，info.topActivity表示当前正在运行的Activity，info.baseActivity表系统后台有此进程在运行
        for (ActivityManager.RunningTaskInfo info : list) {
            if (info.topActivity.getPackageName().equals(MY_PKG_NAME) || info.baseActivity.getPackageName().equals(MY_PKG_NAME)) {
                isAppRunning = true;
//                Log.i("ActivityService isRun()",info.topActivity.getPackageName() + " info.baseActivity.getPackageName()="+info.baseActivity.getPackageName());
            }
        }
        System.out.println("ActivityService isRun()___________"+isAppRunning );
//        Log.i("ActivityService isRun()", "com.ad 程序  ...isAppRunning......"+isAppRunning);
        return isAppRunning;
    }

    private void startMapApp(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        ComponentName cn = new ComponentName("com.yanghaoyi.mainapp", "com.yanghaoyi.mainapp.MainActivity");
        intent.setComponent(cn);
        startActivity(intent);
    }



    class CheckMainAppTask extends TimerTask {

        private Context context;
        public CheckMainAppTask(Context context) {
            this.context = context;
        }

        @Override
        public void run() {
//            if(!isRunning(context,"com.yanghaoyi.mainapp")){
//                startMapApp();
//            }
            if(!isRun(context)){
                startMapApp();
            }
            System.out.println("ActivityService timer___________" );
        }
    }

    public static boolean isRunning(Context context, String packageName) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> list = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : list) {
            String processName = appProcess.processName;
            if (processName != null && processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    private void dumpTasks(Context context) {
        ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(20);
        for (ActivityManager.RunningTaskInfo task : tasks) {
            System.out.println("MyTask______________"+task.baseActivity.getPackageName());
        }
    }

    private void sendService() {
        boolean find = false;
        ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        Intent serviceIntent = new Intent();
        for (ActivityManager.RunningServiceInfo runningServiceInfo : mActivityManager.getRunningServices(100)) {
            if (runningServiceInfo.process.contains(":haoyi")) {//判断service是否在运行
                Log.e("YangHaoyi", "process:" + runningServiceInfo.process);
                find = true;
            }
        }
        //判断服务是否起来，如果服务没起来，就唤醒
        if (!find) {
            Toast.makeText(this, "开始唤醒 YibaServcie", Toast.LENGTH_SHORT).show();
            serviceIntent.setPackage("com.yanghaoyi.keepservice");
            serviceIntent.setAction("com.yanghaoyi");
            startService(serviceIntent);
        }else {
            Toast.makeText(this, "YibaServcie 不用唤醒", Toast.LENGTH_SHORT).show();
        }
    }
}

