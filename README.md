# DLNA-PLAYER 
Generic DLNA Player to Smartthings v 2.0.1

The **DLNA PLAYER** Device Type allows to [SmartThings](http://www.smartthings.com) to send messages and music to almost any DLNA Media Renderer Device

Currently there are more than 20 devices confirmed and more than 30 waiting confirmation [List](https://community.smartthings.com/t/working-speakers-20-devices-confirmed-31-waiting-confirmation-last-addition-heos-7-help-us-to-increase-the-list/12107/)


The DLNA Player Controls Interface

1. Play
2. Stop
3. Next 
4. Forward
5. Mute/Unmute
6. Control Volume
7. Show media description
8. Mode: Msg Enabled, Msg Disabled, Msg On Stopped, Msg on Playing



Its compatible with official smartapps made for sonos speakers

 1. Music & Sounds : Sonos Control by SmartThings Play or pause your Sonos when certain actions take place in your home.
 2. Sonos Mood Music by SmartThings : Plays a selected song or station. 
 3. Sonos Notify with Sound by SmartThings : Play a sound or custom message through your Sonos when the mode changes or other events occur.
 4. Sonos Weather Forecast by SmartThings : Play a weather report through your Sonos when the mode changes or other events occur
 5. Talking Alarm Clock by Michael Struck : Control up to 4 waking schedules using a Sonos speaker as an alarm.
 
Unoficial 

Its compatible with  unofficial smartapps made music and messages

1. Media Renderer Events : Play a custom message, sound or RadioTunes station through your Media Renderer when the mode changes or other events occur.
2. ...


*If you like this app, please consider supporting its development by making a
donation via PayPal.*

[![PayPal](https://www.paypalobjects.com/en_US/i/btn/btn_donate_LG.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=A6XBY99S5FECL)

The **MediaRenderer Connect** search for the Media Renderers devices in your network.



**Installing The MediaRenderer Connect**



Open SmartThings IDE in your web browser and log into your account.

Click on the "My Smart Apps" section in the navigation bar.

On your SmartApps page, click on the "+ New SmartApp" button on the right.

On the "New SmartApp" page, Select the Tab "From Code" , Copy the MediaRenderer_Connect source code from GitHub and paste it into the IDE editor window.

Click the blue "Create" button at the bottom of the page. An IDE editor window containing device handler template should now open.

Click the blue "Save" button above the editor window.

Click the "Publish" button next to it and select "For Me". You have now self-published your SmartApp.



**Installing The Device Type**


Open SmartThings IDE in your web browser and log into your account.

Click on the "My Device Types" section in the navigation bar.

On your Device Types page, click on the "+ New Device Type" button on the right.

On the "New Device Type" page, Select the Tab "From Code" , Copy the MediaRenderer_Player source code from GitHub and paste it into the IDE editor window.

Click the blue "Create" button at the bottom of the page. An IDE editor window containing device handler template should now open.

Click the blue "Save" button above the editor window.

Click the "Publish" button next to it and select "For Me". You have now self-published your SmartApp.


**Searching the Media Renderers**


Open the SmartThings app in your smartphone.

Select the (+) icon to install new things

Go to My Apps section and select MediaRenderer Connect

The MediaRenderer Connect will start to search Your players.

Once the app will show the quantity of media renderers found, activate all the media renderers found and finish the proccess pressing the "Done Button" in both pages.

Get back to main window and you should have the new players in your things section, refresh the page if no players appears.

Revision History
----------------
**Version 1.9.5
* protocol fix compatible con x-rincon-mp3radio needed by sonos to play external sources

**Version 1.9.2
* External TTS Function, allows to use an alternative of TTS build in Function

**Version 1.9.0
* No device present detection
* Its necesary to update the conecctor too and uninstall,reinstall the players, if you do not have software devices its not necesary to update, but recommended.

**Version 1.8.0
excluded by bugs

**Version 1.7.0
* Foobar and Software device autocorrect added
* Its necesary to update the conecctor too and uninstall,reinstall the players, if you do not have software devices its not necesary to update, but recommended.

**Version 1.6.0
* Speech Synthesis Capability added

**Version 1.5.6 
* Fix TTS delay 
* Implemented Msg only when playing and Msg only when no playing
* Several minor fixes
* Fix Resume in control point list (No container)
* Fix Volume Refresh in software MediaRenderers
* Better play event
* Prefix P.L. in container when last song list is display in music apps 
* Prefix Unavailable in control point manage song when last song list is display in music apps 

**Version 1.5.0 
* Improved Track resume and Container resume

**Version 1.4.0 
* Implemented message in progress

**Version 1.3.0 
* Implemented new Delay before message (Some slow MR need more time to load an external http file)

**Version 1.2.0 
* Implemented new Delay between actions (Some slow MR need more time between actions)

**Version 1.1.0 
* Implemented new Do not Disturb state to avoid messages 

**Version 1.0.1 
* Removed Play test button

**Version 1.0.0. Released 2015-02-13**
* First public release.

