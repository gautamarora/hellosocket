package websocket;
import java.net.*;
import java.io.*;

/**
 * Simple WebSocket Server in Java
 * @author gautam
 */
public class WebSocketServer {
    public static void main(String[] args) throws IOException {
        int portNumber = 7000; //default port
        
        if(args.length == 1) {
            portNumber = Integer.parseInt(args[0]);
        }
        boolean listening = true;

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            System.out.println("WebSocket server listening on port " + portNumber);
            while (listening) {
                new WebSocketServerThread(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
    }
}