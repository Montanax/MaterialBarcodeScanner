package com.edwardvanraak.materialbarcodescanner;

import com.google.android.gms.vision.barcode.Barcode;

public interface NewDetectionListener {
    void onNewDetection(Barcode barcode);
}