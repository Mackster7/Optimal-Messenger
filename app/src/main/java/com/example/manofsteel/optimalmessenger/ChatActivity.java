package com.example.manofsteel.optimalmessenger;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Html;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.Locale;


/**
 * Created by Man of Steel on 15-Sep-16.
 */
public class ChatActivity extends AppCompatActivity {
    Button send, mic;
    EditText msg;
    ListView messages;
    int institutionID;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    String botName;
    String latestQuery;
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        String titleText = getIntent().getStringExtra("botName");
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>"+titleText+"</font>"));


        send = (Button) findViewById(R.id.button_send);
        msg = (EditText) findViewById(R.id.add_query);
        messages = (ListView) findViewById(R.id.listview_messages);
        mic = (Button) findViewById(R.id.button_mic);
        latestQuery = "";
        arrayAdapter = new ArrayAdapter<String>(ChatActivity.this, android.R.layout.simple_list_item_activated_1);
        messages.setAdapter(arrayAdapter);
        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                latestQuery = msg.getText().toString();
                msg.setText("");
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("query", latestQuery);
                    jsonObject.put("id", institutionID);
                    String jsonData = jsonObject.toString();
                    new Chat().execute(jsonData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle bundle = getIntent().getExtras();
        institutionID = bundle.getInt("id");
        botName = bundle.getString("botName");
    }

    @Override
    protected void onResume() {
        super.onResume();

         // Display the back arrow.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }
    // Back arrow click event to go to the parent Activity
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private class Chat extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String SOAPACTION = Constants.pkg + "/" + Constants.method_sendQuery;

            try {
                SoapObject soapObject = new SoapObject(Constants.pkg, Constants.method_sendQuery);
                soapObject.addProperty("data", params[0]);

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
                envelope.setOutputSoapObject(soapObject);

                HttpTransportSE httpTransportSE = new HttpTransportSE(Constants.URL);
                httpTransportSE.call(SOAPACTION, envelope);

                soapObject = (SoapObject) envelope.bodyIn;
                String result = soapObject.getProperty(0).toString();
                return result;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int error = jsonObject.getInt("error");

                    switch (error) {
                        case 0:
                            String reply = jsonObject.getString("reply");
                            //.makeText(getApplicationContext(),reply ,Toast.LENGTH_SHORT).show();
                            arrayAdapter.add("Me :\n " + latestQuery);
                            arrayAdapter.add(botName + " : \n" + reply);
                            break;
                        case 1:
                            Toast.makeText(getApplicationContext(), "No Class", Toast.LENGTH_SHORT).show();
                            break;
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "JSON Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Soap Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    msg.setText(result.get(0));
                }
                break;
            }

        }
    }
}