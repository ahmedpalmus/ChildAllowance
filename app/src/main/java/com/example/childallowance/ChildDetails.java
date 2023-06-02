package com.example.childallowance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ChildDetails extends AppCompatActivity {
    Child Info;
    TextView title,age, canteen,limit,allergy;
    ImageView imageView;
String id,type;
    Button edit,products,purchases,qrcode;
    public static ArrayList<Product> memos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_details);
        id=getIntent().getStringExtra("id");
        type=getIntent().getStringExtra("type");

        Info=(Child) getIntent().getSerializableExtra("child");
        memos = new ArrayList<>();

        title=findViewById(R.id.item_name);
        age=findViewById(R.id.item_p);
        canteen=findViewById(R.id.item_cant);
        limit=findViewById(R.id.item_bl);
        allergy=findViewById(R.id.item_al);
        imageView=findViewById(R.id.item_image);
        edit=findViewById(R.id.edit_item);
        products=findViewById(R.id.items);
        purchases=findViewById(R.id.purchases);
        qrcode=findViewById(R.id.qrcode);

        if(type.equals("canteen")){
            products.setVisibility(View.GONE);
            qrcode.setVisibility(View.GONE);
            edit.setVisibility(View.GONE);
        }
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChildDetails.this, AddChild.class);
                intent.putExtra("op_type","edit");
                intent.putExtra("info",Info);
                intent.putExtra("id",id);

                startActivity(intent);
                finish();
            }
        });

        products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChildDetails.this, AllowedProducts.class);
                intent.putExtra("id",id);
                intent.putExtra("child",Info.getId());
                intent.putExtra("type",type);
                intent.putExtra("amount","0.0");
                startActivity(intent);
            }
        });
        purchases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChildDetails.this, ChilePurchase.class);
                intent.putExtra("id",id);
                intent.putExtra("child",Info.getId());
                intent.putExtra("type",type);
                intent.putExtra("amount",Info.getBuy_limit());
                startActivity(intent);
            }
        });
        qrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ChildDetails.this, QRCodePage.class);
                intent.putExtra("child", Info.getId());
                startActivity(intent);
            }
        });
        title.setText(Info.getName());
        age.setText(Info.getAge());
        canteen.setText(Info.getCanteen());
        limit.setText(Info.getBuy_limit());
        allergy.setText(Info.getAllergies());

        getImage(Info.getImage(), imageView);
    }
    public void getImage(final String img, final ImageView viewHolder) {

        class packTask extends AsyncTask<Void, Void, Bitmap> {

            @Override
            protected Bitmap doInBackground(Void... voids) {
                Bitmap image1 = null;
                java.net.URL url = null;
                try {
                    url = new URL(Server.ip + img);
                    image1 = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return image1;
            }

            protected void onPostExecute(Bitmap image) {
                viewHolder.setImageBitmap(image);
            }
        }
        packTask t = new packTask();
        t.execute();
    }
}
