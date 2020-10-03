var express = require("express");
var router = express.Router();
var authenticate = require("../authenticate");
var QuestionModel = require("../models/questionmodel");

router.route("/").post(authenticate.verifyUser, (req, res) => {
  var question = new QuestionModel({
    username: req.user.username,
    diets: req.body.diets,
    car: req.body.car,
    miles: req.body.miles,
    gas: req.body.gas,
    therms: req.body.therms,
    power: req.body.power,
    flown: req.body.flown,
    flymiles: req.body.flymiles,
    housearea: req.body.housearea,
    people: req.body.people,
    score: req.body.score
  });

  // Should save new object to database every time submit is clicked
  console.log("attempting to save object to database");
  question.save((err, response) => {
    if (err) {
      res.send(err);
    } else {
      res.json({
        message: "Successfully added object to database",
        response: response
      });
    }
  });
});

router.route("/").get(authenticate.verifyUser, (req, res) => {
  QuestionModel.findOne({ username: req.user.username }, (err, doc) => {
    if (err) {
      res.send(err);
    } else {
      res.json({
        success: true,
        document: doc
      });
    }
  });
});

router.route("/").patch(authenticate.verifyUser, (req, res) => {
  const updateParams = {
    diets: req.body.diets,
    car: req.body.car,
    miles: req.body.miles,
    gas: req.body.gas,
    therms: req.body.therms,
    power: req.body.power,
    flown: req.body.flown,
    flymiles: req.body.flymiles,
    housearea: req.body.housearea,
    people: req.body.people,
    score: req.body.score
  };

  QuestionModel.updateOne(
    { username: req.user.username },
    updateParams,
    { omitUndefined: true },
    (err, writeOpResult) => {
      if (err) {
        res.send(err)
      } else {
        res.json({
          success: true,
          result: writeOpResult
        })
      }
    }
  )
});

module.exports = router;
