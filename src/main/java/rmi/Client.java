package iiop;

import for_database.Operation;
import Undistributed.Undistributed;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry(12300);

            IRemoteClass stub = (IRemoteClass) registry.lookup("IDatabaseRemote");

            while (true) {
                System.out.print("> ");
                Scanner scanner = new Scanner(System.in);
                String command = scanner.nextLine();

                Operation result = stub.query(command);

                Undistributed.processResult(result);
            }
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
