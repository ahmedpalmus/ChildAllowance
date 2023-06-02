package com.example.childallowance;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class ProductDetails extends AppCompatActivity {
    Product Info;
    TextView title, price, detail;
    ImageView imageView;
    Button edit,select_product;
    String username, type,child;
    String URL = Server.ip + "sendselect.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        username = getIntent().getStringExtra("id");
        type = getIntent().getStringExtra("type");
        child = getIntent().getStringExtra("child");

        Info = (Product) getIntent().getSerializableExtra("item");

        title = findViewById(R.id.item_t);
        price = findViewById(R.id.item_p);
        detail = findViewById(R.id.item_d);
        imageView = findViewById(R.id.item_i);
        edit = findViewById(R.id.edit_item);
        select_product = findViewById(R.id.select_product);

        if (type.equals("canteen")) {
            edit.setVisibility(View.VISIBLE);
            select_product.setVisibility(View.GONE);
        } else{
            edit.setVisibility(View.GONE);
        }
        if (child.equals("child"))  {
            select_product.setVisibility(View.GONE);
        }

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductDetails.this, AddProduct.class);
                intent.putExtra("op_type", "edit");
                intent.putExtra("info", Info);
                intent.putExtra("id", username);
                startActivity(intent);
                finish();
            }
        });
        select_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendInfo();
            }
        });
        title.setText(Info.getItem_title());
        price.setText(Info.getPrice());

        detail.setText(Info.getDetail());

        getImage(Info.getImage(), imageView);
    }

    private void SendInfo() {
        class RegAsync extends AsyncTask<String, Void, String> {
            private Dialog loadingDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = ProgressDialog.show(ProductDetails.this, getResources().getString(R.string.wait), getResources().getString(R.string.connecting));
            }

            @Override
            protected String doInBackground(String... params) {
                Connection con = new Connection();
                HashMap<String, String> data = new HashMap<>();
                data.put("username", username);
                data.put("product", Info.getItem_id());
                data.put("child", child);

                String result = con.sendPostRequest(URL, data);
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                loadingDialog.dismiss();
                if (result.isEmpty() || result.equals("Error"))
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.check), Toast.LENGTH_LONG).show();
                else if (result.equals("failure")) {
                    Toast.makeText(getApplicationContext(), "Try Again", Toast.LENGTH_LONG).show();
                } else if (result.equalsIgnoreCase("Exist")) {
                    Toast.makeText(getApplicationContext(), "The product is already added", Toast.LENGTH_LONG).show();
                    finish();
                }else if (result.equalsIgnoreCase("success")) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.success), Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
        RegAsync la = new RegAsync();
        la.execute();
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
