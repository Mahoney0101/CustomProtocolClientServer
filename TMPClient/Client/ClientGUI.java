package Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
public class ClientGUI {
    private JFrame frame = new JFrame();
    private JButton upload = new JButton("Upload");
    private JButton download = new JButton("Download");
    private Font font = new Font("monospaced",Font.BOLD,30);
    private JButton login = new JButton("Log in");
    private String username;
    private JButton logout = new JButton("Logout");
    TMPHelper helper = null;

    public ClientGUI() throws IOException
    {
        System.setProperty("javax.net.ssl.trustStore","public.jks");
        System.setProperty("javax.net.ssl.trustStorePassword","password");
        setFrame();
    }

    private class EventHandler  implements ActionListener {
        public void actionPerformed(ActionEvent e)
        {
            if(e.getSource()==login){
                try {
                    helper =
                            new TMPHelper("localhost", "2000");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                username=JOptionPane.showInputDialog("Enter your username");
                String loginMessage = "login;";
                boolean valid=false;
                loginMessage+=username+";";
                String password=JOptionPane.showInputDialog("Enter your password");
                loginMessage+=password;
                try {
                    String msg = helper.getEcho( loginMessage);
                    System.out.println(msg);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                login.setVisible(false);
                upload.setVisible(true);
                frame.setSize(500,200);
                upload.addActionListener(this);
                upload.setBounds(50,75,100,40);
                upload.setBackground(Color.white);
                upload.setVisible(true);
                frame.add(upload);

                download.addActionListener(this);
                download.setBackground(Color.white);
                download.setBounds(200,75,100,40);
                download.setVisible(true);
                frame.add(download);

                logout.addActionListener(this);
                logout.setBackground(Color.white);
                logout.setBounds(350,75,100,40);

                logout.setVisible(true);
                frame.add(logout);
                frame.repaint();
            }
            if(e.getSource()==upload){
                String uploadMessage = "upload;";
                uploadMessage += JOptionPane.showInputDialog("Enter a message");
                try {
                    String uploadmsg = helper.getEcho(uploadMessage);
                    System.out.println(uploadmsg);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                frame.repaint();
            }
            if(e.getSource()==logout){
                String logoutMessage = "logout";
                try {
                    String logout = helper.getEcho(logoutMessage);
                    System.out.println(logout);
                    frame.setSize(new Dimension(500,200));
                    frame.getContentPane().removeAll();
                    login.setVisible(true);
                    frame.add(login);
                    frame.repaint();

                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
            if(e.getSource()==download){
                String downloadMessage = "download";
                try {
                    String dlmsg = helper.getEcho(downloadMessage);
                    String[] downloadItems = dlmsg.split("\\;");
                    PrintWriter out = new PrintWriter("ClientFiles/DownloadedMessages.txt");

                    for (int i=0;i<downloadItems.length;i++) {
                        out.println(downloadItems[i]+"\n");
                    }
                    out.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }

            }
        }
    }


    public void setFrame() throws IOException {
        login.setBackground(Color.white);
        frame.setSize(500,200);
        frame.setLayout(null);
        EventHandler handler = new EventHandler();
        login.addActionListener(handler);
        login.setBounds(200,75,100,50);
        login.setVisible(true);
        frame.add(login);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        upload.setVisible(false);
        download.setVisible(false);
        logout.setVisible(false);
        frame.add(upload);
        frame.add(download);
        frame.add(logout);
        frame.setVisible(true);
    }
}

