/**
 *  Turn Off On Leak
 *
 *  Author: jharrell
 */
definition(
    name: "Turn Off On Leak",
    namespace: "smartthings",
    author: "jharrell",
    description: "When water is detected, turn off a switch.",
    category: "Safety & Security",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/water_moisture.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/water_moisture@2x.png"
)

preferences {
	section("When there's water detected...") {
		input "alarm", "capability.waterSensor", title: "Where?"
	}
	section("Turn off...") {
		input "theswitch", "capability.switch", title: "What?"
	}
}

def installed() {
	subscribe(alarm, "water.wet", waterWetHandler)
}

def updated() {
	unsubscribe()
	subscribe(alarm, "water.wet", waterWetHandler)
}

def waterWetHandler(evt) {
	def deltaSeconds = 60

	def timeAgo = new Date(now() - (1000 * deltaSeconds))
	def recentEvents = alarm.eventsSince(timeAgo)
	log.debug "Found ${recentEvents?.size() ?: 0} events in the last $deltaSeconds seconds"

	def alreadyTurnedOff = recentEvents.count { it.value && it.value == "wet" } > 1

	if (alreadyTurnedOff ) {
		log.debug "Already turned off switch within the last $deltaSeconds seconds"
	} else {
                theswitch.off();
		log.debug "$alarm is wet, turning off $theswitch"
	}
}