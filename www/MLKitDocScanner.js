var exec = require('cordova/exec');

exports.scanDocument = function(success, error) {
    exec(success, error, "MLKitDocScanner", "scanDocument", []);
};
