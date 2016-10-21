package com.calculadora.sergi.ivan.carles.calculator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CalculatorActivity extends AppCompatActivity {

    private String sNum1 = "", sNum2= "";
    private long Num1 = 0, Num2 = 0;
    private Button btn_0;
    private Button btn_1;
    private Button btn_2;
    private Button btn_3;
    private Button btn_4;
    private Button btn_5;
    private Button btn_6;
    private Button btn_7;
    private Button btn_8;
    private Button btn_9;
    private Button btn_punt;
    private Button btn_div;
    private Button btn_mult;
    private Button btn_rest;
    private Button btn_sum;
    private Button btn_clear;
    private Button btn_equal;
    private TextView screen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        btn_0 = (Button) findViewById(R.id.btn_0);
        btn_1 = (Button) findViewById(R.id.btn_1);
        btn_2 = (Button) findViewById(R.id.btn_2);
        btn_3 = (Button) findViewById(R.id.btn_3);
        btn_4 = (Button) findViewById(R.id.btn_4);
        btn_5 = (Button) findViewById(R.id.btn_5);
        btn_6 = (Button) findViewById(R.id.btn_6);
        btn_7 = (Button) findViewById(R.id.btn_7);
        btn_8 = (Button) findViewById(R.id.btn_8);
        btn_9 = (Button) findViewById(R.id.btn_9);
        btn_punt = (Button) findViewById(R.id.btn_punt);
        btn_div = (Button) findViewById(R.id.btn_div);
        btn_mult = (Button) findViewById(R.id.btn_mult);
        btn_rest = (Button) findViewById(R.id.btn_rest);
        btn_sum = (Button) findViewById(R.id.btn_sum);
        btn_clear = (Button) findViewById(R.id.btn_clear);
        btn_equal = (Button) findViewById(R.id.btn_equal);
        screen = (TextView) findViewById(R.id.screen);

        boolean is_num_one = true;
        listen_all(is_num_one);

    }

    private void listen_all(final boolean is_num_one) {
        btn_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(is_num_one){
                    sNum1 += "0";
                    screen.setText(sNum1);
                }
                else{
                    sNum2 += "0";
                    screen.setText(sNum2);
                }
            }
        });

        btn_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(is_num_one){
                    sNum1 += "1";
                    screen.setText(sNum1);
                }
                else{
                    sNum2 += "1";
                    screen.setText(sNum2);
                }
            }
        });

        btn_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(is_num_one){
                    sNum1 += "2";
                    screen.setText(sNum1);
                }
                else{
                    sNum2 += "2";
                    screen.setText(sNum2);
                }
            }
        });

        btn_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(is_num_one){
                    sNum1 += "3";
                    screen.setText(sNum1);
                }
                else{
                    sNum2 += "3";
                    screen.setText(sNum2);
                }
            }
        });

        btn_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(is_num_one){
                    sNum1 += "4";
                    screen.setText(sNum1);
                }
                else{
                    sNum2 += "4";
                    screen.setText(sNum2);
                }
            }
        });

        btn_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(is_num_one){
                    sNum1 += "5";
                    screen.setText(sNum1);
                }
                else{
                    sNum2 += "5";
                    screen.setText(sNum2);
                }
            }
        });

        btn_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(is_num_one){
                    sNum1 += "6";
                    screen.setText(sNum1);
                }
                else{
                    sNum2 += "6";
                    screen.setText(sNum2);
                }
            }
        });

        btn_7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(is_num_one){
                    sNum1 += "7";
                    screen.setText(sNum1);
                }
                else{
                    sNum2 += "7";
                    screen.setText(sNum2);
                }
            }
        });

        btn_8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(is_num_one){
                    sNum1 += "8";
                    screen.setText(sNum1);
                }
                else{
                    sNum2 += "8";
                    screen.setText(sNum2);
                }
            }
        });

        btn_9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(is_num_one){
                    sNum1 += "9";
                    screen.setText(sNum1);
                }
                else{
                    sNum2 += "9";
                    screen.setText(sNum2);
                }
            }
        });

        btn_punt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        btn_div.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        btn_mult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        btn_rest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        btn_sum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        btn_equal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }
}
