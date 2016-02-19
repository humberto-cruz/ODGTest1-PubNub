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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Prefixa_01 on 14/01/2016.
 */
public class ContactListFragment extends Fragment {

    private RecyclerView mClientsRecyclerView;
    private ClientAdapter mAdapter;
    private List<Client> clients = new ArrayList<>();

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        Log.d("log", "onCreate ContactListFragment");
        clients = new ArrayList<>();
        mAdapter = new ClientAdapter(clients);

        try {
            MainActivity.mPubNub.presence(Constants.GLOBAL_CHANNEL, new Callback() {

                @Override
                public void reconnectCallback(String channel, Object message) {
                    System.out.println(message);
                }

                @Override
                public void successCallback(String channel, Object message) {
                    //System.out.println(message);
                    Log.d("presence", "success " + message.toString());
                    JSONObject msg = (JSONObject) message;
                    try {
                        String action = msg.getString(Constants.JSON_ACTION);
                        String uuid = msg.getString(Constants.JSON_UUIID);
                        if (action.equals("join")) {
                            addUser(uuid);
                        }
                        else if(action.equals("leave")){
                            removeUser(uuid);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }

                @Override
                public void connectCallback(String channel, Object message) {
                    //System.out.println(message);
                    Log.d("presence", "connect " + message.toString());
                }
            });
        } catch (PubnubException e) {
            e.printStackTrace();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_contact_list, container, false);


        Log.d("log", "onCreate View ContactListFragment");
        mClientsRecyclerView = (RecyclerView) view.findViewById(R.id.client_recycler_view);
        mClientsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mClientsRecyclerView.setAdapter(mAdapter);

        return view;
    }

    /*public void updateUI() throws PubnubException {
        //ClientLab clientLab = ClientLab.get(getActivity());
        //List<Client> clients =  clientLab.getmClients();
        MainActivity.mPubNub.hereNow(Constants.GLOBAL_CHANNEL, new Callback() {
            @Override
            public void successCallback(String channel, Object message) {
                Log.d("MA-dC", "HERE_NOW: " + " User list " + message.toString());
                try {

                    //clients.clear();
                    JSONArray users = ((JSONObject) message).getJSONArray(Constants.JSON_USERS_ARRAY);
                    for (int i = 0; i < users.length(); i++) {
                        Log.d("json", users.getString(i));
                        if (users.getString(i).matches("^(.)*-web$")) {
                            Client client = new Client();
                            client.setmName(users.getString(i));
                            client.setmClientID(users.getString(i));
                            clients.add(client);
                        }
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            mAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }*/

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
            transaction.replace(R.id.fragment, ContactDataFragment.newInstance("odg", mClient.getmName())); //TODO: username is harcoded odg
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

    public void addUser(String user){
        Client newClient = new Client();
        newClient.setmName(user);
        newClient.setmClientID(user);
        clients.add(newClient);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    public void removeUser(String user){
        for(Client client: clients){
            if(client.getmName().equals(user)){
                clients.remove(client);
                break;
            }
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        });
    }



}
