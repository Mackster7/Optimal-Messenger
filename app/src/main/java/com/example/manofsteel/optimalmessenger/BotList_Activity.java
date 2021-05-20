package com.example.manofsteel.optimalmessenger;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.Calendar;


public class BotList_Activity extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<Institution> institutions;
    RecyclerViewAdapter adapter;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String titleText ="Optimal Messenger";
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>"+titleText+"</font>"));
        initComponents();
        setEvent();
    }

    @Override
    protected void onStart() {
        super.onStart();
        try{
            SharedPreferences sharedPreferences = getSharedPreferences(Constants.sp_uid,MODE_PRIVATE);

            int userId=sharedPreferences.getInt("uid",0);
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("uid",userId);

            String data=jsonObject.toString();
            new GetInstitutions().execute(data);
        }
        catch(Exception e)
        {

        }
    }

    private void initComponents()
    {
        setContentView(R.layout.botlist);
        progressDialog=new ProgressDialog(this);
        recyclerView=(RecyclerView) findViewById(R.id.recyclerview);
        //recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        institutions=new ArrayList<>();
        adapter=new RecyclerViewAdapter(institutions,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    private void setEvent()
    {

    }

    private class GetInstitutions extends AsyncTask<String,Void,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Getting BotList.....");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String SOAPACTION=Constants.pkg+"/"+Constants.method_getInstitutions;

            try{
                SoapObject soapObject=new SoapObject(Constants.pkg,Constants.method_getInstitutions);

                SoapSerializationEnvelope envelope=new SoapSerializationEnvelope(SoapEnvelope.VER10);
                envelope.setOutputSoapObject(soapObject);

                HttpTransportSE httpTransportSE=new HttpTransportSE(Constants.URL);
                httpTransportSE.call(SOAPACTION,envelope);

                soapObject=(SoapObject)envelope.bodyIn;
                String result=soapObject.getProperty(0).toString();
                return result;
            }
            catch(Exception e)
            {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            if(s!=null)
            {
                try{
                    JSONObject jsonObject=new JSONObject(s);
                    int error=jsonObject.getInt("error");

                    switch(error)
                    {
                        case 0:
                            JSONArray jsonArray=jsonObject.getJSONArray("InstitutionList");
                            updateList(jsonArray);
                            break;
                        case 1:
                            Toast.makeText(getApplicationContext(),"No Bots to display",Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
                catch(Exception e)
                {
                    Toast.makeText(getApplicationContext(),"JSON Error "+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(getApplicationContext(),"Soap Error",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateList(JSONArray jsonArray) {
        try{
            institutions.clear();
            for(int i=0;i<jsonArray.length();i++)
            {
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                Institution institution = new Institution();
                institution.setId(jsonObject.getInt("institutionId"));
                institution.setImage(jsonObject.getString("image"));
                institution.setName(jsonObject.getString("institutionName"));
                institutions.add(institution);
                adapter.notifyDataSetChanged();
            }
           // Toast.makeText(getApplicationContext(),institutions.toString(),Toast.LENGTH_LONG).show()
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),"Error"+e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }


}
