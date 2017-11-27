import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class ServerC extends UnicastRemoteObject implements ChatServer {
    private ArrayList<ClientProxy> users;


    protected ServerC() throws RemoteException {
        this.users = new ArrayList<>();
    }

    @Override
    public boolean subscribeUser(ClientProxy handle) throws RemoteException {
        System.out.println("Ein User hat sich angemeldet");
        if(users.add(handle))
            return true;
        return false;
    }

    @Override
    public boolean unsubscribeUser(ClientProxy handle) throws RemoteException {
        System.out.println("Ein User hat sich abgemeldet");
        if(users.remove(handle))
            return true;
        return false;
    }

    public void send(String username, String message) {
        System.out.println(username + ": " + message);

        for(int i = 0; i < users.size(); i++) {
            try {
                users.get(i).receiveMessage(username, message);
            } catch (RemoteException e) {}
        }
    }

    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
            ChatServer server = new ServerC();
            Naming.rebind("ChatServer", server);
            System.out.println("Server wurde gestartet.");

        } catch (Exception e) {
            System.out.println("ServerC konnte nicht gestartet werden: " + e);
        }

    }
}
