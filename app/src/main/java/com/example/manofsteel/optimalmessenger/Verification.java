package com.example.manofsteel.optimalmessenger;



import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class Verification extends AppCompatActivity {

    EditText editText_vcode;
    Button button;
    TextView textView;

    String gen_vcode="",vcode="",mailId="";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initComponents();
        setEvent();
    }

    private void initComponents() {
        setContentView(R.layout.activity_verify);
        editText_vcode=(EditText)findViewById(R.id.editText_vcode);
        button=(Button)findViewById(R.id.button_verify);
        textView=(TextView)findViewById(R.id.textView__mail);

        Bundle bundle=getIntent().getExtras();
        gen_vcode=bundle.getString("vcode");
        mailId=bundle.getString("mailId");

        String s="Check your mailId "+encodeId(mailId)+" for Verification Code";
        textView.setText(s);

        button.setEnabled(false);
    }

    private String encodeId(String mailId) {
        String id="";
        String ss[]=id.split("@");
        id=ss[0].replace(ss[0].substring(0,1),"******@")+ss[1];
        return id;
    }

    private void setEvent() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vcode=editText_vcode.getText().toString();
                if(vcode.equals(gen_vcode))
                {
                    startActivity(new Intent(Verification.this,MainActivity.class));
                    finish();
                }
                else
                {
                    editText_vcode.setError("Invalid Verification Code");
                }
            }
        });

        editText_vcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                button.setEnabled(!s.toString().equals(""));
            }
        });
    }
}

