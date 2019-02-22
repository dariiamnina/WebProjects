package iiop;

import for_database.Operation;
import for_database.TablesHashMap;
import for_database.Reader;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class RmiServer {

    public interface IDatabaseRemote extends Remote {

        Operation query(String msg) throws RemoteException;
    }

    public static void main(String args[]) {
        try {
            DatabaseRem obj = new DatabaseRem("test.json");

            IDatabaseRemote stub = (IDatabaseRemote) UnicastRemoteObject.exportObject(obj, 0);

            Registry registry = LocateRegistry.createRegistry(12300);

            registry.bind("IDatabaseRemote", stub);

            System.err.println("Server ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}