package com.example.diegocasas.transferencias;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.InputStream;
import java.util.logging.Handler;

import ir.mahdi.mzip.zip.ZipArchive;

public class MainActivity extends AppCompatActivity {
    PendingIntent mPermissionIntent;
    private static final String ACTION_USB_PERMISSION = "com.example.diegocasas.transferencias";

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final String[] INITIAL_PERMS = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION};

    private static final int INITIAL_REQUEST = 1337;
    private static final int REQUEST_WRITE_STORAGE = INITIAL_REQUEST + 4;
    String rutaOrigen, rutaDestino, archivoOrigen, archivoDestino;
    TextView inte;
    Button zip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean("EXIT", false)) {
            finish();
        }
        verifyStoragePermissions(MainActivity.this);

        if (!canAccessLocation() || !canAccessCamera() || !canAccessWriteStorage() || !canAccessReadStorage() || !canAccessReadContacts() || !canAccessWriteContacts()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
            }
        }

        rutaOrigen = getIntent().getStringExtra("rutaOrigen"); // ruta del archivo que se va a zipear
        archivoOrigen = getIntent().getStringExtra("archivoOrigen"); //  archivo que se va a zipear
        rutaDestino = getIntent().getStringExtra("rutaDestino"); // ruta donde se zipea
        archivoDestino = getIntent().getStringExtra("archivoDestino"); //nombre del zip

        zip = (Button) findViewById(R.id.sendZip);
        inte = (TextView) findViewById(R.id.intent);


        zip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    new CountDownTimer(12000, 1000) {

                        public void onTick(long millisUntilFinished) {
                            Toast.makeText(MainActivity.this, "Espere un momento...", Toast.LENGTH_SHORT).show();
                        }
                        public void onFinish() {
                            sendIntent();
                            zipFile(rutaOrigen, archivoOrigen, rutaDestino, archivoDestino);
                        }
                    }.start();
            }
        });
    }
    public void sendIntent() {

        Intent i = new Intent(this, UsbDetected.class);
        i.putExtra("r_origen", rutaOrigen);
        i.putExtra("a_origen", archivoOrigen);
        i.putExtra("r_destino", rutaDestino);
        i.putExtra("a_destino", archivoDestino);
        startActivity(i);

        //zipFile(rutaOrigen, archivoOrigen, rutaDestino, archivoDestino);
        /*Intent intent = new Intent(this, UsbDetected.class);
        startActivity(intent);*/
    }

    public void recibir(View view){

        /*Intent i = new Intent(this, UsbDetected2.class);
        i.putExtra("r_origen", rutaOrigen);
        i.putExtra("a_origen", archivoOrigen);
        i.putExtra("r_destino", rutaDestino);
        i.putExtra("a_destino", archivoDestino);
        startActivity(i);*/

        Intent intent = new Intent(this, UsbDetected2.class);
        startActivity(intent);
    }

    public void cancel(View view){
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory( Intent.CATEGORY_HOME );
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }

    public void zipFile(String rutaOrigen, String nombreDB, String rutaDestino, String nombreZip){
        ZipArchive zipArchive1 = new ZipArchive();
        //zipArchive.zip(rutaOrigen + nombreDB, rutaDestino + nombreZip, "123456");
        zipArchive1.zip(rutaOrigen + nombreDB, rutaOrigen + nombreZip, "CONTA22015");

        ZipArchive zipArchive2 = new ZipArchive();
        zipArchive2.zip(rutaOrigen + nombreZip, rutaDestino + nombreZip, "CONTA22015");

        ZipArchive zipArchive3 = new ZipArchive();
        zipArchive3.zip(rutaOrigen + nombreDB, "storage/emulated/0/AdmCensal/envios/datos_AdmCensal.zip", "123456");

        File fdelete = new File(rutaOrigen + nombreZip);
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                Log.d("DELETE", rutaOrigen + nombreZip);
            } else {
                Log.d("DELETE", rutaOrigen + nombreZip);
            }
        }
        File fdelete2 = new File(rutaOrigen + nombreDB);
        if (fdelete2.exists()) {
            if (fdelete2.delete()) {
                Log.d("DELETE", rutaOrigen + nombreDB);
            } else {
                Log.d("DELETE", rutaOrigen + nombreDB);
            }
        }
        Toast.makeText(MainActivity.this, "Listo!!", Toast.LENGTH_SHORT).show();
    }

    /*****Deshabilitar back******/
    @Override
    public void onBackPressed() {
    }
    private boolean canAccessWriteStorage() {
        return (hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE));
    }

    private boolean canAccessReadStorage() {
        return (hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE));
    }

    private boolean canAccessReadContacts() {
        return (hasPermission(Manifest.permission.READ_CONTACTS));
    }

    private boolean canAccessWriteContacts() {
        return (hasPermission(Manifest.permission.WRITE_CONTACTS));
    }

    private boolean canAccessCamera() {
        return (hasPermission(Manifest.permission.CAMERA));
    }

    private boolean canAccessLocation() {
        return (hasPermission(Manifest.permission.ACCESS_FINE_LOCATION));
    }

    private boolean hasPermission(String perm) {
        return (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, perm));
    }
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
