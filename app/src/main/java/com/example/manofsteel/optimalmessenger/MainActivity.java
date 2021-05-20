package com.example.manofsteel.optimalmessenger;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    TextInputEditText name;
    TextInputEditText email;
    TextInputEditText phone;
    TextInputEditText address;
    TextInputEditText password;
    MaterialButton done;
    ProgressDialog progressDialog;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        name = findViewById(R.id.user_name);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone_number);
        address = findViewById(R.id.address);
        password = findViewById(R.id.password);
        done =  findViewById(R.id.done_btn);
        done.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        sp = getSharedPreferences("uid",MODE_PRIVATE);
        int userId=sp.getInt("uid",0);
        if(userId != 0) {
            Intent redirect = new Intent(MainActivity.this, BotList_Activity.class);
            startActivity(redirect);
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(getApplicationContext(),"Done button clicked!!",Toast.LENGTH_LONG);
        if(isFilled())
        {
            JSONObject json = new JSONObject();
            try {
                json.put("name", name.getText().toString());
                json.put("email", email.getText().toString());
                json.put("phone", phone.getText().toString());
                json.put("address", address.getText().toString());
                json.put("password", password.getText().toString());
                String data = json.toString();
                new Register().execute(data);
            }
            catch(Exception e){}
        }
    }

    private boolean isFilled()
    {
        if(name.getText().toString().equals("") ) {
            name.setError("Username missing!!");
            return false;
        }
        else if(name.getText().length()<3 ) {
            name.setError("Username must have atleast 3 chars.");
            return false;
        }
        else if(email.getText().toString().equals("")) {
            email.setError("Email missing!!");
            return false;
        }
        else if(phone.getText().length()>10||phone.getText().length()<10) {
            phone.setError("invalid phone no.");
            return false;
        }
        else if(address.getText().toString().equals("")) {
            address.setError("Address missing!!");
            return false;
        }
        else if(password.getText().toString().equals("")) {
            password.setError("Password missing!!");
            return false;
        }
        else  if(password.getText().length()<8) {
            password.setError("Password too short, must have 8 chars!");
            return false;
        }
        else
            return true;
    }
    public class Register extends AsyncTask<String,Void,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Just a moment...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String SoapAction = Constants.pkg+"/"+Constants.registerMethod;
            try{
                SoapObject soapObject=new SoapObject(Constants.pkg,Constants.registerMethod);
                soapObject.addProperty("data",params[0]);

                SoapSerializationEnvelope envelope=new SoapSerializationEnvelope(SoapEnvelope.VER10);
                envelope.setOutputSoapObject(soapObject);

                HttpTransportSE httpTransportSE=new HttpTransportSE(Constants.URL);
                httpTransportSE.call(SoapAction,envelope);

                soapObject=(SoapObject)envelope.bodyIn;
                String result=soapObject.getProperty(0).toString();
                return result;
            }
            catch(Exception e)
            {
                e.printStackTrace();
                Log.e("Error"," e");
                return "Error"+e.getMessage();

            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            if(!s.startsWith("Error"))
                try {
                    JSONObject json = new JSONObject(s);
                    int error = json.getInt("error");
                    switch(error)
                    {
                        case 0 :
                            SharedPreferences.Editor editor=sp.edit();
                            editor.putInt("uid",json.getInt("uid"));
                            editor.commit();
                            /*
                            Intent intent=new Intent(MainActivity.this,Verification.class);
                            intent.putExtra("mailId",json.getString("mailId"));
                            //intent.putExtra("vcode",json.getString("vcode"));
                            */
                            Intent intent = new Intent(MainActivity.this,BotList_Activity.class);
                            startActivity(intent);
                            finish();
                            break;
                        case 1 :
                            email.setError("Try with a different Mail ID!!!");
                            break;
                        case 2 :
                            phone.setError("Phone number already registered!!!");
                            break;
                        default:
                            Toast.makeText(getApplicationContext(),"Oops! Something went wrong!!",Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }
            else
            {
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }
        }
    }
}
