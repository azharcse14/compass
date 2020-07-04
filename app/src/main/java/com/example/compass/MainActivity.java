package com.example.compass;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements sensorEventListener{

    ImageView image;
    TextView txt;
    int mAzimuth;
    private SensorManager mSensorManager;
    private Sensor mRotationV, mAccelerometer, mMagntometer;

    float[] rMat = new float[9];
    float[] orientation = new float[9];
    private float[] mLastAccelerometer= new  float[3];
    private float[] mLastMagntometer= new  float[3];
    private boolean haveSensor = false, haveSensor2 = false;
    private boolean mLastAccelerometeSet = false;
    private boolean mLastMagntometerSet = false;


    @SuppressLint("ServiceCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(SEARCH_SERVICE);
        image = (ImageView) findViewById(R.id.image);
        txt = (TextView) findViewById(R.id.txt);
        start();
    }

    public void start() {
        if(mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)==null){
            if (mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)==null|| mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)==null){
                noSensorAlert();
            }
            else{
                mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                mMagntometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

                haveSensor = mSensorManager.registerListener((SensorEventListener) this,mAccelerometer,SensorManager.SENSOR_DELAY_UI);
                haveSensor2 = mSensorManager.registerListener((SensorEventListener) this,mMagntometer,SensorManager.SENSOR_DELAY_UI);
            }
        }
        else {
            mRotationV = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            haveSensor = mSensorManager.registerListener((SensorEventListener) this,mRotationV,SensorManager.SENSOR_DELAY_UI);
        }
    }

    public void noSensorAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Your device dosen't support the compass.")
                .setCancelable(false)
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
    }

    public void stop(){
        if (haveSensor && haveSensor2){
            mSensorManager.unregisterListener((SensorEventListener) this,mAccelerometer);
            mSensorManager.unregisterListener((SensorEventListener) this,mMagntometer);
        }
        else {
            if (haveSensor){
                mSensorManager.unregisterListener((SensorEventListener) this,mRotationV);
            }
        }
    }

    @Override
    protected  void onPause(){
        super.onPause();
        stop();
    }

    @Override
    protected  void onResume(){
        super.onResume();
        start();
    }

    @Override
    public void onSensorChanged(SensorEvent event){
        if (event.sensor.getType()==Sensor.TYPE_ROTATION_VECTOR){
            SensorManager.getRotationMatrixFromVector(rMat,event.values);
            mAzimuth = (int)((Math.toDegrees(SensorManager.getOrientation(rMat,orientation)[0])+360)%360);
        }
        if (event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            System.arraycopy(event.values,0,mLastAccelerometer,0,event.values.length);
            mLastAccelerometeSet=true;
        }
        else
            if (event.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD){
                System.arraycopy(event.values,0,mLastMagntometer,0,event.values.length);
                mLastMagntometerSet=true;
            }
            if (mLastMagntometerSet && mLastAccelerometeSet){
                SensorManager.getRotationMatrix(rMat,null, mLastAccelerometer, mLastMagntometer);
                SensorManager.getOrientation(rMat,orientation);
                mAzimuth = (int) ((Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0])+360)%360);
            }

            mAzimuth = Math.round(mAzimuth);
            image.setRotation(-mAzimuth);

            String where = "NO";

            if (mAzimuth >= 350 || mAzimuth <= 10)
                where = "N";
        if (mAzimuth >= 350 || mAzimuth <= 280)
            where = "NW";
        if (mAzimuth >= 280 || mAzimuth <= 260)
            where = "W";
        if (mAzimuth >= 260 || mAzimuth <= 190)
            where = "SW";
        if (mAzimuth >= 190 || mAzimuth <= 170)
            where = "S";
        if (mAzimuth >= 170 || mAzimuth <= 100)
            where = "SE";
        if (mAzimuth >= 100 || mAzimuth <= 80)
            where = "E";
        if (mAzimuth >= 80 || mAzimuth <= 10)
            where = "NE";

        txt.setText(mAzimuth+"' "+where);


    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i){

    }
}
