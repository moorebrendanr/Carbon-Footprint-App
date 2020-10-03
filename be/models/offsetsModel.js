const mongoose = require("mongoose");
const passportLocalMongoose = require("passport-local-mongoose");
const Schema = mongoose.Schema;

const offsetSchema = new Schema(
  {
    username: {
      type: String,
      required: true
    },
    terrapass: Number,
    eden: Number,
    total: Number
  },
  {
    timestamps: true
  }
);

offsetSchema.plugin(passportLocalMongoose);
const OffsetModel = mongoose.model("offsets", offsetSchema);
module.exports = OffsetModel;