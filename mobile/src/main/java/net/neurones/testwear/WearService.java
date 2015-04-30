package net.neurones.testwear;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import java.util.HashMap;

/**
 * Created by pyarg on 24/04/15.
 */
public class WearService extends Service implements
        DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;

    static final int MSG_CONNEXION   = -1;
    static final int MSG_DATA_EVENT  = 0;
    static final int MSG_DATA_ACC    = 1;
    static final int MSG_DATA_LINE   = 2;
    static final int MSG_DATA_GYRO   = 3;
    static final int MSG_DATA_MAGN   = 4;
    static final int MSG_DATA_ORI   = 5;

    long sendToViewCount[];

    Messenger mService = null;
    final Messenger mMessenger = new Messenger(new IncomingHandler()); // Target we publish for clients to send messages to IncomingHandler.

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WearService.MSG_CONNEXION:
                    mService = msg.replyTo;
                    break;
                case WearService.MSG_DATA_ACC:
                case WearService.MSG_DATA_LINE:
                case WearService.MSG_DATA_GYRO:
                    break;
                default:
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();

        sendToViewCount = new long[] {0, 0, 0, 0, 0};

        Log.d("service", "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
        Log.d("service", "onDestroy");
    }

    /**
     * When binding to the service, we return an interface to our messenger
     * for sending messages to the service.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Wearable.DataApi.addListener(mGoogleApiClient, this);
        Log.d("service", "onConnected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("service", "onConnectionSuspended");
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        //Log.d("service", "onDataChanged");
        if (mService != null) {
            try {
                for (DataEvent event : dataEvents) {
                    if (event.getType() == DataEvent.TYPE_CHANGED) {
                        DataItem item = event.getDataItem();
                        final DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                        HashMap<String, Object> map = new HashMap<>();

                        if (item.getUri().getPath().compareTo("/acc") == 0) {
                            //map.put("timestamps", dataMap.getLong("acc_val_timestamps"));
                            //map.put("timestamps", dataMap.getLong("acc_val_timestamps"));
                            map.put("timestamps", sendToViewCount[0]++);
                            /*
                            map.put("acc_val_0", dataMap.getFloat("acc_val_0") *10);
                            map.put("acc_val_1", dataMap.getFloat("acc_val_1") *10);
                            map.put("acc_val_2", dataMap.getFloat("acc_val_2") *10);
*/
                            map.put("line_val_0", dataMap.getFloat("line_val_0") *10);
                            map.put("line_val_1", dataMap.getFloat("line_val_1") *10);
                            map.put("line_val_2", dataMap.getFloat("line_val_2") *10);

                            Message msg = Message.obtain(null, WearService.MSG_DATA_ACC, map);
                            msg.replyTo = mMessenger;
                            mService.send(msg);
                        } else if (item.getUri().getPath().compareTo("/ori") == 0) {
                            //map.put("timestamps", dataMap.getLong("acc_val_timestamps"));
                            //map.put("timestamps", dataMap.getLong("acc_val_timestamps"));
                            map.put("timestamps", sendToViewCount[1]++);
                            map.put("ori_val_0", dataMap.getFloat("ori_val_0") * 1000);
                            map.put("ori_val_1", dataMap.getFloat("ori_val_1") * 1000);
                            map.put("ori_val_2", dataMap.getFloat("ori_val_2") * 1000);

                            Message msg = Message.obtain(null, WearService.MSG_DATA_ORI, map);
                            msg.replyTo = mMessenger;
                            mService.send(msg);
                        }
                        /*
                        else if (item.getUri().getPath().compareTo("/line") == 0) {
                            //map.put("timestamps", dataMap.getLong("line_val_timestamps"));
                            map.put("timestamps", sendToViewCount[2]++);
                            map.put("line_val_0", dataMap.getFloat("line_val_0"));
                            map.put("line_val_1", dataMap.getFloat("line_val_1"));
                            map.put("line_val_2", dataMap.getFloat("line_val_2"));

                            Message msg = Message.obtain(null, WearService.MSG_DATA_LINE, map);
                            msg.replyTo = mMessenger;
                            mService.send(msg);
                        }*/
                        else if (item.getUri().getPath().compareTo("/magn") == 0) {
                            //map.put("timestamps", dataMap.getLong("gyro_val_timestamps"));
                            //map.put("timestamps", dataMap.getLong("magn_val_timestamps"));
                            map.put("timestamps", sendToViewCount[3]++);
                            map.put("magn_val_0", dataMap.getFloat("magn_val_0"));
                            map.put("magn_val_1", dataMap.getFloat("magn_val_1"));
                            map.put("magn_val_2", dataMap.getFloat("magn_val_2"));

                            Message msg = Message.obtain(null, WearService.MSG_DATA_MAGN, map);
                            msg.replyTo = mMessenger;
                            mService.send(msg);
                        }
                        else if (item.getUri().getPath().compareTo("/gyro") == 0) {
                            //map.put("timestamps", dataMap.getLong("gyro_val_timestamps"));
                            //map.put("timestamps", dataMap.getLong("gyro_val_timestamps"));
                            map.put("timestamps", sendToViewCount[4]++);
                            map.put("gyro_val_0", (dataMap.getFloat("gyro_val_0") *1000-1000)*100);
                            //map.put("gyro_val_1", dataMap.getFloat("gyro_val_1") *1000);
                            //map.put("gyro_val_2", dataMap.getFloat("gyro_val_2") *1000);
                            //map.put("gyro_val_3", dataMap.getFloat("gyro_val_3") *1000);
                            map.put("gyro_val_4", (dataMap.getFloat("gyro_val_4") *1000-1000)*100);
                            //map.put("gyro_val_5", dataMap.getFloat("gyro_val_5") *1000);
                            //map.put("gyro_val_6", dataMap.getFloat("gyro_val_6") *1000);
                            //map.put("gyro_val_7", dataMap.getFloat("gyro_val_7") *1000);
                            map.put("gyro_val_8", (dataMap.getFloat("gyro_val_8") *1000-1000)*100);

                            Message msg = Message.obtain(null, WearService.MSG_DATA_GYRO, map);
                            msg.replyTo = mMessenger;
                            mService.send(msg);
                        }
                    } else if (event.getType() == DataEvent.TYPE_DELETED) {
                        // DataItem deleted
                    }
                }

            } catch (RemoteException e) {
                // In this case the service has crashed before we could even do anything with it
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("service", "onConnectionFailed " + connectionResult.toString());
    }
}
