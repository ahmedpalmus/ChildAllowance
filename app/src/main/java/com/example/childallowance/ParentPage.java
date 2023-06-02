package com.example.childallowance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ParentPage extends AppCompatActivity {
String id;
Button children,profile,support,products;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_page);
        id=getIntent().getStringExtra("id");

        children=findViewById(R.id.my_child);
        profile=findViewById(R.id.profile);
        support=findViewById(R.id.supp);
        products=findViewById(R.id.products);

        children.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ParentPage.this,ChildList.class);
                intent.putExtra("id",id);
                startActivity(intent);
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ParentPage.this,Profile.class);
                intent.putExtra("id",id);
                startActivity(intent);
            }
        });

        support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(ParentPage.this,SupportList.class);
                intent.putExtra("id",id);
                intent.putExtra("type","user");
                startActivity(intent);
            }
        });
        products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(ParentPage.this,ProductList.class);
                intent.putExtra("id",id);
                intent.putExtra("type","parent");
                intent.putExtra("child","child");
                startActivity(intent);
            }
        });
    }
}