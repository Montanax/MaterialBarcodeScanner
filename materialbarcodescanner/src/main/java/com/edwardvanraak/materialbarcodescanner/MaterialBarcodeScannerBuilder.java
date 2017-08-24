package com.edwardvanraak.materialbarcodescanner;

import android.app.Activity;
import android.graphics.Color;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

public class MaterialBarcodeScannerBuilder {
    @Nullable
    protected Activity activity;
    private ViewGroup rootView;

    private MaterialBarcodeScanner.OnResultListener onResultListener;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;

    private boolean flashEnabledByDefault = false;
    private boolean autoFocusEnabled = false;
    private boolean bleepEnabled = false;
    protected boolean mUsed = false; //used to check if a builder is only used

    private int trackerDetectedResourceID = R.drawable.material_barcode_square_512_green;
    private int trackerResourceID = R.drawable.material_barcode_square_512;
    private int cameraFacingBack = CameraSource.CAMERA_FACING_BACK;
    private int trackerColor = Color.parseColor("#F44336"); //Material Red 500
    private int scannerMode = MaterialBarcodeScanner.SCANNER_MODE_FREE;
    private int barcodeFormats = Barcode.ALL_FORMATS;

    private int focusMode2;

    private String text = "";


    /**
     * Default constructor
     */
    public MaterialBarcodeScannerBuilder() {

    }

    /**
     * Called immediately after a barcode was scanned
     *
     * @param onResultListener
     */
    public MaterialBarcodeScannerBuilder withResultListener(@NonNull MaterialBarcodeScanner.OnResultListener onResultListener) {
        this.onResultListener = onResultListener;
        return this;
    }

    /**
     * Construct a MaterialBarcodeScannerBuilder by passing the activity to use for the generation
     *
     * @param activity current activity which will contain the drawer
     */
    public MaterialBarcodeScannerBuilder(@NonNull Activity activity) {
        this.rootView = activity.findViewById(android.R.id.content);
        this.activity = activity;
    }

    /**
     * Sets the activity which will be used as the parent of the MaterialBarcodeScanner activity
     *
     * @param activity current activity which will contain the MaterialBarcodeScanner
     */
    public MaterialBarcodeScannerBuilder withActivity(@NonNull Activity activity) {
        this.rootView = activity.findViewById(android.R.id.content);
        this.activity = activity;
        return this;
    }

    /**
     * Makes the barcode scanner use the camera facing back
     */
    public MaterialBarcodeScannerBuilder withBackFacingCamera() {
        cameraFacingBack = CameraSource.CAMERA_FACING_BACK;
        return this;
    }

    /**
     * Makes the barcode scanner use camera facing front
     */
    public MaterialBarcodeScannerBuilder withFrontFacingCamera() {
        cameraFacingBack = CameraSource.CAMERA_FACING_FRONT;
        return this;
    }

    /**
     * Either CameraSource.CAMERA_FACING_FRONT or CameraSource.CAMERA_FACING_BACK
     *
     * @param cameraFacing
     */
    public MaterialBarcodeScannerBuilder withCameraFacing(int cameraFacing) {
        cameraFacingBack = cameraFacing;
        return this;
    }

    /**
     * Enables or disables auto focusing on the camera
     */
    public MaterialBarcodeScannerBuilder withEnableAutoFocus(boolean enabled) {
        autoFocusEnabled = enabled;
        return this;
    }

    /**
     * Sets the tracker color used by the barcode scanner, By default this is Material Red 500 (#F44336).
     *
     * @param color
     */
    public MaterialBarcodeScannerBuilder withTrackerColor(int color) {
        trackerColor = color;
        return this;
    }

    /**
     * Enables or disables a bleep sound whenever a barcode is scanned
     */
    public MaterialBarcodeScannerBuilder withBleepEnabled(boolean enabled) {
        bleepEnabled = enabled;
        return this;
    }

    /**
     * Shows a text message at the top of the barcode scanner
     */
    public MaterialBarcodeScannerBuilder withText(String text) {
        this.text = text;
        return this;
    }

    /**
     * Shows a text message at the top of the barcode scanner
     */
    public MaterialBarcodeScannerBuilder withFlashLightEnabledByDefault() {
        flashEnabledByDefault = true;
        return this;
    }

    /**
     * Bit mask (containing values like QR_CODE and so on) that selects which formats this barcode detector should recognize.
     *
     * @param barcodeFormats
     * @return
     */
    public MaterialBarcodeScannerBuilder withBarcodeFormats(int barcodeFormats) {
        this.barcodeFormats = barcodeFormats;
        return this;
    }

    /**
     * Enables exclusive scanning on EAN-13, EAN-8, UPC-A, UPC-E, Code-39, Code-93, Code-128, ITF and Codabar barcodes.
     *
     * @return
     */
    public MaterialBarcodeScannerBuilder withOnly2DScanning() {
        barcodeFormats = Barcode.EAN_13 | Barcode.EAN_8 | Barcode.UPC_A | Barcode.UPC_E | Barcode.CODE_39 | Barcode.CODE_93 | Barcode.CODE_128 | Barcode.ITF | Barcode.CODABAR;
        return this;
    }

    /**
     * Enables exclusive scanning on QR Code, Data Matrix, PDF-417 and Aztec barcodes.
     *
     * @return
     */
    public MaterialBarcodeScannerBuilder withOnly3DScanning() {
        barcodeFormats = Barcode.QR_CODE | Barcode.DATA_MATRIX | Barcode.PDF417 | Barcode.AZTEC;
        return this;
    }

    public MaterialBarcodeScannerBuilder withOnlyPdf417() {
        barcodeFormats = Barcode.PDF417;
        return this;
    }

    /**
     * Enables exclusive scanning on QR Codes, no other barcodes will be detected
     *
     * @return
     */
    public MaterialBarcodeScannerBuilder withOnlyQRCodeScanning() {
        barcodeFormats = Barcode.QR_CODE;
        return this;
    }

    /**
     * Enables the default center tracker. This tracker is always visible and turns green when a barcode is found.\n
     * Please note that you can still scan a barcode outside the center tracker! This is purely a visual change.
     *
     * @return
     */
    public MaterialBarcodeScannerBuilder withCenterTracker() {
        scannerMode = MaterialBarcodeScanner.SCANNER_MODE_CENTER;
        return this;
    }

    /**
     * Enables the center tracker with a custom drawable resource. This tracker is always visible.\n
     * Please note that you can still scan a barcode outside the center tracker! This is purely a visual change.
     *
     * @param trackerResourceId         a drawable resource id
     * @param detectedTrackerResourceId a drawable resource id for the detected tracker state
     * @return
     */
    public MaterialBarcodeScannerBuilder withCenterTracker(int trackerResourceId, int detectedTrackerResourceId) {
        scannerMode = MaterialBarcodeScanner.SCANNER_MODE_CENTER;
        trackerResourceID = trackerResourceId;
        trackerDetectedResourceID = detectedTrackerResourceId;
        return this;
    }

    /**
     * Build a ready to use MaterialBarcodeScanner
     *
     * @return A ready to use MaterialBarcodeScanner
     */
    public MaterialBarcodeScanner build() {
        if (mUsed) {
            throw new RuntimeException("You must not reuse a MaterialBarcodeScanner builder");
        }
        if (activity == null) {
            throw new RuntimeException("Please pass an activity to the MaterialBarcodeScannerBuilder");
        }
        mUsed = true;
        buildMobileVisionBarcodeDetector();
        MaterialBarcodeScanner materialBarcodeScanner = new MaterialBarcodeScanner(this);
        materialBarcodeScanner.setOnResultListener(onResultListener);
        return materialBarcodeScanner;
    }

    /**
     * Build a barcode scanner using the Mobile Vision Barcode API
     */
    protected void buildMobileVisionBarcodeDetector() {
        String focusMode = Camera.Parameters.FOCUS_MODE_FIXED;

        if (autoFocusEnabled) {
            focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE;
        }
        barcodeDetector = new BarcodeDetector.Builder(activity)
            .setBarcodeFormats(barcodeFormats)
            .build();
        cameraSource = new CameraSource.Builder(activity, barcodeDetector)
            .setFacing(cameraFacingBack)
            .setFlashMode(flashEnabledByDefault ? Camera.Parameters.FLASH_MODE_TORCH : null)
            .setFocusMode(focusMode)
            .build();
    }

    public ViewGroup getRootView() {
        return rootView;
    }

    /**
     * Get the activity associated with this builder
     *
     * @return
     */
    public Activity getActivity() {
        return activity;
    }

    /**
     * Get the barcode detector associated with this builder
     *
     * @return
     */
    public BarcodeDetector getBarcodeDetector() {
        return barcodeDetector;
    }

    /**
     * Get the camera source associated with this builder
     *
     * @return
     */
    public CameraSource getCameraSource() {
        return cameraSource;
    }


    /**
     * Get the tracker color associated with this builder
     *
     * @return
     */
    public int getTrackerColor() {
        return trackerColor;
    }

    /**
     * Get the text associated with this builder
     *
     * @return
     */
    public String getText() {
        return text;
    }

    /**
     * Get the bleep enabled value associated with this builder
     *
     * @return
     */
    public boolean isBleepEnabled() {
        return bleepEnabled;
    }

    /**
     * Get the flash enabled by default value associated with this builder
     *
     * @return
     */
    public boolean isFlashEnabledByDefault() {
        return flashEnabledByDefault;
    }

    /**
     * Get the tracker detected resource id value associated with this builder
     *
     * @return
     */
    public int getTrackerDetectedResourceID() {
        return trackerDetectedResourceID;
    }

    /**
     * Get the tracker resource id value associated with this builder
     *
     * @return
     */
    public int getTrackerResourceID() {
        return trackerResourceID;
    }

    /**
     * Get the scanner mode value associated with this builder
     *
     * @return
     */
    public int getScannerMode() {
        return scannerMode;
    }

    public void clean() {
        activity = null;
    }
}
