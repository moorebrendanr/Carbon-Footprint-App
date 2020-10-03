const express = require("express");
const router = express.Router();
const authenticate = require("../authenticate");
const DailyLogModel = require("../models/dailyLogModel");
const dateFns = require("date-fns");

function getDailyTotal(diet, travel, electric, gas) {
  return 2.20462 * (diet + travel + electric + gas)
}

function getDietCarbon(meat = 0, nonLocalProduce = 0, packagedMeals = 0) {
  const meatCarbon = meat * 2.205 * 1.3;
  const nonLocalProduceCarbon = nonLocalProduce * 0.0112;
  if (packagedMeals === 0) {
    return meatCarbon + nonLocalProduceCarbon
  } else {
    return 3 * packagedMeals * (meatCarbon + nonLocalProduceCarbon)
  }
}

function getTravelCarbon(carMiles = 0, busMiles = 0, trainMiles = 0, planeMiles = 0) {
  const carCarbon = 0.9685 * carMiles;
  const busCarbon = 0.9685 * busMiles * 0.02;
  const trainCarbon = 0.002 * trainMiles * (19.37/52.0/200.0);
  const planeCarbon = 0.483 * planeMiles;
  return carCarbon + busCarbon + trainCarbon + planeCarbon
}

function getElectricCarbonFromDollars(billCost) {
  return billCost / 0.124
}

function getElectricCarbonFromkWh(kWh) {
  return kWh * 0.99
}

function getGasCarbonFromDollars(billCost) {
  return billCost / 1.86 * 15.5
}

function getGasCarbonFromTherms(therms) {
  return therms * 15.5
}

async function updatePreviousDays(dailyLog, res, electricCarbon, gasCarbon) {
  const date = dateFns.parse(dailyLog.date, "yyyyMMdd", new Date());
  const thirtyDaysAgo = dateFns.sub(date, {days: 30});
  const comparisonDate = dateFns.format(thirtyDaysAgo, "yyyyMMdd");
  const docs = await DailyLogModel.find({
    username: dailyLog.username,
    $and: [{date: {$gt: comparisonDate}}, {date: {$lt: dailyLog.date}}]
    });
  const days = docs.length + 1;
  console.log("days: "+days);
  let electricCarbonPerDay;
  let gasCarbonPerDay;

  if (electricCarbon && gasCarbon) {
    electricCarbonPerDay = electricCarbon / days;
    gasCarbonPerDay = gasCarbon / days;
    console.log("electricCarbonPerDay: "+electricCarbonPerDay);
    console.log("gasCarbonPerDay: "+gasCarbonPerDay);
    docs.forEach((item) => {
      item.updateOne(
        {
          electricCarbon: electricCarbonPerDay,
          gasCarbon: gasCarbonPerDay,
          dailyCarbonTotal: getDailyTotal(item.dietCarbon, item.travelCarbon, electricCarbonPerDay, gasCarbonPerDay)
        }, null, function (err) {
          if (err) {
            res.send(err)
          }
        }
      )
    })
  } else if (electricCarbon) {
    electricCarbonPerDay = electricCarbon / days;
    console.log("electricCarbonPerDay: "+electricCarbonPerDay);
    docs.forEach((item) => {
      item.updateOne(
        {
          electricCarbon: electricCarbonPerDay,
          dailyCarbonTotal: getDailyTotal(item.dietCarbon, item.travelCarbon, electricCarbonPerDay, item.gasCarbon)
        }, null, function (err) {
          if (err) {
            res.send(err)
          }
        }
      )
    })
  } else if (gasCarbon) {
    gasCarbonPerDay = gasCarbon / days;
    console.log("gasCarbonPerDay "+gasCarbonPerDay);
    docs.forEach((item) => {
      item.updateOne(
        {
          gasCarbon: gasCarbonPerDay,
          dailyCarbonTotal: getDailyTotal(item.dietCarbon, item.travelCarbon, item.electricCarbon, gasCarbonPerDay)
        }, null, function (err) {
          if (err) {
            res.send(err)
          }
        }
      )
    })
  }

  return {electricCarbonPerDay: electricCarbonPerDay, gasCarbonPerDay: gasCarbonPerDay}
}

router.route("/").post(authenticate.verifyUser, function (req, res) {
  const dietCarbon = getDietCarbon(req.body.meatServings, req.body.nonLocalProduceServings, req.body.packagedMeals);
  const travelCarbon = getTravelCarbon(req.body.travelCar, req.body.travelBus, req.body.travelTrain, req.body.travelPlane);
  let electricCarbon;
  if (req.body.receivedElectricBill) {
    if (req.body.electricBillUnit === "dollars") {
      electricCarbon = getElectricCarbonFromDollars(req.body.electricBillAmount)
    } else if (req.body.electricBillUnit === "kilowatt_hours") {
      electricCarbon = getElectricCarbonFromkWh(req.body.electricBillAmount)
    }
  }
  let gasCarbon;
  if (req.body.receivedGasBill) {
    if (req.body.gasBillUnit === "dollars") {
      gasCarbon = getGasCarbonFromDollars(req.body.gasBillAmount)
    } else if (req.body.gasBillUnit === "therms") {
      gasCarbon = getGasCarbonFromTherms(req.body.gasBillAmount)
    }
  }

  const dailyLog = {
    username: req.user.username,
    date: req.body.date,
    receivedElectricBill: req.body.receivedElectricBill,
    electricBillAmount: req.body.electricBillAmount,
    electricBillUnit: req.body.electricBillUnit,
    receivedGasBill: req.body.receivedGasBill,
    gasBillAmount: req.body.gasBillAmount,
    gasBillUnit: req.body.gasBillUnit,
    meatServings: req.body.meatServings,
    packagedMeals: req.body.packagedMeals,
    nonLocalProduceServings: req.body.nonLocalProduceServings,
    travelCar: req.body.travelCar,
    travelBus: req.body.travelBus,
    travelBicycle: req.body.travelBicycle,
    travelTrain: req.body.travelTrain,
    travelPlane: req.body.travelPlane,
    travelWalking: req.body.travelWalking,
    dietCarbon: dietCarbon,
    travelCarbon: travelCarbon
  };

  updatePreviousDays(dailyLog, res, electricCarbon, gasCarbon).then((amountPerDay) => {
    if (!amountPerDay.electricCarbonPerDay) {
      dailyLog.electricCarbon = 0
    } else {
      dailyLog.electricCarbon = amountPerDay.electricCarbonPerDay
    }

    if (!amountPerDay.gasCarbonPerDay) {
      dailyLog.gasCarbon = 0
    } else {
      dailyLog.gasCarbon = amountPerDay.gasCarbonPerDay
    }

    dailyLog.dailyCarbonTotal = getDailyTotal(dailyLog.dietCarbon, dailyLog.travelCarbon, dailyLog.electricCarbon, dailyLog.gasCarbon);

    DailyLogModel.findOneAndUpdate({
      username: dailyLog.username,
      date: dailyLog.date
    }, dailyLog, {
      omitUndefined: true
    }, function (err, doc) {
      if (err) {
        res.send(err)
      } else if (doc) {
        res.json({
          success: true,
          document: doc
        })
      } else {
        const newDoc = new DailyLogModel(dailyLog);
        newDoc.save(function (err, doc) {
          if (err) {
            res.send(err)
          } else {
            res.json({
              success: true,
              document: doc
            })
          }
        })
      }
    });
  }).catch((err) => {
    console.log("Error: "+err);
    res.send(err)
  });
});

/**
 * Returns the first N daily logs from user, where N is the given parameter 'number'.
 * Sorted by date.
 */
router.route("/").get(authenticate.verifyUser, function (req, res, next) {
  const number = req.query.number;
  const username = req.user.username;
  console.log("Number: "+number);
  console.log("User: "+username);
  if (!number) {
    console.log("Number not defined");
    return next(new Error("Must define query parameter 'number'"))
  }
  DailyLogModel.find({username: username}, {}, {sort: {date: -1}}, function (err, docs) {
    if (err) {
      res.send(err)
    } else {
      res.json({
        success: true,
        documents: docs.slice(0, number)
      })
    }
  })
});

module.exports = router;