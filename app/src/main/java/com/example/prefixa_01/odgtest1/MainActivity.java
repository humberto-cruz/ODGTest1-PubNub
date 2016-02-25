package com.example.prefixa_01.odgtest1;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prefixa_01.odgtest1.util.Constants;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;

public class MainActivity extends FragmentActivity{

    private String username;
    private String stdByChannel;
    public static Pubnub mPubNub;
    private LayoutInflater inflater;
    private View layout;
    private Toast toast;

    @Override
    public void onStop() {
        super.onStop();
        if(this.mPubNub!=null){
            this.mPubNub.unsubscribeAll();
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();
        if(this.mPubNub==null){
            initPubNub();
        } else {
            subscribeStdBy();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        //chat message toast
        inflater = getLayoutInflater();
        layout = inflater.inflate(R.layout.chat_message,
                (ViewGroup) findViewById(R.id.chat_message));
        toast = new Toast(getApplicationContext());


        //set username, channel and initiate Pubnub
        this.username = "odg";
        this.stdByChannel = this.username + Constants.STDBY_SUFFIX;



        //TODO: Obtain Canvas in MainActivity
        initPubNub();

        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        MainActivityFragment myFragment = new MainActivityFragment();
        ft.add(R.id.fragment, myFragment);
        ft.commit();


    }

    /**
     * Subscribe to standby channel so that it doesn't interfere with the WebRTC Signaling.
     */
    public void initPubNub(){
        this.mPubNub  = new Pubnub(Constants.PUB_KEY, Constants.SUB_KEY);
        this.mPubNub.setUUID(this.username + Constants.ANDROID_SUFFIX);
        subscribeStdBy();
    }

    /**
     * Subscribe to standby channel
     */
    private void subscribeStdBy(){
        try {
            //user subscribes to a global channel for detecting if he is online or offline
            this.mPubNub.subscribe(Constants.GLOBAL_CHANNEL, new Callback() {
                @Override
                public void connectCallback(String channel, Object message) {
                    Log.d("MA-iPN", "CONNECTED: " + message.toString());
                }

                @Override
                public void errorCallback(String channel, PubnubError error) {
                    Log.d("MA-iPN", "ERROR: " + error.toString());
                }
            });

            this.mPubNub.setHeartbeat(3600);

            

            //user subscribes to a stdbyChannel to receive chat messages
            this.mPubNub.subscribe(this.stdByChannel, new Callback() {
                @Override
                public void successCallback(String channel, Object message) {
                    Log.d("MA-iPN", "MESSAGE: " + message.toString());

                    if (!(message instanceof JSONObject)) return; // Ignore if not JSONObject
                    final JSONObject jsonMsg = (JSONObject) message;
                    try {

                        if (!jsonMsg.has(Constants.JSON_CMD)) return;
                        String cmd = jsonMsg.getString(Constants.JSON_CMD);


                        //Chat message
                        if(cmd.equals("call")){
                            //incomingCall
                            //if (!jsonMsg.has(Constants.JSON_CALL_USER))
                            //    return;     //Ignore Signaling messages.
                            //String user = jsonMsg.getString(Constants.JSON_CALL_USER);
                            String from = jsonMsg.getString(Constants.JSON_FROM);
                            dispatchIncomingCall(from);
                        }
                        else if(cmd.equals("chat")){
                            final String chat = jsonMsg.getString(Constants.JSON_CHAT);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    TextView text = (TextView) layout.findViewById(R.id.text);
                                    text.setText(chat.split("^\\$")[0]);
                                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                                    toast.setDuration(Toast.LENGTH_LONG);
                                    toast.setView(layout);
                                    toast.show();

                                    //Toast.makeText(MainActivity.this, chat.toString(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else if(cmd.equals("draw")){
                            Log.d("MA-iPN", "DRAW: ");
                            if(CallFragment.getCanvas() == null) return;
                            Rectangle rectangle = Rectangle.get(MainActivity.this);
                            JSONArray plots = jsonMsg.getJSONArray(Constants.JSON_PLOTS);

                            Log.d("MA-iPN", "json: " + plots.toString());

                            JSONObject origin = plots.getJSONObject(0);
                            JSONObject window = plots.getJSONObject(1);
                            JSONObject end = plots.getJSONObject(2);

                            float  yIni = (float) origin.getDouble(Constants.JSON_Y);
                            float xIni = (float) origin.getDouble(Constants.JSON_X);
                            float windowHeigth = (float) window.getDouble(Constants.JSON_Y);
                            float windowWidth = (float) window.getDouble(Constants.JSON_X);
                            float  yEnd = (float) end.getDouble(Constants.JSON_Y);
                            float xEnd = (float) end.getDouble(Constants.JSON_X);

                            if(xIni <= xEnd){
                                rectangle.setXini((xIni / windowWidth) * CallFragment.getCanvas().getWidth());
                                rectangle.setXend((xEnd / windowWidth) * CallFragment.getCanvas().getWidth());
                            }
                            else{
                                rectangle.setXini((xEnd / windowWidth) * CallFragment.getCanvas().getWidth());
                                rectangle.setXend((xIni / windowWidth) * CallFragment.getCanvas().getWidth());
                            }
                            if(yIni <= yEnd){
                                rectangle.setYini((yIni / windowHeigth) * CallFragment.getCanvas().getHeight());
                                rectangle.setYend((yEnd / windowHeigth) * CallFragment.getCanvas().getHeight());
                            }
                            else{
                                rectangle.setYini((yEnd / windowHeigth) * CallFragment.getCanvas().getHeight());
                                rectangle.setYend((yIni / windowHeigth) * CallFragment.getCanvas().getHeight());
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    CallFragment.getCanvas().invalidate();
                                }
                            });


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void connectCallback(String channel, Object message) {
                    Log.d("MA-iPN", "CONNECTED: " + message.toString());
                    setUserStatus(Constants.STATUS_AVAILABLE, stdByChannel);
                }

                @Override
                public void errorCallback(String channel, PubnubError error) {
                    Log.d("MA-iPN", "ERROR: " + error.toString());
                }
            });
        } catch (PubnubException e){
            Log.d("HERE","HEREEEE");
            e.printStackTrace();
        }
    }

    /**
     * Handle incoming calls. TODO: Implement an accept/reject functionality.
     * @param remoteUsername
     */
    private void dispatchIncomingCall(String remoteUsername){

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment, ReceiveCallFragment.newInstance(username, remoteUsername));
        transaction.addToBackStack(null);
        transaction.commit();

    }

    private void setUserStatus(String status, String channel){
        try {
            JSONObject state = new JSONObject();
            state.put(Constants.JSON_STATUS, status);
            this.mPubNub.setState(channel, this.username, state, new Callback() {
                @Override
                public void successCallback(String channel, Object message) {
                    Log.d("MA-sUS", "State Set: " + message.toString());
                }
            });
        } catch (JSONException e){
            e.printStackTrace();
        }
    }



}
