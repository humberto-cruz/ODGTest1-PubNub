package com.example.prefixa_01.odgtest1;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prefixa_01.odgtest1.util.Constants;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

/**
 * Created by Prefixa_01 on 14/01/2016.
 */
public class ContactDataFragment extends Fragment {
    private static final String ARG_CLIENT_ID="client_id";

    private Client mClient;
    private TextView mNameTextview;
    private TextView mCallerIDTextView;
    private Button mBtnCall;
    private Button mBtnBack;
    private String clientName;

    String username;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //mCrime = new Crime();
        //UUID crimeId = (UUID) getActivity().getIntent().getSerializableExtra(CrimeActivity.EXTRA_CRIME_ID);
        //UUID clientId = (UUID) getArguments().getSerializable(Constants.CALL_USER);


        username = getArguments().getString(Constants.USER_NAME);
        clientName = getArguments().getString(Constants.CALL_USER);
        //mClient = ClientLab.get(getActivity()).getClient(clientId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_startcall,container,false);
        mBtnCall = (Button) v.findViewById(R.id.btn1);
        mBtnBack = (Button) v.findViewById(R.id.btn2);
        mNameTextview = (TextView) v.findViewById(R.id.name);
        mCallerIDTextView = (TextView) v.findViewById(R.id.number);
        mNameTextview.setText(clientName);
        mCallerIDTextView.setText(clientName);
        mBtnCall.setText("Call");
        mBtnBack.setText("Back");
        mBtnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchCall(clientName);
                //Toast.makeText(getActivity(), "" + mClient.getmName(), Toast.LENGTH_SHORT).show();

            }

        });

        mBtnCall.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    v.setBackgroundColor(Color.rgb(255, 165, 0));
                } else {
                    v.setBackgroundColor(Color.rgb(255, 255, 255));
                }
            }
        });

        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
            }
        });

        return v;
    }


    /**TODO: Debate who calls who. Should one be on standby? Or use State API for busy/available
     * Check that user is online. If they are, dispatch the call by publishing to their standby
     *   channel. If the publish was successful, then replace fragment by CallFragment.
     * The called user will then have the option to accept of decline the call. If they accept,
     *   they will be brought to CallFragment as well, to connect video/audio. If
     *   they decline, a hangup will be issued, and the VideoChat adapter's onHangup callback will
     *   be invoked.
     * @param callNum Number to publish a call to.
     */
    public void dispatchCall(final String callNum){
        final String callNumStdBy = callNum + Constants.STDBY_SUFFIX;
        MainActivity.mPubNub.hereNow(callNumStdBy, new Callback() {
            @Override
            public void successCallback(String channel, Object message) {
                Log.d("MA-dC", "HERE_NOW: " + " CH - " + callNumStdBy + " " + message.toString());
                try {
                    int occupancy = ((JSONObject) message).getInt(Constants.JSON_OCCUPANCY);
                    if (occupancy == 0) {
                        showToast("User is not online!");
                        return;
                    }
                    JSONObject jsonCall = new JSONObject();
                    jsonCall.put(Constants.JSON_CALL_USER, username);
                    jsonCall.put(Constants.JSON_CALL_TIME, System.currentTimeMillis());
                    MainActivity.mPubNub.publish(callNumStdBy, jsonCall, new Callback() {
                        @Override
                        public void successCallback(String channel, Object message) {
                            Log.d("MA-dC", "SUCCESS: " + message.toString());

                            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.fragment, CallFragment.newInstance(username, clientName));
                            transaction.addToBackStack(null);
                            transaction.commit();


                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Ensures that toast is run on the UI thread.
     * @param message
     */
    private void showToast(final String message){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static ContactDataFragment newInstance(String localUser, String callUser){
        Bundle args = new Bundle();
        args.putString(Constants.USER_NAME, localUser);
        args.putString(Constants.CALL_USER, callUser);
        ContactDataFragment fragment = new ContactDataFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
