var MLKitDocScanner = {
    scanDocument: function(successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, 'MLKitDocScanner', 'scanDocument', []);
    }
};

module.exports = MLKitDocScanner;
