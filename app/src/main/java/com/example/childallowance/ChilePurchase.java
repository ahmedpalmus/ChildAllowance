package com.example.childallowance;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ChilePurchase extends AppCompatActivity {
    String username,child,type;
    Button add;
double amount,rem;
    ArrayList<Product> records;
    private final String URL = Server.ip + "getchildpurchases.php";

    ListView simpleList;

    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chile_purchase);
        username=getIntent().getStringExtra("id");
        child=getIntent().getStringExtra("child");
        type=getIntent().getStringExtra("type");
        amount= Double.parseDouble(getIntent().getStringExtra("amount"));
        add=findViewById(R.id.new_member);

        simpleList = findViewById(R.id.prod_list);
        records = new ArrayList<>();

        if(!type.equals("canteen"))
            add.setVisibility(View.GONE);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ChilePurchase.this,AllowedProducts.class);
                intent.putExtra("id",username);
                intent.putExtra("type",type);
                intent.putExtra("child",child);
                intent.putExtra("amount",""+rem);
                startActivity(intent);
            }
        });

        getInfos();
    }

    protected void onResume() {
        super.onResume();
        getInfos();
    }
    private void getInfos() {
        class Async extends AsyncTask<String, Void, String> {
            private Dialog loadingDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = ProgressDialog.show(ChilePurchase.this, "please waite...", "Connecting....");
            }

            @Override
            protected String doInBackground(String... params) {
                Connection con = new Connection();
                HashMap<String, String> data = new HashMap<>();
                data.put("child", child);
                data.put("type", type);
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
                    rem=amount;
                } else {
                    try {
                        records = new ArrayList<>();
                        JSONArray allReq = new JSONArray(result);
                        for (int i = 0; i < allReq.length(); i++) {
                            JSONObject row = allReq.getJSONObject(i);

                            Product temp=new Product();
                            temp.setFullname(row.getString("fullname"));

                            temp.setItem_id(row.getString("item_id"));
                            temp.setItem_title(row.getString("item_title"));
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
                            rem=rem-p;
                        }
                        adapter= new ArrayAdapter<>(getApplicationContext(), R.layout.menu_view, R.id.menu_n,res);
                        simpleList.setAdapter(adapter);
                      /*  simpleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long idd) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(ChilePurchase.this);
                                builder.setTitle("Enter the qunatity");
                                // Set up the input
                                final EditText input = new EditText(ChilePurchase.this);
                                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                                builder.setView(input);
                                // Set up the buttons
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        double quan = Double.parseDouble(input.getText().toString());
                                        // Do something with the user's input
                                        double price=quan* Double.parseDouble(records.get(position).getPrice());
                                        double remain=amount-price;
                                        if(remain>=0.0){
                                            Buy(records.get(position).getItem_id(),quan);
                                        }else{
                                            Toast.makeText(getApplicationContext(), "You can't but this, buy limit exceeded", Toast.LENGTH_LONG).show();
                                        }

                                    }
                                });
                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });

// Create and show the dialog
                                AlertDialog dialog = builder.create();
                                dialog.show();

                            }
                        });
*/
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        Async la = new Async();
        la.execute();
    }


}