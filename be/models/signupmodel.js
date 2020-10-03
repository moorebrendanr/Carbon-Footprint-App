// grab the things we need
var mongoose = require('mongoose');
var passportLocalMongoose = require('passport-local-mongoose');
var Schema = mongoose.Schema;

// create a schema
var signupSchema = new Schema({
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

signupSchema.plugin(passportLocalMongoose);
var Signup = mongoose.model('users', signupSchema);

module.exports = Signup;