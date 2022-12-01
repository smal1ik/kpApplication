package com.example.kpapplication;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;


class JsonThread extends Thread {
    ListWorkers listWorkers;
    boolean connection;
    JsonThread(String name){
        super(name);
    }

    public void run() {
        Gson gson = new Gson();
        try {
            URL workersURL = new URL("https://kptestv1.herokuapp.com/api/workers/allAndroidApp/?format=json");
            InputStream stream = (InputStream) workersURL.getContent();
            this.listWorkers = gson.fromJson(new InputStreamReader(stream), ListWorkers.class);
            connection = true;
        } catch (MalformedURLException e) {
            connection = false;
            e.printStackTrace();
        } catch (IOException e) {
            connection = false;
            e.printStackTrace();
        }
    }

    public ListWorkers getListWorkers(){
        if(connection) {
            return this.listWorkers;
        }else{
            return null;
        }
    }

}
