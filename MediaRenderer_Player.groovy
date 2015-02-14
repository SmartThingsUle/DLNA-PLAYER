/** 
 *  MediaRenderer Player
 *
 *  Author: SmartThings Adapted by Ulises Mujica (Ule)
 *
 */
preferences {
    input("confStationURI", "string", title:"Song URL",
        required:false, displayDuringSetup: false)
}


metadata {
	// Automatically generated. Make future change here.
	definition (name: "DLNA Player", namespace: "mujica", author: "SmartThings-Mod-Ule") {
		capability "Actuator"
		capability "Switch"
		capability "Refresh"
		capability "Sensor"
		capability "Music Player"
		capability "Polling"

		attribute "model", "string"
		attribute "trackUri", "string"
		attribute "transportUri", "string"
		attribute "trackNumber", "string"
		attribute "avteurl", "string"
		attribute "avtcurl", "string"
		attribute "rccurl", "string"
		attribute "rceurl", "string"

		command "subscribe"
		command "getVolume"
		command "getCurrentMedia"
		command "getCurrentStatus"
		command "seek"
		command "unsubscribe"
		command "setLocalLevel", ["number"]
		command "tileSetLevel", ["number"]
		command "playTrackAtVolume", ["string","number"]
		command "playTrackAndResume", ["string","number","number"]
		command "playTextAndResume", ["string","number"]
		command "playTrackAndRestore", ["string","number","number"]
		command "playTextAndRestore", ["string","number"]
		command "playSoundAndTrack", ["string","number","json_object","number"]
		command "playTextAndResume", ["string","json_object","number"]
		command "playStation"
	}

	// Main
	standardTile("main", "device.status", width: 1, height: 1, canChangeIcon: true) {
		state "paused", label:'Paused', action:"music Player.play", icon:"st.Electronics.electronics16", nextState:"playing", backgroundColor:"#ffffff"
		state "playing", label:'Playing', action:"music Player.pause", icon:"st.Electronics.electronics16", nextState:"paused", backgroundColor:"#79b821"
		state "grouped", label:'Grouped', icon:"st.Electronics.electronics16", backgroundColor:"#ffffff"
	}

	// Row 1
	standardTile("nextTrack", "device.status", width: 1, height: 1, decoration: "flat") {
		state "next", label:'', action:"music Player.nextTrack", icon:"st.sonos.next-btn", backgroundColor:"#ffffff"
	}
	standardTile("play", "device.status", width: 1, height: 1, decoration: "flat") {
		state "default", label:'', action:"music Player.play", icon:"st.sonos.play-btn", nextState:"playing", backgroundColor:"#ffffff"
		state "grouped", label:'', action:"music Player.play", icon:"st.sonos.play-btn", backgroundColor:"#ffffff"
	}
	standardTile("previousTrack", "device.status", width: 1, height: 1, decoration: "flat") {
		state "previous", label:'', action:"music Player.previousTrack", icon:"st.sonos.previous-btn", backgroundColor:"#ffffff"
	}

	// Row 2
	standardTile("status", "device.status", width: 1, height: 1, decoration: "flat", canChangeIcon: true) {
		state "playing", label:'Playing', action:"music Player.pause", icon:"st.Electronics.electronics16", nextState:"paused", backgroundColor:"#ffffff"
		state "stopped", label:'Stopped', action:"music Player.play", icon:"st.Electronics.electronics16", nextState:"playing", backgroundColor:"#ffffff"
		state "paused", label:'Paused', action:"music Player.play", icon:"st.Electronics.electronics16", nextState:"playing", backgroundColor:"#ffffff"
		state "grouped", label:'Grouped', action:"", icon:"st.Electronics.electronics16", backgroundColor:"#ffffff"
	}
	standardTile("stop", "device.status", width: 1, height: 1, decoration: "flat") {
		state "default", label:'', action:"music Player.stop", icon:"st.sonos.stop-btn", backgroundColor:"#ffffff"
		state "grouped", label:'', action:"music Player.stop", icon:"st.sonos.stop-btn", backgroundColor:"#ffffff"
	}
	standardTile("mute", "device.mute", inactiveLabel: false, decoration: "flat") {
		state "unmuted", label:"", action:"music Player.mute", icon:"st.custom.sonos.unmuted", backgroundColor:"#ffffff", nextState:"muted"
		state "muted", label:"", action:"music Player.unmute", icon:"st.custom.sonos.muted", backgroundColor:"#ffffff", nextState:"unmuted"
	}

	// Row 3
	controlTile("levelSliderControl", "device.level", "slider", height: 1, width: 3, inactiveLabel: false) {
		state "level", action:"tileSetLevel", backgroundColor:"#ffffff"
	}

	// Row 4
	valueTile("currentSong", "device.trackDescription", inactiveLabel: true, height:1, width:3, decoration: "flat") {
		state "default", label:'${currentValue}', backgroundColor:"#ffffff"
	}

	
	// Row 5
	standardTile("refresh", "device.status", inactiveLabel: false, decoration: "flat") {
		state "default", label:"", action:"refresh.refresh", icon:"st.secondary.refresh", backgroundColor:"#ffffff"
	}
	standardTile("playStation", "device.status", width: 1, height: 1, decoration: "flat") {
		state "default", label:'', action:"playStation", icon:"st.sonos.play-btn", backgroundColor:"#ffffff"
	}
	standardTile("unsubscribe", "device.status", width: 1, height: 1, decoration: "flat") {
		state "previous", label:'Unsubscribe', action:"unsubscribe", backgroundColor:"#ffffff"
	}
	


	main "main"

	details([
		"previousTrack","play","nextTrack",
		"status","stop","mute",
		"levelSliderControl",
		"currentSong",
		"refresh","playStation"
		
		
		//,"unsubscribe"
	])
}

// parse events into attributes
def parse(description) {
	def results = []
	try {
		def msg = parseLanMessage(description)
		longTrace( msg.encodeAsHTML())
		if (msg.headers)
		{
			def hdr = msg.header.split('\n')[0]
			if (hdr.size() > 36) {
				hdr = hdr[0..35] + "..."
			}

			def uuid = ""
			def sid = ""
			if (msg.headers["SID"])
			{
				sid = msg.headers["SID"]
				sid -= "uuid:"
				sid = sid.trim()

				def pos = sid.lastIndexOf("_")
				if (pos > 0) {
					uuid = sid[0..pos-1]
				}
			}

			if (!msg.body) {
				if (sid) {
					updateSid(sid)
				}
			}
			else if (msg.xml) {

				// Process response to getVolume()
				def node = msg.xml.Body.GetVolumeResponse
				if (node.size()) {
					sendEvent(name: "level",value: node.CurrentVolume.text())
				}

				// Process response to getCurrentStatus()
				node = msg.xml.Body.GetTransportInfoResponse
				if (node.size()) {
					def currentStatus = statusText(node.CurrentTransportState.text())
					if (currentStatus) {
						if (currentStatus != "TRANSITIONING") {
							def coordinator = device.getDataValue('coordinator')
							updateDataValue('currentStatus', currentStatus)
							if (coordinator) {
								sendEvent(name: "status", value: "grouped", data: [source: 'xml.Body.GetTransportInfoResponse'])
								sendEvent(name: "switch", value: "off", displayed: false)
							}
							else {
								sendEvent(name: "status", value: currentStatus, data: [source: 'xml.Body.GetTransportInfoResponse'])
								sendEvent(name: "switch", value: currentStatus=="playing" ? "on" : "off", displayed: false)
							}
						}
					}
				}

				// Process group change
				node = msg.xml.property.ZoneGroupState
				if (node.size()) {
					// Important to use parser rather than slurper for this version of Groovy
					def xml1 = new XmlParser().parseText(node.text())
					def myNode =  xml1.ZoneGroup.ZoneGroupMember.find {it.'@UUID' == uuid}
					def myCoordinator = myNode.parent().'@Coordinator'

					if (myCoordinator != myNode.'@UUID') {
						// this player is grouped, find the coordinator

						def coordinator = xml1.ZoneGroup.ZoneGroupMember.find {it.'@UUID' == myCoordinator}
						def coordinatorDni = dniFromUri(coordinator.'@Location')

						updateDataValue("coordinator", coordinatorDni)
						updateDataValue("isGroupCoordinator", "")

						def coordinatorDevice = parent.getChildDevice(coordinatorDni)
						sendEvent(name: "trackDescription", value: "[Grouped with ${coordinatorDevice.displayName}]")
						sendEvent(name: "status", value: "grouped", data: [
							coordinator: [displayName: coordinatorDevice.displayName, id: coordinatorDevice.id, deviceNetworkId: coordinatorDevice.deviceNetworkId]
						])
						sendEvent(name: "switch", value: "off", displayed: false)
					}
					else {
						// Not grouped
						updateDataValue("coordinator", "")
						updateDataValue("isGroupCoordinator", myNode.parent().ZoneGroupMember.size() > 1 ? "true" : "")
					}
					// Return a command to read the current status again to take care of status and group events arriving at
					// about the same time, but in the reverse order
					results << getCurrentStatus()
				}

				// Process subscription update
				node = msg.xml.property.LastChange
				if (node.text().size()>0) {
					
					def xml1 = parseXml(node.text())

					// Play/pause status
					def currentStatus = statusText(xml1.InstanceID.TransportState.'@val'.text())
					if (currentStatus) {
						if (currentStatus != "TRANSITIONING") {
							def coordinator = device.getDataValue('coordinator')
							updateDataValue('currentStatus', currentStatus)

							if (coordinator) {
								sendEvent(name: "status", value: "grouped", data: [source: 'xml.property.LastChange.InstanceID.TransportState'])
								sendEvent(name: "switch", value: "off", displayed: false)
							}
							else {
								sendEvent(name: "status", value: currentStatus, data: [source: 'xml.property.LastChange.InstanceID.TransportState'])
								sendEvent(name: "switch", value: currentStatus=="playing" ? "on" : "off", displayed: false)
							}
						}
					}

					// Volume level
					def currentLevel = xml1.InstanceID.Volume.find{it.'@channel' == 'Master'}.'@val'.text()
					if (currentLevel) {
						sendEvent(name: "level", value: currentLevel, description: description)
					}

					// Mute status
					def currentMute = xml1.InstanceID.Mute.find{it.'@channel' == 'Master'}.'@val'.text()
					if (currentMute) {
						def value = currentMute == "1" ? "muted" : "unmuted"
						sendEvent(name: "mute", value: value, descriptionText: "$device.displayName is $value")
					}

					// Track data
					def trackUri = xml1.InstanceID.CurrentTrackURI.'@val'.text()
					def transportUri = xml1.InstanceID.AVTransportURI.'@val'.text()
					def enqueuedUri = xml1.InstanceID.EnqueuedTransportURI.'@val'.text() 
					def trackNumber = xml1.InstanceID.CurrentTrack.'@val'.text()
										
		
					
					
					
					if (trackUri.contains("//s3.amazonaws.com/smartapp-") || transportUri.contains("//s3.amazonaws.com/smartapp-") ) {
						//log.trace "Skipping event generation for sound file $trackUri"
					}
					else {
                    	
						if (transportUri) {
							state.transportUri = transportUri
						}else{
							transportUri = state.transportUri
						}

						def trackMeta = xml1.InstanceID.CurrentTrackMetaData.'@val'.text()
						def transportMeta = xml1.InstanceID.AVTransportURIMetaData.'@val'.text()
						def enqueuedMeta = xml1.InstanceID.EnqueuedTransportURIMetaData.'@val'.text()

					
						if (trackMeta || transportMeta) {
							def isRadioStation = enqueuedUri.startsWith("x-sonosapi-stream:")

							def metaData = enqueuedMeta ? enqueuedMeta :  transportMeta
							def stationMetaXml = metaData ? parseXml(metaData) : null

							def trackXml = (trackMeta && !isRadioStation) || !stationMetaXml ? parseXml(trackMeta) : stationMetaXml

							def currentName = trackXml.item.title.text()
							def currentArtist = trackXml.item.creator.text()
							def currentAlbum  = trackXml.item.album.text()
							def currentTrackDescription = currentName
							def descriptionText = "$device.displayName is playing $currentTrackDescription"
							if (currentArtist) {
								currentTrackDescription += " - $currentArtist"
								descriptionText += " by $currentArtist"
							}

							sendEvent(name: "trackDescription",
								value: currentTrackDescription,
								descriptionText: descriptionText
							)

							if (stationMetaXml) {
								def station = (transportUri?.startsWith("x-rincon-queue:") || enqueuedUri?.contains("savedqueues")) ? currentName : stationMetaXml.item.title.text()

								def uri = transportUri
								def previousState = device.currentState("trackData")?.jsonValue
								def isDataStateChange = !previousState || (previousState.station != station || previousState.metaData != metaData)

								if (transportUri?.startsWith("x-rincon-queue:")) {
									updateDataValue("queueUri", transportUri)
								}

								def trackDataValue = [
									station: station,
									name: currentName,
									artist: currentArtist,
									album: currentAlbum,
									trackNumber: trackNumber,
									status: currentStatus,
									level: currentLevel,
									uri: uri,
									trackUri: trackUri,
									transportUri: transportUri,
									enqueuedUri: enqueuedUri,
									metaData: metaData,
								]

								if (trackMeta != metaData) {
									trackDataValue.trackMetaData = trackMeta
								}
								results << createEvent(name: "trackData",
									value: trackDataValue.encodeAsJSON(),
									descriptionText: currentDescription,
									displayed: false,
									isStateChange: isDataStateChange
								)
							}
						}
					}
				}
				if (!results) {
					def bodyHtml = msg.body ? msg.body.replaceAll('(<[a-z,A-Z,0-9,\\-,_,:]+>)','\n$1\n')
						.replaceAll('(</[a-z,A-Z,0-9,\\-,_,:]+>)','\n$1\n')
						.replaceAll('\n\n','\n').encodeAsHTML() : ""
					results << createEvent(
						name: "mediaRendererMessage",
						value: "${msg.body.encodeAsMD5()}",
						description: description,
						descriptionText: "Body is ${msg.body?.size() ?: 0} bytes",
						data: "<pre>${msg.headers.collect{it.key + ': ' + it.value}.join('\n')}</pre><br/><pre>${bodyHtml}</pre>",
						isStateChange: false, displayed: false)
				}
			}
			else {
				def bodyHtml = msg.body ? msg.body.replaceAll('(<[a-z,A-Z,0-9,\\-,_,:]+>)','\n$1\n')
					.replaceAll('(</[a-z,A-Z,0-9,\\-,_,:]+>)','\n$1\n')
					.replaceAll('\n\n','\n').encodeAsHTML() : ""
				results << createEvent(
					name: "unknownMessage",
					value: "${msg.body.encodeAsMD5()}",
					description: description,
					descriptionText: "Body is ${msg.body?.size() ?: 0} bytes",
					data: "<pre>${msg.headers.collect{it.key + ': ' + it.value}.join('\n')}</pre><br/><pre>${bodyHtml}</pre>",
					isStateChange: true, displayed: true)
			}
		}
	}
	catch (Throwable t) {
		//results << createEvent(name: "parseError", value: "$t")
		sendEvent(name: "parseError", value: "$t", description: description)
		throw t
	}
	results
}

def installed() {
	def result = [delayAction(5000)]
	result << refresh()
	result.flatten()
}

def on(){
	play()
}

def off(){
	stop()
}

def setModel(String model)
{
	sendEvent(name:"model",value:model,isStateChange:true)
}

def setAVTCURL(String avtcurl)
{
	sendEvent(name:"avtcurl",value:avtcurl,isStateChange:true)
}
def setAVTEURL(String avteurl)
{
	sendEvent(name:"avteurl",value:avteurl,isStateChange:true)
}
def setRCCURL(String rccurl)
{
	sendEvent(name:"rccurl",value:rccurl,isStateChange:true)
}
def setRCEURL(String rceurl)
{
	sendEvent(name:"rceurl",value:rceurl,isStateChange:true)
}

def poll() {
	refresh()
}

def refresh() {
	log.trace "refresh()"
	def result = subscribe()
	result << getCurrentStatus()
	result << getVolume()
	result.flatten()
}

// For use by apps, sets all levels if sent to a non-coordinator in a group
def setLevel(val)
{
	coordinate({
		setOtherLevels(val)
		setLocalLevel(val)
	}, {
		it.setLevel(val)
	})
}

// For use by tiles, sets all levels if a coordinator, otherwise sets only the local one
def tileSetLevel(val)
{
	coordinate({
		setOtherLevels(val)
		setLocalLevel(val)
	}, {
		setLocalLevel(val)
	})
}

def playStation()
{   
	if(!(settings.confStationURI?.trim())){
		settings.confStationURI = "http://2223.live.streamtheworld.com:80/CLASSIC106_SC"
	}
	def result = []
	result << mediaRendererAction("Stop")
	result << setTrack(settings.confStationURI)
	result << mediaRendererAction("Play")
	result = result.flatten()
	result
}

// Always sets only this level
def setLocalLevel(val, delay=0) {
	def v = Math.max(Math.min(Math.round(val), 100), 0)

	def result = []
	if (delay) {
		result << delayAction(delay)
	}
	result << mediaRendererAction("SetVolume", "RenderingControl", device.currentValue("rccurl") , [InstanceID: 0, Channel: "Master", DesiredVolume: v])
	//result << delayAction(500),
	result << mediaRendererAction("GetVolume", "RenderingControl", device.currentValue("rccurl"), [InstanceID: 0, Channel: "Master"])
	result
}

private setOtherLevels(val, delay=0) {
	if (device.getDataValue('isGroupCoordinator')) {
		def previousMaster = device.currentState("level")?.integerValue
		parent.getChildDevices().each {child ->
			if (child.getDeviceDataByName("coordinator") == device.deviceNetworkId) {
				def newLevel = childLevel(previousMaster, val, child.currentState("level")?.integerValue)
				child.setLocalLevel(newLevel, delay)
			}
		}
	}
}

private childLevel(previousMaster, newMaster, previousChild)
{
	if (previousMaster) {
		if (previousChild) {
			Math.round(previousChild * (newMaster / previousMaster))
		}
		else {
			newMaster
		}
	}
	else {
		newMaster
	}
}

def getGroupStatus() {
	def result = coordinate({device.currentValue("status")}, {it.currentValue("status")})
	result
}

def play() {
	coordinate({mediaRendererAction("Play")}, {it.play()})
	
}

def stop() {
	coordinate({mediaRendererAction("Stop")}, {it.stop()})
}

def pause() {
	coordinate({mediaRendererAction("Pause")}, {it.pause()})
}

def nextTrack() {
	coordinate({mediaRendererAction("Next")}, {it.nextTrack()})
}

def previousTrack() {
	coordinate({mediaRendererAction("Previous")}, {it.previousTrack()})
}

def seek(trackNumber) {
	coordinate({mediaRendererAction("Seek", "AVTransport", device.currentValue("avtcurl") , [InstanceID: 0, Unit: "TRACK_NR", Target: trackNumber])}, {it.seek(trackNumber)})
}

def mute()
{
	// TODO - handle like volume?
	coordinate({mediaRendererAction("SetMute", "RenderingControl", device.currentValue("rccurl"), [InstanceID: 0, Channel: "Master", DesiredMute: 1])}, {it.mute()})
}

def unmute()
{
	// TODO - handle like volume?
	coordinate({mediaRendererAction("SetMute", "RenderingControl", device.currentValue("rccurl"), [InstanceID: 0, Channel: "Master", DesiredMute: 0])}, {it.unmute()})
}

def setPlayMode(mode)
{
	coordinate({mediaRendererAction("SetPlayMode", [InstanceID: 0, NewPlayMode: mode])}, {it.setPlayMode(mode)})
}


def playTextAndResume(text, volume=null)
{
	coordinate({
		def sound = textToSpeech(text)
		playTrackAndResume(sound.uri, (sound.duration as Integer) + 1, volume)
	}, {it.playTextAndResume(text, volume)})
}

def playTrackAndResume(uri, duration, volume=null) {
	coordinate({
		def currentTrack = device.currentState("trackData")?.jsonValue
		def currentVolume = device.currentState("level")?.integerValue
		def currentStatus = device.currentValue("status")
		def level = volume as Integer
		def result = []
		result << mediaRendererAction("Stop")
		if (level) {
			result << mediaRendererAction("Stop")
			result << setLocalLevel(level)
		}
		result << setTrack(uri)
		result << mediaRendererAction("Play")

		if (currentTrack) {
			def delayTime = ((duration as Integer) * 1000)+3000
			if (level) {
				delayTime += 1000
			}
			result << delayAction(delayTime)
			if (level) {
				result << mediaRendererAction("Stop")
				result << setLocalLevel(currentVolume)
			}
			result << setTrack(currentTrack)
			if (currentStatus == "playing") {
				result << mediaRendererAction("Play")
			}
		}

		result = result.flatten()
		result
	}, {it.playTrackAndResume(uri, duration, volume)})
}

def playTextAndRestore(text, volume=null)
{
	coordinate({
		def sound = textToSpeech(text)
		playTrackAndRestore(sound.uri, (sound.duration as Integer) + 1, volume)
	}, {it.playTextAndRestore(text, volume)})
}

def playTrackAndRestore(uri, duration, volume=null) {
	coordinate({
		def currentTrack = device.currentState("trackData")?.jsonValue
		currentTrack = device.currentState("trackData")?.jsonValue
		currentTrack = device.currentState("trackData")?.jsonValue
		def currentVolume = device.currentState("level")?.integerValue
		def currentStatus = device.currentValue("status")
		def level = volume as Integer

		def result = []
		if (level) {
			result << mediaRendererAction("Stop")
			result << setLocalLevel(level)
		}

		result << setTrack(uri)
		result << mediaRendererAction("Play")

		if (currentTrack) {
			def delayTime = ((duration as Integer) * 1000)+3000
			if (level) {
				delayTime += 1000
			}
			result << delayAction(delayTime)
			if (level) {
				result << mediaRendererAction("Stop")
				result << setLocalLevel(currentVolume)
			}
			result << setTrack(currentTrack)
		}

		result = result.flatten()
		result
	}, {it.playTrackAndResume(uri, duration, volume)})
}

def playTextAndTrack(text, trackData, volume=null)
{
	coordinate({
		def sound = textToSpeech(text)
		playSoundAndTrack(sound.uri, (sound.duration as Integer) + 1, trackData, volume)
	}, {it.playTextAndResume(text, volume)})
}

def playSoundAndTrack(soundUri, duration, trackData, volume=null) {
	coordinate({
		def level = volume as Integer
		def result = []
		if (level) {
			result << mediaRendererAction("Stop")
			result << setLocalLevel(level)
		}

		result << setTrack(soundUri)
		result << mediaRendererAction("Play")

		def delayTime = ((duration as Integer) * 1000)+3000
		result << delayAction(delayTime)

		result << setTrack(trackData)
		result << mediaRendererAction("Play")

		result = result.flatten()
		result
	}, {it.playTrackAndResume(uri, duration, volume)})
}

def playTrackAtVolume(String uri, volume) {
	coordinate({
		def result = []
		result << mediaRendererAction("Stop")
		result << setLocalLevel(volume as Integer)
		result << setTrack(uri, metaData)
		result << mediaRendererAction("Play")
		result.flatten()
	}, {it.playTrack(uri, metaData)})
}

def playTrack(String uri, metaData="") {
	coordinate({
		def result = setTrack(uri, metaData)
		result << mediaRendererAction("Play")
		result.flatten()
	}, {it.playTrack(uri, metaData)})
}

def playTrack(Map trackData) {
	coordinate({
		def result = setTrack(trackData)
		//result << delayAction(1000)
		result << mediaRendererAction("Play")
		result.flatten()
	}, {it.playTrack(trackData)})
}

def setTrack(Map trackData) {
	coordinate({
		def data = trackData
		def result = []
		if ((data.transportUri.startsWith("x-rincon-queue:") || data.enqueuedUri.contains("savedqueues")) && data.trackNumber != null) {
		// TODO - Clear queue?
			def uri = device.getDataValue('queueUri')
			result << mediaRendererAction("RemoveAllTracksFromQueue", [InstanceID: 0])
			//result << delayAction(500)
			result << mediaRendererAction("AddURIToQueue", [InstanceID: 0, EnqueuedURI: data.uri, EnqueuedURIMetaData: data.metaData, DesiredFirstTrackNumberEnqueued: 0, EnqueueAsNext: 1])
			//result << delayAction(500)
			result << mediaRendererAction("SetAVTransportURI", [InstanceID: 0, CurrentURI: uri, CurrentURIMetaData: metaData])
			//result << delayAction(500)
			result << mediaRendererAction("Seek", "AVTransport", device.currentValue("avtcurl"), [InstanceID: 0, Unit: "TRACK_NR", Target: data.trackNumber])
		} else {
			result = setTrack(data.uri, data.metaData)
		}
		result.flatten()
	}, {it.setTrack(trackData)})
}

def setTrack(String uri, metaData="")
{
	def fileName = "" 
	if (metaData?.size() == 0){
		fileName= getExtensionFromFilename(uri)
		metaData="<DIDL-Lite xmlns=\"urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:upnp=\"urn:schemas-upnp-org:metadata-1-0/upnp/\" xmlns:dlna=\"urn:schemas-dlna-org:metadata-1-0/\"><item id=\"94e5941a264e3c138c33\" parentID=\"8ff689139b04ce1fecb9\" restricted=\"1\"><upnp:class>object.item.audioItem.musicTrack</upnp:class><upnp:album>SmartThings Catalog</upnp:album><upnp:artist>SmartThings</upnp:artist><upnp:albumArtURI>https://graph.api.smartthings.com/api/devices/icons/st.Entertainment.entertainment2-icn?displaySize=2x</upnp:albumArtURI><dc:title>$fileName</dc:title><res duration=\"0:10:00.000\" protocolInfo=\"http-get:*:audio/mpeg:DLNA.ORG_PN=MP3;DLNA.ORG_OP=01;DLNA.ORG_CI=0;DLNA.ORG_FLAGS=01500000000000000000000000000000\" >$uri</res></item> </DIDL-Lite>"
	}
	coordinate({
		def result = []
		result << mediaRendererAction("SetAVTransportURI", [InstanceID: 0, CurrentURI: uri, CurrentURIMetaData: metaData])
		result
	}, {it.setTrack(uri, metaData)})
}

def resumeTrack(Map trackData = null) {
	coordinate({
		def result = restoreTrack(trackData)
		//result << delayAction(500)
		result << mediaRendererAction("Play")
		result
	}, {it.resumeTrack(trackData)})
}

def restoreTrack(Map trackData = null) {
	coordinate({
		def result = []
		def data = trackData
		if (!data) {
			data = device.currentState("trackData")?.jsonValue
		}
		if (data) {
			if ((data.transportUri.startsWith("x-rincon-queue:") || data.enqueuedUri.contains("savedqueues")) && data.trackNumber != null) {
				def uri = device.getDataValue('queueUri')
				result << mediaRendererAction("SetAVTransportURI", [InstanceID: 0, CurrentURI: uri, CurrentURIMetaData: data.metaData])
				//result << delayAction(500)
				result << mediaRendererAction("Seek", "AVTransport",  device.currentValue("avtcurl"), [InstanceID: 0, Unit: "TRACK_NR", Target: data.trackNumber])
			} else {
				result << mediaRendererAction("SetAVTransportURI", [InstanceID: 0, CurrentURI: data.uri, CurrentURIMetaData: data.metaData])
			}
		}
		else {
			log.warn "Previous track data not found"
		}
		result
	}, {it.restoreTrack(trackData)})
}

def playText(String msg) {
	coordinate({
		def result = setText(msg)
		result << mediaRendererAction("Play")
	}, {it.playText(msg)})
}

def setText(String msg) {
	coordinate({
		def sound = textToSpeech(msg)
		setTrack(sound.uri)
	}, {it.setText(msg)})
}

// Custom commands

def subscribe() {
	def result = []
	result << subscribeAction(device.currentValue("avteurl"))
	result << delayAction(10000)
	result << subscribeAction(device.currentValue("rceurl"))
	//result << delayAction(20000)
	//result << subscribeAction("/ZoneGroupTopology/Event")

	result
}
def unsubscribe() {
	def result = [
		unsubscribeAction(device.currentValue("avteurl"), device.getDataValue('subscriptionId')),
		unsubscribeAction(device.currentValue("rceurl"), device.getDataValue('subscriptionId')),

		
		unsubscribeAction(device.currentValue("avteurl"), device.getDataValue('subscriptionId1')),
		unsubscribeAction(device.currentValue("rceurl"), device.getDataValue('subscriptionId1')),

		
		unsubscribeAction(device.currentValue("avteurl"), device.getDataValue('subscriptionId2')),
		unsubscribeAction(device.currentValue("rceurl"), device.getDataValue('subscriptionId2'))
	]
	updateDataValue("subscriptionId", "")
	updateDataValue("subscriptionId1", "")
	updateDataValue("subscriptionId2", "")
	result
}

def getVolume()
{
	mediaRendererAction("GetVolume", "RenderingControl", device.currentValue("rccurl"), [InstanceID: 0, Channel: "Master"])
}

def getCurrentMedia()
{
	mediaRendererAction("GetPositionInfo", [InstanceID:0, Channel: "Master"])
}

def getCurrentStatus() //transport info
{
	mediaRendererAction("GetTransportInfo", [InstanceID:0])
}

def getSystemString()
{
	mediaRendererAction("GetString", "SystemProperties", "/SystemProperties/Control", [VariableName: "UMTracking"])
}

private messageFilename(String msg) {
	msg.toLowerCase().replaceAll(/[^a-zA-Z0-9]+/,'_')
}

private getCallBackAddress()
{
	device.hub.getDataValue("localIP") + ":" + device.hub.getDataValue("localSrvPortTCP")
}

private mediaRendererAction(String action) {
	if(action=="Play"){
		mediaRendererAction(action, "AVTransport", device.currentValue("avtcurl"), [InstanceID:0, Speed:1])
    }else{
		mediaRendererAction(action, "AVTransport", device.currentValue("avtcurl"), [InstanceID:0])
    }
}

private mediaRendererAction(String action, Map body) {
	mediaRendererAction(action, "AVTransport", device.currentValue("avtcurl"), body)
}

private mediaRendererAction(String action, String service, String path, Map body = [InstanceID:0, Speed:1]) {
	def result = new physicalgraph.device.HubSoapAction(
		path:    path ?: "/MediaRenderer/$service/Control",
		urn:     "urn:schemas-upnp-org:service:$service:1",
		action:  action,
		body:    body,
		headers: [Host:getHostAddress(), CONNECTION: "close"]
	)
	result
}

private subscribeAction(path, callbackPath="") {
	def address = getCallBackAddress()
	def ip = getHostAddress()

	def result = new physicalgraph.device.HubAction(
		method: "SUBSCRIBE",
		path: path,
		headers: [
			HOST: ip,
			CALLBACK: "<http://${address}/notify$callbackPath>",
			NT: "upnp:event",
			TIMEOUT: "Second-28800"])
	result
}

private unsubscribeAction(path, sid) {
	def ip = getHostAddress()
	def result = new physicalgraph.device.HubAction(
		method: "UNSUBSCRIBE",
		path: path,
		headers: [
			HOST: ip,
			SID: "uuid:${sid}"])
	result
}

private delayAction(long time) {
	new physicalgraph.device.HubAction("delay $time")
}

private Integer convertHexToInt(hex) {
	Integer.parseInt(hex,16)
}

private String convertHexToIP(hex) {
	[convertHexToInt(hex[0..1]),convertHexToInt(hex[2..3]),convertHexToInt(hex[4..5]),convertHexToInt(hex[6..7])].join(".")
}

private getHostAddress() {
	def parts = device.deviceNetworkId.split(":")
	def ip = convertHexToIP(parts[0])
	def port = convertHexToInt(parts[1])
	return ip + ":" + port
}

private statusText(s) {
	switch(s) {
		case "PLAYING":
			return "playing"
		case "PAUSED_PLAYBACK":
			return "paused"
		case "STOPPED":
			return "stopped"
		default:
			return s
	}
}

private updateSid(sid) {
	if (sid) {
		def sid0 = device.getDataValue('subscriptionId')
		def sid1 = device.getDataValue('subscriptionId1')
		def sid2 = device.getDataValue('subscriptionId2')
		def sidNumber = device.getDataValue('sidNumber') ?: "0"

		if (sidNumber == "0") {
			if (sid != sid1 && sid != sid2) {
				updateDataValue("subscriptionId", sid)
				updateDataValue("sidNumber", "1")
			}
		}
		else if (sidNumber == "1") {
			if (sid != sid0 && sid != sid2) {
				updateDataValue("subscriptionId1", sid)
				updateDataValue("sidNumber", "2")
			}
		}
		else {
			if (sid != sid0 && sid != sid0) {
				updateDataValue("subscriptionId2", sid)
				updateDataValue("sidNumber", "0")
			}
		}
	}
}

private dniFromUri(uri) {
	def segs = uri.replaceAll(/http:\/\/([0-9]+\.[0-9]+\.[0-9]+\.[0-9]+:[0-9]+)\/.+/,'$1').split(":")
	def nums = segs[0].split("\\.")
	(nums.collect{hex(it.toInteger())}.join('') + ':' + hex(segs[-1].toInteger(),4)).toUpperCase()
}

private hex(value, width=2) {
	def s = new BigInteger(Math.round(value).toString()).toString(16)
	while (s.size() < width) {
		s = "0" + s
	}
	s
}

private coordinate(closure1, closure2) {
	def coordinator = device.getDataValue('coordinator')
	if (coordinator) {
		closure2(parent.getChildDevice(coordinator))
	}
	else {
		closure1()
	}
}
private getExtensionFromFilename(fileName) {
	
  def returned_value = fileName.substring(fileName.lastIndexOf("/") + 1).substring(fileName.lastIndexOf("\\") + 1);
  return returned_value
}
private longTrace(text){
	for (int start = 0; start < text.length(); start += 1000) {
        log.trace "${text.substring(start, Math.min(text.length(), start + 1000))}"
    }
}
