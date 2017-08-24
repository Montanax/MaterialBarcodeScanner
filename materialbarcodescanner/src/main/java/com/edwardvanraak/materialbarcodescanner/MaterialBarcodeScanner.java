package com.edwardvanraak.materialbarcodescanner;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.vision.barcode.Barcode;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MaterialBarcodeScanner {

    /**
     * Request codes
     */
    public static final int RC_HANDLE_CAMERA_PERM = 2;

    /**
     * Scanner modes
     */
    public static final int SCANNER_MODE_FREE = 1;
    public static final int SCANNER_MODE_CENTER = 2;

    protected final MaterialBarcodeScannerBuilder materialBarcodeScannerBuilder;

    private MaterialBarcodeScannerFragment materialBarcodeScannerFragment;

    private FrameLayout mContentView; //Content frame for fragments

    private OnResultListener onResultListener;

    public MaterialBarcodeScanner(@NonNull MaterialBarcodeScannerBuilder materialBarcodeScannerBuilder) {
        this.materialBarcodeScannerBuilder = materialBarcodeScannerBuilder;
    }

    public void setOnResultListener(OnResultListener onResultListener) {
        this.onResultListener = onResultListener;
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onBarcodeScannerResult(Barcode barcode) {
        onResultListener.onResult(barcode);
        EventBus.getDefault().removeStickyEvent(barcode);
        EventBus.getDefault().unregister(this);
        materialBarcodeScannerBuilder.clean();
    }

    /**
     * Interface definition for a callback to be invoked when a view is clicked.
     */
    public interface OnResultListener {
        void onResult(Barcode barcode);
    }

    /**
     * Start a scan for a barcode
     * <p>
     * This opens a new activity with the parameters provided by the MaterialBarcodeScannerBuilder
     */
    public void startScan() {
        EventBus.getDefault().register(this);
        if (materialBarcodeScannerBuilder.getActivity() == null) {
            throw new RuntimeException("Could not start scan: Activity reference lost (please rebuild the MaterialBarcodeScanner before calling startScan)");
        }
        int mCameraPermission = ActivityCompat.checkSelfPermission(materialBarcodeScannerBuilder.getActivity(), Manifest.permission.CAMERA);
        if (mCameraPermission != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission();
        }
        else {
            //Open activity
            EventBus.getDefault().postSticky(this);

            // new code - attempting to start a fragment
            FragmentManager fragmentManager = ((AppCompatActivity) materialBarcodeScannerBuilder.getActivity()).getSupportFragmentManager();
            materialBarcodeScannerFragment = MaterialBarcodeScannerFragment.newInstance(null);

            int id = materialBarcodeScannerBuilder.getRootView().getId();

            fragmentManager.beginTransaction()
                .replace(id, materialBarcodeScannerFragment)
                .addToBackStack(MaterialBarcodeScannerFragment.class.getSimpleName())
                .commit();
            // end


            // original code that starts an activity - and it works
            //
            //Intent intent = new Intent(materialBarcodeScannerBuilder.getActivity(), MaterialBarcodeScannerActivity.class);
            //materialBarcodeScannerBuilder.getActivity().startActivity(intent);
        }
    }

    public MaterialBarcodeScannerFragment getMaterialBarcodeScannerFragment() {
        return materialBarcodeScannerFragment;
    }

    protected void requestCameraPermission() {
        final String[] mPermissions = new String[]{Manifest.permission.CAMERA};
        if (!ActivityCompat.shouldShowRequestPermissionRationale(materialBarcodeScannerBuilder.getActivity(), Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(materialBarcodeScannerBuilder.getActivity(), mPermissions, RC_HANDLE_CAMERA_PERM);
            return;
        }
        View.OnClickListener listener = new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(materialBarcodeScannerBuilder.getActivity(), mPermissions, RC_HANDLE_CAMERA_PERM);
            }
        };
        Snackbar.make(materialBarcodeScannerBuilder.getRootView(), R.string.permission_camera_rationale,
            Snackbar.LENGTH_INDEFINITE)
            .setAction(android.R.string.ok, listener)
            .show();
    }

    public MaterialBarcodeScannerBuilder getMaterialBarcodeScannerBuilder() {
        return materialBarcodeScannerBuilder;
    }

}
