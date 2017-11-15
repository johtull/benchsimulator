package package_my_bench;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * Communicates with the server for high score data.
 * @author Robert Brestle
 */
public class HighScore implements Runnable {
    
    public HighScore(String serv) {
        server = serv;
        active = -1;
    }
    
    private boolean getSock() {
        //connect to server
        try {
            if(server.equals("default")) {
                socket = new Socket("127.0.0.1", 10009);
            }else {
                String IP = server.substring(server.indexOf(server.charAt(0)), server.indexOf(':'));
                int port = Integer.parseInt(server.substring(server.indexOf(':') + 1));
                socket = new Socket(IP, port);
            }
            
            //set up streams
            out = socket.getOutputStream();
            in = socket.getInputStream();
            
        }catch(Exception e) {
            JOptionPane.showConfirmDialog(null, "Could not retrieve high scores.", "Error", JOptionPane.PLAIN_MESSAGE);
            active = 6;
            
            System.out.println("Could not connect to the server.");
            return false;
        }
        
        return true;
    }
    
    //send a string to the server
    private void sendString(String s) {
        //initialize byte[]
        byte[] bytearray;
        //convert string to byte[]
        bytearray = s.getBytes();
        
        try{
            //send byte[] length
            out.write(s.length());
            //send byte[]
            out.write(bytearray, 0, s.length());
        }catch(IOException e) {
            System.out.println("Send error.");
        }
    }
    
    //receive a string from the server
    private String receiveString(){
        try{
            //receive byte[] length
            int length = in.read();
            //initialize byte[] with length
            byte[] bytearray = new byte[length];
            //read byte[]
            in.read(bytearray);
            
            return new String(bytearray);
        }catch(IOException e) {
            System.out.println("Receive error.");
        }
        
        return null;
    }
    
    private void close() {
        try{
            out.close();
            in.close();
            socket.close();
        }catch (NullPointerException | IOException e) {
            System.out.println("Socket close error.");
        }
    }
    
    public void setActive(int i) {
        active = i;
    }
    public int getActive() {
        return active;
    }
    
    
    public List<String> getList(boolean h) {
        return h ? hard : norm;
    }
    
    public void retrieveList() {
        active = 3;
        
        norm = new ArrayList<>();
        hard = new ArrayList<>();
        
        //continue loop
        sendString(Boolean.toString(true));
        //get list only
        sendString(Boolean.toString(true));
        
        for(int i = 0; i < 20; ++i) {
            norm.add(receiveString());
        }
        for(int i = 0; i < 20; ++i) {
            hard.add(receiveString());
        }
        active = 4;
    }
    
    public void addToList(String n, int s, boolean h) {
        active = 1;
        //continue loop
        sendString(Boolean.toString(true));
        //update and get list;
        sendString(Boolean.toString(false));
        //send name, score, hardmode
        sendString(n);
        sendString(Integer.toString(s));
        sendString(Boolean.toString(h));
        active = 2;
    }
    
    @Override
    public void run() {
        if(getSock()) {
            active = 0;
            while(active < 5) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {}
            }
            sendString(Boolean.toString(false));
            close();
        }
        
    }
    
    private String server;
    private int active; //0 = working with server, 1 = have lists, 2 = stops thread
    private List<String> norm, hard;
    
    private Socket socket;
    private OutputStream out;
    private InputStream in;
}
