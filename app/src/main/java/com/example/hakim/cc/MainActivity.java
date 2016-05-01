package com.example.hakim.cc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends Activity {
    public int to;
    public int from;
    public String[] val;
    public String s;
    public EditText AmText;
    public Handler handler;

    private GoogleApiClient client2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Spinner s1 = (Spinner) findViewById(R.id.spinner1);
        Spinner s2 = (Spinner) findViewById(R.id.spinner2);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        AmText = (EditText) findViewById(R.id.Amount);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.name, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        val = getResources().getStringArray(R.array.value);
        s1.setAdapter(adapter);
        s2.setAdapter(adapter);
        s1.setOnItemSelectedListener(new spinOne(1));
        s2.setOnItemSelectedListener(new spinOne(2));
        Button b = (Button) findViewById(R.id.button1);
        b.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                TextView t = (TextView) findViewById(R.id.textView4);
                double Am = Double.parseDouble(AmText.getText().toString());

                if (from == to) {
                    Toast.makeText(getApplicationContext(), "Invalid", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        s = getJson("http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.xchange%20where%20pair%20in%20(%22" + val[from] + val[to] + "%22)&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=");
                        JSONObject jObj;
                        jObj = new JSONObject(s);
                        String exResult = jObj.getJSONObject("query").getJSONObject("results").getJSONObject("rate").getString("Rate");
                        Double res = Double.parseDouble(exResult) * Am;
                        t.setText(String.valueOf(res));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (ClientProtocolException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        TextView credit = (TextView) findViewById(R.id.tv1);
        credit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("http://finance.yahoo.com"));
                startActivity(i);
            }

        });
        client2 = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public String getJson(String url) throws ClientProtocolException, IOException {

        StringBuilder build = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        HttpResponse response = client.execute(httpGet);
        HttpEntity entity = response.getEntity();
        InputStream content = entity.getContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(content));
        String con;
        while ((con = reader.readLine()) != null) {
            build.append(con);
        }
        return build.toString();
    }

    @Override
    public void onStart() {
        super.onStart();
        client2.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "CurrencyConverter Page",
                Uri.parse("http://host/path"),
                Uri.parse("android-app://com.example.hakim.cc/http/host/path")
        );
        AppIndex.AppIndexApi.start(client2, viewAction);
    }
    @Override
    public void onStop() {
        super.onStop();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "CurrencyConverter Page",
                Uri.parse("http://host/path"),
                Uri.parse("android-app://com.example.hakim.cc/http/host/path")
        );
        AppIndex.AppIndexApi.end(client2, viewAction);
        client2.disconnect();
    }
    private class spinOne implements OnItemSelectedListener {
        int ide;

        spinOne(int i) {
            ide = i;
        }
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int index, long id) {
            if (ide == 1)
                from = index;
            else if (ide == 2)
                to = index;
        }
        public void onNothingSelected(AdapterView<?> arg0) {
        }

    }
}