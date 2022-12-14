package com.example.campus_positioning_system.Activitys;

import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.campus_positioning_system.Fragments.SettingsFragment;
import com.example.campus_positioning_system.R;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.NotFoundException;
import com.google.zxing.client.android.Intents;
import com.google.zxing.common.HybridBinarizer;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.google.zxing.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

public class QRcodeActivity extends AppCompatActivity {


    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if(result.getContents() == null) {
                    Intent originalIntent = result.getOriginalIntent();
                    if (originalIntent == null) {
                        Log.d("QRcodeActivity", "Cancelled scan");
                        Toast.makeText(QRcodeActivity.this, "Cancelled", Toast.LENGTH_LONG).show();
                    } else if(originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
                        Log.d("QRcodeActivity", "Cancelled scan due to missing camera permission");
                        Toast.makeText(QRcodeActivity.this, "Cancelled due to missing camera permission", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.d("QRcodeActivity", "Scanned");
                    System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXx"+result.getContents());
                    switchActivities(result.getContents());
                    Toast.makeText(QRcodeActivity.this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                }
            });

    /**
     * Method called onCreate of QRcode activity
     * @param savedInstanceState State to restore if there is one
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        System.out.println("On Create QRcode Activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_qrcode);
        View v = findViewById(R.id.qrcode_mainview);
        scanQRCodes(v);
    }

    public void scanBarcode(View view) {
        barcodeLauncher.launch(new ScanOptions());
    }

    public void scanBarcodeInverted(View view){
        ScanOptions options = new ScanOptions();
        options.addExtra(Intents.Scan.SCAN_TYPE, Intents.Scan.INVERTED_SCAN);
        barcodeLauncher.launch(options);
    }

    public void scanMixedBarcodes(View view){
        ScanOptions options = new ScanOptions();
        options.addExtra(Intents.Scan.SCAN_TYPE, Intents.Scan.MIXED_SCAN);
        barcodeLauncher.launch(options);
    }

    public void scanQRCodes(View view){
        ScanOptions options = new ScanOptions();
        options.addExtra(Intents.Scan.SCAN_TYPE,Intents.Scan.QR_CODE_MODE);
        barcodeLauncher.launch(options);
    }


    public void scanPDF417(View view) {
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.PDF_417);
        options.setPrompt("Scan something");
        options.setOrientationLocked(false);
        options.setBeepEnabled(false);
        barcodeLauncher.launch(options);
    }

    @SuppressWarnings("deprecation")
    public void scanBarcodeFrontCamera(View view) {
        ScanOptions options = new ScanOptions();
        options.setCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT);
        barcodeLauncher.launch(options);
    }


    public void scanWithTimeout(View view) {
        ScanOptions options = new ScanOptions();
        options.setTimeout(8000);
        barcodeLauncher.launch(options);
    }


    /**
     * Sample of scanning from a Fragment
     */
    /*
    public static class ScanFragment extends Fragment {
        private final ActivityResultLauncher<ScanOptions> fragmentLauncher = registerForActivityResult(new ScanContract(),
                result -> {
                    if(result.getContents() == null) {
                        Toast.makeText(getContext(), "Cancelled from fragment", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(), "Scanned from fragment: " + result.getContents(), Toast.LENGTH_LONG).show();
                    }
                });

        public ScanFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_qrcode, container, false);
            Button scan = view.findViewById(R.id.qrcode_button);
            scan.setOnClickListener(v -> scanFromFragment());
            return view;
        }

        public void scanFromFragment() {
            fragmentLauncher.launch(new ScanOptions());
        }
    }
}

     */

    /**
     * Handles going back to the map when the user presses the back button
     */
    @Override
    public void onBackPressed() {
        //System.out.println("User wants to go back from Room list");
        //System.out.println("Navigating from Room List back to Main");
        finish();
    }
    private void switchActivities(String origins) {
        Intent switchActivityIntent = new Intent(this, ViewPointOfInterestActivity.class);
        switchActivityIntent.putExtra("Origins_QuickDial", origins);
        this.startActivity(switchActivityIntent);
    }
}
