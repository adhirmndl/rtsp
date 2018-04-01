package rtsp_server;



import java.net.*;
import java.io.*;

public class server extends Thread {
	
	/*
	 int imagenb = 0; //image nb of the image currently transmitted
	    VideoStream video; //VideoStream object used to access video frames
	    static int MJPEG_TYPE = 26; //RTP payload type for MJPEG video
	    static int FRAME_PERIOD = 100; //Frame period of the video to stream, in ms
	    static int VIDEO_LENGTH = 500; //length of the video in frames

	    Timer timer;    //timer used to send the images at the video frame rate
	    byte[] buf;     //buffer used to store the images to send to the client 
	    int sendDelay;  //the delay to send images over the wire. Ideally should be
	                    //equal to the frame rate of the video file, but may be 
	                    //adjusted when congestion is detected.

	    //RTSP variables
	    //----------------
	    //rtsp states
	    final static int INIT = 0;
	    final static int READY = 1;
	    final static int PLAYING = 2;
	    //rtsp message types
	    final static int SETUP = 3;
	    final static int PLAY = 4;
	    final static int PAUSE = 5;
	    final static int TEARDOWN = 6;
	final static int DESCRIBE = 7;
	/*
	
    private String describe() {
        StringWriter writer1 = new StringWriter();
        StringWriter writer2 = new StringWriter();
        
        // Write the body first so we can get the size later
        writer2.write("v=0" + CRLF);
        writer2.write("m=video " + RTSP_dest_port + " RTP/AVP " + MJPEG_TYPE + CRLF);
        writer2.write("a=control:streamid=" + RTSPid + CRLF);
        writer2.write("a=mimetype:string;\"video/MJPEG\"" + CRLF);
        String body = writer2.toString();

        writer1.write("Content-Base: " + VideoFileName + CRLF);
        writer1.write("Content-Type: " + "application/sdp" + CRLF);
        writer1.write("Content-Length: " + body.length() + CRLF);
        writer1.write(body);
        
        return writer1.toString();
}
   
    */
   private ServerSocket serverSocket;
   public void print(String stttt) {
	   System.out.println(stttt);
   }
   
   public server(int port) throws IOException {
      serverSocket = new ServerSocket(port);
      serverSocket.setReuseAddress(false);

   }
   public void print(BufferedReader inn) throws IOException {
	   String s = null;

	   while ((s=inn.readLine())!=null)
	       {
		   if (s.length() == 0)
               break;
	              System.out.println(s);
	       }
   }

   public void run() {
      while(true) {
         try {
            System.out.println("Waiting for client on port " + 
               serverSocket.getLocalPort() + "...");
            Socket server = serverSocket.accept();
            String opti = "RTSP/1.0 200 OK\r\n" + 
            		"CSeq: 2\r\n" + 
            		"Date: Thu, Mar 01 2018 08:00:16 GMT\r\n" + 
            		"Public: OPTIONS, DESCRIBE, SETUP, TEARDOWN, PLAY, PAUSE, GET_PARAMETER, SET_PARAMETER\r\n\r\n";
            
            /*System.out.println("Client Connected : " + server.getRemoteSocketAddress());
            print("\n\n");
            BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));
            String buff;
            print ("Received Data");
            while((buff=in.readLine())!=null) {
            	if (buff.length() == 0)
                    break;
            	print (buff);
            }
            OutputStreamWriter bw = new OutputStreamWriter(server.getOutputStream());
            BufferedWriter bw = new BufferedWriter( new OutputStreamWriter( server.getOutputStream() ) );
            bw.write(opti);
            print ("\nsend");*/
            
            /*
            print(String(opti.length()));
            OutputStreamWriter osw =new OutputStreamWriter(server.getOutputStream(), "UTF-8");
            osw.write(opti, 0, opti.length());
*/
            
            
            
            
            BufferedReader in = new BufferedReader( new InputStreamReader(server.getInputStream()));
            String buff;
            print ("Received Data");
            while((buff=in.readLine())!=null) {
            	if (buff.length() == 0)
                    break;
            	print (buff);
            }      
            
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(server.getOutputStream())));
              
              out.println(opti);
              out.flush();
             
              print ("Received Data");
              while((buff=in.readLine())!=null) {
              	if (buff.length() == 0)
                      break;
              	print (buff);
              }   
              
              out.println(opti);
              out.flush();
             
              
            server.close();
            
         } catch (SocketTimeoutException s) {
            System.out.println("Socket timed out!");
            break;
         } catch (IOException e) {
            e.printStackTrace();
            break;
         }
      }
   }
   
   private String String(int length) {
	// TODO Auto-generated method stub
	return null;
}

public static void main(String args[] ) {
      int port = 554;
      try {
         Thread t = new server(port);
         t.start();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
}