package websocket;
import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.io.*;

public class WebSocketServerThread extends Thread {
    private Socket socket = null;

    public WebSocketServerThread(Socket socket) {
        super("WebSocketServerThread");
        this.socket = socket;
    }
    
    public void run() {
        try (
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ) {
            WebSocketProtocol protocol = new WebSocketProtocol();
            
            boolean isHandshakeCompleted = false;
            String request, response;
            StringBuilder requestBuilder = new StringBuilder();
            LinkedHashMap<String, String> requestMap = new LinkedHashMap<>();
            
            while(!isHandshakeCompleted) {    
                request = in.readLine();
                if(request != null && !request.equals("")) {
                    String[] requestTokens = request.split(": ");
                    if(requestTokens.length == 2) {
                        requestMap.put(requestTokens[0], requestTokens[1]);
                    }
                    requestBuilder.append(request + "\n");
                } else if(requestBuilder.length() > 0) {
                    System.out.println("Request\n" + requestBuilder.toString());
                    response = protocol.buildHandshakeResponse(requestMap);
                    System.out.println("Response\n" + response);
                    out.println(response);
                    System.out.println("-------------------- HANDSHAKE COMPLETED --------------------");
                    System.out.println();
                    
                    request = null;
                    requestBuilder = new StringBuilder();
                    isHandshakeCompleted = true;
                }
            }
            
            boolean isConnectionClosed = false;
            int len = 0;
            int buffLength = 32;
            byte[] b;            
            while(!isConnectionClosed) {
                b = new byte[buffLength];
                len = socket.getInputStream().read(b);
                if(len != -1){
                    System.out.println("Recieved Frames:" + Arrays.toString(b));
                    byte[] message;
                    if(!protocol.isCloseFrame(b[0])) {
                        message = protocol.read(b, len);
                    } else {
                        System.out.println("xxxxxxxxxxxxxxxxxxxx CLOSE CONNECTION xxxxxxxxxxxxxxxxxxxx");
                        System.out.println();
                        socket.close();
                        isConnectionClosed = true;
                        break;
                    }
                    protocol.send(message, socket);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}