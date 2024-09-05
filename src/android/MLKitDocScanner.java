package com.cyruscvc.docscanner;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import com.google.mlkit.vision.document.DocumentRecognizer;
import com.google.mlkit.vision.document.DocumentScannerOptions;
import com.google.mlkit.vision.common.InputImage;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;

public class MLKitDocScanner extends CordovaPlugin {
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        if (action.equals("scanDocument")) {
            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            cordova.setActivityResultCallback(this);
            cordova.getActivity().startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            return true;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");

            // Use Google ML Kit's Document Scanner API here
            InputImage image = InputImage.fromBitmap(photo, 0);
            DocumentScannerOptions options = new DocumentScannerOptions.Builder()
                .setDetectorMode(DocumentScannerOptions.STREAM_MODE)
                .build();

            DocumentScanner scanner = DocumentScanner.getClient(options);
            scanner.process(image)
                .addOnSuccessListener(result -> {
                    callbackContext.success("Document scanned successfully.");
                })
                .addOnFailureListener(e -> {
                    callbackContext.error("Document scan failed.");
                });
        }
    }
}
