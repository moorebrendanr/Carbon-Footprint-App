var qs = require("querystring");
var http = require("https");

var options = {
  "method": "POST",
  "hostname": "api.home.nest.com",
  "port": null,
  "path": "/oauth2/access_token",
  "headers": {
    "content-type": "application/x-www-form-urlencoded"
  }
};

var req = http.request(options, function (res) {
  var chunks = [];

  res.on("data", function (chunk) {
    chunks.push(chunk);
  });

  res.on("end", function () {
    var body = Buffer.concat(chunks);
    console.log(body.toString());
  });
});

req.write(qs.stringify({ code: 'AUTH_CODE',
  client_id: 'CLIENT_ID',
  client_secret: 'CLIENT_SECRET',
  grant_type: 'authorization_code' }));
req.end();