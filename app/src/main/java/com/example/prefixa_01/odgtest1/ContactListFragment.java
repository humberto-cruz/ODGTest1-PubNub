package com.example.prefixa_01.odgtest1;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prefixa_01.odgtest1.util.Constants;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Prefixa_01 on 14/01/2016.
 */
public class ContactListFragment extends Fragment {

    private RecyclerView mClientsRecyclerView;
    private ClientAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_contact_list, container, false);

        mClientsRecyclerView = (RecyclerView) view.findViewById(R.id.client_recycler_view);
        mClientsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();
        return view;
    }

    public void updateUI(){
        ClientLab clientLab = ClientLab.get(getActivity());
        List<Client> clients =  clientLab.getmClients();

        MainActivity.mPubNub.hereNow(false, true,  new Callback() {
            @Override
            public void successCallback(String channel, Object message) {
                Log.d("MA-dC", "HERE_NOW: " + " User list " + message.toString());
                try {
                    int occupancy = ((JSONObject) message).getInt(Constants.JSON_TOTAL_OCCUPANCY);
                    JSONObject channels = ((JSONObject) message).getJSONObject(Constants.JSON_CHANNELS);
                    for(int i=0; i<channels.length(); i++){

                    }
                    Log.d("json", "TOTAL_OCUPPANCY" + occupancy);
                    JSONObject jsonCall = new JSONObject();
                    /*jsonCall.put(Constants.JSON_CALL_USER, username);
                    jsonCall.put(Constants.JSON_CALL_TIME, System.currentTimeMillis());
                    MainActivity.mPubNub.publish(callNumStdBy, jsonCall, new Callback() {
                        @Override
                        public void successCallback(String channel, Object message) {
                            Log.d("MA-dC", "SUCCESS: " + message.toString());

                            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.fragment, CallFragment.newInstance(username, mClient.getmName()));
                            transaction.addToBackStack(null);
                            transaction.commit();


                        }
                    });*/
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        mAdapter = new ClientAdapter(clients);
        mClientsRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();


    }

    private void showToast(final String message){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class ClientHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnFocusChangeListener{
        private TextView mNameTextView;
        private TextView mCallerIDTextView;
        private Client mClient;

        public void bindClient (Client client){
            mClient = client;
            mNameTextView.setText(mClient.getmName());
            mCallerIDTextView.setText(mClient.getmClientID());

        }

        public ClientHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnFocusChangeListener(this);
            mNameTextView = (TextView)itemView.findViewById(R.id.list_item_client_name_text_view);
            mCallerIDTextView = (TextView)itemView.findViewById(R.id.list_item_client_caller_id_text_view);
        }

        @Override
        public void onClick(View v) {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment, ContactDataFragment.newInstance("odg", mClient.getmID())); //TODO: username is harcoded odg
            transaction.addToBackStack(null);
            transaction.commit();
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                v.setBackgroundColor(Color.rgb(255, 165, 0));
            } else {
                v.setBackgroundColor(Color.rgb(255,255,255));
            }
        }
    }

    public class ClientAdapter extends RecyclerView.Adapter<ClientHolder>{
        private List<Client> mClients;

        public ClientAdapter(List<Client> clients){mClients = clients;}


        @Override
        public ClientHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_client,parent,false);
            return new ClientHolder(view);
        }

        @Override
        public void onBindViewHolder(ClientHolder holder, int position) {
            Client client = mClients.get(position);
            holder.bindClient(client);
        }

        @Override
        public int getItemCount() {
            return mClients.size();
        }
    }

}
