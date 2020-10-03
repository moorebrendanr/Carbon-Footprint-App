const express = require('express');
const router = express.Router();
const authenticate = require('../authenticate');
const RefreshTokenModel = require('../models/refreshTokenModel');
const SignupModel = require('../models/usermodel');

router.get('/', function (req, res) {
  const username = req.query.username;
  const refreshToken = req.query.refreshToken;
  if (!username) {
    res.send(new Error("No user was given."));
    return;
  }
  if (!refreshToken) {
    res.send(new Error("No refresh token was given."));
    return;
  }

  SignupModel.findOne({username: username}, {}, {}, function (err, user) {
    if (err) {
      res.send(err)
    } else if (user) {
      authenticate.verifyRefreshToken(refreshToken).then(result => {
        if (result === false) {
          res.status(401).send("Invalid refresh token.")
        } else {
          const token = authenticate.getToken({_id: user._id});
          res.json({
            success: true,
            message: "Successfully refreshed access token",
            token: token
          })
        }
      })
        .catch(err => {
          console.log(err);
          res.status(401).send("Invalid refresh token.")
        })
    } else {
      res.status(401).send("Invalid refresh token.")
    }
  });
});

function invalidateToken(refreshToken, callback) {
  const invalidatedToken = new RefreshTokenModel({
    token: refreshToken
  });
  invalidatedToken.save(function (err, doc) {
    if (err) {
      callback(err)
    } else {
      callback(null, doc)
    }
  })
}

// Invalidate a refresh token
router.post('/', function (req, res) {
  const refreshToken = req.body.token;
  if (!refreshToken) {
    res.send(new Error("No refresh token was given."));
    return;
  }

  invalidateToken(refreshToken, function (err, doc) {
    if (err) {
      res.send(err)
    } else {
      res.json({
        success: true,
        message: "Token has been invalidated",
        doc: doc
      })
    }
  });
});

module.exports = router;