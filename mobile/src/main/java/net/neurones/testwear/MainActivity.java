package net.neurones.testwear;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.HashMap;
import java.util.ArrayList;


public class MainActivity extends Activity {
    /** Messenger for communicating with service. */
    Messenger mService = null;
    /** Flag indicating whether we have called bind on the service. */
    boolean mIsBound;
    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    class IncomingHandler extends Handler { // Handler of incoming messages from clients.
        public void handleMessage(Message msg) {
            if (!acquire) return;

            HashMap<String, Object> map = (HashMap) msg.obj;
            long time = (long) map.get("timestamps");
            switch (msg.what) {
                case WearService.MSG_DATA_ACC:

                    //acc_val0s.add(new DataPoint(time, (float) map.get("acc_val_0")));
                    //acc_val1s.add(new DataPoint(time, (float) map.get("acc_val_1")));
                    //acc_val2s.add(new DataPoint(time, (float) map.get("acc_val_2")));
/*
                    acc_val_0.setText(String.valueOf((float) map.get("acc_val_0")));
                    acc_val_1.setText(String.valueOf((float) map.get("acc_val_1")));
                    acc_val_2.setText(String.valueOf((float) map.get("acc_val_2")));
                    */
                    /*break;
                case WearService.MSG_DATA_LINE: */
                    line_val0s.add(new DataPoint(time, (float) map.get("line_val_0")));
                    line_val1s.add(new DataPoint(time, (float) map.get("line_val_1")));
                    line_val2s.add(new DataPoint(time, (float) map.get("line_val_2")));

                    line_val_0.setText(String.valueOf((float) map.get("line_val_0")));
                    line_val_1.setText(String.valueOf((float) map.get("line_val_1")));
                    line_val_2.setText(String.valueOf((float) map.get("line_val_2")));
                    break;
                /*
                case WearService.MSG_DATA_MAGN:
                    acc_val0s.add(new DataPoint(time, (float) map.get("magn_val_0")));
                    acc_val1s.add(new DataPoint(time, (float) map.get("magn_val_1")));
                    acc_val2s.add(new DataPoint(time, (float) map.get("magn_val_2")));

                    acc_val_0.setText(String.valueOf((float) map.get("magn_val_0")));
                    acc_val_1.setText(String.valueOf((float) map.get("magn_val_1")));
                    acc_val_2.setText(String.valueOf((float) map.get("magn_val_2")));
                    break;*/
                case WearService.MSG_DATA_ORI:
                    acc_val0s.add(new DataPoint(time, (float) map.get("ori_val_0")));
                    acc_val1s.add(new DataPoint(time, (float) map.get("ori_val_1")));
                    acc_val2s.add(new DataPoint(time, (float) map.get("ori_val_2")));

                    acc_val_0.setText(String.valueOf((float) map.get("ori_val_0")));
                    acc_val_1.setText(String.valueOf((float) map.get("ori_val_1")));
                    acc_val_2.setText(String.valueOf((float) map.get("ori_val_2")));
                    break;
                case WearService.MSG_DATA_GYRO:
                    gyro_val0s.add(new DataPoint(time, (float) map.get("gyro_val_0")));
                    //gyro_val1s.add(new DataPoint(time, (float) map.get("gyro_val_1")));
                    //gyro_val2s.add(new DataPoint(time, (float) map.get("gyro_val_2")));
                    //gyro_val3s.add(new DataPoint(time, (float) map.get("gyro_val_3")));
                    gyro_val4s.add(new DataPoint(time, (float) map.get("gyro_val_4")));
                    //gyro_val5s.add(new DataPoint(time, (float) map.get("gyro_val_5")));
                    //gyro_val6s.add(new DataPoint(time, (float) map.get("gyro_val_6")));
                    //gyro_val7s.add(new DataPoint(time, (float) map.get("gyro_val_7")));
                    gyro_val8s.add(new DataPoint(time, (float) map.get("gyro_val_8")));

                    gyro_val_0.setText(String.valueOf((float) map.get("gyro_val_0")));
                    //gyro_val_1.setText(String.valueOf((float) map.get("gyro_val_1")));
                    //gyro_val_2.setText(String.valueOf((float) map.get("gyro_val_2")));
                    //gyro_val_3.setText(String.valueOf((float) map.get("gyro_val_3")));
                    gyro_val_4.setText(String.valueOf((float) map.get("gyro_val_4")));
                    //gyro_val_5.setText(String.valueOf((float) map.get("gyro_val_5")));
                    //gyro_val_6.setText(String.valueOf((float) map.get("gyro_val_6")));
                    //gyro_val_7.setText(String.valueOf((float) map.get("gyro_val_7")));
                    gyro_val_8.setText(String.valueOf((float) map.get("gyro_val_8")));
                    break;
                default:
            }
        }
    }

    /**
     * Class for interacting with the main interface of the service.
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  We are communicating with our
            // service through an IDL interface, so get a client-side
            // representation of that from the raw service object.
            mService = new Messenger(service);

            // We want to monitor the service for as long as we are
            // connected to it.
            try {
                Message msg = Message.obtain(null, WearService.MSG_CONNEXION);
                msg.replyTo = mMessenger;
                mService.send(msg);
            } catch (RemoteException e) {
                // In this case the service has crashed before we could even
                // do anything with it; we can count on soon being
                // disconnected (and then reconnected if it can be restarted)
                // so there is no need to do anything here.
            }

            //Log.d("mobile", "onServiceConnected");
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mService = null;
            //Log.d("mobile", "onServiceDisconnected");
        }
    };

    void doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because there is no reason to be able to let other
        // applications replace our component.
        bindService(new Intent(this, WearService.class), mConnection, Context.BIND_AUTO_CREATE);

        mIsBound = true;
        //Log.d("mobile", "doBindService");
    }

    void doUnbindService() {
        if (mIsBound) {
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
        //Log.d("mobile", "doUnbindService");
    }


    private ArrayList<DataPoint> acc_val0s;
    private ArrayList<DataPoint> acc_val1s;
    private ArrayList<DataPoint> acc_val2s;
    private TextView acc_val_0;
    private TextView acc_val_1;
    private TextView acc_val_2;

    private ArrayList<DataPoint> line_val0s;
    private ArrayList<DataPoint> line_val1s;
    private ArrayList<DataPoint> line_val2s;
    private TextView line_val_0;
    private TextView line_val_1;
    private TextView line_val_2;

    private ArrayList<DataPoint> gyro_val0s;
    private ArrayList<DataPoint> gyro_val1s;
    private ArrayList<DataPoint> gyro_val2s;
    private ArrayList<DataPoint> gyro_val3s;
    private ArrayList<DataPoint> gyro_val4s;
    private ArrayList<DataPoint> gyro_val5s;
    private ArrayList<DataPoint> gyro_val6s;
    private ArrayList<DataPoint> gyro_val7s;
    private ArrayList<DataPoint> gyro_val8s;
    private TableLayout tableLayout;
    private LinearLayout linearLayout;
    private TextView gyro_val_0;
    private TextView gyro_val_1;
    private TextView gyro_val_2;
    private TextView gyro_val_3;
    private TextView gyro_val_4;
    private TextView gyro_val_5;
    private TextView gyro_val_6;
    private TextView gyro_val_7;
    private TextView gyro_val_8;
    private GraphView graphAcc;
    private GraphView graphLine;
    private GraphView graphGyro;
    private boolean acquire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tableLayout = (TableLayout) findViewById(R.id.tablelayout);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        acc_val_0  = (TextView) findViewById(R.id.acc_val_0);
        acc_val_1  = (TextView) findViewById(R.id.acc_val_1);
        acc_val_2  = (TextView) findViewById(R.id.acc_val_2);
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
        graphAcc   = (GraphView) findViewById(R.id.graphAcc);
        graphLine   = (GraphView) findViewById(R.id.graphLine);
        graphGyro   = (GraphView) findViewById(R.id.graphGyro);


        TextView graphLegend   = (TextView) findViewById(R.id.graphLegend);
        graphLegend.setText(Html.fromHtml("<bold>Légende : </bold><font color='#FF0000'>X</font>, <font color='#00FF00'>Y</font>, <font color='#0000FF'>Z</font>"));

        graphAcc.setTitle("Gravitée");
        graphLine.setTitle("Acceleration linéaire");
        graphGyro.setTitle("Gyroscope");

        acquire = true;

        final Button stopAcquisition = (Button) findViewById(R.id.stopAcquisition);
        stopAcquisition.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                displayGraph();
            }
        });

        final Button resetText = (Button) findViewById(R.id.resetText);
        resetText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                resetGraph();
            }
        });
        final Button resetGraph = (Button) findViewById(R.id.resetGraph);
        resetGraph.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                resetGraph();
            }
        });

        acc_val0s  = new ArrayList<DataPoint>();
        acc_val1s  = new ArrayList<DataPoint>();
        acc_val2s  = new ArrayList<DataPoint>();
        line_val0s = new ArrayList<DataPoint>();
        line_val1s = new ArrayList<DataPoint>();
        line_val2s = new ArrayList<DataPoint>();
        gyro_val0s = new ArrayList<DataPoint>();
        gyro_val1s = new ArrayList<DataPoint>();
        gyro_val2s = new ArrayList<DataPoint>();
        gyro_val3s = new ArrayList<DataPoint>();
        gyro_val4s = new ArrayList<DataPoint>();
        gyro_val5s = new ArrayList<DataPoint>();
        gyro_val6s = new ArrayList<DataPoint>();
        gyro_val7s = new ArrayList<DataPoint>();
        gyro_val8s = new ArrayList<DataPoint>();
        //Log.d("mobile", "onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Log.d("mobile", "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Log.d("mobile", "onResume");
        doBindService();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Log.d("mobile", "onPause");
        doUnbindService();
    }

    @Override
     public void onDestroy() {
        super.onDestroy();
        //Log.d("mobile", "onDestroy");
        doUnbindService();
    }

    private DataPoint[] arrayListDataPointToArray(ArrayList<DataPoint> array) {
        int size = array.size();
        DataPoint[] res = new DataPoint[size];

        for (int i=0; i<size; i++) {
            res[i] = array.get(i);
        }
        return res;
    }

    private void displayGraph()
    {
        acquire = false;
        tableLayout.setVisibility(View.GONE);
        linearLayout.setVisibility(View.VISIBLE);

        if (acc_val0s.size() > 0) {
            LineGraphSeries<DataPoint> acc_val0 = new LineGraphSeries<DataPoint>(arrayListDataPointToArray(acc_val0s));
            LineGraphSeries<DataPoint> acc_val1 = new LineGraphSeries<DataPoint>(arrayListDataPointToArray(acc_val1s));
            LineGraphSeries<DataPoint> acc_val2 = new LineGraphSeries<DataPoint>(arrayListDataPointToArray(acc_val2s));

            acc_val0.setColor(Color.RED);
            acc_val1.setColor(Color.GREEN);
            acc_val2.setColor(Color.BLUE);

            graphAcc.addSeries(acc_val0);
            //graphAcc.addSeries(acc_val1);
            //graphAcc.addSeries(acc_val2);
        }

        if (line_val0s.size() > 0 ) {
            LineGraphSeries<DataPoint> line_val0 = new LineGraphSeries<DataPoint>(arrayListDataPointToArray(line_val0s));
            LineGraphSeries<DataPoint> line_val1 = new LineGraphSeries<DataPoint>(arrayListDataPointToArray(line_val1s));
            LineGraphSeries<DataPoint> line_val2 = new LineGraphSeries<DataPoint>(arrayListDataPointToArray(line_val2s));

            line_val0.setColor(Color.RED);
            line_val1.setColor(Color.GREEN);
            line_val2.setColor(Color.BLUE);

            graphLine.addSeries(line_val0);
            graphLine.addSeries(line_val1);
            graphLine.addSeries(line_val2);
        }

        if (gyro_val0s.size() > 0) {
            LineGraphSeries<DataPoint> gyro_val0 = new LineGraphSeries<DataPoint>(arrayListDataPointToArray(gyro_val0s));
            //LineGraphSeries<DataPoint> gyro_val1 = new LineGraphSeries<DataPoint>(arrayListDataPointToArray(gyro_val1s));
            //LineGraphSeries<DataPoint> gyro_val2 = new LineGraphSeries<DataPoint>(arrayListDataPointToArray(gyro_val2s));
            //LineGraphSeries<DataPoint> gyro_val3 = new LineGraphSeries<DataPoint>(arrayListDataPointToArray(gyro_val3s));
            LineGraphSeries<DataPoint> gyro_val4 = new LineGraphSeries<DataPoint>(arrayListDataPointToArray(gyro_val4s));
            //LineGraphSeries<DataPoint> gyro_val5 = new LineGraphSeries<DataPoint>(arrayListDataPointToArray(gyro_val5s));
            //LineGraphSeries<DataPoint> gyro_val6 = new LineGraphSeries<DataPoint>(arrayListDataPointToArray(gyro_val6s));
            //LineGraphSeries<DataPoint> gyro_val7 = new LineGraphSeries<DataPoint>(arrayListDataPointToArray(gyro_val6s));
            LineGraphSeries<DataPoint> gyro_val8 = new LineGraphSeries<DataPoint>(arrayListDataPointToArray(gyro_val8s));


            gyro_val0.setColor(Color.RED);
            //gyro_val1.setColor(Color.CYAN);
            //gyro_val2.setColor(Color.BLACK);
            //gyro_val3.setColor(Color.YELLOW);
            gyro_val4.setColor(Color.GREEN);
            //gyro_val5.setColor(Color.GRAY);
            //gyro_val6.setColor(Color.MAGENTA);
            //gyro_val7.setColor(Color.LTGRAY);
            gyro_val8.setColor(Color.BLUE);

            graphGyro.addSeries(gyro_val0);
            //graphGyro.addSeries(gyro_val1);
            //graphGyro.addSeries(gyro_val2);
            //graphGyro.addSeries(gyro_val3);
            graphGyro.addSeries(gyro_val4);
            //graphGyro.addSeries(gyro_val5);
            //graphGyro.addSeries(gyro_val6);
            //graphGyro.addSeries(gyro_val7);
            graphGyro.addSeries(gyro_val8);
        }
    }

    private void resetGraph()
    {
        acquire = true;
        tableLayout.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.INVISIBLE);

        acc_val_0.setText("null");
        acc_val_1.setText("null");
        acc_val_2.setText("null");
        line_val_0.setText("null");
        line_val_1.setText("null");
        line_val_2.setText("null");
        gyro_val_0.setText("null");
        gyro_val_1.setText("null");
        gyro_val_2.setText("null");
        gyro_val_3.setText("null");
        gyro_val_4.setText("null");
        gyro_val_5.setText("null");
        gyro_val_6.setText("null");
        gyro_val_7.setText("null");
        gyro_val_8.setText("null");

        this.acc_val0s.clear();
        this.acc_val1s.clear();
        this.acc_val2s.clear();
        this.line_val0s.clear();
        this.line_val1s.clear();
        this.line_val2s.clear();
        this.gyro_val0s.clear();
        this.gyro_val1s.clear();
        this.gyro_val2s.clear();
        this.gyro_val3s.clear();
        this.gyro_val4s.clear();
        this.gyro_val5s.clear();
        this.gyro_val6s.clear();
        this.gyro_val7s.clear();
        this.gyro_val8s.clear();

        graphAcc.removeAllSeries();
        graphLine.removeAllSeries();
        graphGyro.removeAllSeries();

    }
}
