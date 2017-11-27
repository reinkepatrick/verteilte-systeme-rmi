import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;


public class Client extends UnicastRemoteObject implements ClientProxy {
    public ChatServer server;

    JTextArea content;
    JTextField message, username;
    JButton connect;
    JFrame frame;

    public Client() throws RemoteException {
        server = null;
    }

    @Override
    public void receiveMessage(String username, String message) throws RemoteException {
        content.append(username + ": " + message + "\n");
    }

    private void connect() {
        if(username.getText().replaceAll(" ", "").length() > 0) {
            try {
                Registry registry = LocateRegistry.getRegistry();
                server = (ChatServer) registry.lookup("ChatServer");

                if(server.subscribeUser(this)) {
                    username.setEditable(false);
                    connect.setText("Trennen");
                } else {
                    JOptionPane.showMessageDialog(frame, "Konnte keine Verbindung herstellen!");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            JOptionPane.showMessageDialog(frame, "Der Name darf nicht leer sein!");
        }
    }

    private void disconnect() {
        try {
            if(server.unsubscribeUser(this)) {
                username.setEditable(true);
                connect.setText("Verbinden");
                content.setText("");
                server = null;
            } else {
                JOptionPane.showMessageDialog(frame, "Abmeldung fehlgeschlagen!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void send() {
        if (message.getText().replaceAll(" ", "").length() > 0) {
            try {
                server.send(username.getText(), message.getText());
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            message.setText("");
        } else {
            JOptionPane.showMessageDialog(frame, "Nachricht darf nicht leer sein!");
        }
    }

    public static void main(String[] args) {
        Client c = null;
        try {
            c = new Client();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        c.initUI();
    }



    public void initUI(){
        frame=new JFrame("Client");
        JPanel main =new JPanel();
        JPanel top =new JPanel();
        JPanel cn =new JPanel();
        JPanel bottom =new JPanel();
        message =new JTextField();
        username =new JTextField();
        content =new JTextArea();
        connect=new JButton("Verbinden");
        JButton send=new JButton("Senden");
        main.setLayout(new BorderLayout(5,5));
        top.setLayout(new GridLayout(1,0,5,5));
        cn.setLayout(new BorderLayout(5,5));
        bottom.setLayout(new BorderLayout(5,5));
        top.add(new JLabel("Dein Name: "));
        top.add(username);
        top.add(connect);
        cn.add(new JScrollPane(content), BorderLayout.CENTER);
        bottom.add(message, BorderLayout.CENTER);
        bottom.add(send, BorderLayout.EAST);
        main.add(top, BorderLayout.NORTH);
        main.add(cn, BorderLayout.CENTER);
        main.add(bottom, BorderLayout.SOUTH);
        main.setBorder(new EmptyBorder(10, 10, 10, 10) );

        connect.addActionListener(e -> {
            if(connect.getText().equalsIgnoreCase("verbinden")) {
                connect();
            } else {
                disconnect();
            }
        });

        username.addActionListener(e -> {
            if(connect.getText().equalsIgnoreCase("verbinden")) {
                connect();
            } else {
                disconnect();
            }
        });

        message.addActionListener(e -> {
            send();
        });

        send.addActionListener(e -> {
            send();
        });

        frame.setContentPane(main);
        frame.setSize(400,600);
        frame.setVisible(true);
    }
}
