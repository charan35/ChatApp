package com.example.altachatapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Terms_And_Conditions extends AppCompatActivity {

    TextView tv1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms__and__conditions);
        tv1=findViewById(R.id.text);
        tv1.setText("WhatsApp Business Policy, which is applicable only to your use of our WhatsApp Business App for small-to-medium sized businesses\n" +
                "WhatsApp Business Data Processing Terms\n" +
                "WhatsApp Intellectual Property Policy\n" +
                "WhatsApp Brand Guidelines");

    }
}
