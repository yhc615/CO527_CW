/*
 * Created on 01-Mar-2016
 */
package rmi;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;

import common.*;

public class RMIServer extends UnicastRemoteObject implements RMIServerI {

	private int totalMessages = -1;
	private int[] receivedMessages;

	public RMIServer() throws RemoteException {
		//lkjkjklkjl
		System.setProperty("java.rmi.server.hostname", "192.168.1.199.");
	}

	public void receiveMessage(MessageInfo msg) throws RemoteException {

		// TO-DO: On receipt of first message, initialise the receive buffer
		if ((receivedMessages == null) || (msg.totalMessages != totalMessages)) {
			totalMessages = msg.totalMessages;
			receivedMessages = new int[msg.totalMessages];
		}
		// TO-DO: Log receipt of the message
		receivedMessages[msg.messageNum] = 1;
		// TO-DO: If this is the last expected message, then identify
		//        any missing messages
		if (msg.messageNum + 1 == totalMessages) {
			System.out.println("Totaling Messages...");

			String str = "Lost Packet Numbers: ";
			int count = 0;
			for (int i = 0; i < totalMessages; i++) {
				if (receivedMessages[i] != 1) {
					count++;
					str = str + " " + (i+1) + ", ";
				}
			}

			if (count == 0) str = str + "None";

			System.out.println("Total messages sent      : " + totalMessages);
			System.out.println("Total messages received  : " + (totalMessages - count));
			System.out.println("Total messages lost      : " + count);
			System.out.println(str);
			System.out.println("Running...");
			//System.exit(0);
		}

	}


	public static void main(String[] args) {

		RMIServer rmis = null;

		// TO-DO: Initialise Security Manager
		if(System.getSecurityManager()==null){
		            System.setSecurityManager(new SecurityManager());
		}
		// TO-DO: Instantiate the server class
		try { rmis = new RMIServer();
		} catch(RemoteException e) {
			System.out.println("Error instantiating server class.");
			System.exit(-1);
		}

		// TO-DO: Bind to RMI registry

		rebindServer("RMIServer", rmis );

		System.out.println("Running...");

	}

	protected static void rebindServer(String serverURL, RMIServer server) {

		// TO-DO:
		// Start / find the registry (hint use LocateRegistry.createRegistry(...)
		// If we *know* the registry is running we could skip this (eg run rmiregistry in the start script)
		try{ LocateRegistry.createRegistry( 1099 );
		} catch (RemoteException e) {
			System.out.println("Error initializing registry.");
			System.exit(-1);
		}

		// TO-DO:
		// Now rebind the server to the registry (rebind replaces any existing servers bound to the serverURL)
		// Note - Registry.rebind (as returned by createRegistry / getRegistry) does something similar but
		// expects different things from the URL field.
		try{ Naming.rebind( serverURL , server );
		} catch (RemoteException e) {
			System.out.println("Error binding server.");
			System.exit(-1);
		} catch (MalformedURLException e) {
			System.out.println("Could not bind server to defined registry as the URL was malformed.");
			System.exit(-1);
		}
	}
}
