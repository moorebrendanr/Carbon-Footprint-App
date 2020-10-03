// grab the things we need
const mongoose = require('mongoose');
const passportLocalMongoose = require('passport-local-mongoose');
const Schema = mongoose.Schema;

// create a schema
const userSchema = new Schema({
  name: {
    type: String,
    unique: true,
    required: true,
    trim: true
  },
  username: {
    type: String,
    unique: true,
    required: true,
    trim: true
  },
  email: {
    type: String,
    unique: true,
    required: true,
    trim: true
  }
}, {
  timestamps: true
});

userSchema.plugin(passportLocalMongoose);
const UserModel = mongoose.model('users', userSchema);

module.exports = UserModel;