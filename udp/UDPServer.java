/*
 * Created on 01-Mar-2016
 */
package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Arrays;

import common.MessageInfo;

public class UDPServer {

	private DatagramSocket recvSoc;
	private int totalMessages = -1;
	private int[] receivedMessages;
	private boolean close;

	private void run() {
		int				pacSize;
		byte[]			pacData;
		DatagramPacket 	pac;

		// TO-DO: Receive the messages and process them by calling processMessage(...).
		//        Use a timeout (e.g. 30 secs) to ensure the program doesn't block forever
		while(!close){
			//initialise buffer
			pacSize = 3000;
			pacData = new byte [pacSize];

			pac = new DatagramPacket(pacData, pacSize);
			try{
				recvSoc.setSoTimeout(30000);
				recvSoc.receive(pac);
			} catch (SocketTimeoutException e){
				System.out.println("Error: Socket Exception, timeout may have occured (30sec)");
				System.out.println("Closing server...");
				System.exit(-1);
			} catch (IOException e){
				System.out.println("Error: IOException");
				System.out.println("Closing server...");
				System.exit(-1);
			}
			processMessage(new String(pac.getData()));
		}
	}

	public void processMessage(String data) {
		MessageInfo msg = null;
		// TO-DO: Use the data to construct a new MessageInfo object
		try{
			msg = new MessageInfo(data.trim());
		} catch (Exception e){
			System.out.println(e);
		}
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
			}
		}

	public UDPServer(int rp) {
		// TO-DO: Initialise UDP socket for receiving data
		System.out.println("Initializing UDP socket for receiving data...");
		try{
			recvSoc = new DatagramSocket(rp);
		} catch (SocketException e){
			System.out.println("Error: SocketException");
			System.out.println("Closing Server...");
			System.exit(-1);
		}
		// Done Initialisation
		System.out.println("UDPServer initialisation Done.");
	}

	public static void main(String args[]) {
		int	recvPort;
		// Get the parameters from command line
		if (args.length < 1) {
			System.err.println("No agrument given: recv port");
			System.exit(-1);
		}
		recvPort = Integer.parseInt(args[0]);

		// TO-DO: Construct Server object and start it by calling run().
		UDPServer udp = new UDPServer(recvPort);
		udp.run();
	}

}
