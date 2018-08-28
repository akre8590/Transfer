package com.example.diegocasas.transferencias;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
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
import android.support.v4.provider.DocumentFile;
import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.FileSystem;
import com.github.mjdev.libaums.fs.UsbFile;
import com.github.mjdev.libaums.fs.UsbFileOutputStream;
import com.github.mjdev.libaums.fs.UsbFileStreamFactory;
import com.onurkaganaldemir.ktoastlib.KToast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;

public class UsbDetected extends AppCompatActivity {
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
    Button det, sig;

    private static final String[] INITIAL_PERMS = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION};

    private static final int INITIAL_REQUEST = 1337;
    private static final int REQUEST_WRITE_STORAGE = INITIAL_REQUEST + 4;

    private static final String ACTION_USB_PERMISSION = "com.example.diegocasas.transferencias";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usb_detected);
        verifyStoragePermissions(UsbDetected.this);

        Intent intent = getIntent();
         final String ruta_destino = intent.getExtras().getString("r_destino");
         final String archivo_destino = intent.getExtras().getString("a_destino");

        det = (Button)findViewById(R.id.detected);
        sig = (Button)findViewById(R.id.next);

        det.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check();
            }
        });
        sig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UsbDetected.this, GeneratedPackage.class);
                i.putExtra("archivo_destino", archivo_destino);
                startActivity(i);
                //siguiente1();
                copyFile2(ruta_destino, archivo_destino);
                copyFile3();
            }
        });
        textInfo = (TextView) findViewById(R.id.info);
    }
    public void check(){

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
            i += "\n" + "USB Detectada!!" + "\n"
                    + "DeviceID: " + device.getDeviceId() + "\n";
        }
        textInfo.setText(i);


        if(!textInfo.getText().toString().matches("")){
            Toast.makeText(this, "Memoria detectada!!", Toast.LENGTH_SHORT).show();
            sig.setVisibility(View.VISIBLE);
        }else{
            Toast.makeText(this, "Memoria no detectada", Toast.LENGTH_SHORT).show();
            sig.setVisibility(View.INVISIBLE);
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

    public void cancel2(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    public void siguiente1(){

        Intent intent = new Intent(this, GeneratedPackage.class);

        startActivity(intent);
    }
    public void copyFile2(String rutaDestino, String archivoDestino) {

        try {
            UsbMassStorageDevice[] devices = UsbMassStorageDevice.getMassStorageDevices(UsbDetected.this);

            for (UsbMassStorageDevice device : devices) {

                // before interacting with a device you need to call init()!
                device.init();
                FileSystem currentFs = device.getPartitions().get(0).getFileSystem();
                UsbFile root = currentFs.getRootDirectory();

                File fileSource = new File(rutaDestino + archivoDestino);
                Toast.makeText(this, "Source: " + fileSource.getAbsolutePath(), Toast.LENGTH_SHORT).show();

                InputStream in = new FileInputStream(fileSource);
                ByteBuffer buffer = ByteBuffer.allocate(4096);
                int len;

                /*UsbFile AdmCensal = root.createDirectory("AdmCensal");
                UsbFile envios = AdmCensal.createDirectory("Envios");*/

                UsbFile file = root.createFile(archivoDestino);
                UsbFileOutputStream mOutPut = new UsbFileOutputStream(file);

                while ((len = in.read(buffer.array())) > 0) {
                    Toast.makeText(this, "COPIANDO...", Toast.LENGTH_SHORT).show();
                    mOutPut.write(buffer.array());//This the key Point
                }
                in.close();
                mOutPut.close();
            }
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            Toast.makeText(this, e1.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (IOException e1) {
            e1.printStackTrace();
            Toast.makeText(this, e1.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public void copyFile3() {

        try {
            UsbMassStorageDevice[] devices = UsbMassStorageDevice.getMassStorageDevices(UsbDetected.this);

            for (UsbMassStorageDevice device : devices) {

                // before interacting with a device you need to call init()!
                device.init();
                FileSystem currentFs = device.getPartitions().get(0).getFileSystem();
                UsbFile root = currentFs.getRootDirectory();

                File fileSource = new File("storage/emulated/0/AdmCensal/envios/datos_AdmCensal.zip");
                Toast.makeText(this, "Source: " + fileSource.getAbsolutePath(), Toast.LENGTH_SHORT).show();

                InputStream in = new FileInputStream(fileSource);
                ByteBuffer buffer = ByteBuffer.allocate(4096);
                int len;

                /*UsbFile AdmCensal = root.createDirectory("AdmCensal");
                UsbFile envios = AdmCensal.createDirectory("Envios");*/

                UsbFile file = root.createFile("datos_AdmCensal.zip");
                UsbFileOutputStream mOutPut = new UsbFileOutputStream(file);

                while ((len = in.read(buffer.array())) > 0) {
                    Toast.makeText(this, "COPIANDO...", Toast.LENGTH_SHORT).show();

                    mOutPut.write(buffer.array());//This the key Point
                }
                Toast.makeText(this, "ARCHIVO COPIADO", Toast.LENGTH_SHORT).show();
                in.close();
                mOutPut.close();
            }
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            Toast.makeText(this, e1.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (IOException e1) {
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
}

