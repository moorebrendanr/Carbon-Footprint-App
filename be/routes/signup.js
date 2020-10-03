var express = require('express');
var router = express.Router();
var passport = require('passport');
var UserModel = require('../models/usermodel');
var authenticate = require('../authenticate');
var error;

router.post('/', function (req, res, next) {
    console.log("Inside signup nodejs");
    // console.log(req.body.password+'  '+req.body.confirmPassword);
    if (req.body.password !== req.body.confirmPassword) {
        /*var err = new Error('Passwords do not match');
        err.status = 400;*/
        res.send("Passwords don't match");
        console.log("Passwords don't match");
        //return next(err);

    }
    if (req.body.password && req.body.confirmPassword && req.body.name
        && req.body.username && req.body.email) {
        console.log("all fields filled out");
        let user = new UserModel({
            name: req.body.name,
            username: req.body.username,
            email: req.body.email
        });
        UserModel.register(user, req.body.password, (err, user) => {
            if (err) {
                console.log("Error registering");
                res.send(err)
            }
            else {
                passport.authenticate('local')(req, res, () => {
                    console.log("registered")
                    res.json({ success: true, status: 'User registered successfully' });
                    console.log("User registered successfully");
                });
            }
        });
    } else {
        console.log("Signup unsuccessful")
    }
});

router.get('/login', function (req, res, next) {
    console.log("successfully posted login. Now trying...");
    passport.authenticate('local', function (err, user, info) {
        if (err) { 
            error = next(err);
            console.log("We have an error:" + error);
            return error;
        }
        if (!user) { 
            return res.json({ success: false, message: info.message });
        }
        req.logIn(user, function (err) {
            if (err) { return next(err); }
            const token = authenticate.getToken({_id: user._id});
            const refreshToken = authenticate.getRefreshToken({_id: user._id});
            res.json({ success: true, token: token, refreshToken: refreshToken, message: 'You are successfully logged in!' });
        });

    })(req, res, next);

});
module.exports = router;

