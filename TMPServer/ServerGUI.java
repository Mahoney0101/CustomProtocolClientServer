import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.ServerSocket;
import java.io.*;
import java.net.*;
import java.security.*;
import java.security.cert.CertificateException;
import javax.net.ssl.*;


public class ServerGUI {
    private JFrame frame = new JFrame();
    final private JButton startServer = new JButton("Start Server");
    private Font font = new Font("monospaced",Font.BOLD,30);

    public ServerGUI(){
        System.setProperty("javax.net.ssl.keyStore","herong.jks");
        System.setProperty("javax.net.ssl.keyStorePassword","password");
        setFrame();
    }

    private class EventHandler  implements ActionListener {
        public void actionPerformed(ActionEvent e)
        {

            if(e.getSource()==startServer){
                try {
                    ServerSocket myConnectionSocket = (SSLServerSocketFactory.getDefault()).createServerSocket(2000);
                    /**/     System.out.println("TMP server ready.");
                    while (true) {
                        /**/        System.out.println("Waiting for a connection.");
                        MyStreamSocket myDataSocket = new MyStreamSocket
                                (myConnectionSocket.accept( ));
                        /**/        System.out.println("connection accepted");
                        Thread theThread =
                                new Thread(new TMPServerThread(myDataSocket));
                        theThread.start();
                    } //end while forever

                } // end try
                catch (Exception ex) {
                    ex.printStackTrace( );
                } // end catch
            }

        }
    }

    public void setFrame(){
        startServer.setBackground(Color.white);
        frame.setSize(500,200);
        frame.setLayout(null);
        JLabel label = new JLabel("TMP SERVER");
        label.setVisible(true);
        label.setBounds(200,125,250,50);
        frame.add(label);
        EventHandler handler = new EventHandler();
        startServer.addActionListener(handler);
        startServer.setBounds(175,75,150,50);
        startServer.setVisible(true);
        frame.add(startServer);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

}
