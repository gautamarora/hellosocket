package websocket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class WebSocketClient {

    public static void main(String[] args) {
        try( 
                Socket socket = new Socket("0.0.0.0", 7000);
                PrintWriter socketOut = new PrintWriter(socket.getOutputStream(), true); //autoFlush = true
                BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                ) {
            String stdInput;
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            while((stdInput = stdIn.readLine()) != null) {
                socketOut.println(stdInput);
                System.out.println("echo:" + socketIn.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
