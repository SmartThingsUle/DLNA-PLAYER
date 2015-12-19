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
 *
 *  To use RadioTunes stations, you mus get a free account and set the key in the app
 *  Once you have an Radio Tunes account, go to http://www.radiotunes.com/settings and select "Hardware Player" and "Good (96k MP3)"
 *  You going to see a url like this http://listen.radiotunes.com/public3/hit00s.pls?listen_key=xxxxxxxxxxxxxxxxx
 *  You can add your key in line 94 to avoid writing by the app
 *  Date: 2015-10-09
 */
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException; 
 
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
	page(name: "mainPage", title: "Play or Stop message, sount, track or station on your Player when something happens", install: true, uninstall: true)
	page(name: "triggersPlay", title: "Play When ...")
    page(name: "triggersStop", title: "Stop When ...")
    page(name: "chooseTrack", title: "Select a track or station")
    page(name: "addMessage", title: "Add the message to play")
    page(name: "ttsKey", title: "Add the Text for Speach Key")
    page(name: "ttsKeyIvona", title: "Add the Ivona Key")
	page(name: "timeIntervalInput", title: "Only during a certain time") {
		section {
			input "starting", "time", title: "Starting", required: false
			input "ending", "time", title: "Ending", required: false
		}
	}
}

def mainPage() {
	dynamicPage(name: "mainPage") {
		section{
        	href "triggersPlay", title: "Select play triggers?",required: flase, description: anythingSet()?"Change triggers":"Tap to set"
            href "triggersStop", title: "Select stop triggers?",required: flase, description: anythingSet("Stop")?"Change triggers":"Tap to set"
        }
        def radioTunesOptions = ["DI 00's Club Hits","DI Ambient","DI Bass & Jackin' House","DI Bassline","DI Big Beat","DI Big Room House","DI Breaks","DI Chill & Tropical House","DI ChillHop","DI Chillout","DI Chillout Dreams","DI Chillstep","DI Classic EuroDance","DI Classic EuroDisco","DI Classic Trance","DI Classic Vocal Trance","DI Club Dubstep","DI Club Sounds","DI DJ Mixes","DI Dark DnB","DI Dark PsyTrance","DI Deep House","DI Deep Nu-Disco","DI Deep Tech","DI Detroit House & Techno","DI Disco House","DI Downtempo Lounge","DI Drum and Bass","DI Drumstep","DI Dub","DI Dub Techno","DI Dubstep","DI EBM","DI EcLectronica","DI Electro House","DI Electro Swing","DI Electronic Pioneers","DI Electronics","DI Electropop","DI Epic Trance","DI EuroDance","DI Funky House","DI Future Beats","DI Future Garage","DI Future Synthpop","DI Gabber","DI Glitch Hop","DI Goa-Psy Trance","DI Hands Up","DI Hard Dance","DI Hard Techno","DI Hardcore","DI Hardstyle","DI House","DI IDM","DI Indie Dance","DI Jazz House","DI Jungle","DI Latin House","DI Liquid DnB","DI Liquid Dubstep","DI Liquid Trap","DI Lounge","DI Mainstage","DI Melodic Progressive","DI Minimal","DI Nightcore","DI Nu Disco","DI Oldschool Acid","DI Oldschool House","DI Oldschool Rave","DI Oldschool Techno & Trance","DI Progressive","DI Progressive Psy","DI PsyChill","DI Psybient","DI Russian Club Hits","DI Soulful House","DI Space Dreams","DI Tech House","DI Techno","DI Trance","DI Trap","DI Tribal House","DI UMF Radio","DI Underground Techno","DI Vocal Chillout","DI Vocal Lounge","DI Vocal Trance","JR Bass Jazz","JR Bebop","JR Blues","JR Blues Rock","JR Bossa Nova","JR Classic Jazz","JR Contemporary Vocals","JR Cool Jazz","JR Current Jazz","JR Dave Koz & Friends","JR Flamenco Jazz","JR Fusion Lounge","JR Guitar Jazz","JR Gypsy Jazz","JR Hard Bop","JR Holiday Jazz","JR Holiday Smooth Jazz","JR Jazz Ballads","JR Latin Jazz","JR Mellow Jazz","JR Mellow Smooth Jazz","JR Paris Café","JR Piano Jazz","JR Piano Trios","JR Saxophone Jazz","JR Sinatra Style","JR Smooth Bossa Nova","JR Smooth Jazz","JR Smooth Jazz 24'7","JR Smooth Lounge","JR Smooth Uptempo","JR Smooth Vocals","JR Straight-Ahead","JR Swing & Big Band","JR Timeless Classics","JR Trumpet Jazz","JR Vibraphone Jazz","JR Vocal Legends","RR 60's Rock","RR 80's Alternative","RR 80's Rock","RR 90's Alternative","RR 90's Rock","RR Black Metal","RR Blues Rock","RR Classic Hard Rock","RR Classic Metal","RR Classic Rock","RR Death Metal","RR Grunge","RR Hair Bands","RR Hard Rock","RR Heavy Metal","RR Indie Rock","RR Industrial","RR Melodic Death Metal","RR Metal","RR Metalcore","RR Modern Rock","RR Nu Metal","RR Pop Rock","RR Power Metal","RR Progressive Rock","RR Punk Rock","RR Rock Ballads","RR Screamo-Emo","RR Soft Rock","RR Symphonic Metal","RR Thrash Metal","RT 00's Hits","RT 00's R&B","RT 60's Rock","RT 80's Alt & New Wave","RT 80's Dance","RT 80's Rock Hits","RT 90's Hits","RT 90's R&B","RT Alternative Rock","RT Ambient","RT American Songbook","RT Baroque Period","RT Bebop Jazz","RT Best of the 60's","RT Best of the 80's","RT Blues Rock","RT Bossa Nova","RT Café de Paris","RT Chillout","RT Classic Christmas","RT Classic Hip-Hop","RT Classic Motown","RT Classic Rock","RT Classical Guitar","RT Classical Period","RT Classical Piano Trios","RT Club Bollywood","RT Contemporary Christian","RT Country","RT DaTempo Lounge","RT Dance Hits","RT Dave Koz & Friends","RT Disco Party","RT Downtempo Lounge","RT Dreamscapes","RT EDM Fest","RT EuroDance","RT Hard Rock","RT Hit 70's","RT Holiday Smooth Jazz","RT Indie Rock","RT Jazz Classics","RT Jpop","RT Lounge","RT Love Music","RT Meditation","RT Mellow Jazz","RT Mellow Smooth Jazz","RT Metal","RT Modern Blues","RT Modern Rock","RT Mostly Classical","RT Movie Soundtracks","RT Mozart","RT Nature","RT New Age","RT Old School Funk & Soul","RT Oldies","RT Piano Jazz","RT Pop Christmas","RT Pop Rock","RT Reggaeton","RT Relaxation","RT Relaxing Ambient Piano","RT Romantic Period","RT Romantica","RT Roots Reggae","RT Salsa","RT Slow Jams","RT Smooth Bossa Nova","RT Smooth Jazz","RT Smooth Jazz 24'7","RT Smooth Lounge","RT Soft Rock","RT Solo Piano","RT Top Hits","RT Uptempo Smooth Jazz","RT Urban Hits","RT Urban Pop Hits","RT Vocal Chillout","RT Vocal Lounge","RT Vocal New Age","RT Vocal Smooth Jazz","RT World"]
        
		section{
            input "actionType", "enum", title: "Action?", required: true, defaultValue: "Message",submitOnChange:true, options: ["Message","Sound","Track","Radio Tunes","Multiple Radio Tunes"]
        }

        section{  
        	href "addMessage", title: "Play this message?",required: actionType == "Message"? true:flase, description: message ? message : "Tap to set", state: message ? "complete" : "incomplete"
        	input "sound", "enum", title: "Play this Sound?", required: actionType == "Sound"? true:flase, defaultValue: "Bell 1", options: ["Bell 1","Bell 2","Dogs Barking","Fire Alarm","Piano","Lightsaber"]
			href "chooseTrack", title: "Play this track",required: actionType == "Track"? true:flase, description: song ? (song?:state.selectedSong?.station) : "Tap to set", state: song ? "complete" : "incomplete"
	        input "radioTunes", "enum", title: "Play this RadioTunes Station?", required: actionType == "Radio Tunes"? true:flase, options: radioTunesOptions
            input "radioTunesM", "enum", title: "Play Random RadioTunes Station?", required: actionType == "Multiple Radio Tunes"? true:flase, multiple:true, options: radioTunesOptions
		}
        section("Radio Tunes settings", hideable: (actionType == "Radio Tunes" || actionType == "Multiple Radio Tunes") && !RTKey ? flase:true, hidden: true) {
        	input "RTKey","text",title:"Radio Tunes Key?", required:actionType == "Radio Tunes" || "Multiple Radio Tunes" ? true:flase, defaultValue: ""
            input "RTServer", "enum", title: "Radio Tunes Server?", required: true, defaultValue: "5", options: ["1","2","3","4","5","6","7","8"]
            input "RTMode", "enum", title: "Multiple Mode?", required: true, defaultValue: "Shuffle", options: ["Loop","Random","Shuffle"]
        }
		section {
			input "sonos", "capability.musicPlayer", title: "On this Sonos player", required: true,multiple:true
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

def triggersPlay(){
	dynamicPage(name: "triggersPlay") {
	    triggers("")
    }
}
def triggersStop(){
        dynamicPage(name: "triggersStop") {
        triggers("Stop")
    }
}

def triggers(command=""){
    	def anythingSet = anythingSet(command)
        if (anythingSet) {
			section(){
				ifSet "motion$command", "capability.motionSensor", title: "Motion Here", required: false, multiple: true
				ifSet "contact$command", "capability.contactSensor", title: "Contact Opens", required: false, multiple: true
				ifSet "contactClosed$command", "capability.contactSensor", title: "Contact Closes", required: false, multiple: true
				ifSet "acceleration$command", "capability.accelerationSensor", title: "Acceleration Detected", required: false, multiple: true
				ifSet "mySwitch$command", "capability.switch", title: "Switch Turned On", required: false, multiple: true
				ifSet "mySwitchOff$command", "capability.switch", title: "Switch Turned Off", required: false, multiple: true
				ifSet "arrivalPresence$command", "capability.presenceSensor", title: "Arrival Of", required: false, multiple: true
				ifSet "departurePresence$command", "capability.presenceSensor", title: "Departure Of", required: false, multiple: true
				ifSet "smoke$command", "capability.smokeDetector", title: "Smoke Detected", required: false, multiple: true
				ifSet "water$command", "capability.waterSensor", title: "Water Sensor Wet", required: false, multiple: true
				ifSet "lock$command", "capability.lock", title: "Lock locks", required: false, multiple: true
				ifSet "lockLocks$command", "capability.lock", title: "Lock unlocks", required: false, multiple: true
				ifSet "button1$command", "capability.button", title: "Button Press", required:false, multiple:true //remove from production
				ifSet "triggerModes$command", "mode", title: "System Changes Mode", required: false, multiple: true
				ifSet "timeOfDay$command", "time", title: "At a Scheduled Time", required: false
			}
		}
		def hideable = anythingSet || app.installationState == "COMPLETE"
		def sectionTitle = anythingSet ? "Select additional triggers" : "Select triggers..."

		section(sectionTitle, hideable: hideable, hidden: true){
			ifUnset "motion$command", "capability.motionSensor", title: "Motion Here", required: false, multiple: true
			ifUnset "contact$command", "capability.contactSensor", title: "Contact Opens", required: false, multiple: true
			ifUnset "contactClosed$command", "capability.contactSensor", title: "Contact Closes", required: false, multiple: true
			ifUnset "acceleration$command", "capability.accelerationSensor", title: "Acceleration Detected", required: false, multiple: true
			ifUnset "mySwitch$command", "capability.switch", title: "Switch Turned On", required: false, multiple: true
			ifUnset "mySwitchOff$command", "capability.switch", title: "Switch Turned Off", required: false, multiple: true
			ifUnset "arrivalPresence$command", "capability.presenceSensor", title: "Arrival Of", required: false, multiple: true
			ifUnset "departurePresence$command", "capability.presenceSensor", title: "Departure Of", required: false, multiple: true
			ifUnset "smoke$command", "capability.smokeDetector", title: "Smoke Detected", required: false, multiple: true
			ifUnset "water$command", "capability.waterSensor", title: "Water Sensor Wet", required: false, multiple: true
			ifUnset "lock$command", "capability.lock", title: "Lock locks", required: false, multiple: true
			ifUnset "lock$command", "capability.lock", title: "Lock unlocks", required: false, multiple: true
			ifUnset "button1$command", "capability.button", title: "Button Press", required:false, multiple:true //remove from production
			ifUnset "triggerModes$command", "mode", title: "System Changes Mode", description: "Select mode(s)", required: false, multiple: true
			ifUnset "timeOfDay$command", "time", title: "At a Scheduled Time", required: false
		}
}

def addMessage() {
	dynamicPage(name: "addMessage") {
		section{
			 input "message","text",title:"Play this message?", required:actionType == "Message"? true:flase
             paragraph "You can use wilcard with your message"
             paragraph "#name = Device Name ex. Kitchen Light, Speaker Room"
             paragraph "#type = Device Type ex. Motion, Temperature"
             paragraph "#value = New Device State ex. On, Off, Active"
             paragraph "#mode = Location Mode ex. Stay, Away"
             paragraph "#location = Location Name ex. Home, Office"
		}
		section("Message settings", hideable:true, hidden: true) {
	        //input "externalTTS", "bool", title: "Force Only External Text to Speech", required: false, defaultValue: false
            input "ttsMode", "enum", title: "Mode?", required: true, defaultValue: "Vioce RSS",submitOnChange:true, options: ["SmartTings","Ivona","Vioce RSS","Google"]
            href "ttsKey", title: "Voice RSS Key", description: ttsApiKey, state: ttsApiKey ? "complete" : "incomplete", required: ttsMode == "Vioce RSS"?true:false
            href "ttsKeyIvona", title: "Ivona Access Key", description: "${ttsAccessKey?:""}-${ttsSecretKey?:""}" ,state: ttsAccessKey && ttsSecretKey ? "complete" : "incomplete",  required: ttsMode == "Ivona" ? true:false
            input "voiceIvona", "enum", title: "Ivona Voice?", required: true, defaultValue: "en-US Salli", options: ["cy-GB Gwyneth","cy-GB Geraint","da-DK Naja","da-DK Mads","de-DE Marlene","de-DE Hans","en-US Salli","en-US Joey","en-AU Nicole","en-AU Russell","en-GB Amy","en-GB Brian","en-GB Emma","en-GB Gwyneth","en-GB Geraint","en-IN Raveena","en-US Chipmunk","en-US Eric","en-US Ivy","en-US Jennifer","en-US Justin","en-US Kendra","en-US Kimberly","es-ES Conchita","es-ES Enrique","es-US Penelope","es-US Miguel","fr-CA Chantal","fr-FR Celine","fr-FR Mathieu","is-IS Dora","is-IS Karl","it-IT Carla","it-IT Giorgio","nb-NO Liv","nl-NL Lotte","nl-NL Ruben","pl-PL Agnieszka","pl-PL Jacek","pl-PL Ewa","pl-PL Jan","pl-PL Maja","pt-BR Vitoria","pt-BR Ricardo","pt-PT Cristiano","pt-PT Ines","ro-RO Carmen","ru-RU Tatyana","ru-RU Maxim","sv-SE Astrid","tr-TR Filiz"]
        }
	}
}

def chooseTrack() {
	dynamicPage(name: "chooseTrack") {
		section{
			input "song","enum",title:"Play this track", required:true, multiple: false, options: songOptions()
		}
	}
}

def ttsKey() {
	dynamicPage(name: "ttsKey") {
		section{
			input "ttsApiKey", "text", title: "TTS Key", required: false
		}
        section ("Voice RSS provides free Text-to-Speech API as WEB service, allows 350 free request per day with high quality voice") {
            href(name: "hrefRegister",
                 title: "Register",
                 required: false,
                 style: "external",
                 url: "http://www.voicerss.org/registration.aspx",
                 description: "Register and obtain you TTS Key")
            href(name: "hrefKnown",
                 title: "Known about Voice RSS",
                 required: false,
                 style: "external",
                 url: "http://www.voicerss.org/",
                 description: "Go to www.voicerss.org")
        }
    }
}

def ttsKeyIvona() {
	dynamicPage(name: "ttsKeyIvona") {
		section{
			input "ttsAccessKey", "text", title: "Ivona Access Key", required: false,  defaultValue:""
            input "ttsSecretKey", "text", title: "Ivona Secret Key", required: false, defaultValue:""
		}
        section ("Ivona provides free Text-to-Speech API as WEB service, allows 50K free request per month with high quality voice") {
            href(name: "hrefRegisterIvona",
                 title: "Register",
                 required: false,
                 style: "external",
                 url: "https://www.ivona.com/us/for-business/speech-cloud/",
                 description: "Register and obtain you Access and Secret Key")
            href(name: "hrefKnownIvona",
                 title: "Known about Ivona",
                 required: false,
                 style: "external",
                 url: "https://www.ivona.com/us/",
                 description: "Go to www.ivona.com")
        }
    }
}
private songOptions() {
	// Make sure current selection is in the set
	log.trace "size ${sonos?.size()}"
	def options = new LinkedHashSet()
	if (state.selectedSong?.station) {
		options << state.selectedSong.station
	}
	else if (state.selectedSong?.description) {
		// TODO - Remove eventually? 'description' for backward compatibility
		options << state.selectedSong.description
	}

	// Query for recent tracks
    
	def dataMaps
	sonos.each {
		log.trace "it $it"
            dataMaps = it.statesSince("trackData", new Date(0), [max:30]).collect{it.jsonValue}
            options.addAll(dataMaps.collect{it.station})
    }
	log.trace "${options.size()} songs in list"
	options.take(30 * (sonos?.size()?:0)) as List
}

private saveSelectedSong() {
	try {
        if (song == state.selectedSong?.station){
        	log.debug "Selected song $song"
        }
        else{
            def dataMaps
            def data
            log.info "Looking for $song"
            
            sonos.each {
                dataMaps = it.statesSince("trackData", new Date(0), [max:30]).collect{it.jsonValue}
                log.info "Searching ${dataMaps.size()} records"
                data = dataMaps.find {s -> s.station == song}
                log.info "Found ${data?.station?:"None"}"
                if (data) {
                    state.selectedSong = data
                    log.debug "Selected song = $state.selectedSong"
                }
                else if (song == state.selectedSong?.station) {
                    log.debug "Selected song not found"
                }
             }
        }
	}
	catch (Throwable t) {
		log.error t
	}
}

private anythingSet(command="") {
	for (name in ["motion","contact","contactClosed","acceleration","mySwitch","mySwitchOff","arrivalPresence","departurePresence","smoke","water","lock","lockLocks","button1","timeOfDay","triggerModes","timeOfDay"]) {
        if (settings["$name$command"]) {
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

}

def updated() {
	log.debug "Updated with settings: ${settings}"
	unsubscribe()
	unschedule()
	subscribeToEvents()
    subscribeToEvents("Stop")
}

def subscribeToEvents(command="") {
	subscribe(settings["contact$command"], "contact.open",  "eventHandler$command")
	subscribe(settings["contactClosed$command"], "contact.closed",  "eventHandler$command")
	subscribe(settings["acceleration$command"], "acceleration.active",  "eventHandler$command")
	subscribe(settings["motion$command"], "motion.active",  "eventHandler$command")
	subscribe(settings["mySwitch$command"], "switch.on", "eventHandler$command")
	subscribe(settings["mySwitchOff$command"], "switch.off", "eventHandler$command")
	subscribe(settings["arrivalPresence$command"], "presence.present",  "eventHandler$command")
	subscribe(settings["departurePresence$command"], "presence.not present",  "eventHandler$command")
	subscribe(settings["smoke$command"], "smoke.detected",  "eventHandler$command")
	subscribe(settings["smoke$command"], "smoke.tested",  "eventHandler$command")
	subscribe(settings["smoke$command"], "carbonMonoxide.detected",  "eventHandler$command")
	subscribe(settings["water$command"], "water.wet",  "eventHandler$command")
	subscribe(settings["lock$command"], "lock.lock",  "eventHandler$command")
	subscribe(settings["lockLock$command"], "lock.unlock",  "eventHandler$command")
	subscribe(settings["button1$command"], "button.pushed",  "eventHandler$command")

	if (settings["timeOfDay$command"]) {
        schedule(settings["timeOfDay$command"], "eventHandler$command")
    }
	if (settings["triggerModes$command"]) {
        subscribe(location, modeChangeHandler)
    }

    if (command == "")
    {
    	subscribe(app, appTouchHandler)

		if (song) {
            saveSelectedSong()
        }

        loadText()

        if (radioTunesM) {
            state.radioTunesM = radioTunesM.sort()
            state.lastRTS = state.radioTunesM[-1]
        }
    }
}

def eventHandler(evt=null) {
    def modeOk = true
    if(evt?.name == "mode"){
    	if (!(evt.value in triggerModes)) {
			modeOk = false
		}
    }
    log.trace "modeOk $modeOk"
    
	if (allOk && modeOk ) {
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

def eventHandlerStop(evt) {
	def modeOk = true
    if(evt?.name == "mode"){
    	if (!(evt.value in triggerModesStop)) {
			modeOk = false
		}
    }
    
    if (allOk && modeOk) {
		log.trace "allOk"
				sonos.stop()
	}
}


def appTouchHandler(evt) {
	takeAction(evt)
    // ttsIvona("the mail has arrived yesterday")
    
}

private takeAction(evt) {
	def radioTunesStations = [["RT 00's Hits":["key":"hit00s","artURI":"http://static.audioaddict.com/d/9/c/0/6/0/d9c060393b8e606a8b124edeec6b28f0.png","description":"All your favorites from the beginning of the century. Expect familiar favorites and hidden gems. "]],["RT 00's R&B":["key":"00srnb","artURI":"http://static.audioaddict.com/7/6/3/d/c/3/763dc3ccb478c6b9f62512cdc00508fe.png","description":"The best in R&B from the noughties!"]],["RT 60's Rock":["key":"60srock","artURI":"http://static.audioaddict.com/2/c/d/a/3/1/2cda314915c57288384a8dacd54e8ef6.png","description":"The very best in classic 60s Rock!"]],["RT 80's Alt & New Wave":["key":"80saltnnewwave","artURI":"http://static.audioaddict.com/e/5/c/0/2/a/e5c02aa3d79950dff491a470a36ebf8d.png","description":"The sounds of the 80s underground. Synths, quirky ideas and pioneering musicians. "]],["RT 80's Dance":["key":"80sdance","artURI":"http://static.audioaddict.com/3/2/3/6/5/a/32365acb3fc314b7752994c566c546d0.png","description":"The best 80s Dance Party on the web!"]],["RT 80's Rock Hits":["key":"80srock","artURI":"http://static.audioaddict.com/3318d27fbfcf6c699fe235e6f1ceb467.png","description":"Your favorite rock hits from the 80s."]],["RT 90's Hits":["key":"hit90s","artURI":"http://static.audioaddict.com/2/4/8/7/d/6/2487d65f7b584e92c73f5fbb3d6cb979.png","description":"Reminisce and relive the hits of the 90s!"]],["RT 90's R&B":["key":"90srnb","artURI":"http://static.audioaddict.com/0/f/2/2/5/9/0f2259b1e91d45b967e9963c761adf5e.png","description":"The hottest 90s R&B hits and jams!"]],["RT Alternative Rock":["key":"altrock","artURI":"http://static.audioaddict.com/4c1e38925a1d195686c180d7fdd355bb.png","description":"The best Alternative Rock hits you want to hear."]],["RT Ambient":["key":"rtambient","artURI":"http://static.audioaddict.com/a/b/7/8/9/a/ab789a23f774577306847150dc425297.png","description":"A blend of ambient, downtempo, and chillout"]],["RT American Songbook":["key":"americansongbook","artURI":"http://static.audioaddict.com/3fbd5add268ebd2aff68bf963ea59660.png","description":"Classic standard voices from the likes of Mel Torme & Nat King Cole"]],["RT Baroque Period":["key":"baroque","artURI":"http://static.audioaddict.com/b/7/b/0/c/2/b7b0c26558f5d6a5b77d71117008c2ae.png","description":"Listen to delightful classical music from the Baroque Period (1600-1760)"]],["RT Bebop Jazz":["key":"bebop","artURI":"http://static.audioaddict.com/3df6292d6a6cbc2b04d007703ab2f7b2.png","description":"Swinging to the sounds of bebop and straight ahead jazz"]],["RT Best of the 60's":["key":"hit60s","artURI":"http://static.audioaddict.com/0/c/0/f/7/3/0c0f73e059ff0f6150a23a0bb3ece5a0.png","description":"Relive the memories of the 60’s greatest hits."]],["RT Best of the 80's":["key":"the80s","artURI":"http://static.audioaddict.com/e/7/8/3/9/b/e7839be1803a7734a554b55537b62968.png","description":"Hear your classic favorites right here!"]],["RT Blues Rock":["key":"bluesrock","artURI":"http://static.audioaddict.com/9/a/b/4/0/1/9ab401f69096e40747d0a6c5a8ccf6d2.png","description":"A fusion of Blues and Rock."]],["RT Bossa Nova":["key":"bossanova","artURI":"http://static.audioaddict.com/42b7913c019bbe47ee258d13a7dbf5bb.png","description":"100% pure bossa nova channel, enjoy the sweet flavors of Brazil!"]],["RT Café de Paris":["key":"cafedeparis","artURI":"http://static.audioaddict.com/3/0/6/2/3/4/3062348e97fe268f62ba94ccbcfdcc57.png","description":"Watch the world go by on a sunny Paris day."]],["RT Chillout":["key":"rtchillout","artURI":"http://static.audioaddict.com/0/a/c/5/a/9/0ac5a9ce5b258e16fec514722b92d580.png","description":"Full of trippy flavors, this channel is just what you need to relax."]],["RT Classic Christmas":["key":"christmas","artURI":"http://static.audioaddict.com/2/0/6/3/e/8/2063e8d092e256d59eaa72f41caf611a.png","description":"Classic Christmas and Holiday favorites."]],["RT Classic Hip-Hop":["key":"classicrap","artURI":"http://static.audioaddict.com/c54b89c96b6917acf4ec3ddd9d8e5f7c.png","description":"The best in classic hip-hop!!"]],["RT Classic Motown":["key":"classicmotown","artURI":"http://static.audioaddict.com/5/1/a/f/a/9/51afa9fce9927303d8e45a8158133048.png","description":"Relive the hits and discover the early sounds of Motown."]],["RT Classic Rock":["key":"classicrock","artURI":"http://static.audioaddict.com/243efc74a39092c1b2c1f5eb3b6ee0e2.png","description":"Relive the classic rock sounds of the 70s and 80s"]],["RT Classical Guitar":["key":"guitar","artURI":"http://static.audioaddict.com/47e1f6acc1b1fd8947eb63053389c228.png","description":"A mix of classical, spanish, and flamenco guitar"]],["RT Classical Period":["key":"classicalperiod","artURI":"http://static.audioaddict.com/8/e/b/d/f/8/8ebdf87f34ee623c96b26086d2e248c7.png","description":"Enjoy musical compositions from masters of the Classical Period (1730-1820)."]],["RT Classical Piano Trios":["key":"classicalpianotrios","artURI":"http://static.audioaddict.com/ae73ffca4235946a6bc958609b3632da.png","description":"Classical Piano Trios hits!"]],["RT Club Bollywood":["key":"clubbollywood","artURI":"http://static.audioaddict.com/ac488cfbc1ddcf62947b71bc51a7b808.png","description":"Dance Hits from Bollywood!!"]],["RT Contemporary Christian":["key":"christian","artURI":"http://static.audioaddict.com/9ed827ddee44c35e34b9de2bdf425048.png","description":"Today's Christian favorites and undiscovered hits"]],["RT Country":["key":"country","artURI":"http://static.audioaddict.com/25bc8f07c1cfd7d90eea968724105082.png","description":"Today's Hit Country with a mix of your favorites"]],["RT DaTempo Lounge":["key":"rtdatempolounge","artURI":"http://static.audioaddict.com/9145b6ea36ca63b0911dc6c537de89cb.png","description":"Amazing combination of jazz, lounge, bossa, and much more!"]],["RT Dance Hits":["key":"dancehits","artURI":"http://static.audioaddict.com/cdb89061045f4207b909558ca01b8637.png","description":"The hottest dance tunes 24/7"]],["RT Dave Koz & Friends":["key":"davekoz","artURI":"http://static.audioaddict.com/1/b/d/b/c/d/1bdbcdda2bcc5f34baa727c6f03334e6.png","description":"Smooth Jazz selected by Dave Koz himself!"]],["RT Disco Party":["key":"discoparty","artURI":"http://static.audioaddict.com/c/c/d/d/b/6/ccddb6f390a0401e7bc4088afa5fbaff.png","description":"The ultimate Disco Party of all time!"]],["RT Downtempo Lounge":["key":"downtempolounge","artURI":"http://static.audioaddict.com/c/9/1/b/3/6/c91b36a066ec8a69606fe9857ee529d6.png","description":"Relax with some Downtempo grooves."]],["RT Dreamscapes":["key":"dreamscapes","artURI":"http://static.audioaddict.com/8ca99e36c4854358d1ce76c30ccae16a.png","description":"Relax to the sounds of dream and Ibiza style chillout"]],["RT EDM Fest":["key":"edmfest","artURI":"http://static.audioaddict.com/4/8/7/2/1/0/4872108c77039672e7f4054cc728a820.png","description":"The best DJs from the biggest festivals around the world!"]],["RT EuroDance":["key":"rteurodance","artURI":"http://static.audioaddict.com/7/e/3/d/b/0/7e3db0459654142b8409e913f40fc95b.png","description":"The newest and best of Eurodance hits"]],["RT Hard Rock":["key":"hardrock","artURI":"http://static.audioaddict.com/7d3e6634592359e2a0c1d6e5f3f5ddaf.jpg","description":"Bang along with the best Hard Rock tracks anywhere"]],["RT Hit 70's":["key":"hit70s","artURI":"http://static.audioaddict.com/6fe772afcb2ea44eab2048f1f60af18d.png","description":"All Hits, All The Time!"]],["RT Holiday Smooth Jazz":["key":"holidaysmoothjazz","artURI":"http://static.audioaddict.com/a/6/d/7/6/d/a6d76dd7cee8ab6883d55a72951ef7ab.png","description":"Holiday Smooth Jazz favorites!"]],["RT Indie Rock":["key":"indierock","artURI":"http://static.audioaddict.com/3c1d60925ae8dac00ef4da3beaa7d371.png","description":"Indie, Alternative, and Underground Rock!"]],["RT Jazz Classics":["key":"jazzclassics","artURI":"http://static.audioaddict.com/30b34af272931d0e032f148a013439dd.png","description":"Classic Jazz hits from the golden age"]],["RT Jpop":["key":"jpop","artURI":"http://static.audioaddict.com/61251c16fe02215ab58207577efd71d0.png","description":"Fresh hits from Japanese pop culture!"]],["RT Lounge":["key":"rtlounge","artURI":"http://static.audioaddict.com/b/4/f/2/b/0/b4f2b0c2ffa8e06a9e05de5cdd0bcfe5.png","description":"Sit back and enjoy the lounge grooves!"]],["RT Love Music":["key":"lovemusic","artURI":"http://static.audioaddict.com/9feee3db11b67dee318c8f36a99adb9b.png","description":"Easy listening and Romantic hits from the heart!!"]],["RT Meditation":["key":"meditation","artURI":"http://static.audioaddict.com/2/1/0/0/c/5/2100c502ffe38ee9710194ab0bdc6846.png","description":"Music for the mind and body"]],["RT Mellow Jazz":["key":"mellowjazz","artURI":"http://static.audioaddict.com/c/4/a/7/e/c/c4a7ec91601ac6ce18859c2edeff9c80.png","description":"Relax and unwind to the Mellow side of Jazz."]],["RT Mellow Smooth Jazz":["key":"mellowsmoothjazz","artURI":"http://static.audioaddict.com/3/b/6/d/9/e/3b6d9e5edaeaac40e071cb63a5e542d4.png","description":"Relaxing Smooth Jazz for your listening pleasure."]],["RT Metal":["key":"metal","artURI":"http://static.audioaddict.com/9be3c5b65f9274df72b78b6ecddf5bb2.png","description":"Best Place for your METAL fix on the net"]],["RT Modern Blues":["key":"modernblues","artURI":"http://static.audioaddict.com/61457ec9c9b71e483c67b8482c010403.png","description":"Listen to the best in Modern Blues!"]],["RT Modern Rock":["key":"modernrock","artURI":"http://static.audioaddict.com/cdcf2c3754d96c41b8bcd81414451770.jpg","description":"Where Modern rock lives on the net."]],["RT Mostly Classical":["key":"classical","artURI":"http://static.audioaddict.com/e3302f3ef1f85c53f3a13d0209102b26.png","description":"Listen and Relax, it's good for you!"]],["RT Movie Soundtracks":["key":"soundtracks","artURI":"http://static.audioaddict.com/7b648b3c6789173a12666a63d421aa96.png","description":"A wide variety of Soundtracks from Movies, show themes, & more!"]],["RT Mozart":["key":"mozart","artURI":"http://static.audioaddict.com/b/0/5/5/c/4/b055c4c2f8fd947e7e417b58b4663dd0.png","description":"Relax to the wonderful works of classical composer Wolfgang Amadeus Mozart (1756-1791)."]],["RT Nature":["key":"nature","artURI":"http://static.audioaddict.com/7501be44c9536cbd48968b2d7a2f4d81.png","description":"soothing sounds of nature music!"]],["RT New Age":["key":"newage","artURI":"http://static.audioaddict.com/ee90d885488f0d224c9cabe5d2ea770b.png","description":"Soothing sounds of new age and world music!"]],["RT Old School Funk & Soul":["key":"oldschoolfunknsoul","artURI":"http://static.audioaddict.com/e/d/0/f/8/e/ed0f8e974f6e137aefab8429f1a21999.png","description":"Classic Funky rhythms and Soulful grooves!"]],["RT Oldies":["key":"oldies","artURI":"http://static.audioaddict.com/b2f77e0f416e1396721b44f9f4b73374.png","description":"Three decades of great oldies on RadioTunes.com"]],["RT Piano Jazz":["key":"pianojazz","artURI":"http://static.audioaddict.com/3a78648bbc729bd4dc9ca944ce89d0df.png","description":"Enjoy the sounds of piano jazz from historic and modern masters."]],["RT Pop Christmas":["key":"popchristmas","artURI":"http://static.audioaddict.com/f/d/0/7/8/a/fd078af11dca797ba88508db9699219c.png","description":"Favorite Pop Christmas hits!"]],["RT Pop Rock":["key":"poprock","artURI":"http://static.audioaddict.com/c2bf1bfc843db7bc6ccf7563bd5ade7f.png","description":"Pop & rock hits!"]],["RT Reggaeton":["key":"reggaeton","artURI":"http://static.audioaddict.com/a/5/5/9/a/7/a559a793e4870517f42bd0eb7642cfa2.png","description":"Reggae rhythms with Latin and Caribbean flavors!"]],["RT Relaxation":["key":"relaxation","artURI":"http://static.audioaddict.com/df99e575070665e78685364ce35a26b8.png","description":"relaxing music for the mind and soul"]],["RT Relaxing Ambient Piano":["key":"relaxingambientpiano","artURI":"http://static.audioaddict.com/3/e/1/e/0/a/3e1e0acfbfd342ebe7c0fda2dc4df0b0.png","description":"Take time to relax to the sounds of ambient piano"]],["RT Romantic Period":["key":"romantic","artURI":"http://static.audioaddict.com/0/d/9/c/4/7/0d9c47e55b7fb9807b9ed9dd375d5cd5.png","description":"Enjoy the best of classical music from the Romantic Period (1780-1910)."]],["RT Romantica":["key":"romantica","artURI":"http://static.audioaddict.com/7e66489ebd3e3229d3fdfd10e950d262.png","description":"Timeless pop classical voices!"]],["RT Roots Reggae":["key":"rootsreggae","artURI":"http://static.audioaddict.com/cbf2c683fa84e16f6a03a178070d32cb.png","description":"The best of classic and modern Roots Reggae "]],["RT Salsa":["key":"salsa","artURI":"http://static.audioaddict.com/01e8dabaa5c29e58b7f6515d309481f5.png","description":"Best Salsa Collection now on RadioTunes.com"]],["RT Slow Jams":["key":"slowjams","artURI":"http://static.audioaddict.com/0/b/e/f/3/4/0bef34a4e4cc5dd4b4ee16c78427133f.png","description":"Soulful R&B jams and ballads."]],["RT Smooth Bossa Nova":["key":"smoothbossanova","artURI":"http://static.audioaddict.com/9/0/4/3/5/3/9043538c481c93174a6a5432b0f58c40.png","description":"The smoothest Bossa Nova direct from Brazil."]],["RT Smooth Jazz":["key":"smoothjazz","artURI":"http://static.audioaddict.com/ea944543ab6247d58e1703d84e451e4f.png","description":"The worlds smoothest jazz 24 hours a day"]],["RT Smooth Jazz 24'7":["key":"smoothjazz247","artURI":"http://static.audioaddict.com/4cb303531bcbff5ef8b61dbfe995568c.png","description":"Smooth Jazz DJs bring you the best music, 24 hours a day!"]],["RT Smooth Lounge":["key":"smoothlounge","artURI":"http://static.audioaddict.com/8a8c05b2da5d78ddaf9e453236e311ff.jpg","description":"Relax and unwind with the smoothest lounge vibes."]],["RT Soft Rock":["key":"softrock","artURI":"http://static.audioaddict.com/3d79cb01a53e1795728d112c1bb00e0b.jpg","description":"The best in Soft Rock music!"]],["RT Solo Piano":["key":"solopiano","artURI":"http://static.audioaddict.com/657f189ce01b74b7734063154775ca31.png","description":"Musical journeys with piano masters and talented undiscovered pianists"]],["RT Top Hits":["key":"tophits","artURI":"http://static.audioaddict.com/a9778d4dec931485c25f5ac182ac71e2.png","description":"Who cares about the chart order, less rap & more hits!"]],["RT Uptempo Smooth Jazz":["key":"uptemposmoothjazz","artURI":"http://static.audioaddict.com/16f9c144baa98176ab4f98dc3538adf4.png","description":"The world's smoothest jazz, grooving 24 hours a day"]],["RT Urban Hits":["key":"urbanjamz","artURI":"http://static.audioaddict.com/05f8add3c89ba75d1b1dfca9cc8e5df9.png","description":"Kickin' with the baddest beats on the 'net"]],["RT Urban Pop Hits":["key":"urbanpophits","artURI":"http://static.audioaddict.com/c/1/1/7/b/8/c117b8cea21df06b15bd86d8b810f8d2.png","description":"The best in Urban crossover hits"]],["RT Vocal Chillout":["key":"rtvocalchillout","artURI":"http://static.audioaddict.com/1/c/c/8/b/5/1cc8b5c256d603302446b51cc242b9fd.png","description":"Enjoy the relaxing vocal sounds of Ibiza chillout"]],["RT Vocal Lounge":["key":"rtvocallounge","artURI":"http://static.audioaddict.com/a/c/c/c/4/9/accc49b2d1b70feb5b21603d67cc444f.png","description":"Relaxing Vocals and Lounge Grooves"]],["RT Vocal New Age":["key":"vocalnewage","artURI":"http://static.audioaddict.com/09885117eb43050f5a64e790a684426d.png","description":"Soothing vocal sounds of new age music!"]],["RT Vocal Smooth Jazz":["key":"vocalsmoothjazz","artURI":"http://static.audioaddict.com/beb3e04b65b282052419bd15c81e1be4.png","description":"The best of vocal smooth jazz!!"]],["RT World":["key":"world","artURI":"http://static.audioaddict.com/a2ffcb17080207ca578d00f5c7eff1f0.png","description":"New Age influenced world music"]],["DI 00's Club Hits":["key":"00sclubhits","artURI":"http://static.audioaddict.com/1/f/2/1/8/9/1f2189badb0bb9ccba20e54163afff69.png","description":"Your favorite dance tunes from the start of the decade. Familiar hits and overlooked classics in abundance."]],["DI Ambient":["key":"ambient","artURI":"http://static.audioaddict.com/c/4/9/3/9/b/c4939b4e0129cd9abada597e35332db3.png","description":"Electronic sounds and atmospheric textures create a genre to enhance your state of mind and take you deeper."]],["DI Bass & Jackin' House":["key":"bassnjackinhouse","artURI":"http://static.audioaddict.com/8/9/b/0/d/f/89b0dfb93cb7eba4d345d116f7fc00e7.png","description":"From the funkiest grooves to the dirtiest beats. Hard-hitting, high energy 4/4 club cuts to move the masses."]],["DI Bassline":["key":"bassline","artURI":"http://static.audioaddict.com/9/8/b/b/d/b/98bbdb73486e5c0431a44117af617576.png","description":"Blending together elements of house music, speed garage, and techno – it’s all about the low end frequencies."]],["DI Big Beat":["key":"bigbeat","artURI":"http://static.audioaddict.com/9/4/9/e/f/b/949efba54329d6d9264dfd54eeebbc31.png","description":"Heavily focused on breakbeats and dusty samples. A defining 90s musical movement still going strong today."]],["DI Big Room House":["key":"bigroomhouse","artURI":"http://static.audioaddict.com/4/7/0/c/3/c/470c3c21ce2cc8a75de82bfd4e150db5.png","description":"Fusing together house elements from the past and the present - prime time music full of uplifting high energy."]],["DI Breaks":["key":"breaks","artURI":"http://static.audioaddict.com/0/9/6/b/4/7/096b470d163f6a8ddf9568c1140de311.png","description":"Inspired by hip hop and UK rave music, breaks features broken up drum loops and creative samples, synths and fx."]],["DI Chill & Tropical House":["key":"chillntropicalhouse","artURI":"http://static.audioaddict.com/4/8/c/5/5/f/48c55f45921a5ba9671612172dce8f38.png","description":"The sounds of Chill & Tropical House are expertly made for lounging and dancing alike with its deeper house vibes."]],["DI ChillHop":["key":"chillhop","artURI":"http://static.audioaddict.com/0/3/a/5/5/d/03a55df1c3c530f09541cb327bbfa160.png","description":"Hip hop, trip hop, downtempo beats and jazz, blended together in a mellow, laid back style for perfect listening."]],["DI Chillout":["key":"chillout","artURI":"http://static.audioaddict.com/0/a/c/5/a/9/0ac5a9ce5b258e16fec514722b92d580.png","description":"Electronic sounds, mellow mid-tempo rhythms, and a groove meant to calm the senses and ease the mind."]],["DI Chillout Dreams":["key":"chilloutdreams","artURI":"http://static.audioaddict.com/6/3/a/3/2/b/63a32b7141318d2710e110c8c98f023b.png","description":"The perfect musical soundtrack for when you want to close your eyes, get truly comfortable, and drift away."]],["DI Chillstep":["key":"chillstep","artURI":"http://static.audioaddict.com/b/8/8/e/4/0/b88e40971c79b1992e0f2918cd860426.png","description":"The brilliant combination of dubstep rhythms with the mellow grooves of chillout. A unique sound all its own."]],["DI Classic EuroDance":["key":"classiceurodance","artURI":"http://static.audioaddict.com/4/9/f/9/e/0/49f9e008cf82d38e765056fee38aef53.png","description":"European pop music born in the 90s full of high energy sounds and big hooks – now heard in gyms and malls worldwide."]],["DI Classic EuroDisco":["key":"classiceurodisco","artURI":"http://static.audioaddict.com/a/c/c/9/8/b/acc98b9031c28e3b597942b266e232d9.png","description":"Conceived in the European discos in the 70s, evolving through the decades into modern electronic masterpieces."]],["DI Classic Trance":["key":"classictrance","artURI":"http://static.audioaddict.com/e/a/0/c/d/f/ea0cdf92be65b3cee2f0b751dccb050f.png","description":"The classic melodies, the epic breakdowns and gigantic builds. Re-experience Trance music in her prime."]],["DI Classic Vocal Trance":["key":"classicvocaltrance","artURI":"http://static.audioaddict.com/9/4/0/0/6/9/940069d31cbc53274b4f0a50cf2fbcbe.png","description":"Classic sounds of Vocal Trance"]],["DI Club Dubstep":["key":"clubdubstep","artURI":"http://static.audioaddict.com/6/1/3/b/5/d/613b5d61816136c6fb771a8dd4794e02.png","description":"The bassbin rattling, speaker-freaking hits of Dubstep – all tried, tested and approved to work in the clubs."]],["DI Club Sounds":["key":"club","artURI":"http://static.audioaddict.com/f/2/6/1/9/8/f26198abff74faaad61a82da02493205.png","description":"The music heard in the biggest venues worldwide. From prime time pushers to deeper house shakers - the sounds of now."]],["DI DJ Mixes":["key":"djmixes","artURI":"http://static.audioaddict.com/1/9/2/4/2/0/1924207c886a3da744a152450f5b6d27.png","description":"From techno, deep house, progressive and trance – check out the sounds of the DJ deep in the mix."]],["DI Dark DnB":["key":"darkdnb","artURI":"http://static.audioaddict.com/d/b/2/f/e/c/db2fec32340bb5bc70750150794f2b02.png","description":"Evil, gritty and twisted Drum & Bass. at 160+ BPM, hear the darkest basslines and the hardest hitting percussion."]],["DI Dark PsyTrance":["key":"darkpsytrance","artURI":"http://static.audioaddict.com/7/a/d/d/f/6/7addf6ec30967ceec317dc46c861f0d1.png","description":"The darker form of PsyTrance, which is a sound all its own – direct from Goa to your headphones."]],["DI Deep House":["key":"deephouse","artURI":"http://static.audioaddict.com/8/8/c/7/9/b/88c79b893d3f1b8ef20061481bc8bb85.png","description":"House music crafted for the smaller and mid-sized rooms - deeper tracks full of silky, smooth grooves."]],["DI Deep Nu-Disco":["key":"deepnudisco","artURI":"http://static.audioaddict.com/c/9/3/1/0/9/c93109f3a47ad465899cfd7ce8eecbaf.png","description":"Elements of house, funk, and disco. Mid-tempo beats, soulful grooves and head nodding selections."]],["DI Deep Tech":["key":"deeptech","artURI":"http://static.audioaddict.com/3/3/4/a/6/5/334a65257316355d68ceae52915e0ca4.png","description":"A fusion of deep house & techno. Punchy grooves, spaced out sounds and forward thinking productions."]],["DI Detroit House & Techno":["key":"detroithousentechno","artURI":"http://static.audioaddict.com/5/d/0/8/6/3/5d086388aca22629f80ba7c65bd4a163.png","description":"Where would dance music be without Detroit? The city that started it all continues to inspire and educate. "]],["DI Disco House":["key":"discohouse","artURI":"http://static.audioaddict.com/3/b/c/1/1/6/3bc1165683457f8b40e6f27c67f035f2.png","description":"The feel good sound inspired from 70s disco combined with the warm kick drum of modern house music."]],["DI Downtempo Lounge":["key":"downtempolounge","artURI":"http://static.audioaddict.com/c/9/1/b/3/6/c91b36a066ec8a69606fe9857ee529d6.png","description":"Head nodding beats, chilled vocals, and lush soundscapes to bring down the sun and start the night."]],["DI Drum and Bass":["key":"drumandbass","artURI":"http://static.audioaddict.com/8/6/3/5/5/3/863553af90444d857b7153dd65297978.png","description":"Born in the mid 90s, drum and bass is all about fast breakbeats, urban vibes, and rib rattling basslines."]],["DI Drumstep":["key":"drumstep","artURI":"http://static.audioaddict.com/e/d/6/a/0/7/ed6a072e2ee5db23ceed7136fa2db72b.png","description":"A hybrid of half-time dubstep and intense drum 'n bass."]],["DI Dub":["key":"dub","artURI":"http://static.audioaddict.com/e/4/b/3/4/6/e4b346b193c1adec01f8489b98a2bf3f.png","description":"An emphasis on the bass and drums, delayed effects, sampled vocals and smokey Reggae inspired vibes."]],["DI Dub Techno":["key":"dubtechno","artURI":"http://static.audioaddict.com/4/6/7/7/d/1/4677d19284fdb4522cd9e60ec4244686.png","description":"The beloved sounds of deep techno saturated with tape delays, heavy reverb and ice cold atmospherics."]],["DI Dubstep":["key":"dubstep","artURI":"http://static.audioaddict.com/5/c/a/a/9/d/5caa9d88fa6e4d94fa961eeb09274cc1.png","description":"The wobbles of the bass, the party rocking beats, and the biggest crowd destroying drops."]],["DI EBM":["key":"ebm","artURI":"http://static.audioaddict.com/9/6/9/d/1/a/969d1a4840606786752ebb02bad71a8a.png","description":"Originating in the early 80s as a mix of industrial, punk and electropop, EBM changed the landscape of dance music and is still going strong today. "]],["DI EcLectronica":["key":"eclectronica","artURI":"http://static.audioaddict.com/9/3/8/5/f/2/9385f28bf483594b3a7c057b98447c99.png","description":"Creative music influenced from techno to chill out, indie to IDM – a unique and undefinable listening experience."]],["DI Electro House":["key":"electro","artURI":"http://static.audioaddict.com/5/2/e/e/0/8/52ee085deb8ac70e729df4f2040f08b5.png","description":"Buzzing basslines, huge kicks, party rocking drops. House music packed full of gigantic bass and massive synths."]],["DI Electro Swing":["key":"electroswing","artURI":"http://static.audioaddict.com/f/3/7/d/d/e/f37dde025f56dee1631102fc3ad9d2b0.png","description":"The combination of 1920s-1940s jazz and swing music, big band horns and modern day electro house. "]],["DI Electronic Pioneers":["key":"electronicpioneers","artURI":"http://static.audioaddict.com/9/3/c/5/e/b/93c5ebadd2ad7a53041b51dc725dcdc2.png","description":"The trailblazers, the renegades and the experimental musicians who gave early inspiration with electronic instruments."]],["DI Electronics":["key":"electronics","artURI":"http://static.audioaddict.com/7/5/2/8/8/a/75288a5811df82a782718253222e8154.png","description":"30+ years of open-genre electronic music. From spatial ambient sounds to experimental techno and more. "]],["DI Electropop":["key":"electropop","artURI":"http://static.audioaddict.com/7/2/8/5/2/e/72852e54a50b903aa0a726f87c0050c2.jpg","description":"Catchy pop music blended together with vintage synthesizers and electronic instrumentation. "]],["DI Epic Trance":["key":"epictrance","artURI":"http://static.audioaddict.com/0/3/7/c/8/d/037c8d66803eccfea64068c77ae5b8c1.png","description":"Trance in its most boisterous form. Uplifting melodies on top of high energy beats create these euphoric anthems."]],["DI EuroDance":["key":"eurodance","artURI":"http://static.audioaddict.com/7/e/3/d/b/0/7e3db0459654142b8409e913f40fc95b.png","description":"Pop music infused with a high energy 4/4 pulse. Heavy on the synthesizers, the melodies and the vocals."]],["DI Funky House":["key":"funkyhouse","artURI":"http://static.audioaddict.com/6/1/e/1/f/2/61e1f2a91781b119e34ef0e42bd472e0.png","description":"Focused on the funkiest grooves, with plenty of the guitar licks and clever samples placed around a 4/4 swing."]],["DI Future Beats":["key":"futurebeats","artURI":"http://static.audioaddict.com/5/6/0/6/5/9/560659bc59ac29cd9a4eb8a63a469267.png","description":"Gritty, off-kilter and typically instrumental, the Future Beats sound is perfectly married with modern technology and hip hop idealism."]],["DI Future Garage":["key":"futuregarage","artURI":"http://static.audioaddict.com/d/6/a/a/1/e/d6aa1e9b4c48141fa573d498eba41a2a.png","description":"2step Garage rhythms, chunky bass line driven grooves and plenty of forward thinking innovation."]],["DI Future Synthpop":["key":"futuresynthpop","artURI":"http://static.audioaddict.com/7/9/b/b/6/2/79bb62753a747e41c1a95f3ec2b9558b.png","description":"Finest selection of futurepop and synthpop."]],["DI Gabber":["key":"gabber","artURI":"http://static.audioaddict.com/a/7/7/e/2/d/a77e2dc16fa3120e8520138351daa1e3.png","description":"The hardest form of techno with punishing tracks designed to drive the crowds into a sweaty frenzy."]],["DI Glitch Hop":["key":"glitchhop","artURI":"http://static.audioaddict.com/1/5/e/e/a/5/15eea5494b52ec59d8f426eff1a76f20.png","description":"The sound of digital malfunctions, electric hum and bit rate distortions perfectly placed alongside laid-back hip hop beats."]],["DI Goa-Psy Trance":["key":"goapsy","artURI":"http://static.audioaddict.com/6/6/9/1/9/1/669191c83ed74b8cdf2aa6d81e8d363c.png","description":"A very psychedelic form of trance, Goa-Psy Trance is a sound full of arpeggiated synths and trippy effects."]],["DI Hands Up":["key":"handsup","artURI":"http://static.audioaddict.com/8/6/4/b/7/7/864b7747bc603062a7fc7517ec7f0345.png","description":"A channel showcasing everything from hard dance, trance and happy hardcore to lift the spirits (and the arms)."]],["DI Hard Dance":["key":"harddance","artURI":"http://static.audioaddict.com/c/1/5/7/3/b/c1573be02af1d585f16df89c43842c19.png","description":"Concrete kicks and punching rhythms, hard dance is a tougher side of music with sharp edges and aggressive power."]],["DI Hard Techno":["key":"hardtechno","artURI":"http://static.audioaddict.com/b/4/7/6/2/7/b476279b174274a9c790ee1c43d4b890.png","description":"Tough as nails warehouse jams full of cold aggression, sinister structures and pounding rhythms that hit hard."]],["DI Hardcore":["key":"hardcore","artURI":"http://static.audioaddict.com/e/2/2/a/3/1/e22a3126abcf1d50831da25a1571c762.png","description":"Strictly for the hardcore. These are the biggest and boldest bangers, and the hardest hitting tracks."]],["DI Hardstyle":["key":"hardstyle","artURI":"http://static.audioaddict.com/7/b/9/a/5/c/7b9a5c73b648f8d10e76b4c3f7073482.png","description":"Hard techno & hardcore. A global phenomenon with powerful kicks, distorted effects and infectious melodies."]],["DI House":["key":"house","artURI":"http://static.audioaddict.com/8/6/e/2/d/3/86e2d391797ddfd688281e6a480810e2.png","description":"Born in Chicago and now global, house music is always evolving but remains true to it’s pure 4/4 structure."]],["DI IDM":["key":"idm","artURI":"http://static.audioaddict.com/9/6/6/d/9/5/966d955e9ffc6124be1d185703a436c4.png","description":"Experimental, influential and pushing the boundaries of electronic music. Truly a sound to experience. "]],["DI Indie Dance":["key":"indiedance","artURI":"http://static.audioaddict.com/2/4/2/5/f/3/2425f3532b9f9e0c2c32ab13889a9aba.png","description":"The spirit of Rock & Roll with an electronic soul. Club culture and live music combined."]],["DI Jazz House":["key":"jazzhouse","artURI":"http://static.audioaddict.com/4/9/e/1/5/9/49e159ac3b8473eac86af4cc1e24ffd3.png","description":"One of the biggest cultural soundtracks with the infectious thump of house music. Expect sultry saxophones, trumpets, and finger snapping grooves."]],["DI Jungle":["key":"jungle","artURI":"http://static.audioaddict.com/1/5/0/1/2/8/1501288819231087619e6e659f122830.png","description":"Jungle keeps the breakbeat tempos high and celebrates the diverse ideas found within urban and rave music."]],["DI Latin House":["key":"latinhouse","artURI":"http://static.audioaddict.com/c/b/f/4/e/a/cbf4ea080e36bf804f12710114dc3fff.png","description":"The sounds of Salsa, Brazilian beats and Latin Jazz with the steady grooves of modern East Coast dance music."]],["DI Liquid DnB":["key":"liquiddnb","artURI":"http://static.audioaddict.com/2/0/3/7/7/c/20377c1dc238c3b1b26686d9e18d525a.png","description":"Smooth as water, with the fast paced rhythms, liquid DNB flows with rolling ease without losing momentum."]],["DI Liquid Dubstep":["key":"liquiddubstep","artURI":"http://static.audioaddict.com/2/9/5/5/b/3/2955b33a5856b82d08b247d395c93b0b.png","description":"Smooth, rolling and steady – this fresh formation of Dubstep keeps the sounds you love with a flowing groove."]],["DI Liquid Trap":["key":"liquidtrap","artURI":"http://static.audioaddict.com/f/3/5/0/f/4/f350f444c8d87b080c08a2abe9b6106f.png","description":"The smoother side of Trap but still packed with mechanical grooves and hip hop moods. "]],["DI Lounge":["key":"lounge","artURI":"http://static.audioaddict.com/8/4/d/3/4/e/84d34e19efed230a40372a3baefc36ae.png","description":"Music to chill to. Music made for when it’s all about kicking off your shoes, laying back, and totally relaxing."]],["DI Mainstage":["key":"mainstage","artURI":"http://static.audioaddict.com/4/8/7/2/1/0/4872108c77039672e7f4054cc728a820.png","description":"The sound of the largest events. From the gargantuan festivals, the huge main rooms and the biggest DJs."]],["DI Melodic Progressive":["key":"melodicprogressive","artURI":"http://static.audioaddict.com/3/d/8/e/b/1/3d8eb1823a29d891b516cc3bf2f539c9.png","description":"The melodic side of progressive house, packed with driving rhythms and forward thinking sounds."]],["DI Minimal":["key":"minimal","artURI":"http://static.audioaddict.com/8/9/6/2/a/e/8962aee714699c3829e75af9c0daf5e7.png","description":"Minimal fuses elements of house, techno and electronica and strips it back to focus on the spaces between the sound."]],["DI Nightcore":["key":"nightcore","artURI":"http://static.audioaddict.com/2/2/0/0/1/3/2200134b0c655a3cd40e0fbf7380c9a0.png","description":"Pitched up vocals, happy hardcore beats, and high energy music non-stop."]],["DI Nu Disco":["key":"nudisco","artURI":"http://static.audioaddict.com/4/b/a/0/6/8/4ba0684daed5c3c422b8ad3aa59c7eaf.png","description":"Modern disco music blending the familiar funk of the 70s and 80s with futuristic beats and up to date grooves."]],["DI Oldschool Acid":["key":"oldschoolacid","artURI":"http://static.audioaddict.com/5/6/d/7/3/4/56d7347c364602778e7dd2a7d978fdf0.png","description":"Acid, one of the characteristics of the TB-303, is celebrated here with the best tracks from house, techno and trance."]],["DI Oldschool House":["key":"oldschoolhouse","artURI":"http://static.audioaddict.com/2/0/8/1/4/e/20814e2198e8a865d013b01e4ac240f9.png","description":"The biggest classics and secret weapons – this is a true treasure chest of house tracks from back in the day."]],["DI Oldschool Rave":["key":"oldschoolrave","artURI":"http://static.audioaddict.com/d/5/f/a/2/6/d5fa2667564e4b467e5ca589cf736188.png","description":"Grab your whistles, white gloves and reach for the laser beams. This is the sound of raving when raving was new."]],["DI Oldschool Techno & Trance ":["key":"classictechno","artURI":"http://static.audioaddict.com/2/b/f/d/7/4/2bfd74849c0ee53409de7ebff2c0d2ca.png","description":"Go back in time and hear the biggest and best tracks within techno and trance that defined a decade of dance culture."]],["DI Progressive":["key":"progressive","artURI":"http://static.audioaddict.com/3/6/6/6/2/3/366623574b853a6d4baadd8e53cd7705.png","description":"Always moving forward, progressive continues to reinvent itself into new sounds and styles made for the floor."]],["DI Progressive Psy":["key":"progressivepsy","artURI":"http://static.audioaddict.com/6/a/2/e/5/3/6a2e53e87c32e51522e834633cf9ff68.png","description":"Progress your mind to undiscovered psychedelic dimensions."]],["DI PsyChill":["key":"psychill","artURI":"http://static.audioaddict.com/2/7/6/c/f/b/276cfbdd00ab5c9056a5230534995dd6.png","description":"Downtempo psychedelic dub grooves, goa ambient, and world beats."]],["DI Psybient":["key":"psybient","artURI":"http://static.audioaddict.com/1/7/8/8/0/2/178802e0d43b3d42f2476a183541d652.jpg","description":"The psychedelic side of ambient."]],["DI Russian Club Hits":["key":"russianclubhits","artURI":"http://static.audioaddict.com/4/b/b/e/7/9/4bbe79959cc5d89ebdacfaa451f57888.png","description":"Russia's hottest club hits."]],["DI Soulful House":["key":"soulfulhouse","artURI":"http://static.audioaddict.com/f/c/2/2/f/1/fc22f19941e8415db97a3ca3179f3fa0.png","description":"House music saturated with feeling – full of melodies, vocals and true soul. Steady warm 4/4 vibes."]],["DI Space Dreams":["key":"spacemusic","artURI":"http://static.audioaddict.com/0/9/c/1/b/6/09c1b6a2c0e65c7421fd1d44667883ab.png","description":"Ambient space music for expanding minds."]],["DI Tech House":["key":"techhouse","artURI":"http://static.audioaddict.com/c/9/9/4/6/7/c994677734e5f735a9001d40d19331b4.png","description":"Blending the warmth of house music with the cold structural precision of techno, tech house bridges the divide."]],["DI Techno":["key":"techno","artURI":"http://static.audioaddict.com/c/2/8/5/d/9/c285d97136321b58f0f629cbe3e059ac.png","description":"Techno is a true musical force full of structure and style. Robotic, mechanical and full of soul, always facing the future."]],["DI Trance":["key":"trance","artURI":"http://static.audioaddict.com/6/3/0/d/5/c/630d5cae24fcb42de6dc7dfbd22b21d6.png","description":"Emotive high energy dance music which embraces melodies, vocals and a true journey of dance music songwriting."]],["DI Trap":["key":"trap","artURI":"http://static.audioaddict.com/b/d/f/7/2/f/bdf72fa4995da25fa8c21f9b84b485d4.png","description":"Born out of Southern Hip-Hop and influenced by techno, trap is analogue drum machines with hip-hop aesthetics."]],["DI Tribal House":["key":"tribalhouse","artURI":"http://static.audioaddict.com/1/e/0/4/a/9/1e04a95bcd04b4be4e1dee1f68c2125d.png","description":"The percussive side of the house and tech house scene, tribal house takes drums and puts them in the forefront."]],["DI UMF Radio":["key":"umfradio","artURI":"http://static.audioaddict.com/2/c/9/1/e/9/2c91e9bbb77821106c9905653a5ade9e.png","description":"UMF Radio 24/7"]],["DI Underground Techno":["key":"undergroundtechno","artURI":"http://static.audioaddict.com/c/f/a/e/e/9/cfaee945340928dd2250e731efda8e6c.png","description":"From gritty Berlin streets to dark corners of Brooklyn, this is techno made by artists pushing the genre further. "]],["DI Vocal Chillout":["key":"vocalchillout","artURI":"http://static.audioaddict.com/1/c/c/8/b/5/1cc8b5c256d603302446b51cc242b9fd.png","description":"Relaxing vibes and a collection of vocal songs providing the laid back soundtrack to your day."]],["DI Vocal Lounge":["key":"vocallounge","artURI":"http://static.audioaddict.com/a/c/c/c/4/9/accc49b2d1b70feb5b21603d67cc444f.png","description":"Laid back grooves and a collection of smooth vocals soothe the ears and relax the mind."]],["DI Vocal Trance":["key":"vocaltrance","artURI":"http://static.audioaddict.com/e/9/0/2/c/a/e902ca41e34c8f5f2c6386bd1378d2e8.png","description":"Lush vocals paired together with emotive dance music. Beautiful melodies and endless energy."]],["RR 60's Rock":["key":"60srock","artURI":"http://static.audioaddict.com/2/c/d/a/3/1/2cda314915c57288384a8dacd54e8ef6.png","description":"The very best in classic 60s Rock!"]],["RR 80's Alternative":["key":"alternative80s","artURI":"http://static.audioaddict.com/a/0/b/0/6/7/a0b0674cf50925b0f9bfb5b40f89a98b.png","description":"The best in Alternative hits from the 80s."]],["RR 80's Rock":["key":"80srock","artURI":"http://static.audioaddict.com/4f9170a0727efa30e93d1acff3938d1c.jpg","description":"Get your 80s rock nostalgia fix"]],["RR 90's Alternative":["key":"alternative90s","artURI":"http://static.audioaddict.com/b/7/6/2/7/3/b76273e5bcdb3a8d595f548951d276e3.png","description":"The best in Alternative hits from the 90s."]],["RR 90's Rock":["key":"90srock","artURI":"http://static.audioaddict.com/3fb0f3e50eddd4fe9c89530e209ff959.jpg","description":"If it ROCKED in the 90s you will hear it here"]],["RR Black Metal":["key":"blackmetal","artURI":"http://static.audioaddict.com/7d739d76ebc1fdbae4dadd25bb1965d2.jpg","description":"From its dark inception until today"]],["RR Blues Rock":["key":"bluesrock","artURI":"http://static.audioaddict.com/e/f/7/9/0/6/ef7906b4e84160e5c95d165b7426c916.png","description":"A fusion of Blues and Rock."]],["RR Classic Hard Rock":["key":"classichardrock","artURI":"http://static.audioaddict.com/f2e7b37880ce298d3396b7b335fbe30b.jpg","description":"Classic Hard Rock hits from the 70s, 80s, and 90s"]],["RR Classic Metal":["key":"classicmetal","artURI":"http://static.audioaddict.com/9/2/7/b/d/0/927bd088daa1bb63966c497b0a6083d2.png","description":"Classic Metal hits and artists"]],["RR Classic Rock":["key":"classicrock","artURI":"http://static.audioaddict.com/d26636837f630057d6b21356d36f1fb7.jpg","description":"Classic rock hits from the 70s and 80s"]],["RR Death Metal":["key":"deathmetal","artURI":"http://static.audioaddict.com/c67fc39c2603300e9e3b14bf09338c86.jpg","description":"Get your death fix now"]],["RR Grunge":["key":"grunge","artURI":"http://static.audioaddict.com/2/3/2/9/1/7/232917998149c86d37b849d562812c90.jpg","description":"Hardcore Punk meets Heavy Metal"]],["RR Hair Bands":["key":"hairbands","artURI":"http://static.audioaddict.com/3bde74a414c5b001817906338f8fac72.jpg","description":"Hair-flying metal and rock!"]],["RR Hard Rock":["key":"hardrock","artURI":"http://static.audioaddict.com/9f4204eebbd94d8ca53a189d2011a6f7.jpg","description":"The best selection of Hard Rock hits anywhere"]],["RR Heavy Metal":["key":"heavymetal","artURI":"http://static.audioaddict.com/a7f48428e166b87577b447d3ff75b292.jpg","description":"Where the heavier side of metal is heard"]],["RR Indie Rock":["key":"indierock","artURI":"http://static.audioaddict.com/b585c3db95ce5084e573da4db7d94030.jpg","description":"Hear the best of Indie Rock"]],["RR Industrial":["key":"industrial","artURI":"http://static.audioaddict.com/a/3/5/8/c/b/a358cb744f70204d6b62e58ec0732461.jpg","description":"Industrialized Rock with heavy machinery"]],["RR Melodic Death Metal":["key":"melodicdeathmetal","artURI":"http://static.audioaddict.com/a/2/0/3/4/1/a2034141cec2ff28a7b78b08a2796226.png","description":"A Melodic blend of Heavy and Death Metal."]],["RR Metal":["key":"metal","artURI":"http://static.audioaddict.com/8dba038dab079236da26ce347b97ed74.jpg","description":"Featuring all metal from its inception until today"]],["RR Metalcore":["key":"metalcore","artURI":"http://static.audioaddict.com/9c6c4055fea238919b208e9afd2d8596.jpg","description":"Melodic choruses and Hardcore breakdowns"]],["RR Modern Rock":["key":"modernrock","artURI":"http://static.audioaddict.com/6ad6bb355bc1e1264c4f617d9a04f125.jpg","description":"Modern hits from the greats to the new bands"]],["RR Nu Metal":["key":"numetal","artURI":"http://static.audioaddict.com/81135974cf871e2918ea5125f24aca71.jpg","description":"Nu Metal variety from the beginning to today"]],["RR Pop Rock":["key":"poprock","artURI":"http://static.audioaddict.com/9c537734af32928ca49e4114689e513d.jpg","description":"The catchiest Rock channel on the net"]],["RR Power Metal":["key":"powermetal","artURI":"http://static.audioaddict.com/32fabbbe247e6f9f7b1be441c5e9ea6f.jpg","description":"Non-stop amazing guitar work and grandiose vocals"]],["RR Progressive Rock":["key":"progressiverock","artURI":"http://static.audioaddict.com/6/4/9/b/a/7/649ba7da6e22624dd57d52a6f5bdcfc6.png","description":"Progressive Rock inspired by Jazz, Blues and Funk."]],["RR Punk Rock":["key":"punkrock","artURI":"http://static.audioaddict.com/823413ff8915d898daea167a52a4dd78.jpg","description":"The best variety in Punk from its inception until now"]],["RR Rock Ballads":["key":"rockballads","artURI":"http://static.audioaddict.com/8/c/9/a/d/1/8c9ad1046f81bb948c7d36f93206937b.png","description":"Hold up your lighters for these Rock Ballads!"]],["RR Screamo-Emo":["key":"screamoemo","artURI":"http://static.audioaddict.com/0a39beab4a16aec97325a7da30e0a3b5.jpg","description":"Emotionally charged music"]],["RR Soft Rock":["key":"softrock","artURI":"http://static.audioaddict.com/d07fab49394001410fda74e0fd7335cb.jpg","description":"The best variety of Soft Rock tracks you know and love"]],["RR Symphonic Metal":["key":"symphonicmetal","artURI":"http://static.audioaddict.com/a581b7cc59cc919a5b546dc82caa097c.jpg","description":"Soaring vocals with power riffs "]],["RR Thrash Metal":["key":"thrashmetal","artURI":"http://static.audioaddict.com/5/0/5/4/5/1/5054515721cbb79c97940117ed083395.png","description":"Adrenaline fuelled, head-banging Thrash Metal!"]],["JR Bass Jazz":["key":"bassjazz","artURI":"http://static.audioaddict.com/0ecb36a21b56961dc17a4238bdbc8ef4.png","description":"Rhythmic sounds of the double bass"]],["JR Bebop":["key":"bebop","artURI":"http://static.audioaddict.com/ee8ae50ca460d96b14fe60c06ad4fa76.png","description":"Enjoy the sounds of fast-tempo improvised jazz"]],["JR Blues":["key":"blues","artURI":"http://static.audioaddict.com/7eb6e8f021df91680057969b53a8fac3.png","description":"America’s roots music with guitar and harmonica"]],["JR Blues Rock":["key":"bluesrock","artURI":"http://static.audioaddict.com/e/f/7/9/0/6/ef7906b4e84160e5c95d165b7426c916.png","description":"A fusion of blues and rock music"]],["JR Bossa Nova":["key":"bossanova","artURI":"http://static.audioaddict.com/f37ec4c15239f47cc62f5102102c7926.png","description":"Experience the gently swaying rhythms of Brazil"]],["JR Classic Jazz":["key":"classicjazz","artURI":"http://static.audioaddict.com/6f05681fe275e06e2ce0a5231190b1b4.png","description":"The artists and music that started it all"]],["JR Contemporary Vocals":["key":"vocaljazz","artURI":"http://static.audioaddict.com/464a58cef2d07a3c19b3cd6d4b540a0a.png","description":"Modern singers from the new era of jazz"]],["JR Cool Jazz":["key":"cooljazz","artURI":"http://static.audioaddict.com/7bbe970517a8140c10e2bfcebed0dd8a.png","description":"Relaxed tempos and lighter tones from the West Coast"]],["JR Current Jazz":["key":"currentjazz","artURI":"http://static.audioaddict.com/7b9f423bb312b2775fd3678fe6c3d4da.png","description":"Explore the modern artists and sounds of jazz"]],["JR Dave Koz & Friends":["key":"davekoz","artURI":"http://static.audioaddict.com/1/b/d/b/c/d/1bdbcdda2bcc5f34baa727c6f03334e6.png","description":"Smooth Jazz selected by Dave Koz himself!"]],["JR Flamenco Jazz":["key":"flamencojazz","artURI":"http://static.audioaddict.com/f/7/c/6/e/0/f7c6e093e2d8b9e52d565939a2ce66c9.png","description":"A perfect blend of flamenco strings with jazz and spanish guitar"]],["JR Fusion Lounge":["key":"fusionlounge","artURI":"http://static.audioaddict.com/527213685ef138ca5ea6c3f71bbb8531.png","description":"A unique blend of jazz, lounge, & bossa nova"]],["JR Guitar Jazz":["key":"guitarjazz","artURI":"http://static.audioaddict.com/d675746d1d4f83fc54a31a7d21748ea0.png","description":"Hear guitar jazz legends and classic compositions"]],["JR Gypsy Jazz":["key":"gypsyjazz","artURI":"http://static.audioaddict.com/0c25c18afdee7301a9772a448dbeb24a.png","description":"Gypsy Jazz & Hot Club Swing with Django in mind"]],["JR Hard Bop":["key":"hardbop","artURI":"http://static.audioaddict.com/b784b141aff28fa00982295555c5fe1d.png","description":"A funky combination of Bebop and Soul Jazz"]],["JR Holiday Jazz":["key":"holidayjazz","artURI":"http://static.audioaddict.com/c027439ca07108e355442ef23fea87eb.png","description":"Jazz up your season with this selection of holiday-themed tunes"]],["JR Holiday Smooth Jazz":["key":"holidaysmoothjazz","artURI":"http://static.audioaddict.com/f/5/8/b/3/6/f58b362d9c54ce342817bcd51b5c0e01.png","description":"Holiday Smooth Jazz favorites!"]],["JR Jazz Ballads":["key":"jazzballads","artURI":"http://static.audioaddict.com/b/d/0/d/4/9/bd0d492ad03f686c3985a1e6a6dac7aa.png","description":"Instrumental ballads played by great jazz artists"]],["JR Latin Jazz":["key":"latinjazz","artURI":"http://static.audioaddict.com/80189d153dd38df150f98c171f443719.png","description":"Where the lively rhythms and spirit of Latin America meet jazz"]],["JR Mellow Jazz":["key":"mellowjazz","artURI":"http://static.audioaddict.com/4f62e86dd8514028ade4b204050d6b79.png","description":"A more relaxed and laid-back selection of jazz"]],["JR Mellow Smooth Jazz":["key":"mellowsmoothjazz","artURI":"http://static.audioaddict.com/3/b/6/d/9/e/3b6d9e5edaeaac40e071cb63a5e542d4.png","description":"Relaxing Smooth Jazz for your listening pleasure."]],["JR Paris Café":["key":"pariscafe","artURI":"http://static.audioaddict.com/a2b045b0147f9125d1b1b9bd8ae6a72e.png","description":"Watch the world go by & enjoy the music on a sunny Paris day "]],["JR Piano Jazz":["key":"pianojazz","artURI":"http://static.audioaddict.com/adad09d59c4f7348b1148cc9791636f8.png","description":"Enjoy the past and present masters of the piano"]],["JR Piano Trios":["key":"pianotrios","artURI":"http://static.audioaddict.com/7c22f01344d1950da09a0ff8186593cb.png","description":"Piano, bass, and drums - the perfect combination!"]],["JR Saxophone Jazz":["key":"saxophonejazz","artURI":"http://static.audioaddict.com/d7e929bb8cce04c5fe8062352a20f31c.png","description":"The signature sound of jazz"]],["JR Sinatra Style":["key":"sinatrastyle","artURI":"http://static.audioaddict.com/2/0/6/5/b/c/2065bc663703060bf60a2d2645a6f2a0.png","description":"For fans of Sinatra and the Great American Songbook"]],["JR Smooth Bossa Nova":["key":"smoothbossanova","artURI":"http://static.audioaddict.com/9/0/4/3/5/3/9043538c481c93174a6a5432b0f58c40.png","description":"The smoothest Bossa Nova direct from Brazil."]],["JR Smooth Jazz":["key":"smoothjazz","artURI":"http://static.audioaddict.com/b7af4587ffcff49c7930ac88fcf9f711.png","description":"The world’s best mix of instrumental Smooth Jazz"]],["JR Smooth Jazz 24'7":["key":"smoothjazz247","artURI":"http://static.audioaddict.com/911ca46b66b1c50ad6167dd1da183fb3.png","description":"Smooth Jazz DJs bring you the best music, 24 hours a day!"]],["JR Smooth Lounge":["key":"smoothlounge","artURI":"http://static.audioaddict.com/8a8c05b2da5d78ddaf9e453236e311ff.jpg","description":"Relax and unwind with the smoothest lounge vibes"]],["JR Smooth Uptempo":["key":"smoothuptempo","artURI":"http://static.audioaddict.com/9f95e88e85575b2cbdab1c4def14a72d.png","description":"Upbeat smooth jazz grooves from your favorite artists"]],["JR Smooth Vocals":["key":"smoothvocals","artURI":"http://static.audioaddict.com/9df076d503ce55eec902a10e2d852925.png","description":"Hear only the best in vocal smooth jazz"]],["JR Straight-Ahead":["key":"straightahead","artURI":"http://static.audioaddict.com/16b5f5fa7940f687d8003119f6506cdc.png","description":"Keeping the traditions of classic jazz alive"]],["JR Swing & Big Band":["key":"swingnbigband","artURI":"http://static.audioaddict.com/f5b869b49b939ad3d862c4e1c49f956b.png","description":"Legendary big bands and star soloists of the swing era"]],["JR Timeless Classics":["key":"timelessclassics","artURI":"http://static.audioaddict.com/614b9e8f72ed053e5cac66ee7f9c23b0.png","description":"A mix of early jazz, swing, big bands, and classic tunes"]],["JR Trumpet Jazz":["key":"trumpetjazz","artURI":"http://static.audioaddict.com/420798739106bbacd1c38c931214cee5.png","description":"Bright sounds of jazz’s most exciting horns"]],["JR Vibraphone Jazz":["key":"vibraphonejazz","artURI":"http://static.audioaddict.com/f6ad2772b240e4b7aebe784bef8714dc.png","description":"The masters who have made their mark on the vibes"]],["JR Vocal Legends":["key":"vocallegends","artURI":"http://static.audioaddict.com/036f741129e25fe38ab82efe6a6ab0e4.png","description":"Unforgettable voices sing the classics"]]]
    def speech
    def domain
    switch(actionType) {
        case "Message":
			speech = safeTextToSpeech(normalizeMessage(message,evt))
			sonos.playTrackAndResume(speech.uri, speech.duration, volume)
            break
        case "Sound":
			sonos.playTrackAndResume(state.sound.uri, state.sound.duration, volume)
            break
		case "Track":
        	if(state.selectedSong){
            	sonos.playTrack(state.selectedSong)
            }else{
            	speech = safeTextToSpeech("No Track was Selected")
				sonos.playTrackAndResume(speech.uri, speech.duration, volume)
            }
            break
        case "Radio Tunes":
            domain = radioTunes.startsWith("RT")?"radiotunes.com:80/radiotunes":radioTunes.startsWith("DI")?"di.fm:80/di":radioTunes.startsWith("JR")?"jazzradio.com:80/jr":radioTunes.startsWith("RR")?"rockradio.com:80/rr":""
          
            sonos.playTrack("x-rincon-mp3radio://pub${RTServer}.${domain}_${radioTunesStations[radioTunes].key[0]}?${RTKey}","<DIDL-Lite xmlns=\"urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:upnp=\"urn:schemas-upnp-org:metadata-1-0/upnp/\" xmlns:dlna=\"urn:schemas-dlna-org:metadata-1-0/\"><item id=\"1\" parentID=\"1\" restricted=\"1\"><upnp:class>object.item.audioItem.audioBroadcast</upnp:class><upnp:album>Radio Tunes</upnp:album><upnp:artist>${groovy.xml.XmlUtil.escapeXml(radioTunesStations[radioTunes].description[0])}</upnp:artist><upnp:albumArtURI>${groovy.xml.XmlUtil.escapeXml(radioTunesStations[radioTunes].artURI[0])}</upnp:albumArtURI><dc:title>${groovy.xml.XmlUtil.escapeXml(radioTunes)}</dc:title><res protocolInfo=\"http-get:*:audio/mpeg:DLNA.ORG_PN=MP3;DLNA.ORG_OP=01;DLNA.ORG_CI=0;DLNA.ORG_FLAGS=01500000000000000000000000000000\" >${groovy.xml.XmlUtil.escapeXml("x-rincon-mp3radio://pub${RTServer}.${domain}_${radioTunesStations[radioTunes].key[0]}?${RTKey}")} </res></item> </DIDL-Lite>")
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
            //def RTStation = state.radioTunesM[-1]
            Collections.rotate(state.radioTunesM, -1)
			domain = state.radioTunesM[-1].startsWith("RT")?"radiotunes.com:80/radiotunes":state.radioTunesM[-1].startsWith("DI")?"di.fm:80/di":state.radioTunesM[-1].startsWith("JR")?"jazzradio.com:80/jr":state.radioTunesM[-1].startsWith("RR")?"rockradio.com:80/rr":""
            sonos.playTrack("x-rincon-mp3radio://pub${RTServer}.${domain}_${radioTunesStations[state.radioTunesM[-1]].key[0]}?${RTKey}","<DIDL-Lite xmlns=\"urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:upnp=\"urn:schemas-upnp-org:metadata-1-0/upnp/\" xmlns:dlna=\"urn:schemas-dlna-org:metadata-1-0/\"><item id=\"1\" parentID=\"1\" restricted=\"1\"><upnp:class>object.item.audioItem.musicTrack</upnp:class><upnp:album>Radio Tunes</upnp:album><upnp:artist>${groovy.xml.XmlUtil.escapeXml(radioTunesStations[state.radioTunesM[-1]].description[0])}</upnp:artist><upnp:albumArtURI>${groovy.xml.XmlUtil.escapeXml(radioTunesStations[state.radioTunesM[-1]].artURI[0])}</upnp:albumArtURI><dc:title>${groovy.xml.XmlUtil.escapeXml(state.radioTunesM[-1])}</dc:title><res protocolInfo=\"http-get:*:audio/mpeg:DLNA.ORG_PN=MP3;DLNA.ORG_OP=01;DLNA.ORG_CI=0;DLNA.ORG_FLAGS=01500000000000000000000000000000\" >${groovy.xml.XmlUtil.escapeXml("x-rincon-mp3radio://pub${RTServer}.${domain}_${radioTunesStations[state.radioTunesM[-1]].key[0]}?${RTKey}")} </res></item> </DIDL-Lite>")
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

}

private textToSpeechT(message){
    if (message) {
        if(ttsAccessKey && ttsSecretKey){
        	[uri: ttsIvona(message), duration: "${5 + Math.max(Math.round(message.length()/12),2)}"]
        }
		else if (ttsApiKey){
            [uri: "x-rincon-mp3radio://api.voicerss.org/" + "?key=$ttsApiKey&hl=en-us&r=0&f=48khz_16bit_mono&src=" + URLEncoder.encode(message, "UTF-8").replaceAll(/\+/,'%20') +"&sf=//s3.amazonaws.com/smartapp-" , duration: "${5 + Math.max(Math.round(message.length()/12),2)}"]
        }
        else{
        	message = message.length() >100 ? message[0..90] :message
        	[uri: "x-rincon-mp3radio://www.translate.google.com/translate_tts?tl=en&client=t&q=" + URLEncoder.encode(message, "UTF-8").replaceAll(/\+/,'%20') +"&sf=//s3.amazonaws.com/smartapp-", duration: "${5 + Math.max(Math.round(message.length()/12),2)}"]
     	}
    }else{
    	[uri: "https://s3.amazonaws.com/smartapp-media/tts/633e22db83b7469c960ff1de955295f57915bd9a.mp3", duration: "10"]
    }
}

private safeTextToSpeech(message) {
	message = message?:"You selected the Text to Speach Function but did not enter a Message"
    switch(ttsMode){
        case "Ivona":
        	[uri: ttsIvona(message), duration: "${5 + Math.max(Math.round(message.length()/12),2)}"]
        break
        case "Vioce RSS":
        	[uri: "x-rincon-mp3radio://api.voicerss.org/" + "?key=$ttsApiKey&hl=en-us&r=0&f=48khz_16bit_mono&src=" + URLEncoder.encode(message, "UTF-8").replaceAll(/\+/,'%20') +"&sf=//s3.amazonaws.com/smartapp-" , duration: "${5 + Math.max(Math.round(message.length()/12),2)}"]
        break
        case "Google":
        	message = message.length() >100 ? message[0..90] :message
        	[uri: "x-rincon-mp3radio://www.translate.google.com/translate_tts?tl=en&client=t&q=" + URLEncoder.encode(message, "UTF-8").replaceAll(/\+/,'%20') +"&sf=//s3.amazonaws.com/smartapp-", duration: "${5 + Math.max(Math.round(message.length()/12),2)}"]
        break
        default:
            try {
            	textToSpeech(message)
            }
            catch (Throwable t) {
                log.error t
                textToSpeechT(message)
            }
         break
    }
}

def normalizeMessage(message, evt){
	if (evt && message){
        message = message.replace("#name", evt?.displayName?:"") 
        message = message.replace("#type", evt?.name?:"")
        message = message.replace("#value", evt?.value?:"") 
    }
    if (message){
    	message = message.replace("#mode", location.mode)
    	message = message.replace("#location", location.name)
    }
return message
}

def ttsIvona(message){
    def regionName = "us-east-1";
    def df = new java.text.SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'")
    df.setTimeZone(TimeZone.getTimeZone("Europe/London"))
    def amzdate = df.format(new Date())
    def canonicalQueryString = "Input.Data=${URLEncoder.encode(message, "UTF-8").replaceAll(/\+/,'%20')}%3F&Input.Type=text%2Fplain&OutputFormat.Codec=MP3&OutputFormat.SampleRate=22050&Parameters.Rate=default&Voice.Language=${voiceIvona.getAt(0..4)}&Voice.Name=${voiceIvona.getAt(6..-1)}&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=$ttsAccessKey%2F${amzdate.getAt(0..7)}%2F$regionName%2Ftts%2Faws4_request&X-Amz-Date=$amzdate&X-Amz-SignedHeaders=host";  
    "http://urbansa.com/tts.php?uri=${URLEncoder.encode("https://tts.${regionName}.ivonacloud.com/CreateSpeech?$canonicalQueryString&X-Amz-Signature=${hmac_sha256(hmac_sha256(hmac_sha256(hmac_sha256(hmac_sha256("AWS4$ttsSecretKey".bytes,amzdate.getAt(0..7)),regionName),"tts"),"aws4_request"), "AWS4-HMAC-SHA256\n$amzdate\n${amzdate.getAt(0..7)}/$regionName/tts/aws4_request\n${sha256Hash("GET\n/CreateSpeech\n$canonicalQueryString\nhost:tts.${regionName}.ivonacloud.com\n\nhost\ne3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855")}").collect { String.format("%02x", it) }.join('')}")}"
}

def sha256Hash(text) {
    java.security.MessageDigest.getInstance("SHA-256").digest(text.bytes).collect { String.format("%02x", it) }.join('')
}

def hmac_sha256(byte[] secretKey, String data) {
	try {
        Mac mac = Mac.getInstance("HmacSHA256")
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, "HmacSHA256")
        mac.init(secretKeySpec)
        byte[] digest = mac.doFinal(data.bytes)
        return digest
	}
	catch (InvalidKeyException e) {
   		log.error "Invalid key exception while converting to HMac SHA256"	
    }
}


