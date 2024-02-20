package com.viddoer.lovecalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.AsyncTask;


public class MainActivity extends AppCompatActivity {

    private EditText username, crush_name;
    private Button submit_button;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.user_name);
        crush_name = findViewById(R.id.crush_name);
        submit_button = findViewById(R.id.result_button);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Processing...");

        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernames = username.getText().toString();
                String crush_names = crush_name.getText().toString();
                if (TextUtils.isEmpty(usernames) || TextUtils.isEmpty(crush_names)){
                    Toast.makeText(MainActivity.this, "Fill All fields...", Toast.LENGTH_SHORT).show();
                } else {
                    love_process(usernames, crush_names);
                }
            }
        });
    }

    private void love_process(String user, String crush) {
        progressDialog.show();

        Map<String, String> params = new HashMap<>();
        params.put("sname", crush);
        params.put("fname", user);

        LoveCalculatorTask task = new LoveCalculatorTask(new LoveCalculatorTask.OnLoveCalculatorListener() {
            @Override
            public void onResultReceived(String result) {
                progressDialog.dismiss();
                if (result.startsWith("HTTP GET request failed") || result.startsWith("java")) {
                    Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
                } else {
                    // Extract result and percentage manually from JSON response
                    String results = extractValue(result, "\"result\":\"(.*?)\"");
                    String percentage = extractValue(result, "\"percentage\":\"(.*?)\"");

                    // Print result and percentage in each line
                    Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                    intent.putExtra("result", results);
                    intent.putExtra("percentage", percentage);
                    intent.putExtra("username", user);
                    intent.putExtra("crush", crush);
                    startActivity(intent);
                    Toast.makeText(MainActivity.this, results, Toast.LENGTH_SHORT).show();
                }
            }
        });

        task.execute(params);
    }

    private static String extractValue(String jsonString, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(jsonString);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private static class LoveCalculatorTask extends AsyncTask<Map<String, String>, Void, String> {
        private OnLoveCalculatorListener listener;

        interface OnLoveCalculatorListener {
            void onResultReceived(String result);
        }

        LoveCalculatorTask(OnLoveCalculatorListener listener) {
            this.listener = listener;
        }

        @Override
        protected String doInBackground(Map<String, String>... params) {
            try {
                Map<String, String> param = params[0];
                String url = "https://love-calculator.p.rapidapi.com/getPercentage";
                URL urlObj = new URL(url + getParamsString(param));
                HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("X-RapidAPI-Key", "4b7669c2d9msh74c0eea809f3683p172f9ajsnd7b435b8d803");
                connection.setRequestProperty("X-RapidAPI-Host", "love-calculator.p.rapidapi.com");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    return response.toString();
                } else {
                    return "HTTP GET request failed with response code: " + responseCode;
                }
            } catch (Exception e) {
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            listener.onResultReceived(result);
        }

        private static String getParamsString(Map<String, String> params) {
            StringBuilder result = new StringBuilder("?");
            for (Map.Entry<String, String> entry : params.entrySet()) {
                result.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            return result.toString();
        }
    }
}
