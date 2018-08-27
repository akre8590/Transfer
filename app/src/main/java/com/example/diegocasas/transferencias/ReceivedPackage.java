package com.example.diegocasas.transferencias;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

public class ReceivedPackage extends AppCompatActivity {


    TextView pkg2;
    Button out2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_received_package);


        pkg2 = (TextView) findViewById(R.id.receivedPKG);
        out2 = (Button) findViewById(R.id.out2);

        new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                pkg2.setVisibility(View.VISIBLE);
            }
            public void onFinish() {
                Toast.makeText(ReceivedPackage.this, "El proceso finalizó", Toast.LENGTH_SHORT).show();
                pkg2.setVisibility(View.INVISIBLE);
                out2.setVisibility(View.VISIBLE);
            }
        }.start();

        out2.setOnClickListener(new View.OnClickListener() {
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
