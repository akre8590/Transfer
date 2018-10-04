package com.example.diegocasas.transferencias;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fxn.cue.Cue;
import com.fxn.cue.enums.Duration;
import com.fxn.cue.enums.Type;
import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.FileSystem;
import com.github.mjdev.libaums.fs.UsbFile;
import com.github.mjdev.libaums.fs.UsbFileInputStream;
import com.onurkaganaldemir.ktoastlib.KToast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;

import ir.mahdi.mzip.zip.ZipArchive;

public class UsbDetected2 extends AppCompatActivity {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    PendingIntent mPermissionIntent;
    TextView textInfo;
    UsbDevice device;
    String TAG = "MainActivity";
    UsbManager manager;
    Button det2, sig2;

    private static final String ACTION_USB_PERMISSION = "com.example.diegocasas.transferencias";
    private static final String[] INITIAL_PERMS = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION};

    private static final int INITIAL_REQUEST = 1337;
    private static final int REQUEST_WRITE_STORAGE = INITIAL_REQUEST + 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usb_detected2);
        verifyStoragePermissions(UsbDetected2.this);

        det2 = (Button) findViewById(R.id.detec2);
        sig2 = (Button) findViewById(R.id.sig2);

        det2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detecte();
            }
        });
        sig2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyFile3();
                sigui();
            }
        });
        textInfo = (TextView) findViewById(R.id.info2);
    }
    public void detecte(){
        manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        /*
         * this block required if you need to communicate to USB devices it's
         * take permission to device
         * if you want than you can set this to which device you want to communicate
         */
        // ------------------------------------------------------------------
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver, filter);
        // -------------------------------------------------------------------
        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        String i = "";
        while (deviceIterator.hasNext()) {
            device = deviceIterator.next();
            manager.requestPermission(device, mPermissionIntent);
            i += "\n" + "MEMORIA USB DETECTADA" + "\n"
                    + "DeviceID: " + device.getDeviceId() + "\n";
        }
        textInfo.setText(i);
        if(!textInfo.getText().toString().matches("")){
            cueCorrect("Memoria detectada. Presione el botón siguiente.");
            //Toast.makeText(this, "Memoria detectada!!", Toast.LENGTH_SHORT).show();
            sig2.setVisibility(View.VISIBLE);
        }else{
            cueError("Memoria no detectada...");
            //Toast.makeText(this, "Memoria no detectada...", Toast.LENGTH_SHORT).show();
            sig2.setVisibility(View.INVISIBLE);
        }
    }
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            UsbDevice usbDevice = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            if (ACTION_USB_PERMISSION.equals(action)) {
                // Permission requested
                synchronized (this) {
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        // User has granted permission
                        // ... Setup your UsbDeviceConnection via mUsbManager.openDevice(usbDevice) ...
                    } else {
                        // User has denied permission
                    }
                }
            }
            if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                // Device removed
                synchronized (this) {
                    // ... Check to see if usbDevice is yours and cleanup ...
                }
            }
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                Toast.makeText(context, "USB Detectado!!!", Toast.LENGTH_SHORT).show();
                // Device attached
            }
        }
    };
    public void cancel3(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    public void sigui(){
        Intent intent = new Intent(this, ReceivedPackage.class);
        startActivity(intent);
    }
    public void copyFile3()  {

        File to = new File("/storage/emulated/0/AdmCensal/recepciones/datos_AdmCensal.zip");

        try {

            UsbMassStorageDevice[] devices = UsbMassStorageDevice.getMassStorageDevices(UsbDetected2.this);

            for (UsbMassStorageDevice device : devices) {
                // before interacting with a device you need to call init()!
                device.init();
                FileSystem currentFs = device.getPartitions().get(0).getFileSystem();
                UsbFile root = currentFs.getRootDirectory();

                //UsbFile file = root.createFile("opera.txt");
                UsbFile file = root.search("datos_AdmCensal.zip");

                //UsbFile[] files = root.listFiles();
                //for(UsbFile file: files) {

                InputStream is = new UsbFileInputStream(file);
                OutputStream out = new BufferedOutputStream(new FileOutputStream(to));
                byte[] bytes = new byte[currentFs.getChunkSize()];
                int count;
                long total = 0;

                while ((count = is.read(bytes)) != -1) {
                    out.write(bytes, 0, count);
                    total += count;
                    cueWarning("Copiando...puede tardar unos minutos");
                    //Toast.makeText(this, "Copiando...", Toast.LENGTH_SHORT).show();

                }
                out.close();
                is.close();
                file.delete();

                ZipArchive zipArchive = new ZipArchive();
                zipArchive.unzip("/storage/emulated/0/AdmCensal/recepciones/datos_AdmCensal.zip", "/storage/emulated/0/AdmCensal/recepciones/","");

                /**File fdelete2 = new File("/storage/emulated/0/AdmCensal/recepciones/datos_AdmCensal.zip");
                if (fdelete2.exists()) {
                    if (fdelete2.delete()) {
                        Log.d("DELETE", "/storage/emulated/0/AdmCensal/recepciones/datos_AdmCensal.zip");
                    } else {
                        Log.d("DELETE", "/storage/emulated/0/AdmCensal/recepciones/datos_AdmCensal.zip");
                    }
                }**/
                cueCorrect("El proceso finalizó");
                //Toast.makeText(this, "Copiado", Toast.LENGTH_SHORT).show();

                //}

               /* File sourceLocation = new File(Root + "/opera.txt");
                File targetLocation = new File("/storage/emulated/0/Download/opera.txt");

                if (sourceLocation.exists()) {
                    InputStream in = new FileInputStream(sourceLocation);
                    OutputStream out = new FileOutputStream(targetLocation);
                    // Copy the bits from instream to outstream
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    in.close();
                    out.close();
                    Toast.makeText(this, "Copy file successful", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Copy file failed", Toast.LENGTH_SHORT).show();
                }*/
            }
        } catch(FileNotFoundException e1){
            e1.printStackTrace();
            Toast.makeText(this, e1.getMessage(), Toast.LENGTH_SHORT).show();
        } catch(IOException e1){
            e1.printStackTrace();
            Toast.makeText(this, e1.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
    /*****Deshabilitar back******/
    @Override
    public void onBackPressed() {
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
    public void cueError(String msg){
        Cue.init()
                .with(UsbDetected2.this)
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
                .with(UsbDetected2.this)
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
                .with(UsbDetected2.this)
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