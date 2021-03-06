/**
 *  Turn It Off When Not in Use
 *
 *  Copyright 2014 Sidney Johnson
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
 */
definition(
    name: "Turn It Off When Not in Use",
    namespace: "sidjohn1",
    author: "Sidney Johnson",
    description: "Turns off device when wattage drops below a set level after a set time. Retires every 5min",
    category: "Green Living",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("Select switches to control...") {
	input name: "switches", type: "capability.switch", multiple: false
	}
    section("Turn them off at...") {
	input name: "stopTime", type: "time", multiple: false
	}
    section("When wattage drops below...") {
	input name: "wattageLow", type: "number", multiple: false
	}    
	section("Turn them on at...") {
	input name: "startTime", type: "time", multiple: false
	}

}

def installed() {
	log.debug "Installed with settings: ${settings}"
	schedule(startTime, "startTimerCallback")
	schedule(stopTime, "stopTimerCallback")

}

def updated(settings) {
	unschedule()
	schedule(startTime, "startTimerCallback")
	schedule(stopTime, "stopTimerCallback")
}

def startTimerCallback() {
	log.debug "Turning on switches"
	switches.on()

}

def stopTimerCallback() {
    if (switches.currentPower <= wattageLow) {
    log.debug "Turning off switches. Current Wattage: ${switches.currentPower}"
	switches.off()
	}
    else {
	log.debug "Waiting for next poll cycle. Current Wattage: ${switches.currentPower}"
    def fiveMinuteDelay = 60 * 5
	runIn(fiveMinuteDelay, stopTimerCallback, [overwrite: true])
    }
}
