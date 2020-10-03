var passport = require('passport');
var LocalStrategy = require('passport-local').Strategy;
var User = require('./models/usermodel');
var JwtStrategy = require('passport-jwt').Strategy;
var ExtractJwt = require('passport-jwt').ExtractJwt;
var jwt = require('jsonwebtoken');
const RefreshTokenBlacklist = require('./models/refreshTokenModel');

var config = require('./config.js');

exports.local = passport.use(new LocalStrategy(User.authenticate()));
passport.serializeUser(User.serializeUser());
passport.deserializeUser(User.deserializeUser());

exports.getToken = function(user) {
  return jwt.sign(user, config.secretKey,
        {expiresIn: "15m"});
};

exports.getRefreshToken = function (user) {
  return jwt.sign(user, config.refreshKey);
};

exports.verifyRefreshToken = async function (refreshToken) {
  if (jwt.verify(refreshToken, config.refreshKey)) {
    try {
      const doc = await RefreshTokenBlacklist.findOne({token: refreshToken}, null, null).exec();
      return !doc
    } catch (e) {
      throw e
    }
  } else {
    return false
  }
};

var opts = {};
opts.jwtFromRequest = ExtractJwt.fromAuthHeaderAsBearerToken();
opts.secretOrKey = config.secretKey;

exports.jwtPassport = passport.use(new JwtStrategy(opts,
    (jwt_payload, done) => {
        console.log("JWT payload: ", jwt_payload);
        User.findOne({_id: jwt_payload._id}, (err, user) => {
            if (err) {
                return done(err, false);
            }
            else if (user) {
                return done(null, user);
            }
            else {
                return done(null, false);
            }
        });
    }));

exports.verifyUser = passport.authenticate('jwt', {session: false});