const mongoose = require('mongoose');
const Schema = mongoose.Schema;

// Model to store refresh tokens.
const refreshTokenSchema = new Schema({
  token: {
    type: String,
    required: true
  }
}, {
  timestamps: true
});

const RefreshTokenModel = mongoose.model("invalidated_tokens", refreshTokenSchema);
module.exports = RefreshTokenModel;