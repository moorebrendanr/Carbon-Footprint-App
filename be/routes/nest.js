var express = require('express');
var router = express.Router()
var authenticate = require('../authenticate');

var nest = require('nest-thermostat').init('chmounik@yahoo.com', 'Fall@9014375411');

router.route('/')
    .get(authenticate.verifyUser,(req,res,next)=>{
        nest.getInfo('b097c209-6da0-4317-94ed-5a74b1904fea', function(data) {
            res.json(data);
            console.log('Currently ' + celsiusToFahrenheit(data.current_temperature) + ' degrees fahrenheit');
            console.log('Target is ' + celsiusToFahrenheit(data.target_temperature) + ' degrees fahrenheit');
        });
    });

function celsiusToFahrenheit(temp) {
    return Math.round(temp * (9 / 5.0) + 32.0);
};


module.exports = router;