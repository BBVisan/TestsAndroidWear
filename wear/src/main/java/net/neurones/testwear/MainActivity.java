package net.neurones.testwear;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.lang.Math;

public class MainActivity extends Activity implements SensorEventListener, DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener  {

    private SensorManager sensorManager;
    private GoogleApiClient mGoogleApiClient;

    private long timestamp;
    private long startTimestamp;
    private static final float NS2S = 1.0f / 1000000000.0f;
    private float[] deltaRotationVector;
    private WatchViewStub stub;

    protected boolean inMovement;
    protected int stopMovementCount;

    private TextView acc_val_0;
    private TextView acc_val_1;
    private TextView acc_val_2;

    private TextView line_val_0;
    private TextView line_val_1;
    private TextView line_val_2;

    private TextView gyro_val_0;
    private TextView gyro_val_1;
    private TextView gyro_val_2;
    private TextView gyro_val_3;
    private TextView gyro_val_4;
    private TextView gyro_val_5;
    private TextView gyro_val_6;
    private TextView gyro_val_7;
    private TextView gyro_val_8;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        stub = (WatchViewStub) findViewById(R.id.watch_view_stub);

        this.deltaRotationVector = new float[4];
        this.inMovement          = false;
        this.stopMovementCount  = 0;
        this.startTimestamp      = 0;

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                acc_val_0 = (TextView) findViewById(R.id.acc_val_0);
                acc_val_1 = (TextView) findViewById(R.id.acc_val_1);
                acc_val_2 = (TextView) findViewById(R.id.acc_val_2);

                line_val_0 = (TextView) findViewById(R.id.line_val_0);
                line_val_1 = (TextView) findViewById(R.id.line_val_1);
                line_val_2 = (TextView) findViewById(R.id.line_val_2);

                gyro_val_0 = (TextView) findViewById(R.id.gyro_val_0);
                gyro_val_1 = (TextView) findViewById(R.id.gyro_val_1);
                gyro_val_2 = (TextView) findViewById(R.id.gyro_val_2);
                gyro_val_3 = (TextView) findViewById(R.id.gyro_val_3);
                gyro_val_4 = (TextView) findViewById(R.id.gyro_val_4);
                gyro_val_5 = (TextView) findViewById(R.id.gyro_val_5);
                gyro_val_6 = (TextView) findViewById(R.id.gyro_val_6);
                gyro_val_7 = (TextView) findViewById(R.id.gyro_val_7);
                gyro_val_8 = (TextView) findViewById(R.id.gyro_val_8);
            }
        });

        sensorManager       = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //Log.d("wear", "onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Log.d("wear", "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register this class as a listener for the orientation and
        // accelerometer sensors
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_NORMAL);
        mGoogleApiClient.connect();
        //Log.d("wear", "onResume");
    }
    @Override
    protected void onPause() {
        // unregister listener
        super.onPause();
        sensorManager.unregisterListener(this);
        //Log.d("wear", "onPause");
    }


    private void displayAccelerometerValues(SensorEvent event) {
        // In this example, alpha is calculated as t / (t + dT),
        // where t is the low-pass filter's time-constant and
        // dT is the event delivery rate.

        final float alpha = 0.8f;

        float[] gravity = new float[3];

        // Isolate the force of gravity with the low-pass filter.
        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0]; //X
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1]; //Y
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2]; //Z

        // Remove the gravity contribution with the high-pass filter.
        //linear_acceleration[0] = event.values[0] - gravity[0];
        //linear_acceleration[1] = event.values[1] - gravity[1];
        //linear_acceleration[2] = event.values[2] - gravity[2];

        if (acc_val_0 != null) {
            if (this.inMovement) {
                //Float.toString(gravity[0]), Float.toString(gravity[1]), Float.toString(gravity[2]),
                new SendDataAsync().execute("acc", String.valueOf(event.timestamp - this.startTimestamp), Float.toString(gravity[0]), Float.toString(gravity[1]), Float.toString(gravity[2]));
            }

            acc_val_0.setText(Float.toString(gravity[0]));
            acc_val_1.setText(Float.toString(gravity[1]));
            acc_val_2.setText(Float.toString(gravity[2]));

            //line_val_0.setText(Float.toString(linear_acceleration[0]));
            //line_val_1.setText(Float.toString(linear_acceleration[1]));
            //line_val_2.setText(Float.toString(linear_acceleration[2]));
        }
    }



    private void displayLinearAccelerationValues(SensorEvent event) {
        // In this example, alpha is calculated as t / (t + dT),
        // where t is the low-pass filter's time-constant and
        // dT is the event delivery rate.

        // Remove the gravity contribution with the high-pass filter.

        if (line_val_0 != null) {
            if (this.inMovement) {
                //Float.toString(gravity[0]), Float.toString(gravity[1]), Float.toString(gravity[2]),
                new SendDataAsync().execute("acc", String.valueOf(event.timestamp - this.startTimestamp), Float.toString(event.values[0]), Float.toString(event.values[1]), Float.toString(event.values[2]));
            }

            line_val_0.setText(Float.toString(event.values[0]));
            line_val_1.setText(Float.toString(event.values[1]));
            line_val_2.setText(Float.toString(event.values[2]));
        }
    }

    private void displayMagneticFieldValues(SensorEvent event) {

        if (acc_val_0 != null) {
            if (this.inMovement) {
                //Float.toString(gravity[0]), Float.toString(gravity[1]), Float.toString(gravity[2]),
                new SendDataAsync().execute("magn", String.valueOf(event.timestamp - this.startTimestamp), Float.toString(event.values[0]), Float.toString(event.values[1]), Float.toString(event.values[2]));
            }

            acc_val_0.setText(Float.toString(event.values[0]));
            acc_val_1.setText(Float.toString(event.values[1]));
            acc_val_2.setText(Float.toString(event.values[2]));
        }
    }

    private void displayGyroscopeValues(SensorEvent event) {
        // This timestep's delta rotation to be multiplied by the current rotation
        // after computing it from the gyro sample data.
        if (timestamp != 0) {
            final float dT = (event.timestamp - timestamp) * NS2S;
            // Axis of the rotation sample, not normalized yet.
            float axisX = event.values[0];
            float axisY = event.values[1];
            float axisZ = event.values[2];

            // Calculate the angular speed of the sample
            float omegaMagnitude = (float) Math.sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ);

            // Normalize the rotation vector if it's big enough to get the axis
            // (that is, EPSILON should represent your maximum allowable margin of error)
            /*
            if (omegaMagnitude > EPSILON) {
                axisX /= omegaMagnitude;
                axisY /= omegaMagnitude;
                axisZ /= omegaMagnitude;
            }*/

            // Integrate around this axis with the angular speed by the timestep
            // in order to get a delta rotation from this sample over the timestep
            // We will convert this axis-angle representation of the delta rotation
            // into a quaternion before turning it into the rotation matrix.
            float thetaOverTwo = omegaMagnitude * dT / 2.0f;
            float sinThetaOverTwo = (float) Math.sin(thetaOverTwo);
            float cosThetaOverTwo = (float) Math.cos(thetaOverTwo);
            deltaRotationVector[0] = sinThetaOverTwo * axisX;
            deltaRotationVector[1] = sinThetaOverTwo * axisY;
            deltaRotationVector[2] = sinThetaOverTwo * axisZ;
            deltaRotationVector[3] = cosThetaOverTwo;
        }
        timestamp = event.timestamp;
        float[] deltaRotationMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);
        // User code should concatenate the delta rotation we computed with the current rotation
        // in order to get the updated rotation.
        // rotationCurrent = rotationCurrent * deltaRotationMatrix;

        if (deltaRotationMatrix[0] == 1.0f && deltaRotationMatrix[4] == 1.0f && deltaRotationMatrix[8] == 1.0f) {
            if (this.stopMovementCount >= 10) {
                this.inMovement = false;
                this.startTimestamp = 0;
            } else
                this.stopMovementCount++;

        } else if (startTimestamp == 0 && !this.inMovement) {
            this.inMovement        = true;
            this.startTimestamp    = event.timestamp;
        }

        if (this.inMovement) {
            this.stopMovementCount = 0;
        }

        if (gyro_val_0 != null) {

            float[] orientation = new float[3]; // 0:azimuth, 1:pitch, 2:roll
            SensorManager.getOrientation(deltaRotationMatrix, orientation);

            if (this.inMovement) {
                new SendDataAsync().execute("gyro", String.valueOf(event.timestamp - this.startTimestamp), Float.toString(deltaRotationMatrix[0]), Float.toString(deltaRotationMatrix[1]), Float.toString(deltaRotationMatrix[2]), Float.toString(deltaRotationMatrix[3]), Float.toString(deltaRotationMatrix[4]), Float.toString(deltaRotationMatrix[5]), Float.toString(deltaRotationMatrix[6]), Float.toString(deltaRotationMatrix[7]), Float.toString(deltaRotationMatrix[8]));
                new SendDataAsync().execute("ori", String.valueOf(event.timestamp - this.startTimestamp), Float.toString(orientation[0]), Float.toString(orientation[1]), Float.toString(orientation[2]));
            }

            acc_val_0.setText(Float.toString(orientation[0]));
            acc_val_1.setText(Float.toString(orientation[1]));
            acc_val_2.setText(Float.toString(orientation[2]));

            gyro_val_0.setText(Float.toString(deltaRotationMatrix[0]));
            gyro_val_1.setText(Float.toString(deltaRotationMatrix[1]));
            gyro_val_2.setText(Float.toString(deltaRotationMatrix[2]));
            gyro_val_3.setText(Float.toString(deltaRotationMatrix[3]));
            gyro_val_4.setText(Float.toString(deltaRotationMatrix[4]));
            gyro_val_5.setText(Float.toString(deltaRotationMatrix[5]));
            gyro_val_6.setText(Float.toString(deltaRotationMatrix[6]));
            gyro_val_7.setText(Float.toString(deltaRotationMatrix[7]));
            gyro_val_8.setText(Float.toString(deltaRotationMatrix[8]));
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (false && event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            displayAccelerometerValues(event);
        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            displayGyroscopeValues(event);
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            displayMagneticFieldValues(event);
        } else if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            displayLinearAccelerationValues(event);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //Log.d("wear", "onAccuracyChanged");
    }

    @Override
    public void onConnected(Bundle bundle) {
        //Log.d("wear", "onConnected");

    }

    @Override
    public void onConnectionSuspended(int i) {
        //Log.d("wear", "onConnectionSuspended");

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        //Log.d("wear", "onDataChanged");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //Log.d("wear", "onConnectionFailed " + connectionResult.toString());
    }

    private class SendDataAsync extends AsyncTask<String, Integer, Float> {
        protected Float doInBackground(String... values) {
            String capteur = values[0];
            PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/" + capteur);
            putDataMapReq.getDataMap().putLong(capteur + "_val_timestamps", Long.valueOf(values[1]));

            for( int i=2; i<values.length; i++) {
                putDataMapReq.getDataMap().putFloat(capteur + "_val_" + (i-2), Float.parseFloat(values[i]));
            }
            PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
            Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq);

            return 0.0f;
        }

        protected void onProgressUpdate(Integer... progress) {
        }


        protected void onPostExecute(Float taux) {
        }

    }
}
