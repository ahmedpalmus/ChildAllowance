package com.example.childallowance;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class AddChild extends AppCompatActivity {
    EditText ename, eage, elimit, eallergy;
    Child memo;
    ImageView image;
    Button save, cancel, img_btn,del;
    String URL = Server.ip + "add_child.php";
    String URL2 = Server.ip + "getcanteens.php";
    ArrayList<Info> infos = new ArrayList<Info>();

    TextView l1, l2,l3,l4;
    String id, name,limit,allergy="none", Image = "none", age,canteen_id="", op_type, child_id = "0";
    Spinner canteens;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_child);

        id = getIntent().getStringExtra("id");
        op_type = getIntent().getStringExtra("op_type");

        ename = findViewById(R.id.ch_name);
        eage = findViewById(R.id.ch_age);
        elimit = findViewById(R.id.ch_limit);
        eallergy = findViewById(R.id.ch_allergies);
        canteens = findViewById(R.id.fm_canteens);

        img_btn = findViewById(R.id.fm_image_btn);
        image = findViewById(R.id.fm_image);


        del = findViewById(R.id.fm_del);
        save = findViewById(R.id.fm_save);
        cancel = findViewById(R.id.fm_cancel);
        l1 = findViewById(R.id.l1);

        l2= findViewById(R.id.l2);
        l3= findViewById(R.id.l3);
        l4= findViewById(R.id.l4);

        if (op_type.equals("edit")) {
            memo = (Child) getIntent().getSerializableExtra("info");
            ename.setText(memo.getName());
            eage.setText(memo.getAge());
            eallergy.setText(memo.getAllergies());
            elimit.setText(memo.getBuy_limit());
            child_id = memo.getId();
            del.setVisibility(View.VISIBLE);
            getImage(memo.getImage(), image);
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Save();
            }
        });

        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(AddChild.this);
                alert.setTitle("Deleting a child");
                alert.setMessage("Are You sure?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        op_type = "del";
                        name="";
                        age="";
                        allergy="";
                        limit="";
                        SendInfo();
                    }
                });

                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                alert.create().show();

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser(400);
            }

        });

        getInfos();
    }

    private void getInfos() {
        class Async extends AsyncTask<String, Void, String> {
            private Dialog loadingDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = ProgressDialog.show(AddChild.this, getResources().getString(R.string.wait), getResources().getString(R.string.connecting));
            }

            @Override
            protected String doInBackground(String... params) {
                Connection con = new Connection();
                HashMap<String, String> data = new HashMap<>();
                data.put("username", "username");

                String result = con.sendPostRequest(URL2, data);
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                loadingDialog.dismiss();
                infos.clear();
                if (result.isEmpty() || result.equals("Error"))
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.check), Toast.LENGTH_LONG).show();
                else if (result.equals("failure")) {
                } else {
                    try {
                        infos= new ArrayList<>();
                        JSONArray allReq = new JSONArray(result);
                        for (int i = 0; i < allReq.length(); i++) {

                            JSONObject row = allReq.getJSONObject(i);
                            Info temp=new Info();
                            String username = row.getString("username");
                            String fullname = row.getString("fullname");
                            String phone = row.getString("phone");
                            String email = row.getString("email");
                            String state = row.getString("state");


                            temp.setUsername(username);
                            temp.setFullname(fullname);
                            temp.setPhone(phone);
                            temp.setEmail(email);
                            temp.setState(state);

                            infos.add(temp);
                        }
                        String[] array =new String[infos.size()];
                        for(int i=0;i<infos.size();i++){
                            array[i]=infos.get(i).getFullname();
                        }

                        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(AddChild.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, array); //selected item will look like a spinner set from XML
                        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        canteens.setAdapter(spinnerArrayAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        Async la = new Async();
        la.execute();
    }

    private void showFileChooser(int code) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), code);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 400 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedimg = data.getData();
            try {
                image.setImageBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedimg));
                Bitmap univLogo = ((BitmapDrawable) image.getDrawable()).getBitmap();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                univLogo.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                Image = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

    public void Save() {

        name = ename.getText().toString().trim();
        age = eage.getText().toString().trim();
        limit = elimit.getText().toString().trim();
        allergy = eallergy.getText().toString().trim();
        int index=canteens.getSelectedItemPosition();
        canteen_id = infos.get(index).getUsername();

        l1.setTextColor(Color.BLACK);
        l2.setTextColor(Color.BLACK);
        l3.setTextColor(Color.BLACK);
        l4.setTextColor(Color.BLACK);

        boolean err = false;
        if (name.length() < 2) {
            l1.setTextColor(Color.RED);
            err = true;
        }
        if (age.length() < 1) {
            l2.setTextColor(Color.RED);
            err = true;
        }if (limit.length() < 1) {
            l3.setTextColor(Color.RED);
            err = true;
        }

        if (!err) {
            SendInfo();
        } else {
            Toast.makeText(getApplicationContext(), "Enter all the required fields", Toast.LENGTH_LONG).show();
        }


    }

    private void SendInfo() {
        class RegAsync extends AsyncTask<String, Void, String> {
            private Dialog loadingDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = ProgressDialog.show(AddChild.this, getResources().getString(R.string.wait), getResources().getString(R.string.connecting));
            }

            @Override
            protected String doInBackground(String... params) {
                Connection con = new Connection();
                HashMap<String, String> data = new HashMap<>();
                data.put("user_id", id);
                data.put("name", name);
                data.put("age", age);
                data.put("limit", limit);
                data.put("allergy", allergy);
                data.put("op_type", op_type);
                data.put("child_id", child_id);
                data.put("image", Image);
                data.put("canteen_id", canteen_id);

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
                } else if (result.equalsIgnoreCase("success")) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.success), Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
        RegAsync la = new RegAsync();
        la.execute();
    }
}