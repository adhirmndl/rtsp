package rtsp_server;



import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import java.io.*;

public class server extends Thread {
	static String uri=null;
	static String opt=null;
	static String des=null;
	static String set=null;
	static String ply=null;
	static String ps=null;
	static String trd=null;
	private static ArrayList<String> nonce = new ArrayList<String>();
	public static void print(char c[]) {
		for (int i=0;i<c.length;i++) {
			System.out.print(c[i]);
		}
		System.out.println("");
	}
	public static String get_GMT() {
		TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
		Calendar time = Calendar.getInstance(TimeZone.getDefault());
		time.add(Calendar.MILLISECOND, -time.getTimeZone().getOffset(time.getTimeInMillis()));
		Date date = time.getTime();
		String tmp1 = (date.toString());
		String tmp[] = tmp1.split(" ");
		//return format = Mon, Apr 02 2018 03:07:43 GMT
		return tmp[0]+", "+tmp[1]+" "+tmp[2]+" "+tmp[5]+" "+tmp[3]+" "+tmp[4];
		
	}
	public static String get_cmd(String s) {
		print (s);
		return s.split(" ")[0];
	}
	public static String parse_opt(String s) {
		/*
		 * RTSP/1.0 200 OK
		 * CSeq: 2
		 * Date: Mon, Apr 02 2018 05:13:52 GMT
		 * Public: OPTIONS, DESCRIBE, SETUP, TEARDOWN, PLAY, PAUSE, GET_PARAMETER, SET_PARAMETER
		 */
		String back[] = s.split("\r\n");
		
		List<String> data = new ArrayList<String>();
		uri = back[0].split(" ")[1];
		String UA = back[2];
		opt = back[0].split(" ")[2] + " 200 OK\r\n" + 
				back[1] +"\r\n"+ 
				"Date: "+get_GMT()+"\r\n" + 
				"Public: OPTIONS, DESCRIBE, SETUP, TEARDOWN, PLAY, PAUSE, GET_PARAMETER, SET_PARAMETER\r\n" + 
				"\r\n";
		return (opt);
		
	}
	public static String parse_opt(char ss[]) {
		/*
		 * RTSP/1.0 200 OK
		 * CSeq: 2
		 * Date: Mon, Apr 02 2018 05:13:52 GMT
		 * Public: OPTIONS, DESCRIBE, SETUP, TEARDOWN, PLAY, PAUSE, GET_PARAMETER, SET_PARAMETER
		 */
		String s="";
		int pos=0;
		for (int i=2;i<1000;i++) {
			if (ss[i-2]=='\n'&&ss[i]=='\n') {
				pos = i;break;
			}
		}
		for (int i=0;i<=pos;i++) {
			s+=ss[i];
		}
		String back[] = s.split("\r\n");
		
		List<String> data = new ArrayList<String>();
		uri = back[0].split(" ")[1];
		String UA = back[2];
		opt = back[0].split(" ")[2] + " 200 OK\r\n" + 
				back[1] +"\r\n"+ 
				"Date: "+get_GMT()+"\r\n" + 
				"Public: OPTIONS, DESCRIBE, SETUP, TEARDOWN, PLAY, PAUSE, GET_PARAMETER, SET_PARAMETER\r\n" + 
				"\r\n";
		print("<<");
		print(opt);
		return (opt);
		
	}
	public static String getNewSsrc() {
        Random random = new Random(System.currentTimeMillis());
        long num = random.nextLong() & 0xffffffffL;
        return Long.toString(num, 16).toUpperCase();
    }
	public static String byteArrayToHexString(byte[] bytes) {
		char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
		char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
	public static String encodeMD5Hash(String s) {
        String encoded = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(s.getBytes());
            encoded = byteArrayToHexString(md.digest()).toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            print (e.getMessage());
        }
        return encoded;
    }
	public static String generateNonce() {
        long startTime = System.nanoTime();
        String n = encodeMD5Hash(getNewSsrc()).toLowerCase();
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000;
        return n;
    }
	public static boolean is_valid_nonce(String s) {
		for(int i=0;i<nonce.size();i++) {
			if (nonce.get(i)==s) {
				return true;
			}
		}
		return false;
	}
	public static Boolean is_auth(String s) {
		if(s.indexOf("Authorization:")==-1)  // kaam baki he abhi
			return false;
		return true;
	}
	public static String get_CSeq(String s) {
		String bf[] = s.split("\r\n");
		for (int i=0;i<bf.length;i++) {
			if(bf[i].indexOf("CSeq: ")!=-1) {
				return bf[i];
			}
		}
		return "";
	}
	public static String get_url_base(String s) {
		return s.substring(s.indexOf("rtsp://"), s.indexOf("RTSP/1.0")-1);
		
	}
	public static String get_host_ip() {
		InetAddress ip;
		try {
			ip = InetAddress.getLocalHost();
			return ip.getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return"Invalid IP";
		
	}
	public static String parse_des(String s) {
		if (!get_cmd(s).equalsIgnoreCase("DESCRIBE")) {
			return "-1";
		}
		if (is_auth(s)) {
			/*
			 * DESCRIBE rtsp://10.0.1.173:554/main RTSP/1.0
			 * CSeq: 4
			 * Authorization: Digest username="", realm="LIVE555 Streaming Media", nonce="12d13994550a7ab862683e53b36416d8", uri="rtsp://10.0.1.173:554/main", response="2b4500f90353ccfad60fcede30239073"
			 * User-Agent: LibVLC/2.2.4 (LIVE555 Streaming Media v2016.02.22)\
			 * Accept: application/sdp
			 * 
			 * reply
			 * 
			 * RTSP/1.0 200 OK
			 * CSeq: 7
			 * Date: Thu, Mar 01 2018 08:02:31 GMT
			 * Content-Base: rtsp://10.0.1.173/main/
			 * Content-Type: application/sdp
			 * Content-Length: 522
			 * 
			 * v=0
			 * o=- 1519886639634710 1 IN IP4 10.0.1.173
			 * s=RTSP/RTP stream from IPNC
			 * i=main
			 * t=0 0
			 * a=tool:LIVE555 Streaming Media v2010.07.29
			 * a=type:broadcast
			 * a=control:*
			 * a=range:npt=0-
			 * a=x-qt-text-nam:RTSP/RTP stream from IPNC
			 * a=x-qt-text-inf:main
			 * m=video 0 RTP/AVP 96
			 * b=AS:12000
			 * a=rtpmap:96 H264/90000
			 * a=fmtp:96 packetization-mode=1;profile-level-id=4D001E;sprop-parameter-sets=Z00AHpWoLASabgICAgQ=,aO48gA==
			 * a=control:track1
			 * m=application 0 RTP/AVP 98
			 * b=AS:0
			 * a=rtpmap:98 vnd.onvif.metadata/90000
			 * a=control:track2
			 * 
			 */
			des = "RTSP/1.0 200 OK\r\n"+
			 get_CSeq(s)+"\r\n"+
			 "Date: "+get_GMT() + "\r\n"+
			 "Content-Base: "+get_url_base(s)+"\r\nContent-Type: application/sdp\r\nContent-Length: 522\r\n\r\nv=0\r\no=- 1519886639634710 1 IN IP4 "+get_host_ip()+"\r\ns=RTSP/RTP stream from IPNC\r\ni=main\r\nt=0 0a=tool:iVIS Streaming Media v02.04.2018\r\n" + 
			 		"a=type:broadcast\r\n" + 
			 		"a=control:*\r\n" + 
			 		"a=range:npt=0-\r\n" + 
			 		"a=x-qt-text-nam:RTSP/RTP stream from IPNC\r\n" + 
			 		"a=x-qt-text-inf:main\r\n" + 
			 		"m=video 0 RTP/AVP 96\r\n" + 
			 		"b=AS:12000\r\n" + 
			 		"a=rtpmap:96 H264/90000\r\n" + 
			 		"a=fmtp:96 packetization-mode=1;profile-level-id=4D001E;sprop-parameter-sets=Z00AHpWoLASabgICAgQ=,aO48gA==\r\n" + 
			 		"a=control:track1\r\n" + 
			 		"m=application 0 RTP/AVP 98\r\n" + 
			 		"b=AS:0\r\n" + 
			 		"a=rtpmap:98 vnd.onvif.metadata/90000\r\n" + 
			 		"a=control:track2";
			 
			 return (des);
			
		}
		else {
			//plain digest describe
			/*
			RTSP/1.0 401 Unauthorized
			CSeq: 3
			Date: Thu, Mar 01 2018 08:00:16 GMT
			WWW-Authenticate: Digest realm="LIVE555 Streaming Media", nonce="12d13994550a7ab862683e53b36416d8"*/
			des = "RTSP/1.0 401 Unauthorized\r\n"+get_CSeq(s) + "\r\nDate: "+get_GMT()+"\r\n" + "WWW-Authenticate: Digest realm="+'"'+"iVIS Streaming Media"+'"'+", nonce="+'"'+generateNonce()+'"'+"\r\n\r\n";
			return " ";
			
			
		}
	}
   
	private ServerSocket serverSocket;
	public static void print(String stttt) {
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
            //reading data from socket
            BufferedReader in = new BufferedReader( new InputStreamReader(server.getInputStream()));
            String buff;
            print ("\n\n\n");
            char s[]  = new char[10000];
            in.read(s);
            String p = s.toString();
            print ("P = " + p);
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(server.getOutputStream())));
            print (get_cmd(s.toString()));
            if (s.toString().subSequence(0, 6).equals("OPTIONS")) {
            	print ("Found option");
            	out.println(parse_opt(s));
                out.flush();
            	
            }
            else {
            	print ("Not found");
            }

            
              
            print ("\n\nReply Sent.."); 
            
            //describe
            
             /*
              print ("Received Data");
              while((buff=in.readLine())!=null) {
              	if (buff.length() == 0)
                      break;
              	print (buff);
              }   
              
              out.println(opt);
              out.flush();

             */ 
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

public static void main(String args[] ) {
      int port = 554;
      //print(get_GMT());
      String s = "OPTIONS rtsp://10.0.1.173:554/main RTSP/1.0\r\nCSeq: 2\r\nUser-Agent: LibVLC/2.2.4 (LIVE555 Streaming Media v2016.02.22)\r\n\r\n";
      s="DESCRIBE rtsp://10.0.1.173:554/main RTSP/1.0\r\n" + 
      		"CSeq: 3\r\n" + 
      		"User-Agent: LibVLC/2.2.4 (LIVE555 Streaming Media v2016.02.22)\r\n" + 
      		"Accept: application/sdp\r\n\r\n";
      s = "DESCRIBE rtsp://10.0.1.173:554/main RTSP/1.0\r\n" + 
      		"CSeq: 4\r\n" + 
      		"Authorization: Digest username=\"\", realm=\"LIVE555 Streaming Media\", nonce=\"12d13994550a7ab862683e53b36416d8\", uri=\"rtsp://10.0.1.173:554/main\", response=\"2b4500f90353ccfad60fcede30239073\"\r\n" + 
      		"User-Agent: LibVLC/2.2.4 (LIVE555 Streaming Media v2016.02.22)\r\n" + 
      		"Accept: application/sdp\r\n\r\n";
      //parse_opt(s);
      //print (parse_des(s));
      try {
         Thread t = new server(port);
         t.start();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
}