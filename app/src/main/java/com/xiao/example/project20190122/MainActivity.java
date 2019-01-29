package com.xiao.example.project20190122;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import static com.xiao.example.project20190122.model.RockerView.DirectionMode.DIRECTION_8;

import com.xiao.example.project20190122.model.RockerView;
import com.xiao.example.project20190122.util.CheckCodeUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    final String HOST = "192.168.1.103";
    final int PORT = 4001;

    private RockerView mRockerView;
    private TextView mTvShake;
    private TextView mTvAngle;
    private TextView mTvLevel;
    private TextView mTvModel;
    private TextView mTVCode;
    private Button btnRecord;
    private Button btnSave;
    private TextView mTvMessage;

    List<Double> angleList = new ArrayList<>();
    List<Integer> levelList = new ArrayList<>();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) { //设置横屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        mRockerView = (RockerView) findViewById(R.id.rocker);
        btnRecord = (Button) findViewById(R.id.btn_record2);
        btnSave = (Button) findViewById(R.id.btn_save2);
        mTvShake = (TextView) findViewById(R.id.tv_shake);
        mTVCode = (TextView) findViewById(R.id.tv_code);
        mTvAngle = (TextView) findViewById(R.id.tv_angle);
        mTvLevel = (TextView) findViewById(R.id.tv_level);
       // mTvMessage = (TextView) findViewById(R.id.tv_result);
        mTvModel = (TextView) findViewById(R.id.tv_model);

        mTvModel.setText("当前模式：方向有改变时回调；8个方向");
        // mRockerView.setCallBackMode(RockerView.CallBackMode.CALL_BACK_MODE_STATE_CHANGE);
        mRockerView.setOnShakeListener(DIRECTION_8, new RockerView.OnShakeListener() {
            @Override
            public void onStart() {
               // System.out.println("shake开始");
            }

            @Override
            public void direction(RockerView.Direction direction) {
                if (direction == RockerView.Direction.DIRECTION_CENTER) {
                    mTvShake.setText("当前方向：中心");
                } else if (direction == RockerView.Direction.DIRECTION_DOWN) {
                    mTvShake.setText("当前方向：下");
                } else if (direction == RockerView.Direction.DIRECTION_LEFT) {
                    mTvShake.setText("当前方向：左");
                } else if (direction == RockerView.Direction.DIRECTION_UP) {
                    mTvShake.setText("当前方向：上");
                } else if (direction == RockerView.Direction.DIRECTION_RIGHT) {
                    mTvShake.setText("当前方向：右");
                } else if (direction == RockerView.Direction.DIRECTION_DOWN_LEFT) {
                    mTvShake.setText("当前方向：左下");
                } else if (direction == RockerView.Direction.DIRECTION_DOWN_RIGHT) {
                    mTvShake.setText("当前方向：右下");
                } else if (direction == RockerView.Direction.DIRECTION_UP_LEFT) {
                    mTvShake.setText("当前方向：左上");
                } else if (direction == RockerView.Direction.DIRECTION_UP_RIGHT) {
                    mTvShake.setText("当前方向：右上");
                }
            }

            @Override
            public void onFinish() {
            }
        });
        mRockerView.setOnAngleChangeListener(new RockerView.OnAngleChangeListener() {
            @Override
            public void onStart() {
                //System.out.println("angle开始");
            }

            @Override
            public void angle(double angle) {
                angle= 360-angle;
                mTvAngle.setText("当前角度：" + angle);
                angleList.add(angle);
                if (angleList.size()>2&&levelList.size()>0){
                    if ((angleList.get(angleList.size()-1)-angleList.get(angleList.size()-2))>2){
                        int temp_angle = angleList.get(angleList.size()-1).intValue();
                        if (temp_angle>=2048){
                            temp_angle=2048;
                        }
                        int temp_level = levelList.get(levelList.size()-1);
                        MyAsyncTask task = new MyAsyncTask(mTVCode, CheckCodeUtil.getCommand(temp_level,temp_angle));
                        task.execute();
                    }

                }
            }

            @Override
            public void onFinish() {
              //  System.out.println("angle结束");
                angleList.clear();
                levelList.clear();
                MyAsyncTask2 task2 = new MyAsyncTask2(mTVCode, "$CMM800800*00");
                task2.execute();
            }
        });
        mRockerView.setOnDistanceLevelListener(new RockerView.OnDistanceLevelListener() {
            @Override
            public void onDistanceLevel(int level) {
                mTvLevel.setText("当前距离级别：" + level);
               // System.out.println("距离级别" + level);
                levelList.add(level);
                if (levelList.size()>2){
                    if ((levelList.get(levelList.size()-1)-levelList.get(levelList.size()-2))>5){
                        int temp_angle = angleList.get(angleList.size()-1).intValue();
                        if (temp_angle>=2048){
                            temp_angle=2048;
                        }
                        int temp_level = levelList.get(levelList.size()-1);
                        MyAsyncTask task = new MyAsyncTask(mTVCode, CheckCodeUtil.getCommand(temp_level,temp_angle));
                        task.execute();
                    }
                }
            }
        });

        btnRecord.setOnClickListener(new View.OnClickListener() {
            final String COMMAND = "COR1";

            @Override
            public void onClick(View v) {
                MyAsyncTask task = new MyAsyncTask(mTVCode, COMMAND);
                task.execute();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            final String COMMAND = "COP1";

            @Override
            public void onClick(View v) {
                MyAsyncTask task = new MyAsyncTask(mTVCode, COMMAND);
                task.execute();
            }
        });
    }

    class MyAsyncTask extends AsyncTask<String, String, String> {
        private TextView tvMessage;
        private String commmand;

        public MyAsyncTask() {

        }

        public MyAsyncTask(TextView textView, String commmand) {
            this.tvMessage = textView;
            this.commmand = commmand;
        }

        @Override
        protected String doInBackground(String... strings) {
            DataOutputStream dataOutputStream = null;
            DataInputStream dataInputStream = null;
            try {
                // System.out.println("doInBackground参数为：" + Arrays.toString(strings));
                Socket socket = new Socket(HOST, PORT);
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataInputStream = new DataInputStream(socket.getInputStream());
                System.out.println(commmand);
                dataOutputStream.writeUTF(commmand);
                String message = dataInputStream.readUTF();
                dataOutputStream.flush();
                //System.out.println("服务端第一次返回的信息：" + message);
                return message;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (null != dataOutputStream)
                        dataOutputStream.close();
                    if (null != dataInputStream)
                        dataInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            //System.out.println("开始执行子线程。。。");
        }

        /**
         * 此方法更新UI
         *
         * @param s
         */
        @Override
        protected void onPostExecute(String s) {
            // System.out.println("onPostExecute方法：" + s);
            tvMessage.setText("指令：" + s);
        }
    }

    class MyAsyncTask2 extends AsyncTask<String, String, String> {
        private TextView tvMessage;
        private String commmand;

        public MyAsyncTask2() {

        }

        public MyAsyncTask2(TextView textView, String commmand) {
            this.tvMessage = textView;
            this.commmand = commmand;
        }

        @Override
        protected String doInBackground(String... strings) {
            DataOutputStream dataOutputStream = null;
            DataInputStream dataInputStream = null;
            try {
                // System.out.println("doInBackground参数为：" + Arrays.toString(strings));
                Socket socket = new Socket(HOST, PORT);
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataInputStream = new DataInputStream(socket.getInputStream());
               // System.out.println(commmand);
                dataOutputStream.writeUTF(commmand);
                String message = dataInputStream.readUTF();
                dataOutputStream.flush();
               // System.out.println("服务端第二次返回的信息：" + message);
                return message;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (null != dataOutputStream)
                        dataOutputStream.close();
                    if (null != dataInputStream)
                        dataInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            //System.out.println("开始执行子线程。。。");
        }

        /**
         * 此方法更新UI
         *
         * @param s
         */
        @Override
        protected void onPostExecute(String s) {
            // System.out.println("onPostExecute方法：" + s);
            tvMessage.setText("指令：" + s);
        }
    }
}
