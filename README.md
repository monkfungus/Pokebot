# Pokebot
Pokebot sends and receives 'pokes' through Cisco Spark.

Currently, this is a desktop client being built by me as an opportunity to figure out how to interact with Spark using HTTP GET and POST requests, and to put into practice stuff I normally only get to use on assignments. Also it's a slick idea

Eventually, this whole project will run on two microcontrollers, one in Ireland and one in Murca that will be able to 'poke' one another. The idea is each microcontroller has wifi capabilities that will allow it to control a bot in a Spark Room. This bot will be able to send messages and read messages it has been mentioned in, so the two bots will be able to communicate with one another through Spark, which I will be able to observe with ease.

The physical Pokebots will have two caricature style figures that will display the state of the bot - if it's poking or being poked. The microcontrollers will thus have to spin a motor/servo to communicate their state whenever it changes - at the moment there's a certain unused dvd drive hooked up to a NodeMCU being used but it's not exactly aesthetically pleasing. Also the bots will have to allow the end user to connect to new wifi networks and input passwords/ select which network they would like to connect to. The NodeMCU I have SHOULD support WPA2-Enterprise, but to get it to connect to a network such as eduroam that wants a username/password rather than a cert I think I'll have to write the PEAP stuff myself but sure look, it supports WPA2 Personal just fine so that's the beginning aim.

Ideally also the two bots will be super simple to plug and play, with an LCD screen to display a menu and some input buttons for text/selection features to allow someone to connect it to a new wifi network and test the connection. 

Cisco Spark was picked as the link between the bots as I did a bit of work with Spark at a Cisco Jam event at the start of this year and copped they allow you to have bots, it's also very user friendly and should allow both me and the other person with a Pokebot (shewhoshallnotbenamed) to see what's happening with the bots. 