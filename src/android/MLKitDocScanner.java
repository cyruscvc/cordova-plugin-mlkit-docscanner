package com.cyruscvc.docscanner;

import android.content.Intent;
import android.content.IntentSender;
import android.util.Log;
import androidx.activity.result.IntentSenderRequest;
import androidx.core.content.FileProvider;
import com.google.android.gms.mlkit.document.GmsDocumentScannerOptions;
import com.google.android.gms.mlkit.document.GmsDocumentScanning;
import com.google.android.gms.mlkit.document.GmsDocumentScanningResult;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class MLKitDocScanner extends CordovaPlugin {
    private CallbackContext callbackContext;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;

        if ("scanDocument".equals(action)) {
            scanDocument();
            return true;
        }
        return false;
    }

    private void scanDocument() {
        try {
            GmsDocumentScannerOptions options = new GmsDocumentScannerOptions.Builder()
                    .setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_BASE)
                    .setResultFormats(GmsDocumentScannerOptions.RESULT_FORMAT_PDF, GmsDocumentScannerOptions.RESULT_FORMAT_JPEG)
                    .setGalleryImportAllowed(false)
                    .setPageLimit(3)
                    .build();

            GmsDocumentScanning.getClient(cordova.getContext(), options)
                    .getStartScanIntent(cordova.getContext())
                    .addOnSuccessListener(intentSender -> {
                        cordova.setActivityResultCallback(this);
                        cordova.getActivity().startIntentSenderForResult(intentSender, 1001, null, 0, 0, 0);
                    })
                    .addOnFailureListener(e -> {
                        callbackContext.error(e.getMessage());
                    });

        } catch (Exception e) {
            callbackContext.error(e.getMessage());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1001) {
            try {
                GmsDocumentScanningResult result = GmsDocumentScanningResult.fromActivityResultIntent(data);
                if (resultCode == cordova.getActivity().RESULT_OK && result != null) {
                    JSONObject resultData = new JSONObject();
                    if (result.getPdf() != null) {
                        String pdfPath = result.getPdf().getUri().getPath();
                        File externalFile = new File(pdfPath);
                        String externalUri = FileProvider.getUriForFile(cordova.getContext(), cordova.getActivity().getPackageName() + ".provider", externalFile).toString();
                        resultData.put("pdfUri", externalUri);
                    }
                    callbackContext.success(resultData);
                }
            } catch (Exception e) {
                callbackContext.error(e.getMessage());
            }
        }
    }
}
