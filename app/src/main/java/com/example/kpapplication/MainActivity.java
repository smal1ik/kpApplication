package com.example.kpapplication;

import androidx.appcompat.app.AppCompatActivity;


import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.session.PlaybackState;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.content.Context;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private WebView myWebView;
    private LinearLayout linearLayout;
    private ListView listView;
    private SQLiteDatabase db;
    private DBHelper dbHelper;
    private SimpleCursorAdapter adapter;
    private Cursor c;
    private EditText editText;

    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myWebView = findViewById(R.id.webview);
        listView = findViewById(R.id.listview);
        linearLayout = findViewById(R.id.linear);
        editText = findViewById(R.id.editText);
        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();
        boolean check = false;

        if(hasConnection(this)) {
            try {
                check = fillAll();
            } catch (InterruptedException e) {
                Toast.makeText(this, "Пожалуйста, перезапустите приложение и проверьте интернет соединение", Toast.LENGTH_LONG).show();
            }
        }

        if(check){
            myWebView.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.INVISIBLE);

            myWebView.setWebViewClient(new myWebClient());
            myWebView.loadUrl("https://kpfront1.herokuapp.com/");
            WebSettings webSettings = myWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setJavaScriptEnabled(true);
            webSettings.setDomStorageEnabled(true);
            webSettings.setBuiltInZoomControls(true);
            webSettings.setUseWideViewPort(true);
            webSettings.setLoadWithOverviewMode(true);
        }else{
            myWebView.setVisibility(View.INVISIBLE);
            linearLayout.setVisibility(View.VISIBLE);

            c = db.rawQuery("SELECT * FROM " + dbHelper.TABLE_NAME, null);
            int[] view = new int[]{R.id.idworker,R.id.display_name,0,0,0,0,0,0,0, R.id.number_phone};
            String[] fields = c.getColumnNames();
            adapter = new SimpleCursorAdapter(this, R.layout.layout, c, fields, view);


            if(!editText.getText().toString().isEmpty())
                adapter.getFilter().filter(editText.getText().toString());

            // установка слушателя изменения текста
            editText.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) { }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                // при изменении текста выполняем фильтрацию
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    adapter.getFilter().filter(s.toString());
                }
            });

            // установка фильра для поиска
            adapter.setFilterQueryProvider(new FilterQueryProvider() {
                @Override
                public Cursor runQuery(CharSequence constraint) {

                    if (constraint == null || constraint.length() == 0) {

                        return db.rawQuery("select * from " + DBHelper.TABLE_NAME, null);
                    }
                    else {
                        return db.rawQuery("select * from " + DBHelper.TABLE_NAME + " where display_name " +
                                " like ? OR mobile_phone like ? OR inner_phone like ?", new String[]{"%" + constraint.toString() + "%", "%" + constraint.toString() + "%", "%" + constraint.toString() + "%"});
                    }
                }
            });

            // установка слушателя нажатия
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Log.d("mytag", String.valueOf(i));
                    Cursor cursor = db.rawQuery("SELECT * FROM " + dbHelper.TABLE_NAME + " WHERE _id = " + l, null);
                    cursor.moveToFirst();
                    String display_name = cursor.getString(1);
                    String departament = cursor.getString(5);
                    String mail = cursor.getString(6);
                    String inner_phone = cursor.getString(7);
                    String mobile_phone = cursor.getString(9);
                    String post = cursor.getString(10);
                    String room = cursor.getString(11);

                    CustomDialogFragment dialog = new CustomDialogFragment(display_name, departament, mail, inner_phone, mobile_phone, room, post);
                    dialog.show(getSupportFragmentManager(), "custom");

                }
            });
            listView.setAdapter(adapter);


            Toast.makeText(this,"Интернета нет", Toast.LENGTH_LONG).show();
        }


    }

    public static boolean hasConnection(final Context context)
    {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        return false;
    }

    public class myWebClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon){
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url){
            view.loadUrl(url);
            return true;
        }

        public void onBackPressed(){
            if(myWebView.canGoBack()){
                myWebView.goBack();
            }
            else{
                this.onBackPressed();
            }
        }

    }

    public boolean fillAll() throws InterruptedException {
        JsonThread jsonThread = new JsonThread("JSON Thread");
        jsonThread.start();
        jsonThread.join();
        if (jsonThread.getListWorkers() != null) {
            removeAll();
            for (int i = 0; i < jsonThread.getListWorkers().size(); i++) {
                String str = null;
                if(!jsonThread.getListWorkers().get(i).state || !jsonThread.getListWorkers().get(i).isHidden){
                    continue;
                }
                if (jsonThread.getListWorkers().get(i).mobile_phone.length() > 0) {
                    str = TextUtils.join("", jsonThread.getListWorkers().get(i).mobile_phone.split("-|\\s"));
                    str = "+7 (" + str.substring(1, 4) + ") " + str.substring(4, 7) + "-" + str.substring(7, 9) + "-" + str.substring(9);
//                Log.i("MyTag", str + " " + jsonThread.getListWorkers().get(i).display_name);
                }
                db.execSQL("INSERT INTO " + dbHelper.TABLE_NAME + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                        new Object[]
                                {i + 1,
                                        jsonThread.getListWorkers().get(i).display_name,
                                        jsonThread.getListWorkers().get(i).last_name,
                                        jsonThread.getListWorkers().get(i).first_name,
                                        jsonThread.getListWorkers().get(i).middle_name,
                                        jsonThread.getListWorkers().get(i).department,
                                        jsonThread.getListWorkers().get(i).mail,
                                        jsonThread.getListWorkers().get(i).inner_phone,
                                        jsonThread.getListWorkers().get(i).outer_phone,
                                        str,
                                        jsonThread.getListWorkers().get(i).post,
                                        jsonThread.getListWorkers().get(i).room,
                                        jsonThread.getListWorkers().get(i).birth_date,
                                        jsonThread.getListWorkers().get(i).account_name});
            }
            return true;
        }
        return false;
//        Log.i("API", String.valueOf(jsonThread.getListWorkers().get(0).first_name));
    }

    public void removeAll()
    {
        db.delete(dbHelper.TABLE_NAME, null, null);
    }

}