import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatServer extends Remote {
    public boolean subscribeUser (ClientProxy handle) throws RemoteException;
    public boolean unsubscribeUser (ClientProxy handle) throws RemoteException;
    public void send(String username, String message) throws RemoteException;
}