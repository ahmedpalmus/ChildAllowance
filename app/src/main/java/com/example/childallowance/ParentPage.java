package com.example.childallowance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ParentPage extends AppCompatActivity {
String id;
Button children,profile,support;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_page);
        id=getIntent().getStringExtra("id");

        children=findViewById(R.id.my_child);
        profile=findViewById(R.id.profile);
        support=findViewById(R.id.supp);

        children.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ParentPage.this,ChildList.class);
                intent.putExtra("id",id);
                startActivity(intent);
            }
        });
    }
}