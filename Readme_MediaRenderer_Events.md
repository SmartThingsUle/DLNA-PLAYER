MediaRenderer Events
--------

The **MediaRenderer Events** SmartApp allows you play messages, sounds, Tracks and Radio Stations from RadioTunes
[RadioTunes](http://www.radiotunes.com/).

[RadioTunes](http://www.radiotunes.com/) is a music streaming service knowns before like (Sky.fm) 

More than 200 music station, with several genres, now you need to have premium account

*If you like this app, please consider supporting its development by making a donation via PayPal.*

[![PayPal](https://www.paypalobjects.com/en_US/i/btn/btn_donate_LG.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=A6XBY99S5FECL)

### Installation

1. Self-publish MadiaRenderer Events SmartApp by creating a new SmartApp in the
[SmartThings IDE](https://graph.api.smartthings.com/ide/apps) and pasting the
[source code](https://raw.githubusercontent.com/SmartThingsUle/DLNA-PLAYER/master/Media Renderer Events.groovy)
in the "From Code" tab. Please refer to the
[SmartThings Developer Documentation](http://docs.smartthings.com/en/latest/index.html)
for more information.

2. Self-publish MadiaRenderer player Smart type by creating a new Device Type or Updating in the
[SmartThings IDE](https://graph.api.smartthings.com/ide/apps) and pasting the
[source code](https://raw.githubusercontent.com/SmartThingsUle/DLNA-PLAYER/master/MediaRenderer_Player.groovy)
in the "From Code" tab. Please refer to the
[SmartThings Developer Documentation](http://docs.smartthings.com/en/latest/index.html)
for more information.

3. To use RadioTunes stations, you mus get a free account and set the key in the app

4. Once you have an Radio Tunes account, go to http://www.radiotunes.com/settings and select "Hardware Player" and "Good (96k MP3)" , You going to see a url like this http://listen.radiotunes.com/public3/hit00s.pls?listen_key=xxxxxxxxxxxxxxxxx

5. You can add your key in line 100 of Mediarenderer events SmartApp  to avoid writing by the app or input in the smartapp defaultValue: ""


MediaRenderer Connect

### Revision History

**Version 1.6.0.**

* Multiple languages added to voice RSS
* Ivona TTS more reliable 


**Version 1.5.0.**

* Beta Ivona TTS 

**Version 1.4.0.**

* Now more than 200 stations
* Messages with wildcards

**Version 1.3.0.**

* Multiple Speakers added
* Now you can select several speakers to send music, messages, track, stations, the sound is not in sync, the satart play depends of the speaker and web response.

**Version 1.2.0.**

* New Text to Speech added
* Now you can avoid Text to speech failures, you can add the voicerss.org service, its free, you must to register and get you key

**Version 1.1.0.**

* Multiple Radio Station Added, Mode:  Loop, Random, Shuffle
* Now you can select your favorites radio strations and play a different station each time an event is detected.

**Version 1.0.0. Released 10/09/2015**

* Initial public release.


### License

Copyright © 2015 

This program is free software: you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by the Free
Software Foundation, either version 3 of the License, or (at your option)
any later version.

This program is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program.  If not, see <http://www.gnu.org/licenses/>.
