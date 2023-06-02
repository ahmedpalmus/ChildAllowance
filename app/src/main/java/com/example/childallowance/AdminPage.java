package com.example.childallowance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AdminPage extends AppCompatActivity {
    String id;
    Button canteens,profile,support;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_page);
        id=getIntent().getStringExtra("id");

        canteens=findViewById(R.id.canteen);
        profile=findViewById(R.id.profile);
        support=findViewById(R.id.supp);

        canteens.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(AdminPage.this,CanteenList.class);
                intent.putExtra("id",id);
                startActivity(intent);
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(AdminPage.this,Profile.class);
                intent.putExtra("id",id);
                startActivity(intent);
            }
        });
        support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(AdminPage.this,SupportList.class);
                intent.putExtra("id",id);
                intent.putExtra("type","admin");
                startActivity(intent);
            }
        });
    }
}