/**
 *  Copyright 2015 SmartThings
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 * Media Renderer Messages
 *
 *  Author: SmartThings-Ule
 *  Date: 2015-10-09
 *  v 1.1.2.
 *  To use RadioTunes stations, you mus get a free account and set the key in the app
 *  Once you have an Radio Tunes account, go to http://www.radiotunes.com/settings and select "Hardware Player" and "Good (96k MP3)"
 *  You going to see a url like this http://listen.radiotunes.com/public3/hit00s.pls?listen_key=xxxxxxxxxxxxxxxxx
 *  You can add your key in line 94 to avoid writing by the app
 *  Date: 2015-10-09
 */
definition(
	name: "Media Renderer Events",
	namespace: "smartthings",
	author: "SmartThings",
	description: "Play a sound,RadioTunes station or custom message through your MediaRenderer,Sonos when the mode changes or other events occur.",
	category: "SmartThings Labs",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Partner/sonos.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Partner/sonos@2x.png"
)

preferences {
	page(name: "mainPage", title: "Play a message on your Sonos when something happens", install: true, uninstall: true)
	page(name: "chooseTrack", title: "Select a song or station")
	page(name: "timeIntervalInput", title: "Only during a certain time") {
		section {
			input "starting", "time", title: "Starting", required: false
			input "ending", "time", title: "Ending", required: false
		}
	}
}

		

def mainPage() {
	dynamicPage(name: "mainPage") {
		def anythingSet = anythingSet()
        if (anythingSet) {
			section("Play message when"){
				ifSet "motion", "capability.motionSensor", title: "Motion Here", required: false, multiple: true
				ifSet "contact", "capability.contactSensor", title: "Contact Opens", required: false, multiple: true
				ifSet "contactClosed", "capability.contactSensor", title: "Contact Closes", required: false, multiple: true
				ifSet "acceleration", "capability.accelerationSensor", title: "Acceleration Detected", required: false, multiple: true
				ifSet "mySwitch", "capability.switch", title: "Switch Turned On", required: false, multiple: true
				ifSet "mySwitchOff", "capability.switch", title: "Switch Turned Off", required: false, multiple: true
				ifSet "arrivalPresence", "capability.presenceSensor", title: "Arrival Of", required: false, multiple: true
				ifSet "departurePresence", "capability.presenceSensor", title: "Departure Of", required: false, multiple: true
				ifSet "smoke", "capability.smokeDetector", title: "Smoke Detected", required: false, multiple: true
				ifSet "water", "capability.waterSensor", title: "Water Sensor Wet", required: false, multiple: true
				ifSet "button1", "capability.button", title: "Button Press", required:false, multiple:true //remove from production
				ifSet "triggerModes", "mode", title: "System Changes Mode", required: false, multiple: true
				ifSet "timeOfDay", "time", title: "At a Scheduled Time", required: false
			}
		}
		def hideable = anythingSet || app.installationState == "COMPLETE"
		def sectionTitle = anythingSet ? "Select additional triggers" : "Play message when..."

		section(sectionTitle, hideable: hideable, hidden: true){
			ifUnset "motion", "capability.motionSensor", title: "Motion Here", required: false, multiple: true
			ifUnset "contact", "capability.contactSensor", title: "Contact Opens", required: false, multiple: true
			ifUnset "contactClosed", "capability.contactSensor", title: "Contact Closes", required: false, multiple: true
			ifUnset "acceleration", "capability.accelerationSensor", title: "Acceleration Detected", required: false, multiple: true
			ifUnset "mySwitch", "capability.switch", title: "Switch Turned On", required: false, multiple: true
			ifUnset "mySwitchOff", "capability.switch", title: "Switch Turned Off", required: false, multiple: true
			ifUnset "arrivalPresence", "capability.presenceSensor", title: "Arrival Of", required: false, multiple: true
			ifUnset "departurePresence", "capability.presenceSensor", title: "Departure Of", required: false, multiple: true
			ifUnset "smoke", "capability.smokeDetector", title: "Smoke Detected", required: false, multiple: true
			ifUnset "water", "capability.waterSensor", title: "Water Sensor Wet", required: false, multiple: true
			ifUnset "button1", "capability.button", title: "Button Press", required:false, multiple:true //remove from production
			ifUnset "triggerModes", "mode", title: "System Changes Mode", description: "Select mode(s)", required: false, multiple: true
			ifUnset "timeOfDay", "time", title: "At a Scheduled Time", required: false
		}
        def radioTunesOptions = ["00's Hits","00's R&B","60's Rock","80's Alt & New Wave","80's Dance","80's Rock Hits","90's Hits","90's R&B","Alternative Rock","Ambient","American Songbook","Baroque Period","Bebop Jazz","Best of the 60's","Best of the 80's","Blues Rock","Bossa Nova","Café de Paris","Chillout","Classic Hip-Hop","Classic Motown","Classic Rock","Classical Guitar","Classical Period","Classical Piano Trios","Club Bollywood","Contemporary Christian","Country","DaTempo Lounge","Dance Hits","Dave Koz & Friends","Disco Party","Downtempo Lounge","Dreamscapes","EDM Fest","EuroDance","Halloween Channel","Hard Rock","Hit 70's","Indie Rock","Jazz Classics","Jpop","Lounge","Love Music","Meditation","Mellow Jazz","Mellow Smooth Jazz","Metal","Modern Blues","Modern Rock","Mostly Classical","Movie Soundtracks","Mozart","Nature","New Age","Old School Funk & Soul","Oldies","Piano Jazz","Pop Rock","Reggaeton","Relaxation","Relaxing Ambient Piano","Romantic Period","Romantica","Roots Reggae","Salsa","Slow Jams","Smooth Bossa Nova","Smooth Jazz","Smooth Jazz 24'7","Smooth Lounge","Soft Rock","Solo Piano","Top Hits","Uptempo Smooth Jazz","Urban Hits","Urban Pop Hits","Vocal Chillout","Vocal Lounge","Vocal New Age","Vocal Smooth Jazz","World"]
		section{
            input "actionType", "enum", title: "Action?", required: true, defaultValue: "Message",submitOnChange:true, options: ["Message","Sound","Track","Radio Tunes","Multiple Radio Tunes"]
            input "message","text",title:"Play this message?", required:actionType == "Message"? true:flase, multiple: false
        }
		section("Message settings", hideable:true, hidden: true) {
	        input "externalTTS", "bool", title: "Use External Text to Speech", required: false, defaultValue: false
        }
        section{  
        	input "sound", "enum", title: "Play this Sound?", required: actionType == "Sound"? true:flase, defaultValue: "Bell 1", options: ["Bell 1","Bell 2","Dogs Barking","Fire Alarm","Piano","Lightsaber"]
			input "song","enum",title:"Play this track", required:actionType == "Track"? true:flase, multiple: false, options: songOptions()
	        input "radioTunes", "enum", title: "Play this RadioTunes Station?", required:actionType == "Radio Tunes"? true:flase, defaultValue: "hit00s", options: radioTunesOptions
            input "radioTunesM", "enum", title: "Play Random RadioTunes Station?", required: actionType == "Multiple Radio Tunes"? true:flase, multiple:true, options: radioTunesOptions
		}
        section("Radio Tunes settings", hideable: (actionType == "Radio Tunes" || actionType == "Multiple Radio Tunes") && !RTKey ? flase:true, hidden: true) {
        	input "RTKey","text",title:"Radio Tunes Key?", required:actionType == "Radio Tunes" || "Multiple Radio Tunes" ? true:flase, defaultValue: ""
            input "RTServer", "enum", title: "Radio Tunes Server?", required: true, defaultValue: "5", options: ["1","2","3","4","5","6","7","8"]
            input "RTMode", "enum", title: "Multiple Mode?", required: true, defaultValue: "Shuffle", options: ["Loop","Random","Shuffle"]
        }
		section {
			input "sonos", "capability.musicPlayer", title: "On this Sonos player", required: true
		}
		section("More options", hideable: true, hidden: true) {
			input "resumePlaying", "bool", title: "Resume currently playing music after notification", required: false, defaultValue: true
			input "volume", "number", title: "Temporarily change volume", description: "0-100%", required: false
			input "frequency", "decimal", title: "Minimum time between actions (defaults to every event)", description: "Minutes", required: false
			href "timeIntervalInput", title: "Only during a certain time", description: timeLabel ?: "Tap to set", state: timeLabel ? "complete" : "incomplete"
			input "days", "enum", title: "Only on certain days of the week", multiple: true, required: false,
				options: ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"]
			if (settings.modes) {
            	input "modes", "mode", title: "Only when mode is", multiple: true, required: false
            }
			input "oncePerDay", "bool", title: "Only once per day", required: false, defaultValue: false
		}
		section([mobileOnly:true]) {
			label title: "Assign a name", required: false
			mode title: "Set for specific mode(s)", required: false
		}
	}
}

private songOptions() {

	// Make sure current selection is in the set

	def options = new LinkedHashSet()
	if (state.selectedSong?.station) {
		options << state.selectedSong.station
	}
	else if (state.selectedSong?.description) {
		// TODO - Remove eventually? 'description' for backward compatibility
		options << state.selectedSong.description
	}

	// Query for recent tracks
	if (sonos){
        def states = sonos.statesSince("trackData", new Date(0), [max:30])
        def dataMaps = states.collect{it.jsonValue}
        options.addAll(dataMaps.collect{it.station})

        log.trace "${options.size()} songs in list"
        options.take(20) as List
    }
}

private saveSelectedSong() {
	try {
		def thisSong = song
		log.info "Looking for $thisSong"
		def songs = sonos.statesSince("trackData", new Date(0), [max:30]).collect{it.jsonValue}
		log.info "Searching ${songs.size()} records"

		def data = songs.find {s -> s.station == thisSong}
		log.info "Found ${data?.station}"
		if (data) {
			state.selectedSong = data
			log.debug "Selected song = $state.selectedSong"
		}
		else if (song == state.selectedSong?.station) {
			log.debug "Selected existing entry '$song', which is no longer in the last 20 list"
		}
		else {
			log.warn "Selected song '$song' not found"
		}
	}
	catch (Throwable t) {
		log.error t
	}
}

private anythingSet() {
	for (name in ["motion","contact","contactClosed","acceleration","mySwitch","mySwitchOff","arrivalPresence","departurePresence","smoke","water","button1","timeOfDay","triggerModes","timeOfDay"]) {
		if (settings[name]) {
			return true
		}
	}
	return false
}

private ifUnset(Map options, String name, String capability) {
	if (!settings[name]) {
		input(options, name, capability)
	}
}

private ifSet(Map options, String name, String capability) {
	if (settings[name]) {
		input(options, name, capability)
	}
}

def installed() {
	log.debug "Installed with settings: ${settings}"
	subscribeToEvents()
}

def updated() {
	log.debug "Updated with settings: ${settings}"
	unsubscribe()
	unschedule()
	subscribeToEvents()
}

def subscribeToEvents() {
    subscribe(app, appTouchHandler)
	subscribe(contact, "contact.open", eventHandler)
	subscribe(contactClosed, "contact.closed", eventHandler)
	subscribe(acceleration, "acceleration.active", eventHandler)
	subscribe(motion, "motion.active", eventHandler)
	subscribe(mySwitch, "switch.on", eventHandler)
	subscribe(mySwitchOff, "switch.off", eventHandler)
	subscribe(arrivalPresence, "presence.present", eventHandler)
	subscribe(departurePresence, "presence.not present", eventHandler)
	subscribe(smoke, "smoke.detected", eventHandler)
	subscribe(smoke, "smoke.tested", eventHandler)
	subscribe(smoke, "carbonMonoxide.detected", eventHandler)
	subscribe(water, "water.wet", eventHandler)
	subscribe(button1, "button.pushed", eventHandler)

	if (triggerModes) {
		subscribe(location, modeChangeHandler)
	}

	if (timeOfDay) {
		schedule(timeOfDay, scheduledTimeHandler)
	}

	if (song) {
		saveSelectedSong()
	}

	loadText()
    
    if (radioTunesM) {
        state.radioTunesM = radioTunesM.sort()
    	state.lastRTS = state.radioTunesM[-1]
    }
    
    log.debug "state.sound : $state.sound"
    
}

def eventHandler(evt) {
	log.trace "eventHandler($evt?.name: $evt?.value)"
	if (allOk) {
		log.trace "allOk"
		def lastTime = state[frequencyKey(evt)]
		if (oncePerDayOk(lastTime)) {
			if (frequency) {
				if (lastTime == null || now() - lastTime >= frequency * 60000) {
					takeAction(evt)
				}
				else {
					log.debug "Not taking action because $frequency minutes have not elapsed since last action"
				}
			}
			else {
				takeAction(evt)
			}
		}
		else {
			log.debug "Not taking action because it was already taken today"
		}
	}
}
def modeChangeHandler(evt) {
	log.trace "modeChangeHandler $evt.name: $evt.value ($triggerModes)"
	if (evt.value in triggerModes) {
		eventHandler(evt)
	}
}

def scheduledTimeHandler() {
	eventHandler(null)
}

def appTouchHandler(evt) {
	takeAction(evt)
}

private takeAction(evt) {
	log.trace "takeAction() $actionType"
	def radioTunesStations = [["00's Hits":["key":"hit00s","artURI":"http://static.audioaddict.com/d/9/c/0/6/0/d9c060393b8e606a8b124edeec6b28f0.png","description":"All your favorites from the beginning of the century. Expect familiar favorites and hidden gems. "]],["00's R&B":["key":"00srnb","artURI":"http://static.audioaddict.com/7/6/3/d/c/3/763dc3ccb478c6b9f62512cdc00508fe.png","description":"The best in R&B from the noughties!"]],["60's Rock":["key":"60srock","artURI":"http://static.audioaddict.com/2/c/d/a/3/1/2cda314915c57288384a8dacd54e8ef6.png","description":"The very best in classic 60s Rock!"]],["80's Alt & New Wave":["key":"80saltnnewwave","artURI":"http://static.audioaddict.com/e/5/c/0/2/a/e5c02aa3d79950dff491a470a36ebf8d.png","description":"The sounds of the 80s underground. Synths, quirky ideas and pioneering musicians. "]],["80's Dance":["key":"80sdance","artURI":"http://static.audioaddict.com/3/2/3/6/5/a/32365acb3fc314b7752994c566c546d0.png","description":"The best 80s Dance Party on the web!"]],["80's Rock Hits":["key":"80srock","artURI":"http://static.audioaddict.com/3318d27fbfcf6c699fe235e6f1ceb467.png","description":"Your favorite rock hits from the 80s."]],["90's Hits":["key":"hit90s","artURI":"http://static.audioaddict.com/2/4/8/7/d/6/2487d65f7b584e92c73f5fbb3d6cb979.png","description":"Reminisce and relive the hits of the 90s!"]],["90's R&B":["key":"90srnb","artURI":"http://static.audioaddict.com/0/f/2/2/5/9/0f2259b1e91d45b967e9963c761adf5e.png","description":"The hottest 90s R&B hits and jams!"]],["Alternative Rock":["key":"altrock","artURI":"http://static.audioaddict.com/4c1e38925a1d195686c180d7fdd355bb.png","description":"The best Alternative Rock hits you want to hear."]],["Ambient":["key":"ambient","artURI":"http://static.audioaddict.com/a/b/7/8/9/a/ab789a23f774577306847150dc425297.png","description":"A blend of ambient, downtempo, and chillout"]],["American Songbook":["key":"americansongbook","artURI":"http://static.audioaddict.com/3fbd5add268ebd2aff68bf963ea59660.png","description":"Classic standard voices from the likes of Mel Torme & Nat King Cole"]],["Baroque Period":["key":"baroque","artURI":"http://static.audioaddict.com/b/7/b/0/c/2/b7b0c26558f5d6a5b77d71117008c2ae.png","description":"Listen to delightful classical music from the Baroque Period (1600-1760)"]],["Bebop Jazz":["key":"bebop","artURI":"http://static.audioaddict.com/3df6292d6a6cbc2b04d007703ab2f7b2.png","description":"Swinging to the sounds of bebop and straight ahead jazz"]],["Best of the 60's":["key":"hit60s","artURI":"http://static.audioaddict.com/0/c/0/f/7/3/0c0f73e059ff0f6150a23a0bb3ece5a0.png","description":"Relive the memories of the 60’s greatest hits."]],["Best of the 80's":["key":"the80s","artURI":"http://static.audioaddict.com/e/7/8/3/9/b/e7839be1803a7734a554b55537b62968.png","description":"Hear your classic favorites right here!"]],["Blues Rock":["key":"bluesrock","artURI":"http://static.audioaddict.com/9/a/b/4/0/1/9ab401f69096e40747d0a6c5a8ccf6d2.png","description":"A fusion of Blues and Rock."]],["Bossa Nova":["key":"bossanova","artURI":"http://static.audioaddict.com/42b7913c019bbe47ee258d13a7dbf5bb.png","description":"100% pure bossa nova channel, enjoy the sweet flavors of Brazil!"]],["Café de Paris":["key":"cafedeparis","artURI":"http://static.audioaddict.com/3/0/6/2/3/4/3062348e97fe268f62ba94ccbcfdcc57.png","description":"Watch the world go by on a sunny Paris day."]],["Chillout":["key":"rtchillout","artURI":"http://static.audioaddict.com/0/a/c/5/a/9/0ac5a9ce5b258e16fec514722b92d580.png","description":"Full of trippy flavors, this channel is just what you need to relax."]],["Classic Hip-Hop":["key":"classicrap","artURI":"http://static.audioaddict.com/c54b89c96b6917acf4ec3ddd9d8e5f7c.png","description":"The best in classic hip-hop!!"]],["Classic Motown":["key":"classicmotown","artURI":"http://static.audioaddict.com/5/1/a/f/a/9/51afa9fce9927303d8e45a8158133048.png","description":"Relive the hits and discover the early sounds of Motown."]],["Classic Rock":["key":"classicrock","artURI":"http://static.audioaddict.com/243efc74a39092c1b2c1f5eb3b6ee0e2.png","description":"Relive the classic rock sounds of the 70s and 80s"]],["Classical Guitar":["key":"guitar","artURI":"http://static.audioaddict.com/47e1f6acc1b1fd8947eb63053389c228.png","description":"A mix of classical, spanish, and flamenco guitar"]],["Classical Period":["key":"classicalperiod","artURI":"http://static.audioaddict.com/8/e/b/d/f/8/8ebdf87f34ee623c96b26086d2e248c7.png","description":"Enjoy musical compositions from masters of the Classical Period (1730-1820)."]],["Classical Piano Trios":["key":"classicalpianotrios","artURI":"http://static.audioaddict.com/ae73ffca4235946a6bc958609b3632da.png","description":"Classical Piano Trios hits!"]],["Club Bollywood":["key":"clubbollywood","artURI":"http://static.audioaddict.com/ac488cfbc1ddcf62947b71bc51a7b808.png","description":"Dance Hits from Bollywood!!"]],["Contemporary Christian":["key":"christian","artURI":"http://static.audioaddict.com/9ed827ddee44c35e34b9de2bdf425048.png","description":"Today's Christian favorites and undiscovered hits"]],["Country":["key":"country","artURI":"http://static.audioaddict.com/25bc8f07c1cfd7d90eea968724105082.png","description":"Today's Hit Country with a mix of your favorites"]],["DaTempo Lounge":["key":"datempolounge","artURI":"http://static.audioaddict.com/9145b6ea36ca63b0911dc6c537de89cb.png","description":"Amazing combination of jazz, lounge, bossa, and much more!"]],["Dance Hits":["key":"dancehits","artURI":"http://static.audioaddict.com/cdb89061045f4207b909558ca01b8637.png","description":"The hottest dance tunes 24/7"]],["Dave Koz & Friends":["key":"davekoz","artURI":"http://static.audioaddict.com/1/b/d/b/c/d/1bdbcdda2bcc5f34baa727c6f03334e6.png","description":"Smooth Jazz selected by Dave Koz himself!"]],["Disco Party":["key":"discoparty","artURI":"http://static.audioaddict.com/c/c/d/d/b/6/ccddb6f390a0401e7bc4088afa5fbaff.png","description":"The ultimate Disco Party of all time!"]],["Downtempo Lounge":["key":"downtempolounge","artURI":"http://static.audioaddict.com/c/9/1/b/3/6/c91b36a066ec8a69606fe9857ee529d6.png","description":"Relax with some Downtempo grooves."]],["Dreamscapes":["key":"dreamscapes","artURI":"http://static.audioaddict.com/8ca99e36c4854358d1ce76c30ccae16a.png","description":"Relax to the sounds of dream and Ibiza style chillout"]],["EDM Fest":["key":"edmfest","artURI":"http://static.audioaddict.com/4/8/7/2/1/0/4872108c77039672e7f4054cc728a820.png","description":"The best DJs from the biggest festivals around the world!"]],["EuroDance":["key":"eurodance","artURI":"http://static.audioaddict.com/7/e/3/d/b/0/7e3db0459654142b8409e913f40fc95b.png","description":"The newest and best of Eurodance hits"]],["Halloween Channel":["key":"halloween","artURI":"http://static.audioaddict.com/1/2/7/f/6/5/127f652560dcd295f3ef414792045b81.png","description":"The Spookiest Songs of the Season!"]],["Hard Rock":["key":"hardrock","artURI":"http://static.audioaddict.com/7d3e6634592359e2a0c1d6e5f3f5ddaf.jpg","description":"Bang along with the best Hard Rock tracks anywhere"]],["Hit 70's":["key":"hit70s","artURI":"http://static.audioaddict.com/6fe772afcb2ea44eab2048f1f60af18d.png","description":"All Hits, All The Time!"]],["Indie Rock":["key":"indierock","artURI":"http://static.audioaddict.com/3c1d60925ae8dac00ef4da3beaa7d371.png","description":"Indie, Alternative, and Underground Rock!"]],["Jazz Classics":["key":"jazzclassics","artURI":"http://static.audioaddict.com/30b34af272931d0e032f148a013439dd.png","description":"Classic Jazz hits from the golden age"]],["Jpop":["key":"jpop","artURI":"http://static.audioaddict.com/61251c16fe02215ab58207577efd71d0.png","description":"Fresh hits from Japanese pop culture!"]],["Lounge":["key":"rtlounge","artURI":"http://static.audioaddict.com/b/4/f/2/b/0/b4f2b0c2ffa8e06a9e05de5cdd0bcfe5.png","description":"Sit back and enjoy the lounge grooves!"]],["Love Music":["key":"lovemusic","artURI":"http://static.audioaddict.com/9feee3db11b67dee318c8f36a99adb9b.png","description":"Easy listening and Romantic hits from the heart!!"]],["Meditation":["key":"meditation","artURI":"http://static.audioaddict.com/2/1/0/0/c/5/2100c502ffe38ee9710194ab0bdc6846.png","description":"Music for the mind and body"]],["Mellow Jazz":["key":"mellowjazz","artURI":"http://static.audioaddict.com/c/4/a/7/e/c/c4a7ec91601ac6ce18859c2edeff9c80.png","description":"Relax and unwind to the Mellow side of Jazz."]],["Mellow Smooth Jazz":["key":"mellowsmoothjazz","artURI":"http://static.audioaddict.com/3/b/6/d/9/e/3b6d9e5edaeaac40e071cb63a5e542d4.png","description":"Relaxing Smooth Jazz for your listening pleasure."]],["Metal":["key":"metal","artURI":"http://static.audioaddict.com/9be3c5b65f9274df72b78b6ecddf5bb2.png","description":"Best Place for your METAL fix on the net"]],["Modern Blues":["key":"modernblues","artURI":"http://static.audioaddict.com/61457ec9c9b71e483c67b8482c010403.png","description":"Listen to the best in Modern Blues!"]],["Modern Rock":["key":"modernrock","artURI":"http://static.audioaddict.com/cdcf2c3754d96c41b8bcd81414451770.jpg","description":"Where Modern rock lives on the net."]],["Mostly Classical":["key":"classical","artURI":"http://static.audioaddict.com/e3302f3ef1f85c53f3a13d0209102b26.png","description":"Listen and Relax, it's good for you!"]],["Movie Soundtracks":["key":"soundtracks","artURI":"http://static.audioaddict.com/7b648b3c6789173a12666a63d421aa96.png","description":"A wide variety of Soundtracks from Movies, show themes, & more!"]],["Mozart":["key":"mozart","artURI":"http://static.audioaddict.com/b/0/5/5/c/4/b055c4c2f8fd947e7e417b58b4663dd0.png","description":"Relax to the wonderful works of classical composer Wolfgang Amadeus Mozart (1756-1791)."]],["Nature":["key":"nature","artURI":"http://static.audioaddict.com/7501be44c9536cbd48968b2d7a2f4d81.png","description":"soothing sounds of nature music!"]],["New Age":["key":"newage","artURI":"http://static.audioaddict.com/ee90d885488f0d224c9cabe5d2ea770b.png","description":"Soothing sounds of new age and world music!"]],["Old School Funk & Soul":["key":"oldschoolfunknsoul","artURI":"http://static.audioaddict.com/e/d/0/f/8/e/ed0f8e974f6e137aefab8429f1a21999.png","description":"Classic Funky rhythms and Soulful grooves!"]],["Oldies":["key":"oldies","artURI":"http://static.audioaddict.com/b2f77e0f416e1396721b44f9f4b73374.png","description":"Three decades of great oldies on RadioTunes.com"]],["Piano Jazz":["key":"pianojazz","artURI":"http://static.audioaddict.com/3a78648bbc729bd4dc9ca944ce89d0df.png","description":"Enjoy the sounds of piano jazz from historic and modern masters."]],["Pop Rock":["key":"poprock","artURI":"http://static.audioaddict.com/c2bf1bfc843db7bc6ccf7563bd5ade7f.png","description":"Pop & rock hits!"]],["Reggaeton":["key":"reggaeton","artURI":"http://static.audioaddict.com/a/5/5/9/a/7/a559a793e4870517f42bd0eb7642cfa2.png","description":"Reggae rhythms with Latin and Caribbean flavors!"]],["Relaxation":["key":"relaxation","artURI":"http://static.audioaddict.com/df99e575070665e78685364ce35a26b8.png","description":"relaxing music for the mind and soul"]],["Relaxing Ambient Piano":["key":"relaxingambientpiano","artURI":"http://static.audioaddict.com/3/e/1/e/0/a/3e1e0acfbfd342ebe7c0fda2dc4df0b0.png","description":"Take time to relax to the sounds of ambient piano"]],["Romantic Period":["key":"romantic","artURI":"http://static.audioaddict.com/0/d/9/c/4/7/0d9c47e55b7fb9807b9ed9dd375d5cd5.png","description":"Enjoy the best of classical music from the Romantic Period (1780-1910)."]],["Romantica":["key":"romantica","artURI":"http://static.audioaddict.com/7e66489ebd3e3229d3fdfd10e950d262.png","description":"Timeless pop classical voices!"]],["Roots Reggae":["key":"rootsreggae","artURI":"http://static.audioaddict.com/cbf2c683fa84e16f6a03a178070d32cb.png","description":"The best of classic and modern Roots Reggae "]],["Salsa":["key":"salsa","artURI":"http://static.audioaddict.com/01e8dabaa5c29e58b7f6515d309481f5.png","description":"Best Salsa Collection now on RadioTunes.com"]],["Slow Jams":["key":"slowjams","artURI":"http://static.audioaddict.com/0/b/e/f/3/4/0bef34a4e4cc5dd4b4ee16c78427133f.png","description":"Soulful R&B jams and ballads."]],["Smooth Bossa Nova":["key":"smoothbossanova","artURI":"http://static.audioaddict.com/9/0/4/3/5/3/9043538c481c93174a6a5432b0f58c40.png","description":"The smoothest Bossa Nova direct from Brazil."]],["Smooth Jazz":["key":"smoothjazz","artURI":"http://static.audioaddict.com/ea944543ab6247d58e1703d84e451e4f.png","description":"The worlds smoothest jazz 24 hours a day"]],["Smooth Jazz 24'7":["key":"smoothjazz247","artURI":"http://static.audioaddict.com/4cb303531bcbff5ef8b61dbfe995568c.png","description":"Smooth Jazz DJs bring you the best music, 24 hours a day!"]],["Smooth Lounge":["key":"smoothlounge","artURI":"http://static.audioaddict.com/8a8c05b2da5d78ddaf9e453236e311ff.jpg","description":"Relax and unwind with the smoothest lounge vibes."]],["Soft Rock":["key":"softrock","artURI":"http://static.audioaddict.com/3d79cb01a53e1795728d112c1bb00e0b.jpg","description":"The best in Soft Rock music!"]],["Solo Piano":["key":"solopiano","artURI":"http://static.audioaddict.com/657f189ce01b74b7734063154775ca31.png","description":"Musical journeys with piano masters and talented undiscovered pianists"]],["Top Hits":["key":"tophits","artURI":"http://static.audioaddict.com/a9778d4dec931485c25f5ac182ac71e2.png","description":"Who cares about the chart order, less rap & more hits!"]],["Uptempo Smooth Jazz":["key":"uptemposmoothjazz","artURI":"http://static.audioaddict.com/16f9c144baa98176ab4f98dc3538adf4.png","description":"The world's smoothest jazz, grooving 24 hours a day"]],["Urban Hits":["key":"urbanjamz","artURI":"http://static.audioaddict.com/05f8add3c89ba75d1b1dfca9cc8e5df9.png","description":"Kickin' with the baddest beats on the 'net"]],["Urban Pop Hits":["key":"urbanpophits","artURI":"http://static.audioaddict.com/c/1/1/7/b/8/c117b8cea21df06b15bd86d8b810f8d2.png","description":"The best in Urban crossover hits"]],["Vocal Chillout":["key":"vocalchillout","artURI":"http://static.audioaddict.com/1/c/c/8/b/5/1cc8b5c256d603302446b51cc242b9fd.png","description":"Enjoy the relaxing vocal sounds of Ibiza chillout"]],["Vocal Lounge":["key":"vocallounge","artURI":"http://static.audioaddict.com/a/c/c/c/4/9/accc49b2d1b70feb5b21603d67cc444f.png","description":"Relaxing Vocals and Lounge Grooves"]],["Vocal New Age":["key":"vocalnewage","artURI":"http://static.audioaddict.com/09885117eb43050f5a64e790a684426d.png","description":"Soothing vocal sounds of new age music!"]],["Vocal Smooth Jazz":["key":"vocalsmoothjazz","artURI":"http://static.audioaddict.com/beb3e04b65b282052419bd15c81e1be4.png","description":"The best of vocal smooth jazz!!"]],["World":["key":"world","artURI":"http://static.audioaddict.com/a2ffcb17080207ca578d00f5c7eff1f0.png","description":"New Age influenced world music"]]]

    switch(actionType) {
        case "Message":
			sonos.playTrackAndResume(state.soundMessage.uri, state.soundMessage.duration, volume)
            break
        case "Sound":
			sonos.playTrackAndResume(state.sound.uri, state.sound.duration, volume)
            break
		case "Track":
			sonos.playTrack(state.selectedSong)
            break
        case "Radio Tunes":
            sonos.playTrack("x-rincon-mp3radio://pub${RTServer}.radiotunes.com:80/radiotunes_${radioTunesStations[radioTunes].key[0]}?${RTKey}","<DIDL-Lite xmlns=\"urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:upnp=\"urn:schemas-upnp-org:metadata-1-0/upnp/\" xmlns:dlna=\"urn:schemas-dlna-org:metadata-1-0/\"><item id=\"1\" parentID=\"1\" restricted=\"1\"><upnp:class>object.item.audioItem.musicTrack</upnp:class><upnp:album>Radio Tunes</upnp:album><upnp:artist>${groovy.xml.XmlUtil.escapeXml(radioTunesStations[radioTunes].description[0])}</upnp:artist><upnp:albumArtURI>${groovy.xml.XmlUtil.escapeXml(radioTunesStations[radioTunes].artURI[0])}</upnp:albumArtURI><dc:title>${groovy.xml.XmlUtil.escapeXml(radioTunes)}</dc:title><res protocolInfo=\"http-get:*:audio/mpeg:DLNA.ORG_PN=MP3;DLNA.ORG_OP=01;DLNA.ORG_CI=0;DLNA.ORG_FLAGS=01500000000000000000000000000000\" >${groovy.xml.XmlUtil.escapeXml("x-rincon-mp3radio://pub${RTServer}.radiotunes.com:80/radiotunes_${radioTunesStations[radioTunes].key[0]}?${RTKey}")} </res></item> </DIDL-Lite>")
            break
        case "Multiple Radio Tunes":
			switch(RTMode){
            	 case "Random": 
                    state.radioTunesM.sort{Math.random()}
                    break
                case "Shuffle":
                    if (state.radioTunesM[-1] == state.lastRTS) {
                        state.radioTunesM.sort{Math.random()}
                        state.lastRTS = state.radioTunesM[-1]
                    }
                    break
            }
            Collections.rotate(state.radioTunesM, -1)
            sonos.playTrack("x-rincon-mp3radio://pub${RTServer}.radiotunes.com:80/radiotunes_${radioTunesStations[state.radioTunesM[-1]].key[0]}?${RTKey}","<DIDL-Lite xmlns=\"urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:upnp=\"urn:schemas-upnp-org:metadata-1-0/upnp/\" xmlns:dlna=\"urn:schemas-dlna-org:metadata-1-0/\"><item id=\"1\" parentID=\"1\" restricted=\"1\"><upnp:class>object.item.audioItem.musicTrack</upnp:class><upnp:album>Radio Tunes</upnp:album><upnp:artist>${groovy.xml.XmlUtil.escapeXml(radioTunesStations[state.radioTunesM[-1]].description[0])}</upnp:artist><upnp:albumArtURI>${groovy.xml.XmlUtil.escapeXml(radioTunesStations[state.radioTunesM[-1]].artURI[0])}</upnp:albumArtURI><dc:title>${groovy.xml.XmlUtil.escapeXml(state.radioTunesM[-1])}</dc:title><res protocolInfo=\"http-get:*:audio/mpeg:DLNA.ORG_PN=MP3;DLNA.ORG_OP=01;DLNA.ORG_CI=0;DLNA.ORG_FLAGS=01500000000000000000000000000000\" >${groovy.xml.XmlUtil.escapeXml("x-rincon-mp3radio://pub${RTServer}.radiotunes.com:80/radiotunes_${radioTunesStations[state.radioTunesM[-1]].key[0]}?${RTKey}")} </res></item> </DIDL-Lite>")
            break
	}

	if (frequency || oncePerDay) {
		state[frequencyKey(evt)] = now()
	}
	log.trace "Exiting takeAction()"
}

private frequencyKey(evt) {
	"lastActionTimeStamp"
}

private dayString(Date date) {
	def df = new java.text.SimpleDateFormat("yyyy-MM-dd")
	if (location.timeZone) {
		df.setTimeZone(location.timeZone)
	}
	else {
		df.setTimeZone(TimeZone.getTimeZone("America/New_York"))
	}
	df.format(date)
}

private oncePerDayOk(Long lastTime) {
	def result = true
	if (oncePerDay) {
		result = lastTime ? dayString(new Date()) != dayString(new Date(lastTime)) : true
		log.trace "oncePerDayOk = $result"
	}
	result
}

// TODO - centralize somehow
private getAllOk() {
	modeOk && daysOk && timeOk
}

private getModeOk() {
	def result = !modes || modes.contains(location.mode)
	log.trace "modeOk = $result"
	result
}

private getDaysOk() {
	def result = true
	if (days) {
		def df = new java.text.SimpleDateFormat("EEEE")
		if (location.timeZone) {
			df.setTimeZone(location.timeZone)
		}
		else {
			df.setTimeZone(TimeZone.getTimeZone("America/New_York"))
		}
		def day = df.format(new Date())
		result = days.contains(day)
	}
	log.trace "daysOk = $result"
	result
}

private getTimeOk() {
	def result = true
	if (starting && ending) {
		def currTime = now()
		def start = timeToday(starting, location?.timeZone).time
		def stop = timeToday(ending, location?.timeZone).time
		result = start < stop ? currTime >= start && currTime <= stop : currTime <= stop || currTime >= start
	}
	log.trace "timeOk = $result"
	result
}

private hhmm(time, fmt = "h:mm a")
{
	def t = timeToday(time, location.timeZone)
	def f = new java.text.SimpleDateFormat(fmt)
	f.setTimeZone(location.timeZone ?: timeZone(time))
	f.format(t)
}

private getTimeLabel()
{
	(starting && ending) ? hhmm(starting) + "-" + hhmm(ending, "h:mm a z") : ""
}
// TODO - End Centralize

private loadText() {
	switch (sound) {
		case "Bell 1":
			state.sound = [uri: "http://s3.amazonaws.com/smartapp-media/sonos/bell1.mp3", duration: "10"]
			break;
		case "Bell 2":
			state.sound = [uri: "http://s3.amazonaws.com/smartapp-media/sonos/bell2.mp3", duration: "10"]
			break;
		case "Dogs Barking":
			state.sound = [uri: "http://s3.amazonaws.com/smartapp-media/sonos/dogs.mp3", duration: "10"]
			break;
		case "Fire Alarm":
			state.sound = [uri: "http://s3.amazonaws.com/smartapp-media/sonos/alarm.mp3", duration: "17"]
			break;
		case "Piano":
			state.sound = [uri: "http://s3.amazonaws.com/smartapp-media/sonos/piano2.mp3", duration: "10"]
			break;
		case "Lightsaber":
			state.sound = [uri: "http://s3.amazonaws.com/smartapp-media/sonos/lightsaber.mp3", duration: "10"]
			break;
        	default:
			 state.sound = externalTTS ? textToSpeechT("You selected the sound option but did not enter a sound in the $app.label Smart App") : textToSpeech("You selected the sound option but did not enter a sound in the $app.label Smart App")
			break;
	}
    if (actionType == "Message"){
    	state.soundMessage = externalTTS ? textToSpeechT(message instanceof List ? message[0] : message) :  textToSpeech(message instanceof List ? message[0] : message)
    }
}

private textToSpeechT(message){
	if (message) {
	    [uri: "x-rincon-mp3radio://www.translate.google.com/translate_tts?tl=en&client=t&q=" + URLEncoder.encode(message, "UTF-8").replaceAll(/\+/,'%20') +"&sf=//s3.amazonaws.com/smartapp-", duration: "${5 + Math.max(Math.round(message.length()/12),2)}"]
    }else{
    	[uri: "x-rincon-mp3radio://www.translate.google.com/translate_tts?tl=en&client=t&q=" + URLEncoder.encode("You selected the Text to Speach Function but did not enter a Message", "UTF-8").replaceAll(/\+/,'%20') +"&sf=//s3.amazonaws.com/smartapp-", duration: "10"]
    }
}
