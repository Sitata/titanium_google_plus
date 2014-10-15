// This is a test harness for your module
// You should do something interesting in this harness
// to test out the module and to provide instructions
// to users on how to use it by example.


// open a single window
var win = Ti.UI.createWindow({
	backgroundColor:'white'
});
var label = Ti.UI.createLabel();


var google = require('com.sitata.titanium.googleplus');
Ti.API.info("module is => " + google);

label.text = "Log in."

// Use the google plus SDK to sign in
label.addEventListener("click", function(e) {

	google.clientId = "INSERT YOUR GOOGLE API CLIENT KEY HERE";
  google.scopes = ["profile"]; // use the new google plus scopes here

  Ti.API.info("LOGGED IN ALREADY? " + google.isLoggedIn());
  Ti.API.info("EXECUTING SIGN IN");
  google.signin({
    success: function(event) {
      Ti.API.info("LOGGED IN! " + JSON.stringify(event));
      /*
      	ON IOS, your Event Object will look something like the following:
					{
						"type":"success",
						"idToken":"eyJhbGciOiJSUzI1NiIsImtpZCI6Ijdk....",
						"expiresIn":3600,
						"accessToken":"ya29.nQBxG9pfZvpczd_Dlkasd,xld9IlLlwISoYRAnpqDe6se2NxaRNia",
						"code":"4/SEnWWxliE_Oaj3q3AUdl39adx.kd9geXmXvfARQvth2CycOkgI",
						"userEmail":"some@email.com",
						"refreshToken":"1/KC7VdqretsT1hoXWD_Jal83dlskSDPykoY7Y58",
						"source": {
							"id":"com.sitata.titanium.googleplus"
						}
					}

				If you are already logged in, the event will not contain any extra information:
					{
						"source": {
							"id": "com.sitata.titanium.googleplus"},
							"type": "success"
						}
					}
      */

    },
    error: function(event) {
      Ti.API.info("ERROR! " + JSON.stringify(event));
    }
  });

});


win.add(label);
win.open();

