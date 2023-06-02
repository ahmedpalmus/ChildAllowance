package com.example.childallowance;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.widget.Toast.LENGTH_LONG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class PurchaseList extends AppCompatActivity {
    String username,type,child;
    Button add;
    private static final int PERMISSION_REQUEST_CODE = 200;
boolean cam;
    ArrayList<Product> records;
    private final String URL = Server.ip + "getpurchase.php";
    private final String URL2 = Server.ip + "getchilddata.php";

    ListView simpleList;

    ArrayAdapter<String> adapter;
    // Create lanucher variable inside onAttach or onCreate or global
    ActivityResultLauncher<Intent> launchMap = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    child = data.getStringExtra("qrCode");
                    getChildInfos();
                    finish();
                }

            }
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_list);
        username=getIntent().getStringExtra("id");
        type=getIntent().getStringExtra("type");

        add=findViewById(R.id.new_process);

        simpleList = findViewById(R.id.prod_list);
        records = new ArrayList<>();


        if(!type.equals("canteen"))
            add.setVisibility(View.GONE);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cam ) {
                    Intent intent = new Intent(PurchaseList.this, ScanCode.class);
                    launchMap.launch(intent);
                } else {
                    Toast.makeText(PurchaseList.this, "Camera Permission bot granted", LENGTH_LONG).show();
                    checkPermission();
                }
            }
        });

        getInfos();
    }

    protected void onResume() {
        super.onResume();
        getInfos();
    }
    private void getChildInfos() {
        class Async extends AsyncTask<String, Void, String> {
            private Dialog loadingDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = ProgressDialog.show(PurchaseList.this, "please waite...", "Connecting....");
            }

            @Override
            protected String doInBackground(String... params) {
                Connection con = new Connection();
                HashMap<String, String> data = new HashMap<>();
                data.put("child", child);

                String result = con.sendPostRequest(URL2, data);
                return result;
            }

            @Override
            protected void onPostExecute(String result) {

                if (result.isEmpty() || result.equals("Error"))
                    Toast.makeText(getApplicationContext(), "Check connection", Toast.LENGTH_LONG).show();
                else if (result.equals("failure")) {
                    Toast.makeText(getApplicationContext(), "try again", Toast.LENGTH_LONG).show();
                } else {
                    try{
                        Child temp=new Child();
                    JSONArray allReq = new JSONArray(result);
                    for (int i = 0; i < allReq.length(); i++) {
                        JSONObject row = allReq.getJSONObject(i);


                        temp.setId(row.getString("child_id"));
                        temp.setName(row.getString("fullname"));
                        temp.setAge(row.getString("age"));
                        temp.setBuy_limit(row.getString("buy_limit"));
                        temp.setAllergies(row.getString("allergies"));
                        temp.setImage(row.getString("image"));
                        temp.setCanteen(row.getString("canteen_id"));
                        break;
                    }

                        Intent intent = new Intent(PurchaseList.this, ChildDetails.class);
                        intent.putExtra("id",username);
                        intent.putExtra("child", temp);
                        intent.putExtra("type", "canteen");

                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        Async la = new Async();
        la.execute();
    }
    private void getInfos() {
        class Async extends AsyncTask<String, Void, String> {
            private Dialog loadingDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = ProgressDialog.show(PurchaseList.this, "please waite...", "Connecting....");
            }

            @Override
            protected String doInBackground(String... params) {
                Connection con = new Connection();
                HashMap<String, String> data = new HashMap<>();
                data.put("username", username);

                String result = con.sendPostRequest(URL, data);
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                loadingDialog.dismiss();
                records.clear();
                String res1[] = new String[0];
                adapter= new ArrayAdapter<>(getApplicationContext(), R.layout.menu_view, R.id.menu_n, res1);
                simpleList.setAdapter(adapter);
                adapter.clear();
                adapter.notifyDataSetChanged();
                if (result.isEmpty() || result.equals("Error"))
                    Toast.makeText(getApplicationContext(), "Check connection", Toast.LENGTH_LONG).show();
                else if (result.equals("failure")) {
                    Toast.makeText(getApplicationContext(), "try again", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        records = new ArrayList<>();
                        JSONArray allReq = new JSONArray(result);
                        for (int i = 0; i < allReq.length(); i++) {
                            JSONObject row = allReq.getJSONObject(i);

                            Product temp=new Product();
                            temp.setItem_id(row.getString("item_id"));
                            temp.setItem_title(row.getString("item_title"));
                            temp.setFullname(row.getString("fullname"));
                            temp.setPrice(row.getString("price"));
                            temp.setDetail(row.getString("detail"));
                            temp.setImage(row.getString("image"));
                            temp.setQuantity(row.getString("amount"));
                            temp.setAdd_date(row.getString("add_date"));

                            records.add(temp);

                        }

                        String res[] = new String[records.size()];
                        for (int j = 0; j < records.size(); j++) {
                            res[j] =records.get(j).getFullname()+"\n"+records.get(j).getItem_title()+"     Price: "+records.get(j).getPrice()+" SR\n"
                                    +"Date: "+records.get(j).getAdd_date();
                            double p=Double.parseDouble(records.get(j).getPrice())*Double.parseDouble(records.get(j).getQuantity());
                        }
                        adapter= new ArrayAdapter<>(getApplicationContext(), R.layout.menu_view, R.id.menu_n,res);
                        simpleList.setAdapter(adapter);
                       /* simpleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long idd) {

                            *//*    Intent intent = new Intent(PurchaseList.this, ProductDetails.class);
                                intent.putExtra("item", records.get(position));
                                intent.putExtra("type", type);
                                intent.putExtra("id", username);
                                intent.putExtra("child", child);
                                startActivity(intent);*//*
                            }
                        });*/

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        Async la = new Async();
        la.execute();
    }

    private void checkPermission() {

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), CAMERA) == PackageManager.PERMISSION_GRANTED) {
            cam = true;
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{CAMERA}, PERMISSION_REQUEST_CODE);
            } else {
                Toast.makeText(PurchaseList.this, "Permission", LENGTH_LONG).show();
            }
        }

    }
}