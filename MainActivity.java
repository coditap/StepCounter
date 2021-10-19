package com.example.tomek.superkalkulator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    boolean displayClear = true;
    boolean containDot = false;
    private double num_a = 0.0;
    private String oldOperation = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onDigitPressed(View view) {
        Button b = (Button) view;
        String digit = b.getText().toString();

        TextView tv = (TextView) findViewById(R.id.textDisplay);

        if (digit.equals(".")) {
            if (containDot) {
                digit = "";
            } else {
                containDot = true;
            }
        }

        if (displayClear) {
            tv.setText(digit);
            displayClear = false;
        } else {
            tv.setText(tv.getText() + digit);
        }
    }

    public void onOperationPressed(View view) {
        Button b = (Button) view;
        String operation = b.getText().toString();

        TextView tv = (TextView) findViewById(R.id.textDisplay);
        double num_b = Double.parseDouble(tv.getText().toString());

        double result = num_b;

        switch (oldOperation) {
            case "+":
                result = num_a + num_b;
                break;
            case "-":
                result = num_a - num_b;
                break;
            case "x":
                result = num_a * num_b;
                break;
            case ":":
                result = num_a / num_b;
                break;
        }

        tv.setText(String.valueOf(result));
        num_a = result;
        oldOperation = operation;
        displayClear = true;
        containDot = false;
    }
}
