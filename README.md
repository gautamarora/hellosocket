# Hello Socket

Hello Socket is an experimentation playground for realtime communication and collaboration projects.

## Roadmap

v1 - WebSocket server in Java

v2 - WebRTC server in Java

## WebSocket Server

This is an implementation of the WebSocket protocol using Java Sockets. It supports multiple clients to connect using threads.

### Get the code
* Checkout source: `git clone git@github.com:gautamarora/hellosocket.git`
* Import the project in eclipse or simply `cd hellosocket/` and open in your editor of choice

### Running the websocket server
* If using eclipse, go to WebSocketServer and 'Run as…' Java Application
* If using command line, `cd hellosocket/bin/` and run with `java websocket/WebSocketServer`
* WebSocket Server will start on port 7000, by default


### Running websocket clients
* Install ws: `npm -g ws`
* Start wscat: `wscat -c ws://0.0.0.0:7000` 
* You can re-issue the wscat command to start multiple websocket client connections
* In its current version, the server will simply echo back commands issued by the client