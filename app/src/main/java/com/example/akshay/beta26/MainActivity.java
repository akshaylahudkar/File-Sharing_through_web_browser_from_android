package com.example.akshay.beta26;



        import fi.iki.elonen.NanoHTTPD;

        import android.os.Environment;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.widget.TextView;

        import java.io.BufferedReader;
        import java.io.BufferedInputStream;

        import java.io.File;
        import java.io.FileInputStream;
        import java.io.FileReader;
        import java.io.IOException;
        import java.net.Inet4Address;
        import java.net.InetAddress;
        import java.net.NetworkInterface;
        import java.net.SocketException;
        import java.util.Enumeration;
        import java.util.Map;
import java.util.HashMap;
        import java.util.Set;
import java.net.*;

public class MainActivity extends AppCompatActivity {
    private WebServer server;
    static FileInputStream fis;
    static BufferedInputStream bis;
    private static final String TAG = "MYSERVER";
    private static final int PORT = 8080;
    String ipAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView text = (TextView) findViewById(R.id.ipaddr);
        ipAddress = getLocalIpAddress();

        if (ipAddress != null) {
            text.setText("Please Access:" + "http://" + ipAddress + ":" + PORT);
        } else {
            text.setText("Wi-Fi Network Not Available");
        }

        server = new WebServer();
        try {
            server.start();
        } catch(IOException ioe) {
            Log.w("Httpd", "The server could not start.");
        }
        Log.w("Httpd", "Web server initialized.");

    }

    // DON'T FORGET to stop the server
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (server != null)
            server.stop();
    }

    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if(!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        String ipAddr = inetAddress.getHostAddress();
                        return ipAddr;
                    }
                }
            }
        } catch (SocketException ex) {
            Log.d(TAG, ex.toString());
        }
        return null;
    }


    private class WebServer extends NanoHTTPD {
        String MIME_TYPE;

        public WebServer()
        {
            super(PORT);
        }

        @Override
        /* public Response serve(String uri, Method method,
                              Map<String, String> header,
                              Map<String, String> parameters,
                              Map<String, String> files) {
            String answer = "";
            try {
                // Open file from SD Card
                File root = Environment.getExternalStorageDirectory();
                FileReader index = new FileReader(root.getAbsolutePath() +
                        "/www/index.html");
                BufferedReader reader = new BufferedReader(index);
                String line = "";
                while ((line = reader.readLine()) != null) {
                    answer += line;
                }
                reader.close();

            } catch(IOException ioe) {
                Log.w("Httpd", ioe.toString());
            }


            return new NanoHTTPD.Response(answer);
        }
        */


        public Response serve(String uri, Method method,
                              Map<String, String> header, Map<String, String> parameters,
                              Map<String, String> files) {
            File rootDir = Environment.getExternalStorageDirectory();
            File[] filesList = null;
            String filepath = "";
            if (uri.trim().isEmpty()) {
                filesList = rootDir.listFiles();
            } else {
                filepath = uri.trim();
            }
            filesList = new File(filepath).listFiles();
            String answer = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"><title>sdcard0 Listing the internal storage</title>";
            if (new File(filepath).isDirectory()) {
                for (File detailsOfFiles : filesList) {
                    answer += "<a href=\"" + detailsOfFiles.getAbsolutePath()
                            + "\" alt = \"\">"
                            + detailsOfFiles.getAbsolutePath() + "</a><br>";
                }
            } else {

          // answer += "</head></html>" + "uri: " + uri + " \nfiles " + files
             //    + " \nparameters " +parameters+ " \nheader ";

           // answer += "</head></html>" + "uri: " + uri ;
           try {
                File file=new File(uri);
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                MIME_TYPE= URLConnection.guessContentTypeFromName(file.getName());
                System.out.println("\nMIME TYPE: "+MIME_TYPE);
                System.out.println("\nFILE NAME: "+file.getName());
            } catch (IOException ioe) {
                System.out.println("File IO Exception");
            }
            return new NanoHTTPD.Response(Response.Status.OK, MIME_TYPE, bis);
}

        return new NanoHTTPD.Response(answer);
        }
    }
}
