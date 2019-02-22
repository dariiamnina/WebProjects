package iiop;

import for_database.Operation;
import for_database.TablesHashMap;
import for_database.Reader;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public interface IRemoteClass extends Remote {
    Operation query(String msg) throws RemoteException;
}
