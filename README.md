Playnomics PlayRM Android SDK Integration Guide
=============================================

[![Build Status](https://api.travis-ci.org/playnomics/playnomics-android.png)](https://travis-ci.org/playnomics/playnomics-android)

## Considerations for Cross-Platform Applications

If you want to deploy your application to multiple platforms (eg: iOS, Android, etc), you'll need to create a separate Playnomics Applications in the control panel. Each application must incorporate a separate `<APPID>` particular to that application. In addition, message frames and their respective creative uploads will be particular to that app in order to ensure that they are sized appropriately - proportionate to your application screen size.


Getting Started
===============

## Download and Installing the SDK

You can download the SDK from our [releases page](https://github.com/playnomics/playnomics-android/releases).

You can also forking this [repo](https://github.com/playnomics/playnomics-android), building the PlaynomicsSDK project.

* Add the PlaynomicsAndroidSDK.jar file or the project to your Android application's build path.

* Add the following permissions to your Android application's manifest file if they don't already exist:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

## Starting a PlayRM Session

To start logging automatically tracking user engagement data, you need to first start a session. **No other SDK calls will work until you do this.**

In the root `Activity` of your application, start the PlayRM Session in the `onCreateMethod`:

```java

import com.playnomics.android.sdk.Playnomics;

class YourActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        final long applicationId = <APPID>L;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //optionally set the log level for the SDK, default is ERROR
        Playnomics.setLogLevel(LogLevel.VERBOSE);
        Playnomics.start(this, applicationId);
    }
}
```

You can either provide a dynamic `<USER-ID>` to identify each user:

```objectivec
public static void start(Context context, long applicationId, String userId);
```

or have PlayRM, generate a *best-effort* unique-identifier for the user:

```objectivec
public static void start(Context context, long applicationId);
```

If you do choose to provide a `<USER-ID>`, this value should be persistent, anonymized, and unique to each user. This is typically discerned dynamically when a user starts the application. Some potential implementations:

* An internal ID (such as a database auto-generated number).
* A hash of the user's email address.

**You cannot use the user's Facebook ID or any personally identifiable information (plain-text email, name, etc) for the `<USER-ID>`.**

### IMPORTANT

In every `Activity`, you will need to also notify the SDK when your application is pausing and resuming:

```java
@Override
protected void onResume() {
    Playnomics.onActivityResumed(this);
    super.onResume();
}

@Override
protected void onPause() {
    Playnomics.onActivityPaused(this);
    super.onPause();
}
```

Messaging Integration
=====================
This guide assumes you're already familiar with the concept of placements and messaging, and that you have all of the relevant `placements` setup for your application.

If you are new to PlayRM's messaging feature, please refer to <a href="http://integration.playnomics.com" target="_blank">integration documentation</a>.

Once you have all of your placements created with their associated `<PLACEMENT-ID>`s, you can start the integration process.

## SDK Integration

We recommend that you preload all of your placements when your application loads, so that you can quickly show a placement when necessary:

```java
public static void preloadPlacements(String... placementNames);
```

```java
//...
public void onCreate(Bundle savedInstanceState) {
    //...
    Playnomics.start(this, applicationId);
    Playnomics.preloadPlacements("appStart", "levelComplete");
}
```

Then when you're ready, you can show the placement:

```java
public static void showPlacement(String placementName, Activity activity);
```

<table>
    <thead>
        <tr>
            <th>Name</th>
            <th>Type</th>
            <th>Description</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td><code>placementName</code></td>
            <td>String</td>
            <td>Unique identifier for the placement</td>
        </tr>
        <tr>
            <td><code>activity</code></td>
            <td>Activity</td>
            <td>The Activity where you are showing the Placement.</td>
        </tr>
    </tbody>
</table>

Optionally, associate an implementation of the `IPlaynomicsPlacementDelegate` interface, to process rich data callbacks. See [Using Rich Data Callbacks](#using-rich-data-callbacks) for more information.


```java
public static void showPlacement(String placementName, Activity activity,
            IPlaynomicsPlacementDelegate delegate);
```
<table>
    <thead>
        <tr>
            <th>Name</th>
            <th>Type</th>
            <th>Description</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td><code>placementName</code></td>
            <td>String</td>
            <td>Unique identifier for the placement</td>
        </tr>
        <tr>
            <td><code>activity</code></td>
            <td>Activity</td>
            <td>The Activity where you are showing the Placement.</td>
        </tr>
        <tr>
            <td><code>delegate</code></td>
            <td>id&lt;IPlaynomicsPlacementDelegate&gt;</td>
            <td>
                Processes rich data callbacks, see <a href="#using-rich-data-callbacks">Using Rich Data Callbacks</a>.
            </td>
        </tr>
    </tbody>
</table>

## Using Rich Data Callbacks

Using an implementation of `IPlaynomicsPlacementDelegate` your application can receive notifications when a placement is:

* Is shown in the screen.
* Receives a touch event on the creative.
* Is dismissed by the user, when they press the close button.
* Can't be rendered in the view because of connectivity or other issues.

```java
public interface IPlaynomicsPlacementDelegate {
    void onShow(Map<String, Object> jsonData);

    void onTouch(Map<String, Object> jsonData);

    void onClose(Map<String, Object> jsonData);

    void onRenderFailed();
}
```

For each of these events, your delegate may also receive Rich Data that has been tied with this creative. Rich Data is a JSON message that you can associate with your message creative. In all cases, the `jsonData` value can be `null`.

The actual contents of your JSON message can be delayed until the time of the messaging campaign configuration. However, the structure of your message needs to be decided before you can process it in your application. See [example use-cases for rich data](#example-use-cases-for-rich-data) below.

## Validate Integration
After you've finished the installation, you should verify that your application is correctly integrated by checkout the integration verification section of your application page.

Using Android SDK v1.1.0+ you can register your device as a Test Device and validate your events on the self-check page for your application: **`https://controlpanel.playnomics.com/applications/<APPID>`**

To test your in-app campaigns, you can enter your device's Android ID and select which segments to fall into.  Optionally, you can opt to not select any segments to simply see your device's data flowing through the validator.

This page will update with events as they occur in real-time, with any errors flagged. 

We strongly recommend running the self-check validator before deploying your newly integrated application to production.

Full Integration
=================

<div class="outline">
    <ul>
        <li>
            <a href="#monetization">Monetization</a>
        </li>
        <li>
            <a href="#install-attribution">Install Attribution</a>
        </li>
        <li>
            <a href="#custom-event-tracking">Custom Event Tracking</a>
        </li>
        <li>
            <a href="#push-notifications">Push Notifications</a>
        </li>
        <li>
            <a href="#example-use-cases-for-rich-data">Example Use-Cases for Rich Data</a>
        </li>
        <li>
            <a href="#support-issues">Support Issues</a>
        </li>
        <li>
            <a href="#change-log">Change Log</a>
        </li>
    </ul>
</div>

## Monetization

PlayRM allows you to track monetization through in-app purchases denominated in real US dollars.

```java
public static void transactionInUSD(float priceInUSD, int quantity);
```
<table>
    <thead>
        <tr>
            <th>Name</th>
            <th>Type</th>
            <th>Description</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td><code>priceInUSD</code></td>
            <td>float</td>
            <td>The price of the item in USD.</td>
        </tr>
        <tr>
            <td><code>quantity</code></td>
            <td>int</td>
            <td>
               The number of items being purchased at the price specified.
            </td>
        </tr>
    </tbody>
</table>


```java
float priceInUSD = 0.99f;
int quantity = 1;

Playnomics.transactionInUSD(priceInUSD, quantity);
```

## Install Attribution

PlayRM allows you track and segment based on the source of install attribution. You can track this at the level of a source like *AdMob* or *MoPub*, and optionally include a campaign and an install date. By default, PlayRM tracks the install date by the first day we started seeing engagement date for your user.

```java
public static void attributeInstall(String source);

public static void attributeInstall(String source, String campaign);

public static void attributeInstall(String source, String campaign,
            Date installDateUtc);
```
<table>
    <thead>
        <tr>
            <th>Name</th>
            <th>Type</th>
            <th>Description</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td><code>source</code></td>
            <td>String</td>
            <td>The source of install.</td>
        </tr>
        <tr>
            <td><code>campaign</code></td>
            <td>String</td>
            <td>
               The campaign for this source.
            </td>
        </tr>
        <tr>
            <td><code>installDate</code></td>
            <td>Date</td>
            <td>
               The date this user installed your app.
            </td>
        </tr>
    </tbody>
</table>

```java

String source = "AdMob";
String campaign = "Holiday";
GregorianCalendar cal = new GregorianCalendar();
Date installDate = cal.getTime();

Playnomics.attributeInstall(source, campaign, installDate);
```

## Custom Event Tracking

Custom events may be defined in a number of ways.  They may be defined at certain key gameplay points like, finishing a tutorial, or may they refer to other important events in a user's lifecycle. Users can be segmented based on when and how many times they have achieved a particular event.

Each time a user reaches a event, track it with this call:

```java
public static void customEvent(String customEventName);
```
<table>
    <thead>
        <tr>
            <th>Name</th>
            <th>Type</th>
            <th>Description</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td><code>customEventName</code></td>
            <td>String</td>
            <td>
                A name to describe your event.
            </td>
        </tr>
    </tbody>
</table>

Example client-side calls for a user reaching a event, with generated IDs:

```objectivec
String eventName = "level 1 complete";
Playnomics.customEvent(eventName);
```

Push Notifications
==================

PlayRM Android currently only supports working with Google Cloud Messaging. You should review **[Google's documentation](http://developer.android.com/google/gcm/index.html)** before starting this part of the integration.

At a high level, the Google Cloud Messaging architecture requires:

* A [Google Cloud API Project](http://developer.android.com/google/gcm/gs.html) that you create. This application will have a **Project ID** (the sender ID) and a `private API key`.
    * In the Control Panel, you provide Playnomics with your `private API Key` authorizing us to send messages to your users
* Your Android application needs [Google Play Services](http://developer.android.com/google/play-services/setup.html) setup.
    * Adding Google Play Services allows your application to communicate with the Google Cloud Messaging and other Google Services.
    * Using this service requires that the device has the most recent version of the Google Play Services apk installed to register and receive push notifications. Users typically have this updated automatically, but in the event that they don't, we provide some sample code for how to prompt the user to do so.
* Your Android application also needs the `android-support-v4.jar` added to its build path.
* Your Android application's manifest file needs to include receivers and services related to push notifications.

## Edit Your Android Manifest File

Add the following permissions your **AndroidManifest.xml** file:

```xml
<uses-permission android:name="android.permission.GET_ACCOUNTS" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="com.google.android.c2dm.permission.RECEIVE" />

<permission android:name="com.example.gcm.permission.C2D_MESSAGE"
    android:protectionLevel="signature" />
<uses-permission android:name="com.example.gcm.permission.C2D_MESSAGE" />
```

Add the following under the `application`:

```xml
<!-- google play services -->
<meta-data android:name="com.google.android.gms.version"
   android:value="@integer/google_play_services_version" />
<!-- push receiver -->
<receiver
    android:name="com.playnomics.android.push.GcmBroadcastReceiver"
    android:permission="com.google.android.c2dm.permission.SEND" >
    <intent-filter>
        <action android:name="com.google.android.c2dm.intent.RECEIVE" />
        <category android:name="YOUR_PACKAGE_NAME" />
    </intent-filter>
    <intent-filter>
        <action android:name="com.playnomics.android.push.PUSH_OPENED" />
        <category android:name="YOUR_PACKAGE_NAME" />
    </intent-filter>
</receiver>
<!-- push background service -->
<service android:name="com.playnomics.android.push.GcmIntentService" />
```

`YOUR_PACKAGE_NAME` is the package from the `manifest` node:

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="YOUR_PACKAGE_NAME"
    android:versionCode="1"
    android:versionName="1.0" >
```

## Register for Push Notifications

To enable push notifcations with Google Play, you need to first implement two interfaces: `com.playnomics.android.sdk.IGoogleCloudMessageConfig` and `com.playnomics.android.sdk.IPushNotificationDelegate`.

`com.playnomics.android.sdk.IGoogleCloudMessageConfig` gives the PlayRM SDK with your configuration settings for push notifications.

```java
public interface IGoogleCloudMessageConfig extends IPushConfig {
    /*
     * Provides the SDK with your Google API project's Project ID. This is ID is associated
     * with the private key that your provide to Playnomics.
     *
     * @returns Your Google API Project's Project ID
     */
    public String getSenderId();
}

```

```java
public abstract interface IPushConfig {
    /*
     * Provides the SDK with a destination to send the user when they interact with
     * your push notification. This activity's intent will be flagged as FLAG_ACTIVITY_NEW_TASK and
     * FLAG_ACTIVITY_CLEAR_TOP, so that a new version of this activity will be launched.
     *
     * @result Returns an Class for a derivative of Activity.
     */

    public Class<?> getNotificationDestination();

    /*
     * Provides the SDK with the icon resource that should be shown adjacent to message in the Notification
     * Manager. This can be the same as your application's icon resource.
     */
    public int getNotificationIcon();
}
```

`com.playnomics.android.sdk.IPushNotificationDelegate` provides your application with important notifications about the registration process.

```java
public interface IPushNotificationDelegate {
    /*
     * Called when the user has been successfully registered for push notifications. 
     *
     * @param registrationId The SDK automatically tracks this ID. This is provided for your own logging and debugging.
     */
    public void onPushRegistrationSuccess(String registrationId);

    /*
     * Called when the SDK could not successfully register for push notifications. Google Play Services is not 
     * installed on the device.
     */
    public void onPushRegistrationFailure();

    /*
     * Called when the SDK could not successfully register for push notifications. This maybe due to network 
     * connectivity at the time we attempted to register this device.
     *
     * @param ex Root cause of registration failure.
     */
    public void onPushRegistrationFailure(Exception ex);

    /*
     * Called  when the SDK could not successfully register for push notifications. This likely due to the 
     * user's device having an out-dated version of Google Play Services.
     *
     * @param Error code received when registering for push notifications.
     */
    public void onPushRegistrationFailure(int errorCode);
}
```

Then pass your implementations of these interfaces into:

```java
static void enablePushNotifications(IPushConfig pushConfig, IPushNotificationDelegate notificationDelegate);
```

You should do this after the session has started.

## Sample Implementation

Below is a sample implementation:

```java

public class YourActivity
    extends Activity
    implements IGoogleCloudMessageConfig, IPushNotificationDelegate {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        final long applicationId = <APPID>L;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Playnomics.setLogLevel(LogLevel.VERBOSE);
        Playnomics.start(this, applicationId);
        //call this after start the session
        Playnomics.enablePushNotifications(this, this);
    }

    @Override
    public Class<?> getNotificationDestination() {
        return PlaynomicsTestAppActivity.class;
    }

    @Override
    public int getNotificationIcon() {
        return R.drawable.ic_launcher;
    }

    @Override
    public String getSenderId() {
        return "463115531919";
    }

    @Override
    public void onPushRegistrationSuccess(String registrationId) {
        Log.d(this.getClass().getName(), String.format("Registered device %s", registrationId));
    }

    @Override
    public void onPushRegistrationFailure() {
        Log.e(this.getClass().getName(), "Failed to register device");
    }

    @Override
    public void onPushRegistrationFailure(Exception ex) {
        Log.e(this.getClass().getName(), "Failed to register device", ex);
    }

    @Override
    public void onPushRegistrationFailure(int errorCode) {
        final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
        Log.e(this.getClass().getName(), "Failed to register device, GooglePlayServices is out of date");

        if (GooglePlayServicesUtil.isUserRecoverableError(errorCode)) {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(errorCode, this,
                    PLAY_SERVICES_RESOLUTION_REQUEST);
            
            if(dialog != null){
                dialog.show();
            }
        }
    }
}

```

Example Use-Cases for Rich Data
===============================

Here are three common use cases for placements and a messaging campaigns:

* [Game Start Placement](#game-start-placement)
* [Event Driven Placement - Open the Store](#event-driven-placement-open-the-store) for instance, when the user is running low on premium currency
* [Event Driven Placement - Level Completion](#event-driven-drame-level-completion)

### Game Start Placement

In this use-case, we want to configure a placement that is always shown to users when they start playing a new session. The message shown to the user may change based on the desired segments:

<table>
    <thead>
        <tr>
            <th >
                Segment
            </th>
            <th>
                Priority
            </th>
            <th>
                Code Callback
            </th>
            <th style="width:250px;">
                Creative
            </th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td>
                At-Risk
            </td>
            <td>1st</td>
            <td>
                In this case, we're worried once-active users are now in danger of leaving the session. We might offer them <strong>50 MonsterBucks</strong> to bring them back.
            </td>
            <td>
                <img src="http://playnomics.com/integration-dev/img/messaging/50-free-monster-bucks.png"/>
            </td>
        </tr>
        <tr>
            <td>
                Lapsed 7 or more days
            </td>
            <td>2nd</td>
            <td>
                In this case, we want to thank the user for coming back and incentivize these lapsed users to continue doing so. We might offer them <strong>10 MonsterBucks</strong> to increase their engagement and loyalty.
            </td>
            <td> 
                <img src="http://playnomics.com/integration-dev/img/messaging/10-free-monster-bucks.png"/>
            </td>
        </tr>
        <tr>
            <td>
                Default - users who don't fall into either segment.
            </td>
            <td>3rd</td>
            <td>
                In this case, we can offer a special item to them for returning to the grame.
            </td>
            <td>
                <img src="http://playnomics.com/integration-dev/img/messaging/free-bfb.png"/>
            </td>
        </tr>
    </tbody>
</table>

We want our game to process messages for awarding items to users. We process this data with an implementation of the `IPlaynomicsPlacementDelegate` interface.


```java
public class AwardDelegate implements IPlaynomicsPlacementDelegate {

    public void onTouch(Map<String, Object> data){
        if(data == null){ return; }

        if(data.containsKey("type") && data.get("type").equals("award")){
            if(data.containsKey("award")){

                Map<String, Object> award =  (Map<String, Object>) data.get("award");

                String item = (String) award.get("item");
                Integer quantity = (Integer) award.get("quantity");

                //call your own inventory object
                Inventory.addItem(item, quantity);
            }

        }
    }
}
```

And then attaching this `AwardDelegate` class to the placement shown in an activity:


```java
public class GameActivity extends Activity {
    @Override
    protected void onCreate(){
        String placementName = "placementName";

        IPlaynomicsPlacementDelegate delegate = new AwardDelegate();
        Playnomics.showPlacement(placementName, this, delegate)
    }
}
```
The related messages would be configured in the Control Panel to use this callback by placing this in the **Target Data** for each message:

Grant 10 Monster Bucks
```json
{
    "type" : "award",
    "award" : 
    {
        "item" : "MonsterBucks",
        "quantity" : 10
    }
}
```

Grant 50 Monster Bucks
```json
{
    "type" : "award",
    "award" : 
    {
        "item" : "MonsterBucks",
        "quantity" : 50
    }
}
```

Grant Bazooka
```json
{
    "type" : "award",
    "award" :
    {
        "item" : "Bazooka",
        "quantity" : 1
    }
}
```

### Event Driven Placement - Open the Store

An advantage of placements is that they can be triggered by in-game events. For each in-game event you would configure a separate placement. While segmentation may be helpful in deciding what message you show, it may be sufficient to show the same message to all users.

In particular one event, for examle, a user may deplete their premium currency and you want to remind them that they can re-up through your store. In this context, we display the same message to all users.

<table>
    <thead>
        <tr>
            <th>
                Segment
            </th>
            <th>
                Priority
            </th>
            <th>
                Code Callback
            </th>
            <th>
                Creative
            </th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td>
                Default - all users, because this message is intended for anyone playing the game.
            </td>
            <td>1st</td>
            <td>
                You notice that the user's in-game, premium currency drops below a certain threshold, now you can prompt them to re-up with this <strong>message</strong>.
            </td>
            <td>
                <img src="http://playnomics.com/integration-dev/img/messaging/running-out-of-monster-bucks.png"/>
            </td>
        </tr>
    </tbody>
</table>

Related delegate callback code:

```csharp
public class StorePlacementDelegate implements IPlaynomicsPlacementDelegate {

    public void onTouch(Map<String, Object> data){
        if(data == null){ return; }
        if(!data.isNull("type") && data.getString("type").Equals("action")){
            if(data.containsKey("actionType") &&
                data.get("actionType").equals("openStore")){
                //opens the store in our game
                Store.open();
            }
        }
    }
}
```

The Default message would be configured in the Control Panel to use this callback by placing this in the **Target Data** for the message :

```json
{
    "type" : "action",
    "action" : "openStore"
}
```

### Event Driven Placement - Level Completion

In the following example, we wish to generate third-party revenue from users unlikely to monetize by showing them a segmented message after completing a level or challenge: 

<table>
    <thead>
        <tr>
            <th>
                Segment
            </th>
            <th>
                Priority
            </th>
            <th>
                Callback Behavior
            </th>
            <th>
                Creative
            </th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td>
                Non-monetizers, in their 5th day of game play
            </td>
            <td>1st</td>
            <td>Show them a 3rd party ad, because they are unlikely to monetize.</td>
            <td>
                <img src="http://playnomics.com/integration-dev/img/messaging/third-party-ad.png"/>
            </td>
        </tr>
        <tr>
            <td>
                Default: everyone else
            </td>
            <td>2nd</td>
            <td>
                You simply congratulate them on completing the level and grant them some attention currency, "Mana" for completeing the level.
            </td>
            <td>
                <img src="http://playnomics.com/integration-dev/img/messaging/darn-good-job.png"/>
            </td>
        </tr>
    </tbody>
</table>

This another continuation on the `AwardDelegate`, with some different data. The related messages would be configured in the Control Panel:

* **Non-monetizers, in their 5th day of game play**, a Target URL: `HTTP URL for Third Party Ad`
* **Default**, Target Data:

```json
{
    "type" : "award",
    "award" :
    {
        "item" : "Mana",
        "quantity" : 20
    }
}
```

Support Issues
==============
If you have any questions or issues, please contact <a href="mailto:support@playnomics.com">support@playnomics.com</a>.

Change Log
==========

#### Version 1.1.0
* Support for Push Notifications for Google Cloud Messaging
* `setTestMode` has been marked as deprecated. We now support test devices for validating and testing integrations.

#### Version 1.0.0
* Support for 3rd party html-based advertisements
* Support for simplified, fullscreen placements and internal messaging creatives
* Support for custom events
* A greatly simplified interface and API
* More robust error and exception handling
* Performance improvements, including background event queueing and better support for offline-mode
* Version number reset

#### Version 3.1
* Added support for Messaging with Rich Data Callbacks.

#### Version 3.0.1
* Bug fixes for reporting the device ID
* Performance improvements

#### Version 3
* Support for internal messaging
* Added milestone module

#### Version 2
* First release

View version <a href="https://github.com/playnomics/android-sdk/tags">tags</a>. View [releases](https://github.com/playnomics/playnomics-android/releases).
