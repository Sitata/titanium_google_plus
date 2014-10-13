/**
 * titanium_google_plus
 *
 * Created by Your Name
 * Copyright (c) 2014 Your Company. All rights reserved.
 */

#import "TiModule.h"
#import <GooglePlus/GooglePlus.h>


@protocol TiFGooglePlusSigninStateListener
@required
-(void)signin;
-(void)signout;
@end



@interface ComSitataTitaniumGoogleplusModule : TiModule <GPPSignInDelegate>
{
    BOOL loggedIn;
    NSString *clientId;
    NSArray *scopeArr;
    
    KrollCallback *successCallback;
    KrollCallback *errorCallback;
}




-(BOOL)isLoggedIn;
-(void)signin:(id)args;
-(void)signout:(id)args;
-(void)disconnect:(id)args;


@end
