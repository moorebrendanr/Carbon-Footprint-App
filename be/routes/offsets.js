const express = require("express");
const router = express.Router();
const authenticate = require("../authenticate");
const OffsetsModel = require('../models/offsetsModel');

router.route('/').get(authenticate.verifyUser, function (req, res) {
  OffsetsModel.findOne(
    {username: req.user.username},
    {}, {}, function (err, doc) {
      if (err) {
        res.send(err)
      } else if (doc) {
        res.json({
          success: true,
          document: doc
        })
      } else {
        res.json({
          success: false,
          message: "No document found for user."
        })
      }
    }
  )
});

router.route('/').post(authenticate.verifyUser, function (req, res) {
  let terrapassVal = req.body.terrapass;
  if (!terrapassVal) {
    terrapassVal = 0
  }

  let edenVal = req.body.eden;
  if (!edenVal) {
    edenVal = 0
  }

  const totalVal = 200.4 * (terrapassVal + edenVal);

  const offset = {
    username: req.user.username,
    $inc: {
      terrapass: terrapassVal,
      eden: edenVal,
      total: totalVal
    }
  };

  OffsetsModel.findOneAndUpdate(
    {username: offset.username},
    offset,
    {
      new: true,
      omitUndefined: true,
      upsert: true
    },
    function (err, doc) {
      if (err) {
        res.send(err)
      } else {
        res.json({
          success: true,
          document: doc
        })
      }
    }
  )
});

module.exports = router;