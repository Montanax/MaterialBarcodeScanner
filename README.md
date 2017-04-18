# MaterialBarcodeScanner
Easy to use barcode reader for your Android Project (Uses Google Mobile Vision API)

[ ![Download](https://api.bintray.com/packages/edwardvraak/maven/MaterialBarcodeScanner/images/download.svg) ](https://bintray.com/edwardvraak/maven/MaterialBarcodeScanner/_latestVersion)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-MaterialBarcodeScanner-brightgreen.svg?style=flat)](http://android-arsenal.com/details/1/3439)



**In active development so if you have a suggestion or feature request please feel free to open an issue!**

### Overview
- Integrate in a **few minutes**
- Quick and simple api
- No external apps required
- Uses Google Mobile Vision API (fast, local and rotation free)
- Automatically parses QR Codes, Data Matrix, PDF-417, and Aztec values
- Supports 1D barcodes: EAN-13, EAN-8, UPC-A, UPC-E, Code-39, Code-93, Code-128, ITF, Codabar
- Supports 2D barcodes: QR Code, Data Matrix, PDF-417, Aztec

# Setup
## 1. Provide gradle dependency

```gradle
compile 'com.edwardvanraak:MaterialBarcodeScanner:0.0.6-ALPHA'
```

## 2. Build a MaterialBarcodeScanner

```java
    private void startScan() {
        /**
         * Build a new MaterialBarcodeScanner
         */
        final MaterialBarcodeScanner materialBarcodeScanner = new MaterialBarcodeScannerBuilder()
                .withActivity(MainActivity.this)
                .withEnableAutoFocus(true)
                .withBleepEnabled(true)
                .withBackfacingCamera()
                .withText("Scanning...")
                .withResultListener(new MaterialBarcodeScanner.OnResultListener() {
                    @Override
                    public void onResult(Barcode barcode) {
                        barcodeResult = barcode;
                        result.setText(barcode.rawValue);
                    }
                })
                .build();
        materialBarcodeScanner.startScan();
    }

 ```
 
 Hook it up to a button
 
```java
  fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startScan();
            }
        });
 ```
 
## 3. Start scanning!

Check out the full [example project](https://github.com/EdwardvanRaak/MaterialBarcodeScanner/blob/master/app/src/main/java/com/edwardvanraak/materialbarcodescannerexample/MainActivity.java) for code required for camera permissions on Android 6.0 Marshmallow

# Additional Setup

## Center tracking mode

By default a barcode is tracked/highlighted at the location at which it was found.  
With <i>center tracking mode</i> a square image will be shown during scanning that will turn green when a barcode is found.
Please note that you can still scan a barcode outside the center tracker! This is purely a visual change.

To activate <i>center tracking mode</i> simply call the following builder method:
```java
.withCenterTracker()
```
If you want to provide your own image for the tracker you can use:
```java
.withCenterTracker(R.drawable.your_tracker_image, R.drawable.your_detected_state_tracker_image):
```
<img src="https://raw.githubusercontent.com/EdwardvanRaak/MaterialBarcodeScanner/master/DEV/screens/center_tracker_screenshot.png" width="150">

## Exclusive barcode scanning

In some situations you might want to scan for only a certain type of barcode like QR-Codes or 2D barcodes. You can do this with the following builder methods:

```java
.withOnlyQRCodeScanning()
.withOnly3DScanning()
.withOnly2DScanning()
```

If you want to scan for a very specific combination of barcodes you can setup the builder like this:		
```java		
.withBarcodeFormats(Barcode.AZTEC | Barcode.EAN_13 | Barcode.CODE_93)		
```

## Screenshots
![Image](https://raw.githubusercontent.com/EdwardvanRaak/MaterialBarcodeScanner/master/DEV/screens/screenshot1.png)

# Developed By

* Edward van Raak
 * edwardvraak@gmail.com
 * [Donate with Paypal](https://www.paypal.me/EdwardvanRaak)

