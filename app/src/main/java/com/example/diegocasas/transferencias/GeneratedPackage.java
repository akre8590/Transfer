package com.example.diegocasas.transferencias;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.fxn.cue.Cue;
import com.fxn.cue.enums.Duration;
import com.fxn.cue.enums.Type;
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


    //TextView pkg;
    Button out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generated_package);

        Intent intent = getIntent();
        final String ad = intent.getExtras().getString("archivo_destino");

        //pkg = (TextView) findViewById(R.id.generatedPKG);
        out = (Button) findViewById(R.id.out);


        new CountDownTimer(70000, 1000) {

            public void onTick(long millisUntilFinished) {
                //pkg.setVisibility(View.VISIBLE);
            }
            public void onFinish() {
                //Toast.makeText(GeneratedPackage.this, "El proceso finalizó", Toast.LENGTH_SHORT).show();

                try {
                    UsbMassStorageDevice[] devices = UsbMassStorageDevice.getMassStorageDevices(GeneratedPackage.this);

                    for (UsbMassStorageDevice device : devices) {

                        // before interacting with a device you need to call init()!
                        device.init();
                        FileSystem currentFs = device.getPartitions().get(0).getFileSystem();
                        UsbFile root = currentFs.getRootDirectory();

                        UsbFile file = root.search(ad);

                        if (file.getLength() > 0 ){
                            cueCorrect("El archivo se copió en la memoria USB exitosamente");
                            //Toast.makeText(GeneratedPackage.this, "El archivo se copió en la memoria USB exitosamente", Toast.LENGTH_SHORT).show();
                        } else {
                            cueError("Hubo un error de copiado en la memoria");
                            //Toast.makeText(GeneratedPackage.this, "Hubo un error de copiado", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                    Toast.makeText(GeneratedPackage.this, e1.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (IOException e1) {
                    e1.printStackTrace();
                    Toast.makeText(GeneratedPackage.this, e1.getMessage(), Toast.LENGTH_SHORT).show();
                }

                //pkg.setVisibility(View.INVISIBLE);
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
    public void cueError(String msg){
        Cue.init()
                .with(GeneratedPackage.this)
                .setMessage(msg)
                .setGravity(Gravity.CENTER_VERTICAL)
                .setType(Type.CUSTOM)
                .setDuration(Duration.SHORT)
                .setBorderWidth(5)
                .setCornerRadius(10)
                .setCustomFontColor(Color.parseColor("#FA5858"),
                        Color.parseColor("#ffffff"),
                        Color.parseColor("#e84393"))
                .setPadding(30)
                .setTextSize(25)
                .show();
    }

    public void cueCorrect(String msg){
        Cue.init()
                .with(GeneratedPackage.this)
                .setMessage(msg)
                .setGravity(Gravity.CENTER_VERTICAL)
                .setType(Type.CUSTOM)
                .setDuration(Duration.SHORT)
                .setBorderWidth(5)
                .setCornerRadius(10)
                .setCustomFontColor(Color.parseColor("#088A85"), //fondo
                        Color.parseColor("#ffffff"), //letra
                        Color.parseColor("#01DFD7")) //contorno
                .setPadding(30)
                .setTextSize(25)
                .show();
    }
}