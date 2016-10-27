package com.calculadora.sergi.ivan.carles.calculator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CalculatorActivity extends AppCompatActivity {

    private String sNum1 = "", sNum2= "",sOp="";
    private float Resultado = 0;
    private Button btn_clear;
    private Button btn_equal;
    private TextView screen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        btn_clear = (Button) findViewById(R.id.btn_clear);
        btn_equal = (Button) findViewById(R.id.btn_equal);
        screen = (TextView) findViewById(R.id.screen);
        listen_all();
    }

    public void numberClick(View view) {
        Button b = (Button) view;
        CharSequence num = b.getText();
        String numero = num.toString();
        screen.setText(screen.getText()+ numero);
    }

    public void operationClick(View view) {
        sNum1 = screen.getText().toString();
        Button b = (Button) view;
        CharSequence op = b.getText();
        sOp = op.toString();
        screen.setText("");
    }

    private void listen_all() {
        btn_equal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sNum2 = screen.getText().toString();
                switch(sOp){
                    case "/":
                        Resultado = Float.parseFloat(sNum1) / Float.parseFloat(sNum2);
                        break;
                    case "x":
                        Resultado = Float.parseFloat(sNum1) * Float.parseFloat(sNum2);
                        break;
                    case "-":
                        Resultado = Float.parseFloat(sNum1) - Float.parseFloat(sNum2);
                        break;
                    case "+":
                        Resultado = Float.parseFloat(sNum1) + Float.parseFloat(sNum2);
                        break;
                }
                if (Math.round(Resultado) == Resultado) {
                    screen.setText(String.format("%d",(int)Resultado));
                } else {
                    screen.setText(String.format("%f",Resultado));
                }
                sNum2 = "";
            }
        });

        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                screen.setText("");
                sNum1 = "";
                sNum2 = "";
            }
        });
    }
}
