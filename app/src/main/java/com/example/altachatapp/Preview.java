package com.example.altachatapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Preview extends AppCompatActivity {

    TextView text;
    String Text;
    ImageView imageview;
    Button cancel,set;
    LinearLayout linear;
    ImageView back;

    public static final String mypreference = "mypref";
    public static final String Name = "person_name";
    SharedPreferences sharedpreferences;
    android.support.v7.widget.Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        getSupportActionBar().hide();
        toolbar = findViewById(R.id.toolbar);
        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        text = findViewById(R.id.text);
        imageview = findViewById(R.id.imageview);
        set = findViewById(R.id.set);
        cancel = findViewById(R.id.cancel);
        linear = findViewById(R.id.linear);

        Text = getIntent().getStringExtra("text");
        text.setText(Text);



        sharedpreferences = getSharedPreferences(mypreference,Context.MODE_PRIVATE);

       /* if (sharedpreferences.contains(Name))
            text.setText(sharedpreferences.getString(Name, ""));*/

        Text = text.getText().toString();
        if(Text.equalsIgnoreCase("Black"))
        {
            linear.setBackgroundResource(R.color.black);
        }
        if(text.getText().toString().equalsIgnoreCase("LightBlue"))
        {
            linear.setBackgroundResource(R.color.white);
        }
        if(Text.equalsIgnoreCase("Grey"))
        {
            linear.setBackgroundResource(R.color.cardview_dark_background);
        }
        if(Text.equalsIgnoreCase("Blue"))
        {
            linear.setBackgroundResource(R.color.design_default_color_primary);
        }
        if(Text.equalsIgnoreCase("orange"))
        {
            linear.setBackgroundResource(R.color.orange);
        }
        if(Text.equalsIgnoreCase("lightyellow"))
        {
            linear.setBackgroundResource(R.color.lightyellow);
        }
        if(Text.equalsIgnoreCase("color1"))
        {
            linear.setBackgroundResource(R.color.color1);
        }
        if(Text.equalsIgnoreCase("color2"))
        {
            linear.setBackgroundResource(R.color.color2);
        }
        if(Text.equalsIgnoreCase("color3"))
        {
            linear.setBackgroundResource(R.color.color3);
        }
        if(Text.equalsIgnoreCase("color4"))
        {
            linear.setBackgroundResource(R.color.color4);
        }
        if(Text.equalsIgnoreCase("color5"))
        {
            linear.setBackgroundResource(R.color.color5);
        }
        if(Text.equalsIgnoreCase("color6"))
        {
            linear.setBackgroundResource(R.color.color6);
        }


        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String n = text.getText().toString();
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(Name, n);
                editor.commit();
                text.setText("");

                Toast.makeText(getApplicationContext(), "Wallpaper added successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Preview.this, MainActivityChat.class);
                intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
