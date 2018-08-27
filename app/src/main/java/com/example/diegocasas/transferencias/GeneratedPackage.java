package com.example.diegocasas.transferencias;

import android.Manifest;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.FileSystem;
import com.github.mjdev.libaums.fs.UsbFile;
import com.github.mjdev.libaums.fs.UsbFileOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GeneratedPackage extends AppCompatActivity {


    TextView pkg;
    Button out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generated_package);

        pkg = (TextView) findViewById(R.id.generatedPKG);
        out = (Button) findViewById(R.id.out);


        new CountDownTimer(70000, 1000) {

            public void onTick(long millisUntilFinished) {
                pkg.setVisibility(View.VISIBLE);
            }
            public void onFinish() {
                Toast.makeText(GeneratedPackage.this, "El proceso finalizó", Toast.LENGTH_SHORT).show();
                pkg.setVisibility(View.INVISIBLE);
                out.setVisibility(View.VISIBLE);
            }
        }.start();
            out.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.embarcadero.AdmCensal");
                    if (launchIntent != null) {
                        startActivity(launchIntent);//null pointer check in case package name was not found
                    }

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("EXIT", true);
                    startActivity(intent);
                }
            });
    }
    /*****Deshabilitar back******/
    @Override
    public void onBackPressed() {

    }

}


