package network;

import java.io.*;
import java.net.*;
import java.util.*;

import static java.lang.System.out;

public class NetworkInfo {

	public NetworkInfo() {
		
		try {
		
		    Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
		    for (NetworkInterface netint : Collections.list(nets))
		        displayInterfaceInformation(netint);
	
		}
		catch(SocketException ex) {
	        
	        System.err.println("Error listing network intefaces");
	        ex.printStackTrace();
	    }

	} // End NetworkInfo()
	
	static void displayInterfaceInformation(NetworkInterface netint) throws SocketException {
	    out.printf("Display name: %s\n", netint.getDisplayName());
	    out.printf("Name: %s\n", netint.getName());
	    Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
	    for (InetAddress inetAddress : Collections.list(inetAddresses)) {
	        out.printf("InetAddress: %s\n", inetAddress);
	    }
	    out.printf("\n");
	 }

} // End class NetworkInfo
