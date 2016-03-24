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
 *
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
		section() {
			input "mode", "enum", title: "Mode?", required: true, defaultValue: "Speaker",submitOnChange:true, options: ["Speaker","HTML Player"]
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


def html() {
    def text = params.text
    def content
	def metadata = ""
    
    log.trace text
    content = "<form  name='search' id='search'><input type='text' placeholder='Search' id='text' name='text'></form>"
    
    
    if (text){
		def speech = [uri: "x-rincon-mp3radio://tts.freeoda.com/alexa.php/" + "?key=$alexaApiKey&text=" + URLEncoder.encode(text, "UTF-8").replaceAll(/\+/,'%20') +"&sf=//s3.amazonaws.com/smartapp-" , duration: "0"]
        if (mode == "Speaker"){
            log.trace speech.uri
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
    
    render contentType: "text/html", data: 
    
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
