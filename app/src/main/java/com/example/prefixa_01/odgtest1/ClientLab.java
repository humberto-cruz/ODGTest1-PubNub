package com.example.prefixa_01.odgtest1;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Prefixa_01 on 14/01/2016.
 */
public class ClientLab {

    private static ClientLab sClientLab;
    private List<Client> mClients;

    public static ClientLab get(Context context){
        if(sClientLab == null){
            sClientLab = new ClientLab(context);
        }
        return sClientLab;
    }

    private ClientLab(Context context){
        mClients = new ArrayList<>();

        Client myClient = new Client();
        myClient.setmName("h");
        myClient.setmName("h");
        mClients.add(myClient);

        for(int i = 0; i<100; i++){
            Client client = new Client();
            client.setmName("Client #" + i);
            client.setmClientID("+12551234567");
            mClients.add(client);
        }
    }

    public List<Client> getmClients(){return mClients;}

    public Client getClient(UUID id){
        for (Client client:mClients) {
            if (client.getmID().equals(id)){
                return client;
            }
        }
        return null;
    }
}
