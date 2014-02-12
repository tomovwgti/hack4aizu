
package com.xively.android.consumer;

import java.util.ArrayList;
import java.util.List;

import net.arnx.jsonic.JSON;

import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xively.android.service.IHttpService;
import com.xively.android.service.Response;
import com.xively.pojo.DataPoints;
import com.xively.pojo.Datapoint;
import com.xively.pojo.Feed;

/**
 * A primitive activity to demo simple remote client connection to the Xively's
 * AIDL Service service.
 * 
 * @author s0pau
 */
public class DemoActivity extends Activity {
    private static final String TAG = DemoActivity.class.getSimpleName();
    IHttpService service;
    HttpServiceConnection connection;

    private TextView mResultField;
    private ListView mListView;
    private ArrayList<String> mListString;

    // API-Key
    private final String myApiKey = "API-Key";
    // Feed ID
    private final int myFeedId = 1495248008;

    // ------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        initService();
        mResultField = (TextView) findViewById(R.id.result);
        mListView = (ListView) findViewById(R.id.listView1);
        mListView.setVisibility(View.INVISIBLE);
        mListString = new ArrayList<String>();

        // Setup the UI
        Button button1 = (Button) findViewById(R.id.doService1);
        button1.setOnClickListener(new OnClickListener() {

            // 最新の情報を取得
            public void onClick(View v) {
                Response response = null;
                try {
                    service.setApiKey(myApiKey);
                    response = service.getFeed(myFeedId);
                } catch (RemoteException e) {
                    Log.e(DemoActivity.TAG, "onClick failed", e);
                }
                if (response != null) {
                    Feed feed = JSON.decode(response.getContent(), Feed.class);

                    mListString.clear();
                    mListString.add("DATE/UTC    : " + feed.getUpdated());
                    mListString.add("HUMIDITY    : "
                            + feed.getDatastreams().get(0).getCurrent_value());
                    mListString.add("ILLUMINATION: "
                            + feed.getDatastreams().get(1).getCurrent_value());
                    mListString.add("MOTION      : "
                            + feed.getDatastreams().get(2).getCurrent_value());
                    mListString.add("PRESSURE    : "
                            + feed.getDatastreams().get(3).getCurrent_value());
                    mListString.add("SOUND       : "
                            + feed.getDatastreams().get(4).getCurrent_value());
                    mListString.add("TEMPERATURE : "
                            + feed.getDatastreams().get(5).getCurrent_value());
                    mListString.add("LOCATION    : " + feed.getLocation().getLat() + ", "
                            + feed.getLocation().getLon());
                    mListString.add("DATE/JST    : " + feed.getUpdateLocal().toString());

                    Log.d(TAG, "DATE/UTC    : " + feed.getUpdated());
                    Log.d(TAG, "HUMIDITY    : " + feed.getDatastreams().get(0).getCurrent_value());
                    Log.d(TAG, "ILLUMINATION: " + feed.getDatastreams().get(1).getCurrent_value());
                    Log.d(TAG, "MOTION      : " + feed.getDatastreams().get(2).getCurrent_value());
                    Log.d(TAG, "PRESSURE    : " + feed.getDatastreams().get(3).getCurrent_value());
                    Log.d(TAG, "SOUND       : " + feed.getDatastreams().get(4).getCurrent_value());
                    Log.d(TAG, "TEMPERATURE : " + feed.getDatastreams().get(5).getCurrent_value());
                    Log.d(TAG, "LOCATION    : " + feed.getLocation().getLat() + ", "
                            + feed.getLocation().getLon());
                    Log.d(TAG, "DATE/JST    : " + feed.getUpdateLocal().toString());
                    mResultField.setVisibility(View.GONE);
                    mResultField.setText(response.getContent());

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(DemoActivity.this,
                            android.R.layout.simple_list_item_1, mListString);
                    mListView.setAdapter(adapter);
                    mListView.setVisibility(View.VISIBLE);
                }
            }
        });

        Button button2 = (Button) findViewById(R.id.doService2);
        button2.setOnClickListener(new OnClickListener() {

            // 指定したIDの過去の情報を取得（この例は"温度"）
            public void onClick(View v) {
                Response response = null;

                try {
                    service.setApiKey(myApiKey);
                    List parameters = new ArrayList();
                    parameters.add(new BasicNameValuePair("duration", "6hours"));
                    parameters.add(new BasicNameValuePair("interval", "0"));
                    String query = URLEncodedUtils.format(parameters, "UTF-8");
                    response = service.getDatapoints(myFeedId, "Temperature", query);
                } catch (RemoteException e) {
                    Log.e(DemoActivity.TAG, "onClick failed", e);
                }

                if (response != null) {
                    DataPoints points = JSON.decode(response.getContent(), DataPoints.class);
                    mListString.clear();
                    for (Datapoint point : points.getDatapoints()) {
                        mListString.add("DATE/TIME: " + point.getAtLoacl().toString() + ", VALUE: "
                                + point.getValue());
                        Log.i(TAG, "DATE/TIME: " + point.getAtLoacl().toString() + ", VALUE: "
                                + point.getValue());
                    }
                    mResultField.setVisibility(View.GONE);
                    mResultField.setText(response.getContent());
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(DemoActivity.this,
                            android.R.layout.simple_list_item_1, mListString);
                    mListView.setAdapter(adapter);
                    mListView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseService();
    }

    /**
     * Binds this activity to the service.
     */
    private void initService() {
        connection = new HttpServiceConnection();
        Intent i = new Intent("com.xively.android.service.HttpService");
        boolean ret = bindService(i, connection, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "initService() bound with " + ret);
    }

    /**
     * Unbinds this activity from the service.
     */
    private void releaseService() {
        unbindService(connection);
        connection = null;
        Log.d(TAG, "releaseService() unbound.");
    }

    class HttpServiceConnection implements ServiceConnection {
        public void onServiceConnected(ComponentName name, IBinder boundService) {
            service = IHttpService.Stub.asInterface((IBinder) boundService);
            Log.d(DemoActivity.TAG, "onServiceConnected() connected");
            Toast.makeText(DemoActivity.this, "Service connected", Toast.LENGTH_LONG).show();
        }

        public void onServiceDisconnected(ComponentName name) {
            service = null;
            Log.d(DemoActivity.TAG, "onServiceDisconnected() disconnected");
            Toast.makeText(DemoActivity.this, "Service connected", Toast.LENGTH_LONG).show();
        }
    }
}
