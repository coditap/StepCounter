package pb.pum.pumstepcounter;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    private TextView stepsView;
    private TextView caloriesView;
    private int stepsCount;

    private final int lastValuesCount = 16;
    private float[] lastMagnitudeValues = new float[lastValuesCount];
    private int collectedValues = 0;

    private SensorManager sensorManager;

    float[] gravity = new float[3];
    float maxAccelerationMagnitude = 16f;
    boolean blocked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);

        caloriesView = (TextView) findViewById(R.id.caloriesView);
        stepsView = (TextView) findViewById(R.id.stepsView);
    }

    public final void fabListener(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private final SensorEventListener sensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            final float x = event.values[0];
            final float y = event.values[1];
            final float z = event.values[2];

            // tutaj odfiltrowuje grawitacje
            // http://developer.android.com/reference/android/hardware/SensorEvent.html

            final float alpha = 0.96f;
            gravity[0] = alpha * gravity[0] + (1 - alpha) * x;
            gravity[1] = alpha * gravity[1] + (1 - alpha) * y;
            gravity[2] = alpha * gravity[2] + (1 - alpha) * z;

            // odejmuje odfiltrowana grawitacje i dostaje przyspieszenie liniowe
            float[] linear_acceleration = new float[3];
            linear_acceleration[0] = x - gravity[0];
            linear_acceleration[1] = y - gravity[1];
            linear_acceleration[2] = z - gravity[2];

            // licze wielkosc wektora przyspieszenia wedlug wzoru sqrt(x*x + y*y + z*z);
            float currentMagnitude = (float) Math.sqrt(linear_acceleration[0] * linear_acceleration[0]
                    + linear_acceleration[1] * linear_acceleration[1] + linear_acceleration[2] * linear_acceleration[2]);

            if (collectedValues < lastValuesCount) {
                lastMagnitudeValues[collectedValues] = currentMagnitude;
                ++collectedValues;
                return;
            } else {
                for (int i = 0; i != lastValuesCount - 1; ++i) {
                    lastMagnitudeValues[i] = lastMagnitudeValues[i+1];
                }
                lastMagnitudeValues[lastValuesCount-1] = currentMagnitude;
            }

            // usredniam lastValuesCount wynikow, domyslnie 16
            float sum = 0.0f;
            for (int i = 0; i != lastValuesCount; ++i) {
                sum += lastMagnitudeValues[i];
            }
            currentMagnitude = sum / lastValuesCount;

            if (currentMagnitude > maxAccelerationMagnitude)
                maxAccelerationMagnitude = currentMagnitude;

            if (!blocked && currentMagnitude < 0.6f * maxAccelerationMagnitude) {
                ++stepsCount;
                blocked = true;
                stepsView.setText(String.valueOf(stepsCount));
            } else if (blocked && currentMagnitude > 0.6f * maxAccelerationMagnitude) {
                blocked = false;
            }

            float caloriesPerMile = 0.57f * Settings.getInstance().getWeightInLbs();
            // standardowy uzytkownik wykonuje 2200 krokow na mile
            // mozna by bylo ustawiac na ekranie opcji
            int stepsPerMile = 2200;
            float caloriesPerStep = caloriesPerMile / stepsPerMile;
            float caloriesBurnt = stepsCount * caloriesPerStep;
            caloriesView.setText(String.format("%3.2f", caloriesBurnt));

            if (maxAccelerationMagnitude > 3.0f)
                maxAccelerationMagnitude -= 0.02f;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    public void resetSteps(View view) {
        stepsCount = 0;
        maxAccelerationMagnitude = 12f;
        stepsView.setText(String.valueOf(stepsCount));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
