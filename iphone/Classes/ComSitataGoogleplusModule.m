/**
 * titanium_google_plus
 *
 * Created by Your Name
 * Copyright (c) 2014 Your Company. All rights reserved.
 */

#import "ComSitataGoogleplusModule.h"
#import "TiBase.h"
#import "TiHost.h"
#import "TiUtils.h"

#import "TiApp.h"
#import <GoogleOpenSource/GoogleOpenSource.h>


@implementation ComSitataGoogleplusModule

#pragma mark Internal

// this is generated for your module, please do not change it
-(id)moduleGUID
{
	return @"f082bd88-a035-4c3f-b376-2e96ec5ff1dc";
}

// this is generated for your module, please do not change it
-(NSString*)moduleId
{
	return @"com.sitata.titanium.googleplus";
}

#pragma mark Lifecycle

-(void)startup
{
	// this method is called when the module is first loaded
	// you *must* call the superclass
    [super startup];
	NSLog(@"[INFO] %@ loaded",self);
}

-(void)shutdown:(id)sender
{
	// this method is called when the module is being unloaded
	// typically this is during shutdown. make sure you don't do too
	// much processing here or the app will be quit forceably

	// you *must* call the superclass
	[super shutdown:sender];
}

#pragma mark Cleanup

-(void)dealloc
{
    [[GPPSignIn sharedInstance] release];
    RELEASE_TO_NIL(clientId);
    RELEASE_TO_NIL(scopeArr);
    RELEASE_TO_NIL(successCallback);
    RELEASE_TO_NIL(errorCallback);
    
	// release any resources that have been retained by the module
	[super dealloc];
}

#pragma mark Internal Memory Management

-(void)didReceiveMemoryWarning:(NSNotification*)notification
{
	// optionally release any resources that can be dynamically
	// reloaded once memory is available - such as caches
	[super didReceiveMemoryWarning:notification];
}

#pragma mark Listener Notifications

-(void)_listenerAdded:(NSString *)type count:(int)count
{
    NSLog(@"Listener Added - %@", type);
    
	if (count == 1 && [type isEqualToString:@"my_event"])
	{
		// the first (of potentially many) listener is being added
		// for event named 'my_event'
	}
}

-(void)_listenerRemoved:(NSString *)type count:(int)count
{
    NSLog(@"Listener Removed - %@", type);
    
	if (count == 0 && [type isEqualToString:@"my_event"])
	{
		// the last listener called for event named 'my_event' has
		// been removed, we can optionally clean up any resources
		// since no body is listening at this point for that event
	}
}

// Setup GPPSign in shared instance with config from user.
-(void)setupSignIn
{
    GPPSignIn *signIn = [GPPSignIn sharedInstance];
    signIn.shouldFetchGooglePlusUser = YES;   // fetch the google plus profile if possible
    signIn.shouldFetchGoogleUserID = YES;     // fetch google user id
    signIn.shouldFetchGoogleUserEmail = YES;  // return the user's email too
    // If attemptSSO is true, try to authenticate with the Google+ app, if installed.
    signIn.attemptSSO = YES;
    signIn.clientID = clientId;
    signIn.scopes = (scopeArr == nil ? [NSMutableArray new] : scopeArr);
    NSString * language = [[NSLocale preferredLanguages] objectAtIndex:0];
    signIn.language = language;
    signIn.delegate = self;

}


#pragma GPPSignInDelegates

// This is called by the google routines and should return with extra authentication
// details or an error.
- (void)finishedWithAuth: (GTMOAuth2Authentication *)auth
                   error: (NSError *) error {
    NSLog(@"[INFO] Finished authentication from google.");
    NSMutableDictionary *event = [NSMutableDictionary dictionary];
    if (error) {
        loggedIn = false;
        // Pass back the error string
        [event setValue:[auth errorString] forKey:@"error"];
        if (errorCallback) {
            NSLog(@"[INFO] Firing Cancel Event.");
            [self _fireEventToListener:@"error" withObject:nil listener:successCallback
                            thisObject:nil];
        }
    } else {
        loggedIn = true;
        // Set data
        [event setValue:[[GPPSignIn sharedInstance] idToken] forKey:@"idToken"];
        [event setValue:[[GPPSignIn sharedInstance] userEmail] forKey:@"accountId"];
        [event setValue:[auth accessToken] forKey:@"accessToken"];
        [event setValue:[auth refreshToken] forKey:@"refreshToken"];
        [event setValue:[auth expiresIn] forKey:@"expiresIn"];
        [event setValue:[auth code] forKey:@"code"];
        if (successCallback)
        {
            NSLog(@"[INFO] Firing Success Event.");
            [self _fireEventToListener:@"success" withObject:event listener:successCallback thisObject:nil];
        }
    }
}




// Handle when we resume back from the login process.
// GPPURLHandler will fire and proceed to collect addition user information
// if the url scheme matches what is defined in the plist. GPPURLHandler routine
// will call finishedWithAuth.
-(BOOL)handleRelaunch
{
    NSDictionary *launchOptions = [[TiApp app] launchOptions];
    if (launchOptions!=nil)
    {
        NSURL *url = [launchOptions objectForKey:UIApplicationLaunchOptionsURLKey];
        NSString *sourceApplication = [launchOptions objectForKey:UIApplicationLaunchOptionsSourceApplicationKey];
        id annotation = [launchOptions objectForKey:UIApplicationLaunchOptionsAnnotationKey];

        if (url != nil && sourceApplication != nil) {
            return [GPPURLHandler handleURL:url
                          sourceApplication:sourceApplication
                                 annotation:annotation];
        }

    }
    return NO;
}

// Handle when titanium issues resume
-(void)resumed:(id)note
{
    [self handleRelaunch];
}


#pragma Public APIs

// Starts the authentication process.
// It provides single sign-on via the Google+ app (if installed), Chrome for iOS (if installed),
// or Mobile Safari.
-(void)signin:(id)args
{
    ENSURE_SINGLE_ARG(args,NSDictionary);
    id success = [args objectForKey:@"success"];
    id error = [args objectForKey:@"error"];
    RELEASE_TO_NIL(successCallback);
    RELEASE_TO_NIL(errorCallback);
    successCallback = [success retain];
    errorCallback = [error retain];
    
    [self setupSignIn];
    GPPSignIn *gps = [GPPSignIn sharedInstance];
    if ([gps hasAuthInKeychain]) {
        GTMOAuth2Authentication *auth = [gps authentication];
        [self finishedWithAuth:auth error:NULL];
    } else {
        [[GPPSignIn sharedInstance] authenticate];
    }
}

// Removes the OAuth 2.0 token from the keychain.
-(void)signout:(id)args
{
    [[GPPSignIn sharedInstance] signOut];
}

// Disconnects the user from the app and revokes previous authentication.
-(void)disconnect:(id)args
{
    [[GPPSignIn sharedInstance] disconnect];
}

// Returns true if user is currently logged in.
-(BOOL)isLoggedIn:(id)args
{
    return [[GPPSignIn sharedInstance] hasAuthInKeychain];
}


/**
 * JS:
 * 
 * var googleplus = require('com.sitata.titanium.googleplus');
 * googleplus.clientId = "absld";
 *
 */
-(void)setClientId:(id)arg
{
    [clientId autorelease];
    clientId = [[TiUtils stringValue:arg] copy];
}
/**
 * JS:
 *
 * var googleplus = require('com.sitata.titanium.googleplus');
 * Ti.API.info(googleplus.clientId);
 *
 */
-(id)getClientId
{
    return clientId;
}
/**
 * JS:
 *
 * var googleplus = require('com.sitata.titanium.googleplus');
 * googleplus.clientId = "absld";
 *
 */
-(void)setScopes:(id)args
{
    scopeArr = [NSMutableArray new];

    ENSURE_TYPE_OR_NIL(args, NSArray);
    for (id item in args) {
        NSString *temp = [TiUtils stringValue:item];
        [scopeArr addObject:temp];
    }
}
/**
 * JS:
 *
 * var googleplus = require('com.sitata.titanium.googleplus');
 * Ti.API.info(googleplus.clientId);
 *
 */
-(id)getScopes
{
    return scopeArr;
}

@end
