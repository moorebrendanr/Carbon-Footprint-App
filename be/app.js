const express = require('express');
const path = require('path');
const logger = require('morgan');
const cookieParser = require('cookie-parser');
const bodyParser = require('body-parser');
const passport = require('passport');
const mongoose = require('mongoose');
const index = require('./routes/index');
const users = require('./routes/users');
const wemo = require('./routes/wemo');
const signup = require('./routes/signup');
const questions = require('./routes/questions');
const nest = require('./routes/nest');
const dailyLog = require('./routes/dailyLog');
const offsets = require('./routes/offsets');
const cors = require('cors');
const config = require('./config');
const token = require('./routes/token');

const app = express();

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');

app.use(cors());
app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));
app.use(passport.initialize());

app.use('/', index);
app.use('/users', users);
app.use('/wemo',wemo);
app.use('/signup',signup);
app.use('/question',questions);
app.use('/nest',nest);
app.use('/dailylog', dailyLog);
app.use('/offsets', offsets);
app.use('/token', token);


const url = config.mongoUrl;
mongoose.Promise = global.Promise;
mongoose.connection.openUri(url);
mongoose.set('useFindAndModify', false);
//mongoose.createConnection(url);


// catch 404 and forward to error handler
app.use(function(req, res, next) {
  const err = new Error('Not Found');
  err.status = 404;
  next(err);
});

// error handler
app.use(function(err, req, res) {
  // set locals, only providing error in development
  res.locals.message = err.message;
  res.locals.error = req.app.get('env') === 'development' ? err : {};

  // render the error page
  res.status(err.status || 500);
  res.render('error');
});

module.exports = app;
