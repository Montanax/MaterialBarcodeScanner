package com.edwardvanraak.materialbarcodescannerexample;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScanner;
import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScannerBuilder;
import com.google.android.gms.vision.barcode.Barcode;

import static junit.framework.Assert.assertNotNull;

public class MainActivity extends AppCompatActivity {
    public static final String BARCODE_KEY = "BARCODE";

    private Barcode barcodeResult;
    private TextView result;

    @Override
    protected void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        result = findViewById(R.id.barcodeResult);
        final FloatingActionButton fab = findViewById(R.id.fab);
        assertNotNull(result);
        assertNotNull(fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                startScan();
            }
        });
        if (bundle != null) {
            Barcode restoredBarcode = bundle.getParcelable(BARCODE_KEY);
            if (restoredBarcode != null) {
                result.setText(restoredBarcode.rawValue);
                barcodeResult = restoredBarcode;
            }
        }
    }

    private void startScan() {
        new MaterialBarcodeScannerBuilder()
            .withActivity(this)
            .withEnableAutoFocus(true)
            .withBleepEnabled(false)
            .withBackFacingCamera()
            .withCenterTracker()
            .withOnlyPdf417()
            .withText("Scanning...")
            .withResultListener(new MaterialBarcodeScanner.OnResultListener() {

                @Override
                public void onResult(Barcode barcode) {
                    barcodeResult = barcode;
                    result.setText(barcode.rawValue);
                }
            })
            .build()
            .startScan();
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        bundle.putParcelable(BARCODE_KEY, barcodeResult);
        super.onSaveInstanceState(bundle);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != MaterialBarcodeScanner.RC_HANDLE_CAMERA_PERM) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }
        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startScan();
            return;
        }
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        };

        new AlertDialog.Builder(this).setTitle("Error")
            .setMessage(R.string.no_camera_permission)
            .setPositiveButton(android.R.string.ok, listener)
            .show();
    }
}