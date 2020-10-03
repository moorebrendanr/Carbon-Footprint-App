const mongoose = require("mongoose");
const passportLocalMongoose = require("passport-local-mongoose");
const Schema = mongoose.Schema;

const dailyLogSchema = new Schema(
  {
    username: {
      type: String,
      required: true
    },
    date: {
      type: String,
      required: true
    },
    receivedElectricBill: {
      type: Boolean,
      required: true
    },
    electricBillAmount: Number,
    electricBillUnit: String,
    receivedGasBill: {
      type: Boolean,
      required: true
    },
    gasBillAmount: Number,
    gasBillUnit: String,
    meatServings: {
      type: Number,
      default: 0
    },
    packagedMeals: {
      type: Number,
      default: 0
    },
    nonLocalProduceServings: {
      type: Number,
      default: 0
    },
    travelCar: {
      type: Number,
      default: 0
    },
    travelBus: {
      type: Number,
      default: 0
    },
    travelBicycle: {
      type: Number,
      default: 0
    },
    travelTrain: {
      type: Number,
      default: 0
    },
    travelPlane: {
      type: Number,
      default: 0
    },
    travelWalking: {
      type: Number,
      default: 0
    },
    dietCarbon: Number,
    travelCarbon: Number,
    electricCarbon: Number,
    gasCarbon: Number,
    dailyCarbonTotal: Number
  },
  {
    timestamps: true
  }
);

dailyLogSchema.plugin(passportLocalMongoose);
const DailyLogModel = mongoose.model("daily_logs", dailyLogSchema);
module.exports = DailyLogModel;