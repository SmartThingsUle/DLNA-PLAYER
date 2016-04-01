/**
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
 *  Alexa Connect
 *
 *  Author: Ule
 *  Date: 2016-03-20
 * v 1.1  added control devices (switches)
 */
 
 
definition(
	name: "Alexa Connect",
	namespace: "mujica",
	author: "Ule",
	description: "Connect external text command to Alexa",
	category: "SmartThings Labs",
	iconUrl: "http://urbansa.com/icons/mr.png",
	iconX2Url: "http://urbansa.com/icons/mr@2x.png",
	oauth: true)

preferences {
	page(name: "mainPage", title: "Alexa Connect", install: true, uninstall: true)
}

def mainPage() {
	dynamicPage(name: "mainPage") {
		section("Authorize voice controlled devices"){
        	input "switchs", "capability.switch", title: "Switches", required: false ,multiple:true
        }
        
        section() {
			input "mode", "enum", title: "Mode?", required: true, defaultValue: "Speaker",submitOnChange:true, options: ["Speaker","HTML Player"]
            input "language", "enum", title: "Language?", required: true, defaultValue: "EN", options: ["EN","SP","DK","DK","NL"]
            input "sonos", "capability.musicPlayer", title: "On this Speaker", required: true,multiple:true
            input "alexaApiKey", "text", title: "Alexa Access Key", required:true,  defaultValue:"millave"
            input "redirect", "bool", title: "Redirect?", defaultValue: false
            input "urlRedirect", "text", title: "Url Redirect", defaultValue:""
		}
        
        section("Web URL") {
        	paragraph "${state.webUrl?:""}"
        }
        section("Reset Token") {
        	paragraph "Activating this option, creates a new token when push the button in smart app list."
			input "resetOauth", "bool", title: "Reset AOuth Access Token?", defaultValue: false
        }
	}
}

mappings {
	path("/ui") {
		action: [
			GET: "html",
		]
	}
}

def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"
	initialize()
}

def initialize() {
	subscribe(app, getURL)
	if (!state.webUrl){
    	getURL(null)
    }
}

def getURL(e) {
	if (resetOauth) {
		log.debug "Reseting Access Token"
		state.accessToken = null
	}

	if (!state.accessToken) {
		createAccessToken()
		log.debug "Creating new Access Token: $state.accessToken"
	}

	state.webUrl = "https://graph.api.smartthings.com/api/smartapps/installations/${app.id}/ui?access_token=${state.accessToken}&"
	log.debug "WebUrl : ${state.webUrl}"
}

def extractInts( String input ) {
  input.findAll( /\d+/ )*.toInteger()
}

def html() {
    def text = params.text
    def content
    def order
    def command
    def action
    def device
	def metadata = ""
    def matcher
    def intensityN
    def intensity
    def speech
    def supportCommand 
    
   
    
    
    /* You can use this example to fill your language commands */
   
    /* s = start ,  e = ends , a = any , r = regex */



def commands = [
        "EN": ["on": ["a":["turn on","switch on"],"e":["on"]],"off":["a":["turn off","switch off"],"e":["off"]],"setLevel":["r":["turn on.+\\d+%","set.+\\d+%","dim.+\\d+%"]]],
		"SP": ["on": ["s":["luces","enciende","ilumina"]],"off":["s":["apaga","oscurece","luces fuera"]],"setLevel":["r":["enciende.+\\d+%","ilumina.+\\d+%","disminuye.+\\d+%"]]],
		"DK": ["on": ["a":"turn on","a":"switch on","e":"on"],"off":["a":"turn off","a":"switch off","e":"off"]],
		"NL": ["on": ["a":"turn on","a":"switch on","e":"on"],"off":["a":"turn off","a":"switch off","e":"off"]],
		"LI": ["on": ["a":"turn on","a":"switch on","e":"on"],"off":["a":"turn off","a":"switch off","e":"off"]],
		"FI": ["on": ["a":"turn on","a":"switch on","e":"on"],"off":["a":"turn off","a":"switch off","e":"off"]],
		"FC": ["on": ["a":"turn on","a":"switch on","e":"on"],"off":["a":"turn off","a":"switch off","e":"off"]],
		"FR": ["on": ["a":"turn on","a":"switch on","e":"on"],"off":["a":"turn off","a":"switch off","e":"off"]],
		"DL": ["on": ["a":"turn on","a":"switch on","e":"on"],"off":["a":"turn off","a":"switch off","e":"off"]],
		"IT": ["on": ["a":"turn on","a":"switch on","e":"on"],"off":["a":"turn off","a":"switch off","e":"off"]],
		"NO": ["on": ["a":"turn on","a":"switch on","e":"on"],"off":["a":"turn off","a":"switch off","e":"off"]],
		"PL": ["on": ["a":"turn on","a":"switch on","e":"on"],"off":["a":"turn off","a":"switch off","e":"off"]],
		"RU": ["on": ["a":"turn on","a":"switch on","e":"on"],"off":["a":"turn off","a":"switch off","e":"off"]],
		"SW": ["on": ["a":"turn on","a":"switch on","e":"on"],"off":["a":"turn off","a":"switch off","e":"off"]],
		"IS": ["on": ["a":"turn on","a":"switch on","e":"on"],"off":["a":"turn off","a":"switch off","e":"off"]],
		"RO": ["on": ["a":"turn on","a":"switch on","e":"on"],"off":["a":"turn off","a":"switch off","e":"off"]],
		"TR": ["on": ["a":"turn on","a":"switch on","e":"on"],"off":["a":"turn off","a":"switch off","e":"off"]],
		"BR": ["on": ["a":"turn on","a":"switch on","e":"on"],"off":["a":"turn off","a":"switch off","e":"off"]],
       	"TW": ["on": ["a":"turn on","a":"switch on","e":"on"],"off":["a":"turn off","a":"switch off","e":"off"]],
		"CN": ["on": ["a":"turn on","a":"switch on","e":"on"],"off":["a":"turn off","a":"switch off","e":"off"]],
        ]
   
    
    log.trace "text $text"

    if (text){
    	order = text.toLowerCase()
        commands[language].each { actions, actionsValues ->
            actionsValues.each{key,value ->
                switch(key){
                    case "a":
                        value.each{
                            if (order.contains(it)){
                                action = actions
                            }
                        }
                    break
                    case "s":
                        value.each{
                            if (order.startsWith(it)){
                                action = actions
                            }
                        }
                    break
                    case "e":
                        value.each{
                            if (order.endsWith(it)){
                                action = actions
                            }
                        }
                    break
                    case "r":
                        value.each{
                            if (order ==~ /$it/){
                                action = actions
                            }
                        }
                    break
                }
			}
            
        }
        
        switchs.each {
            if (order.contains(it.displayName.toLowerCase())){
                device = it.displayName.toLowerCase()
                if (action){
                	it.supportedCommands.each {com ->
                        if (action == com.name)
							supportCommand = true;
                        }
                }
            }
           
        }
    }
    
    
    if(action && !device){
    	text = "nodevice"
    }
    if(!action && device){
    	text = "noaction"
    }
    if(action && device && !supportCommand){
    	text = "nosupportcommand"
    }
    
    if(action && device && supportCommand){
    text = "ok";
    	switch(action){
            case "on":
            switchs.each {
                if (it.displayName.toLowerCase() == device ){
                    it.on()
                }
            }
            break
            case "off":
            switchs.each {
                if (it.displayName.toLowerCase() == device ){
                    it.off()
                }
            }
            break
            case "setLevel":

            matcher = order =~ /\d+%/
            if (matcher){
                intensity = (matcher[0] =~ /\d+/)[0].toInteger()
                intensity = intensity > 100 ? 100:intensity
            }
            if (intensity){
                switchs.each {
                    if (it.displayName.toLowerCase() == device ){
                        it.setLevel(intensity)
                    }
                }
            }
            break
       }
    }
    
    
    
    
    
    
    
    content = "<form  name='search' id='search'><input type='text' placeholder='Search' id='text' name='text'></form>"
    
    
    if (text){
		if (text == "ok" || text == "nodevice" || text == "noaction" || text == "nosupportcommand"){
        	speech = [uri: "x-rincon-mp3radio://tts.freeoda.com/sound/" + text + ".mp3", duration: "10"]
        }else{
        	speech = [uri: "x-rincon-mp3radio://tts.freeoda.com/alexa.php/" + "?key=$alexaApiKey&text=" + URLEncoder.encode(text, "UTF-8").replaceAll(/\+/,'%20') +"&sf=//s3.amazonaws.com/smartapp-" , duration: "0"]
        }
        
        if (mode == "Speaker"){
            sonos.playTrack(speech.uri)
            if(redirect){
                metadata = "<meta http-equiv='refresh' content='0; url=$urlRedirect' />"
            }
        }else{
            content = "<audio controls><source src='${speech.uri.replace("x-rincon-mp3radio:","http:")}' type='audio/mpeg'></audio>"
            if(redirect){
                content = content + "<form method='get' action='$urlRedirect'><button type='submit'>Redirect</button></form>"
            }
        }
    }
    
    
    def info = ""
    render contentType: "text/html", data: 
    
    info + 
    
    """
    <!DOCTYPE html>
    <html>
    <head>
        $metadata
        <meta name='viewport' content='width=device-width' />
        <meta name='mobile-web-app-capable' content='yes'>
        <meta name='apple-mobile-web-app-capable' content='yes' />
        <meta name='apple-mobile-web-app-status-bar-style' content='black' />
    </head>
    <body>
        <div id='container'>$content</div>
    </body>
    </html>
    """
}
