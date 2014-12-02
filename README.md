titanium_google_plus
====================

This Titanium Mobile Module wraps up the google plus API.


Important
---------

The ambitious goal of this project is to mirror the google plus api. That said, only signing in on iOS is currently supported. If you would like to leverage the google account manager to provide a single sign on experience for android, please refer to [this module](https://github.com/Sitata/titanium_google_auth_util) which will be deprecated as this module matures.

Pull requests are welcome!

.signin
-------

```javascript
var google = require('com.sitata.googleplus');
google.setClientId(Alloy.CFG.googleClientIdIos);
google.setScopes(["profile", "email"]);
google.signin({
  success: function(event) {
    if (event.accessToken) {
      doServerPost({provider: 'google', code: event.accessToken});
    } else {
      handleBadPost();
    }
  },
  error: function(event) {
    Ti.API.info("ERROR! " + JSON.stringify(event));
    handleBadPost();
  }
});
```
This should launch a webbrowser to provide sign in for the iOS user. If the iOS user has google plus installed, the api should leverage that app for credentials.

