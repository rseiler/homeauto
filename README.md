# Home Automation

This project contains my home automation projects.


## MiLight

Scans if a specific device is turned on and turns on the MiLight if it's after the sunset or schedules to turn on the
MiLight after the sunset.

Use case: I come home, my phone logs into the WiFi, and the light is turned on if it's dark.


## Wireless Socket

Scans if a specific device is turned on/off and turns on/off the wireless socket.

Use case: if the computer is off then the wireless socket on which the monitors, speakers, ... is turned off. If the
computer is turned on then the wireless socket is turned on.


## Mono

Packs the MiLight and Wireless-Socket projects into one application. This is done to save memory (since it runs on a 
Raspberry) and so that not every program does it's own device scan.


## MiLight Web

Connects to a MiLight-Wifi-Box and provides a website to control the MiLight.

Use case: just an easy way to control the MiLight with the computer and the phone.
