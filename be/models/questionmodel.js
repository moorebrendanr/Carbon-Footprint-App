// grab the things we need
var mongoose = require("mongoose");
var passportLocalMongoose = require("passport-local-mongoose");
var Schema = mongoose.Schema;

// create a schema
var questionSchema = new Schema(
  {
    username: {
      type: String,
      required: true,
      trim: true
    },
    diets: [String],
    car: {
      type: Boolean,
      required: true
    },
    miles: {
      type: Number
    },
    power: {
      type: Number,
      required: true,
      trim: true
    },
    gas: {
      type: Boolean,
      required: true
    },
    therms: {
      type: Number
    },
    flown: {
      type: Boolean,
      required: true
    },
    flymiles: {
      type: Number
    },
    housearea: {
      type: Number,
      required: true
    },
    people: {
      type: Number,
      required: true
    },
    score: {
      type: Number,
      required: true
    }
  },
  {
    timestamps: true
  }
);

questionSchema.plugin(passportLocalMongoose);

var QuestionModel = mongoose.model("Questions", questionSchema);

module.exports = QuestionModel;
