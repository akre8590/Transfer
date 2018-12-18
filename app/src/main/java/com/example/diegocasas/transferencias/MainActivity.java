package com.example.diegocasas.transferencias;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fxn.cue.Cue;
import com.fxn.cue.enums.Duration;
import com.fxn.cue.enums.Type;

import java.io.File;
import java.io.InputStream;
import java.util.logging.Handler;
import java.util.zip.ZipInputStream;

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
    String rutaOrigen, rutaDestino, archivoOrigen, archivoDestino, sup_ent;
    TextView inte;
    Button zip, rec;

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
        if (getIntent().getStringExtra("rutaOrigen") != null && getIntent().getStringExtra("archivoOrigen") != null && getIntent().getStringExtra("rutaDestino") != null && getIntent().getStringExtra("archivoDestino") != null && getIntent().getStringExtra("tipofigura") != null){
            rutaOrigen = getIntent().getStringExtra("rutaOrigen"); // ruta del archivo que se va a zipear
            archivoOrigen = getIntent().getStringExtra("archivoOrigen"); //  archivo que se va a zipear
            rutaDestino = getIntent().getStringExtra("rutaDestino"); // ruta donde se zipea
            archivoDestino = getIntent().getStringExtra("archivoDestino"); //nombre del zip
            sup_ent = getIntent().getStringExtra("tipofigura");
            deleteAdmCensal();

            zip = (Button) findViewById(R.id.sendZip);
            rec = (Button) findViewById(R.id.received);
            inte = (TextView) findViewById(R.id.intent);

            if (sup_ent.equals("E")){
                zip.setVisibility(View.VISIBLE);
                zip.setText("ENVIAR A SUPERVISOR");
                rec.setVisibility(View.INVISIBLE);
            } else if (sup_ent.equals("S")){
                zip.setVisibility(View.VISIBLE);
                rec.setVisibility(View.VISIBLE);
            } else {
                cueWarning("No se recibieron parámetros");
            }

            rec.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recibir();
                }
            });

            zip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new CountDownTimer(12000, 1000) {

                        public void onTick(long millisUntilFinished) {
                            rec.setVisibility(View.INVISIBLE);
                            zip.setVisibility(View.INVISIBLE);
                            cueWarning("Espere un momento...");
                            //Toast.makeText(MainActivity.this, "Espere un momento...", Toast.LENGTH_SHORT).show();
                        }
                        public void onFinish() {
                            sendIntent();
                            zipFile(rutaOrigen, archivoOrigen, rutaDestino, archivoDestino);
                        }
                    }.start();
                }
            });
        } else {
            cueWarning("Sin parámetros");
        }

    }
    public void deleteAdmCensal(){
        File datosAC = new File("storage/emulated/0/AdmCensal/envios/datos_AdmCensal.zip");
        if (datosAC.exists()) {
            if (datosAC.delete()) {
                Log.d("DELETE", "Archivo se ha borrado");
            } else {
                Log.d("DELETE", "No se pudo borrar el archivo");
            }
        }

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
    public void recibir(){
        /**Intent i = new Intent(this, UsbDetected2.class);
        i.putExtra("r_origen", rutaOrigen);
        i.putExtra("a_origen", archivoOrigen);
        i.putExtra("r_destino", rutaDestino);
        i.putExtra("a_destino", archivoDestino);
        startActivity(i);**/
        Intent intent = new Intent(this, UsbDetected2.class);
        startActivity(intent);
    }
    public void cancel(View view){
        finish();
        System.exit(0);

        /**Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory( Intent.CATEGORY_HOME );
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);**/
    }
    public void zipFile(String rutaOrigen, String nombreDB, String rutaDestino, String nombreZip){
        ZipArchive zipArchive1 = new ZipArchive();
        //zipArchive.zip(rutaOrigen + nombreDB, rutaDestino + nombreZip, "123456");
        zipArchive1.zip(rutaOrigen + nombreDB, rutaOrigen + nombreZip, "CONTA22015");

        ZipArchive zipArchive2 = new ZipArchive();
        zipArchive2.zip(rutaOrigen + nombreZip, rutaDestino + nombreZip, "CONTA22015");

        ZipArchive zipArchive3 = new ZipArchive();
        zipArchive3.zip(rutaOrigen + nombreDB, "storage/emulated/0/AdmCensal/envios/datos_AdmCensal.zip", "");

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
        cueCorrect("Conecte una memoria USB!!!");
        //Toast.makeText(MainActivity.this, "Listo!!", Toast.LENGTH_SHORT).show();
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
    public void cueCorrect(String msg){
        Cue.init()
                .with(MainActivity.this)
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
    public void cueWarning(String msg){
        Cue.init()
                .with(MainActivity.this)
                .setMessage(msg)
                .setGravity(Gravity.CENTER_VERTICAL)
                .setType(Type.CUSTOM)
                .setDuration(Duration.SHORT)
                .setBorderWidth(5)
                .setCornerRadius(10)
                .setCustomFontColor(Color.parseColor("#0080FF"), //fondo
                        Color.parseColor("#ffffff"), //letra
                        Color.parseColor("#00BFFF")) //contorno
                .setPadding(30)
                .setTextSize(25)
                .show();
    }
}