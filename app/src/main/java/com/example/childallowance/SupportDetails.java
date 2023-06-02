package com.example.childallowance;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class SupportDetails extends AppCompatActivity {
    String username, type;
    Support support;
    LinearLayout linearLayout;
    EditText editText;
    TextView date, detail,user;
    String URL = Server.ip + "sendresp.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_details);

        username = getIntent().getStringExtra("id");
        type = getIntent().getStringExtra("type");
        support = (Support) getIntent().getSerializableExtra("support");

        linearLayout = findViewById(R.id.linear17);

        editText = findViewById(R.id.s_resp);
        date = findViewById(R.id.s_date);
        detail = findViewById(R.id.s_details);
        user = findViewById(R.id.s_user);

        if (type.equals("admin")) {
            editText.setEnabled(true);
            linearLayout.setVisibility(View.VISIBLE);
        }

        date.setText(support.getSupp_date());
        detail.setText(support.getSupp_detail());
        user.setText(support.getUsername());
        if (!support.getSupp_response().equals("none"))
            editText.setText(support.getSupp_response());

    }

    public void SuppDetClick(View view) {
        if (view.getId() == R.id.s_save) {
            String response = editText.getText().toString().trim();
            if (response.length() < 3) {
                Toast.makeText(SupportDetails.this, "Enter valid response", Toast.LENGTH_LONG).show();
            } else {
                SendResp(support.getSupp_id(), response);
            }

        } else if (view.getId() == R.id.s_cancel) {
            finish();
        }
    }

    private void SendResp(String id, String resp) {
        class RegAsync extends AsyncTask<String, Void, String> {
            private Dialog loadingDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = ProgressDialog.show(SupportDetails.this, getResources().getString(R.string.wait), getResources().getString(R.string.connecting));
            }

            @Override
            protected String doInBackground(String... params) {
                Connection con = new Connection();
                HashMap<String, String> data = new HashMap<>();
                data.put("supp_id", params[0]);
                data.put("response", params[1]);
                String result = con.sendPostRequest(URL, data);
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                loadingDialog.dismiss();
                if (result.isEmpty() || result.equals("Error"))
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.check), Toast.LENGTH_LONG).show();
                else if (result.equals("failure")) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.noResult), Toast.LENGTH_LONG).show();
                } else if (result.equalsIgnoreCase("success")) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.success), Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
        RegAsync la = new RegAsync();
        la.execute(id, resp);
    }

}