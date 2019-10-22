var exec = require('cordova/exec');

exports.toggleWifi = function (arg0, success, error) {
    exec(success, error, 'Wifi', 'toggleWifi', [arg0]);
};

exports.getListeWifi = function (success, error) {
    exec(success, error, 'Wifi', 'getListeWifi', []);
};

exports.connect = function (arg0, success, error) {
    exec(success, error, 'Wifi', 'connect', [arg0]);
};

exports.hasPermissions = function (success, error) {
    exec(success, error, 'Wifi', 'hasPermissions', []);
};