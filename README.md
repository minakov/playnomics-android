Playnomics PlayRM Android SDK Integration Guide
=============================================

[![Build Status](https://api.travis-ci.org/playnomics/playnomics-android.png)](https://travis-ci.org/playnomics/playnomics-android)

## Considerations for Cross-Platform Applications

If you want to deploy your application to multiple platforms (eg: iOS, Android, etc), you'll need to create a separate Playnomics Applications in the control panel. Each application has a separate `<APPID>` used for tracking and messaging integration.

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

The actual contents of your JSON message can be delayed until the time of the messaging campaign configuration. However, the structure of your message needs to be decided before you can process it in your application. See [example use-cases for rich data](https://github.com/playnomics/playnomics-android/wiki/Rich-Data-Callbacks).

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
            <a href="#user-information">User Information</a>
        </li>
        <li>
            <a href="#user-segmentation">User Segmentation</a>
        </li>
        <li>
            <a href="https://github.com/playnomics/playnomics-android/wiki/Rich-Data-Callbacks">
                Rich Data Callbacks
            </a>
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

## User Information

Specify user information like gender and birth year.

####User Gender:

```java
public static void setUserGender(String gender);
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
            <td><code>gender</code></td>
            <td>String</td>
            <td>
                Specify user gender Female/Male.
                <br>Must be exactly “F”, “M”, or “U” (lowercase accepted)
            </td>
        </tr>
    </tbody>
</table>

Example client-side calls for setting user gender:

```objectivec
String gender = "F";
Playnomics.setUserGender(gender);
```


####User Birth Year:

```java
public static void setUserBirthYear(int birthYear);
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
            <td><code>birthYear</code></td>
            <td>int</td>
            <td>
                Specify user birth year.
                <br>Must be year in 4 digit format.
            </td>
        </tr>
    </tbody>
</table>

Example client-side calls for setting user birth year:

```objectivec
int birthYear = 2014;
Playnomics.setUserBirthYear(birthYear);
```

## User Segmentation

Each user is placed in zero or more segments. To retrieve the segmentation Ids for a user call the method below.

```java
public static void fetchUserSegmentIds(final IPlaynomicsSegmentationDelegate delegate);
```

Using an implementation of `IPlaynomicsSegmentationDelegate` your application can receive notifications once the segmentation Ids are fetched.

```java
public interface IPlaynomicsSegmentationDelegate {
    public void onFetchedUserSegmentIds(List<Long> segmentationIds);

    public void onFetchedUserSegmentIdsError(String error, String description);
}
```

Support Issues
==============
If you have any questions or issues, please contact <a href="mailto:support@playnomics.com">support@playnomics.com</a>.

Change Log
==========
#### Version 1.3.4
* Fixed ANR in EventWorker.stop coz of lockup in HttpsURLConnectionImpl.disconnect.

#### Version 1.3.3
* Fixed NullPointerException crash in EventWoker HttpsURLConnectionImpl.getResponseCode.

#### Version 1.3.2
* Fixed Crash in EventWorker and added robolectric for Android Unit test.

#### Version 1.3.1
* Fixed AppRunning ‘d’ interval in milliseconds to reflect (1, 2, 4, 8, 15, 15, ...,15, 15).

#### Version 1.3.0
* Added support for USS (User Segmentation feature)
* Added UserInfo when app version changes and OS version, device model info.
* Added setUserGender and setUserBirthYear
* Changed sending of AppRunning from every 1 minute to (1, 2, 4, 8, 15, 15, ...,15, 15) minutes
* Fixed NullPointerException when connection or url is null.

#### Version 1.2.2
* Fix for Application Not Responding error when pausing Activity with no network connectivity.

#### Version 1.2.1
* Bug fix for session state management. Important for engagement tracking.

#### Version 1.2.0
* Internal refactoring for Unity SDK

#### Version 1.1.1
* Bug fix session needs to be reset after significant session downtime

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
