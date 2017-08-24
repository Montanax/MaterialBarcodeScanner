package com.edwardvanraak.materialbarcodescanner;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.edwardvanraak.materialbarcodescanner.R.id.topText;
import static com.google.android.gms.common.GoogleApiAvailability.getInstance;
import static junit.framework.Assert.assertNotNull;

public class MaterialBarcodeScannerActivity extends AppCompatActivity {
    private static final Logger logger = LoggerFactory.getLogger(MaterialBarcodeScannerActivity.class);
    static final int RC_HANDLE_GMS = 9001;

    private MaterialBarcodeScannerBuilder materialBarcodeScannerBuilder;
    private BarcodeDetector barcodeDetector;

    @Nullable
    private CameraSourcePreview cameraSourcePreview;

    private GraphicOverlay<BarcodeGraphic> barcodeGraphicOverlay;

    /**
     * true if no further barcode should be detected or given as a result
     */
    private boolean detectionConsumed = false;
    private boolean flashOn = false;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        if (getWindow() != null) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        else {
            logger.error("Barcode scanner could not go into fullscreen mode!");
        }

        setContentView(R.layout.barcode_capture);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMaterialBarcodeScanner(MaterialBarcodeScanner materialBarcodeScanner) {
        materialBarcodeScannerBuilder = materialBarcodeScanner.getMaterialBarcodeScannerBuilder();
        barcodeDetector = materialBarcodeScanner.getMaterialBarcodeScannerBuilder().getBarcodeDetector();
        startCameraSource();
        setupLayout();
    }

    private void setupLayout() {
        final TextView topTextView = findViewById(topText);
        assertNotNull(topTextView);

        if (!materialBarcodeScannerBuilder.getText().isEmpty()) {
            String topText = materialBarcodeScannerBuilder.getText();
            topTextView.setText(topText);
        }
        setupButtons();
        setupCenterTracker();
    }

    private void setupCenterTracker() {
        if (materialBarcodeScannerBuilder.getScannerMode() == MaterialBarcodeScanner.SCANNER_MODE_CENTER) {
            final ImageView centerTracker = findViewById(R.id.barcode_square);
            centerTracker.setImageResource(materialBarcodeScannerBuilder.getTrackerResourceID());
            barcodeGraphicOverlay.setVisibility(View.INVISIBLE);
        }
    }

    private void updateCenterTrackerForDetectedState() {
        if (materialBarcodeScannerBuilder.getScannerMode() == MaterialBarcodeScanner.SCANNER_MODE_CENTER) {
            final ImageView centerTracker = findViewById(R.id.barcode_square);
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    centerTracker.setImageResource(materialBarcodeScannerBuilder.getTrackerDetectedResourceID());
                }
            });
        }
    }

    private void setupButtons() {
        final LinearLayout flashOnButton = findViewById(R.id.flashIconButton);
        assertNotNull(flashOnButton);
        flashOnButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (flashOn) {
                    disableTorch();
                }
                else {
                    enableTorch();
                }
                flashOn ^= true;
            }
        });
    }

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() throws SecurityException {
        // check that the device has play services available.
        int code = getInstance().isGooglePlayServicesAvailable(getApplicationContext());

        final Runnable finisher = new Runnable() {

            @Override
            public void run() {
                finish();
            }
        };

        if (code != ConnectionResult.SUCCESS) {
            getInstance().getErrorDialog(this, code, RC_HANDLE_GMS).show();
        }
        barcodeGraphicOverlay = findViewById(R.id.graphicOverlay);
        NewDetectionListener listener = new NewDetectionListener() {

            @Override
            public void onNewDetection(Barcode barcode) {
                if (!detectionConsumed) {
                    detectionConsumed = true;
                    logger.debug("Barcode detected! => {}", barcode.displayValue);
                    EventBus.getDefault().postSticky(barcode);
                    updateCenterTrackerForDetectedState();
                    if (materialBarcodeScannerBuilder.isBleepEnabled()) {
                        new SoundPlayer(getApplicationContext(), R.raw.bleep);
                    }

                    barcodeGraphicOverlay.postDelayed(finisher, 50);
                }
            }
        };
        BarcodeTrackerFactory barcodeFactory = new BarcodeTrackerFactory(barcodeGraphicOverlay, listener, materialBarcodeScannerBuilder.getTrackerColor());
        barcodeDetector.setProcessor(new MultiProcessor.Builder<>(barcodeFactory).build());
        CameraSource mCameraSource = materialBarcodeScannerBuilder.getCameraSource();
        if (mCameraSource != null) {
            try {
                cameraSourcePreview = findViewById(R.id.preview);
                cameraSourcePreview.start(mCameraSource, barcodeGraphicOverlay);
            }
            catch (IOException e) {
                logger.error("Unable to start camera source.", e);
                mCameraSource.release();
            }
        }
    }

    private void enableTorch() throws SecurityException {
        materialBarcodeScannerBuilder.getCameraSource().setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        try {
            materialBarcodeScannerBuilder.getCameraSource().start();
        }
        catch (IOException e) {
            logger.error("Oops!", e);
        }
    }

    private void disableTorch() throws SecurityException {
        materialBarcodeScannerBuilder.getCameraSource().setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        try {
            materialBarcodeScannerBuilder.getCameraSource().start();
        }
        catch (IOException e) {
            logger.error("Oops!", e);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();

        if (cameraSourcePreview != null) {
            cameraSourcePreview.stop();
        }
    }

    /**
     * Releases the resources associated with the camera source, the associated detectors, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (isFinishing()) {
            clean();
        }
    }

    private void clean() {
        EventBus.getDefault().removeStickyEvent(MaterialBarcodeScanner.class);

        if (cameraSourcePreview != null) {
            cameraSourcePreview.release();
            cameraSourcePreview = null;
        }
    }
}