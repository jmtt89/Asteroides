package asteroides.example.org.asteroides.views.converter;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hbb20.CountryCodePicker;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import asteroides.example.org.asteroides.R;

public class ConverterActivity extends Activity {
    private Spinner origin;
    private ImageButton swap;
    private Spinner destiny;
    private TextView actualStr;
    private EditText amount;

    private View response_wrapper;
    private TextView destinyStr;
    private TextView amountConverted;

    private ProgressBar progressBar;

    FloatingActionButton convert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converter);
        //getActionBar().setDisplayHomeAsUpEnabled(true);

        origin = findViewById(R.id.origin);
        swap = findViewById(R.id.swap);
        destiny = findViewById(R.id.destiny);
        actualStr = findViewById(R.id.actualStr);
        amount = findViewById(R.id.amount);

        response_wrapper = findViewById(R.id.response_wrapper);
        destinyStr = findViewById(R.id.destinyStr);
        amountConverted = findViewById(R.id.amountConverted);

        convert = findViewById(R.id.convert);

        progressBar = findViewById(R.id.progressBar);

        response_wrapper.setVisibility(View.GONE);
        actualStr.setText(origin.getSelectedItem().toString());
        destinyStr.setText(destiny.getSelectedItem().toString());

        origin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                response_wrapper.setVisibility(View.GONE);
                actualStr.setText(origin.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        destiny.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                response_wrapper.setVisibility(View.GONE);
                destinyStr.setText(destiny.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        swap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tmp = origin.getSelectedItemPosition();
                origin.setSelection(destiny.getSelectedItemPosition());
                destiny.setSelection(tmp);
            }
        });

        convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                new ExchangeRate().execute(origin.getSelectedItem().toString(), destiny.getSelectedItem().toString());
            }
        });
    }

    class ExchangeRate extends AsyncTask<String, Void, Double>{
        @Override
        protected Double doInBackground(String... symbols) {
            if(symbols[0].equals(symbols[1])){
                return 1.0;
            }
            HttpURLConnection conexion = null;
            try {
                URL url=new URL("https://api.fixer.io/latest?base="+symbols[0]+"&symbols="+symbols[0]+","+symbols[1]);
                conexion = (HttpURLConnection) url.openConnection();
                if (conexion.getResponseCode() == HttpURLConnection.HTTP_OK) {

                    String json = readStream(conexion.getInputStream());
                    JSONObject object = new JSONObject(json);
                    return object.getJSONObject("rates").getDouble(symbols[1]);
                } else {
                    Log.e("Asteroides", conexion.getResponseMessage());
                }
            } catch (Exception e) {
                Log.e("Asteroides", e.getLocalizedMessage(), e);
            } finally {
                if (conexion!=null) conexion.disconnect();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Double rate) {
            super.onPostExecute(rate);
            progressBar.setVisibility(View.GONE);
            amountConverted.setText(String.format("%s", Double.parseDouble(amount.getText().toString()) * rate));
            response_wrapper.setVisibility(View.VISIBLE);
        }

        private String readStream(InputStream in) {
            BufferedReader reader = null;
            StringBuffer response = new StringBuffer();
            try {
                reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return response.toString();
        }
    }

}
