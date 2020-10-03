var express = require('express');
var router = express.Router()
var Wemo = require('wemo-client');
var wemo = new Wemo();
var authenticate = require('../authenticate');
var deviceinfo;
router.route('/').get(authenticate.verifyUser, function(req, res, next) {
    console.log('Inside Get');
    if(typeof client === 'undefined'){
        wemo.discover(function(err, deviceInfo) {
            console.log('Wemo Device Found: %j', deviceInfo);
            deviceinfo = deviceInfo;
            // Get the client for the found device
            client = wemo.client(deviceInfo);
            
            // You definitely want to listen to error events (e.g. device went offline),
            // Node will throw them as an exception if they are left unhandled  
            client.on('error', function(err) {
                console.log('Error: %s', err.code);
            });
        });
    }
        client.getInsightParams(function(err, binaryState, instantPower, data) {
            //client = wemo.client(deviceInfo);
            console.log('Instant power: %s', instantPower);
            var cons = JSON.parse(JSON.stringify(data));
            console.log('Power Consumed Today: %s',cons.TotalPower);
            /*res.setHeader('Access-Control-Allow-Origin', '*');
            res.setHeader('Access-Control-Allow-Headers', 'Content-type');
            res.setHeader('Access-Control-Allow-Methods', 'GET,PUT,POST,DELETE,OPTIONS');*/
            cons["InstantPower"] = instantPower;
            res.json(cons);
        });
    // Turn the switch on
    //client.setBinaryState(1);
    });

module.exports = router;