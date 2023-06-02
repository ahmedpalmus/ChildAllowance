package com.example.childallowance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CanteenPage extends AppCompatActivity {
    String id;
    Button product,purchases,profile,support;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canteen_page);
        id=getIntent().getStringExtra("id");

        profile=findViewById(R.id.profile);
        support=findViewById(R.id.supp);
        product=findViewById(R.id.products);
        purchases=findViewById(R.id.purchase);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CanteenPage.this,Profile.class);
                intent.putExtra("id",id);
                startActivity(intent);
            }
        });
        support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(CanteenPage.this,SupportList.class);
                intent.putExtra("id",id);
                intent.putExtra("type","user");
                startActivity(intent);
            }
        });
        product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(CanteenPage.this,ProductList.class);
                intent.putExtra("id",id);
                intent.putExtra("type","canteen");
                intent.putExtra("child","child");
                startActivity(intent);
            }
        });
        purchases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(CanteenPage.this,PurchaseList.class);
                intent.putExtra("id",id);
                intent.putExtra("type","canteen");
                startActivity(intent);
            }
        });
    }
}