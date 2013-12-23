package websocket;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.LinkedHashMap;

public class WebSocketProtocol {

    public String buildHandshakeResponse(LinkedHashMap<String, String> requestMap) throws NoSuchAlgorithmException {
        StringBuilder output = new StringBuilder();
        
        String magicString = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
        MessageDigest sha = MessageDigest.getInstance("SHA1");
        String secWsAccept = Base64.encodeBytes(sha.digest((requestMap.get("Sec-WebSocket-Key") + magicString).getBytes()));
        
        output.append("HTTP/1.1 101 Switching Protocols\r\n");
        output.append("Server: " + "HelloSocket" + "\r\n");
        output.append("Connection: " + "Upgrade" + "\r\n");
        output.append("Upgrade: " + "WebSocket" + "\r\n");
        output.append("Sec-WebSocket-Accept: " + secWsAccept + "\r\n");

        return output.toString();
    }
    
        
    //Testing the accept header hashing
    public static void main(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String secWsKey = "HKxLTLtIb4+Kt2SFg2flvQ==";
        String magicString = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
        
        MessageDigest sha = MessageDigest.getInstance("SHA1");
        String secWsAccept = Base64.encodeBytes(sha.digest((secWsKey + magicString).getBytes()));
        
        System.out.println(secWsAccept);
    }


    public boolean isCloseFrame(byte b0) {
        // Text frame  1000 0001 = 129 -> 129 - 256 => -127
        // Close frame 1000 1000 = 136 -> 136 - 256 => -120
        // System.out.println("First Byte:" + Arrays.toString(b[0]));
        byte closeByte = (byte) 136;
        return b0 == closeByte;
    }


    /**
     * source: http://stackoverflow.com/questions/8125507/how-can-i-send-and-receive-websocket-messages-on-the-server-side
     */
    public byte[] read(byte[] b, int len) {
        byte rLength = 0;
        int rMaskIndex = 2;
        int rDataStart = 0;
        //not inspecting b[0] again, as assuming it to be a text frame
        byte data = b[1];
        byte op = (byte) 127;
        rLength = (byte) (data & op);

        if(rLength==(byte)126) rMaskIndex=4;
        if(rLength==(byte)127) rMaskIndex=10;

        byte[] masks = new byte[4];

        int j=0;
        int i=0;
        for(i=rMaskIndex;i<(rMaskIndex+4);i++){
            masks[j] = b[i];
            j++;
        }
        rDataStart = rMaskIndex + 4;
        int messLen = len - rDataStart;
        byte[] message = new byte[messLen];
        for(i=rDataStart, j=0; i<len; i++, j++){
            message[j] = (byte) (b[i] ^ masks[j % 4]);
        }
        System.out.println("Recieved Message:" + Arrays.toString(message));
        System.out.println("Recieved Message:" + new String(message));
        return message;
    }
    
    /**
     * source: http://stackoverflow.com/questions/8125507/how-can-i-send-and-receive-websocket-messages-on-the-server-side
     */
    public void send(byte[] mess, Socket clientSocket) throws IOException{
        byte[] rawData = mess;//.getBytes();

        int frameCount  = 0;
        byte[] frame = new byte[10];

        frame[0] = (byte) 129;

        if(rawData.length <= 125){
            frame[1] = (byte) rawData.length;
            frameCount = 2;
        }else if(rawData.length >= 126 && rawData.length <= 65535){
            frame[1] = (byte) 126;
            byte len = (byte) rawData.length;
            frame[2] = (byte)((len >> 8 ) & (byte)255);
            frame[3] = (byte)(len & (byte)255); 
            frameCount = 4;
        }else{
            frame[1] = (byte) 127;
            byte len = (byte) rawData.length;
            frame[2] = (byte)((len >> 56 ) & (byte)255);
            frame[3] = (byte)((len >> 48 ) & (byte)255);
            frame[4] = (byte)((len >> 40 ) & (byte)255);
            frame[5] = (byte)((len >> 32 ) & (byte)255);
            frame[6] = (byte)((len >> 24 ) & (byte)255);
            frame[7] = (byte)((len >> 16 ) & (byte)255);
            frame[8] = (byte)((len >> 8 ) & (byte)255);
            frame[9] = (byte)(len & (byte)255);
            frameCount = 10;
        }

        int bLength = frameCount + rawData.length;

        byte[] reply = new byte[bLength];

        int bLim = 0;
        for(int i=0; i<frameCount;i++){
            reply[bLim] = frame[i];
            bLim++;
        }
        for(int i=0; i<rawData.length;i++){
            reply[bLim] = rawData[i];
            bLim++;
        }
//        byte[] reply = new byte[4];
//        reply[0] = (byte) 129;
//        reply[1] = 2;
//        reply[2] = 97;
//        reply[3] = 98;
        System.out.println("Sending Frames:" + new String(reply));
        System.out.println("Sending Frames:" + Arrays.toString(reply));
        clientSocket.getOutputStream().write(reply);
        clientSocket.getOutputStream().flush();
    }

}
