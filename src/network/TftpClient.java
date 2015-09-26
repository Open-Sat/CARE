/*
 *                             Sun Public License 
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License").  You may not use this file except in compliance with
 * the License.  A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the SLAMD Distributed Load Generation Engine.
 * The Initial Developer of the Original Code is Geoffrey Said.
 * Portions created by Geoffrey Said are Copyright (C) 2006.
 * All Rights Reserved.
 *
 * Contributor(s):  Geoffrey Said
 */
package network;

import java.io.*;  
import java.net.*;
import java.util.*;


/**
 * Defines a client that can connect with a TFTP server and send (put) and
 * receive (get) files. I left the Sun Public License in the header because
 * I started with the Sun code. However I added a send feature and file 
 * management adn I removed the statistics.  The low level code remained 
 * but I lot of the management code changed.
 * 
 */
public class TftpClient
{
    // Static public variables declaration.
    /**
     * Standard TFTP port to use when initializing communication with the
     * TFTP server.
     */
    public static final int DEFAULT_TFTP_SERVER_PORT = 69;

    /**
     * Data terminator to include in TFTP packet.
     */
    public static final byte PACKET_DATA_TERMINATOR = 00;

    /**
     * Maximum TFTP data size.
     */
    public static final int MAX_DATA_FIELD_SIZE = 512;

    /**
     * Maximum TFTP packet size to be used in creating byte buffers. Equals
     * maximum data size plus 4 bytes for opcode and block number.
     */
    public static final int MAX_PACKET_SIZE = MAX_DATA_FIELD_SIZE + 4;


    /**
     * Default client UDP port to use if no random one is used. Matches cFS' tfapp
     */
    public static final int DEFAULT_UDP_PORT = 1237;

    /**
     * Default timeout, in milliseconds, to wait before the client retransmits
     * last packet.
     */
    public static final int DEFAULT_SOCKET_TIMEOUT = 3000;

    /**
     * Default number of retries before transmitting an error TFTP packet to
     * the server.
     */
    public static final int DEFAULT_NUMBER_OF_RETRIES = 3;

    /**
     * Default download mode when building a data TFTP packet.
     */
    public static final String DEFAULT_MODE = "octet";

    /**
     * Default TFTP server IP.
     */
    public static final String DEFAULT_TFTP_SERVER_IP = "127.0.0.1";

    /**
     * Default filename to fetch from the TFTP server.
     */
    public static final String DEFAULT_FILENAME = "filename";

    /**
     * Value that defines a read request TFTP packet.
     */
    public static final byte PACKET_RRQ = 01;

    /**
     * Value that defines a write request TFTP packet.
     */
    public static final byte PACKET_WRQ = 02;

    /**
     * Value that defines a data TFTP packet.
     */
    public static final byte PACKET_DATA = 03;

    /**
     * Value that defines an acknowledge TFTP packet.
     */
    public static final byte PACKET_ACK = 04;

    /**
     * Value that defines an error TFTP packet.
     */
    public static final byte PACKET_ERROR = 05;

    /**
     * Value that defines an error TFTP packet.
     */
    public static final byte PACKET_UNDEFINED = 06;

    /**
     * Ascii mode description string used in building a data TFTP packet
     */
    public static final String DOWNLOAD_NETASCII = "netascii";

    /**
     * Binary mode description string used in building a data TFTP packet
     */
    public static final String DOWNLOAD_OCTET = "octet";

    /**
     * Mail mode description string used in building a data TFTP packet.
     */
    public static final String DOWNLOAD_MAIL = "mail";

    /**
     * Value that defines a <B>NOT DEFINED</B> error TFTP packet.
     */
    public static final byte ERROR_NOT_DEFINED = 00;

    /**
     * Value that defines a <B>FILE NOT FOUND</B> error TFTP packet.
     */
    public static final byte ERROR_FILE_NOT_FOUND = 01;

    /**
     * Value that defines an <B>ACCESS VIOLATION</B> error TFTP packet.
     */
    public static final byte ERROR_ACCESS_VIOLATION = 02;

    /**
     * Value that defines a <B>DISK FULL</B> error TFTP packet.
     */
    public static final byte ERROR_DISK_FULL = 03;

    /**
     * Value that defines an <B>ILLEGAL TFTP OPERATION</B> error TFTP packet.
     */
    public static final byte ERROR_ILLEGAL_TFTP_OPERATION = 04;

    /**
     * Value that defines an <B>UNKNOWN TRANSFER ID</B> error TFTP packet.
     */
    public static final byte ERROR_UNKNOWN_TRANSFER_ID = 05;

    /**
     * Value that defines a <B>FILE ALREADY EXISTS</B> error TFTP packet.
     */
    public static final byte ERROR_FILE_ALREADY_EXISTS = 06;

    /**
     * Value that defines a <B>NO SUCH USER</B> error TFTP packet.
     */
    public static final byte ERROR_NO_SUCH_USER = 07;

    /**
     * String array that defines all error messages to write in an error TFTP
     * packet.
     */
    public static final String[] ERROR_MESSAGES = { "Timeout error occurred",
                                                    "File not found",
                                                    "Access violation",
                                                    "Disk is full",
                                                    "Illegal tftp operation",
                                                    "Unknown transfer ID",
                                                    "File already exists",
                                                    "No such user",
                                                    "No error message" };
    // Private instance variables.
    // Byte value that defines a no error situation.
    private static final byte ERROR_NO_ERROR = 10;

    // UDP socket and packet objects.
    private DatagramSocket socket;
    private DatagramPacket sendPacket, receivePacket;

    // Byte array stream objects.  Used to store all data sent/received.
    private ByteArrayOutputStream receivedData = new ByteArrayOutputStream();
    private ByteArrayOutputStream tftpSendPacket = new ByteArrayOutputStream();
    private ByteArrayInputStream tftpReceivedPacket;

    // Object instance status variables.
    // Byte arrays
    private byte[] opCode = new byte[2];
    private byte[] errorCode = new byte[2];
    private byte[] blockNumber = new byte[2];
    private byte[] data;

    // Integers that store communication ports, the length of the last piece of
    // data received, and the total data received
    private int localPort;
    private int remotePort;
    private int dataLength;
    private int totalDataLength;
    private int totalActualDataLength;

    // Integers that store UDP socket timeout and number of retries
    // before client sends error packet.
    private int timeout;
    private int retries;

    // Strings storing fileName to retrieve, data transfer mode, error messages,
    // and server IP address.
    private String fileName;
    private String mode;
    private String errorMessage;
    private String serverIP;

    /**
     * Class constructor which sets every instance variable to its default value.
     * <P>
     * In particular:
     * <UL>
     * <LI>Server IP is set to <I>127.0.0.1</I>.
     * <LI>File name is set to the file <I>filename</I>.
     * </UL>
     */
    public TftpClient()
    {
        setVariables(DEFAULT_TFTP_SERVER_IP, DEFAULT_FILENAME, DEFAULT_TFTP_SERVER_PORT, PACKET_RRQ,
                DEFAULT_MODE, DEFAULT_SOCKET_TIMEOUT, DEFAULT_NUMBER_OF_RETRIES);
    }

    /**
     * Class constructor that initializes the TFTP server IP to the
     * <CODE>serverIP</CODE> variable. All other variables are set to default.
     * <P>
     * In particular:
     * <UL>
     * <LI>File name is set to the file <I>filename</I>.
     * </UL>
     *
     * @param serverIP    the TFTP server IP address to connect to when retrieving
     *                                    files.
     */
    public TftpClient(String serverIP)
    {
        setVariables(serverIP, DEFAULT_FILENAME, DEFAULT_TFTP_SERVER_PORT, PACKET_RRQ,
                DEFAULT_MODE, DEFAULT_SOCKET_TIMEOUT, DEFAULT_NUMBER_OF_RETRIES);
    }

    /**
     * Class constructor that initializes the TFTP server IP to the
     * <CODE>serverIP</CODE> variable and the file name to the string stored in
     * the <CODE>filename</CODE> variable.  All other instance variables are set
     * to their defaults.
     *
     * @param serverIP    the TFTP server IP address to connect to when retrieving
     *                                    files.
     * @param filename    the name of the file to fetch from the TFTP server.
     */
    public TftpClient(String serverIP, String filename)
    {
        setVariables(serverIP, filename, DEFAULT_TFTP_SERVER_PORT, PACKET_RRQ,
                DEFAULT_MODE, DEFAULT_SOCKET_TIMEOUT, DEFAULT_NUMBER_OF_RETRIES);
    }

    /**
     * Class constructor that initializes the TFTP server IP to the
     * <CODE>serverIP</CODE> variable and the packet time out to the
     * <CODE>timeout</CODE> variable.  All other instance variables are set to
     * their defaults.
     * <P>
     * In particular:
     * <UL>
     * <LI> File name is set to the file <I>filename</I>.
     * </UL>
     *
     * @param serverIP    the TFTP server IP address to connect to when retrieving
     *                                    files.
     * @param timeout        the time out, in milliseconds, before a packet is
     *                                    re-tansmitted.
     */
    public TftpClient(String serverIP, int timeout)
    {
        setVariables(serverIP, DEFAULT_FILENAME, DEFAULT_TFTP_SERVER_PORT, PACKET_RRQ,
                DEFAULT_MODE, timeout, DEFAULT_NUMBER_OF_RETRIES);
    }

    /**
     * Class constructor that initializes the TFTP server IP to the
     * <CODE>serverIP</CODE> variable, the packet time out to the
     * <CODE>timeout</CODE>, and the maximum number of packet retransmitions to
     * <CODE>retries</CODE>.
     * <P>
     * In particular:
     * <UL>
     * <LI> File name is set to the file <I>filename</I>.
     * </UL>
     *
     * @param serverIP    the TFTP server IP address to connect to when retrieving
     *                                    files.
     * @param timeout        the time out, in milliseconds, before a packet is
     *                                    re-transmitted.
     * @param retries        the maximum number of packet retransmissions before
     *                                    sending an error TFTP packet.
     */
    public TftpClient(String serverIP, int timeout, int retries)
    {
        setVariables(serverIP, DEFAULT_FILENAME, DEFAULT_TFTP_SERVER_PORT, PACKET_RRQ,
                DEFAULT_MODE, timeout, retries);
    }

    /*
     * The setVariables method calls other set methods to correctly build an
     * instance.  Used by the constructors to initialize TFTPClient objects
     */
    private void setVariables(String serverIP, String filename, int remotePort,
                    byte opCode, String mode, int timeout, int retries)
    {
        byte[] blockNumber = {0,0};

        setServerIP(serverIP);
        setRemotePort(remotePort);
        setFileName(filename);
        setOpCode(opCode);
        setDataTransferMode(mode);
        setErrorCode(ERROR_NO_ERROR);
        setBlockNumber(blockNumber);
        setTimeout(timeout);
        setNumberOfRetries(retries);
        setDataLength(0);
        setTotalDataLength(0);
        setLocalPort(DEFAULT_UDP_PORT);
        setErrorMessage(ERROR_MESSAGES[8]);
    }

    // Public set methods.
    /**
     * Assigns the passed filename to the object's <CODE>fileName</CODE> field.
     *
     * @param fileName    the file name to retrieve from the TFTP server.
     */
    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    /**
     * Assigns the download mode according to the passed parameter.  This affects
     * the <CODE>mode</CODE> field.  Contains checks to avoid illegal options.
     * If an illegal mode value is passed, the value in <CODE>DEFAULT_MODE</CODE>
     * is used.
     *
     * @param mode    a string containing the download mode.
     */
    public void setDataTransferMode(String mode)
    {
        boolean defaultValue = false;

        // Checks to detect correct values
        if (! mode.equalsIgnoreCase(DOWNLOAD_NETASCII))
            if (! mode.equalsIgnoreCase(DOWNLOAD_OCTET))
                if (! mode.equalsIgnoreCase(DOWNLOAD_MAIL))
                    defaultValue = true;
        // Accept the passed value according to the boolean variable defaultValue
        if (defaultValue)
            this.mode = DEFAULT_MODE;
        else
            this.mode = mode;
    }

    /**
     * Assigns passed server IP to the <CODE>serverIP</CODE> field.
     * No checks are performed.
     *
     * @param serverIP    the server IP address to use.
     */
    public void setServerIP(String serverIP)
    {
        this.serverIP = serverIP;
    }

    /**
     * Assigns passed timeout, in milliseconds, to the <CODE>timeout</CODE> field.
     * If the value is negative then the number 0 is assigned.  This disables
     * socket timeout checking.
     *
     * @param timeout        an integer storing the timeout in milliseconds.
     */
    public void setTimeout(int timeout)
    {
        if (timeout >= 0)
            this.timeout = timeout;
        else
            this.timeout = 0;
    }

    /**
     * Assigns the passed number of retries to the <CODE>retries</CODE> field.
     * If the value is negative then the number 0 is assigned.  This will force
     * the client to send an error TFTP packet and close if an
     * acknowledgment/data packet is not received.
     *
     * @param retries        the maximum number of retransmissions that the client is
     *                        allowed to perform before sending an error packet.
     */
    public void setNumberOfRetries(int retries)
    {
        if (retries >= 0)
            this.retries = retries;
        else
            this.retries = 0;
    }


    // Private set methods.
    /*
     * Assigns the local port provided by the socket object. If passed value is
     * 0 or negative, the value of DEFAULT_UDP_PORT is used.
     */
    private void setLocalPort(int localPort)
    {
        if (localPort > 0)
            this.localPort = localPort;
        else
            this.localPort = DEFAULT_UDP_PORT;
    }

    /*
     * Assigns the remote port which is contained in the first packet sent by the
     * TFTP server.
     */
    private void setRemotePort(int remotePort)
    {
        if (remotePort > 0)
            this.remotePort = remotePort;
        else
            this.remotePort = DEFAULT_TFTP_SERVER_PORT;
    }

    /*
     * Assigns the number of bytes received to the dataLength field.  0 is used
     * if a negative value is passed.
     */
    private void setDataLength(int dataLength)
    {
        if (dataLength > 0)
            this.dataLength = dataLength;
        else
            this.dataLength = 0;
    }

    /*
     * Assigns the total number of bytes received to the totalDataLength field.
     * If a negative number is passed, 0 is stored.
     */
    private void setTotalDataLength(int totalDataLength)
    {
        if (totalDataLength > 0)
            this.totalDataLength = totalDataLength;
        else
            this.totalDataLength = 0;
    }

    /*
     * Assigns the passed byte value to the opCode byte array's second location.
     * If an illegal value is passed, the PACKET_RRQ value is set.
     */
    private void setOpCode(byte secondOpCodeByte)
    {
        this.opCode[0] = 0;

        switch (secondOpCodeByte)
        {
            case PACKET_RRQ:
            case PACKET_WRQ:
            case PACKET_DATA:
            case PACKET_ACK:
            case PACKET_ERROR:
                this.opCode[1] = secondOpCodeByte;
                break;
            default:
                this.opCode[1] = PACKET_RRQ;
        }
    }

    /*
     * Assigns the passed error code value to the errorCode byte array's second
     * location.  If an illegal value is passed, the ERROR_NO_ERROR value is set.
     */
    private void setErrorCode(byte secondErrorCodeByte)
    {
        this.errorCode[0] = 0;

        switch (secondErrorCodeByte)
        {
            case ERROR_NO_ERROR:
            case ERROR_NOT_DEFINED:
            case ERROR_FILE_NOT_FOUND:
            case ERROR_ACCESS_VIOLATION:
            case ERROR_DISK_FULL:
            case ERROR_ILLEGAL_TFTP_OPERATION:
            case ERROR_UNKNOWN_TRANSFER_ID:
            case ERROR_FILE_ALREADY_EXISTS:
            case ERROR_NO_SUCH_USER:
                this.errorCode[1] = secondErrorCodeByte;
                break;
            default:
                this.errorCode[1] = ERROR_NO_ERROR;
        }
    }

    /*
     * Initializes the blockNumber byte array.
     */
    private void setBlockNumber(byte[] blockNumber)
    {
        this.blockNumber = blockNumber;
    }

    /*
     * Assigns the passed message to the errorMessage String field.
     */
    private void setErrorMessage(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    /*
     * Assigns the integer to the totalActualDataLength field.
     */
    private void setTotalActualDataLength(int actualLength)
    {
        if (actualLength >= 0)
            this.totalActualDataLength = actualLength;
        else
            this.totalActualDataLength = 0;
    }
    // End of set methods.

    // Public get methods.
    /**
     * Returns the file name stored in the <CODE>fileName</CODE> field.
     *
     * @return    the file name to fetch from the TFTP server.
     */
    public String getFileName()
    {
        return fileName;
    }

    /**
     * Returns the data download mode stored in the <CODE>mode</CODE> field.
     *
     * @return    the transfer mode to used during file transfers.
     */
    public String getDataTransferMode()
    {
        return mode;
    }

    /**
     * Returns the server IP address stored in the <CODE>serverIP</CODE> field.
     *
     * @return    the TFTP server IP address.
     */
    public String getServerIP()
    {
        return serverIP;
    }

    /**
     * Returns the error message stored in the <CODE>errorMessage</CODE> field.
     *
     * @return    the error message.
     */
    public String getErrorMessage()
    {
        return errorMessage;
    }

    /**
     * Returns the local socket port stored in the <CODE>localPort</CODE> field.
     *
     * @return    the local port number.
     */
    public int getLocalPort()
    {
        return localPort;
    }

    /**
     * Returns the remote socket port stored in the <CODE>remotePort</CODE> field.
     *
     * @return    the remote port number.
     */
    public int getRemotePort()
    {
        return remotePort;
    }

    /**
     * Returns the socket time out stored in the <CODE>timeout</CODE> field.
     *
     * @return    the time out.
     */
    public int getTimeout()
    {
        return timeout;
    }

    /**
     * Returns the number of retries stored in the <CODE>retries</CODE> field.
     *
     * @return     the maximum number of retransmissions.
     */
    public int getNumberOfRetries()
    {
        return retries;
    }

    /**
     * Returns the <CODE>errorCode</CODE> byte array.
     *
     * @return    the error code byte array.
     */
    public byte[] getErrorCode()
    {
        return errorCode;
    }

    /**
     * Returns the value stored in the <CODE>totalDataLength</CODE>.
     *
     * @return    the size of the fetched file as an integer.
     */
    public int getFetchedDataLength()
    {
        return totalDataLength;
    }
 
    // End of public get methods

    // Private get methods
    /*
     * Returns the amount of received byte data stored in the dataLength field.
     */
    private int getDataLength()
    {
        return dataLength;
    }

    /*
     * Returns the opCode byte array second location.
     */
    private byte getOpCode()
    {
        return opCode[1];
    }

    /*
     * Returns the blockNumber byte array.
     */
    private byte[] getBlockNumber()
    {
        return blockNumber;
    }

    /*
     * Returns the Actual number of bytes sent by the tftp server.
     */
    private int getActualDownloadedBytes()
    {
        return  totalActualDataLength;
    }
    // End of get methods.

    /*
     * Try to create a new socket, set the time out and return local port number.
     */
    private int initializeSocket() throws TFTPClientException
    {
        // If there are any problems throw exception
        try
        {
            socket = new DatagramSocket(localPort,InetAddress.getByName("127.0.0.1"));
            socket.setSoTimeout(getTimeout());
            return socket.getLocalPort();
        }
        catch(SocketException socketException)
        {
            if (socket != null && ! socket.isClosed())
                socket.close();
            throw new TFTPClientException("An error occurred while initialising the "
                                          + "UDP socket. The error is "
                                          + socketException.toString());
        }
        catch (Exception e) {
        	
        	e.printStackTrace();
        }
        return socket.getLocalPort();
    }

    /*
     * Create send and receive datagram packets.  Throw exception if server IP is
     * illegal.
     */
    private void initializePackets() throws TFTPClientException
    {
        byte[] buffer = new byte[MAX_PACKET_SIZE];

        try
        {
            sendPacket = new DatagramPacket(buffer, buffer.length,
                                                InetAddress.getByName(getServerIP()), getRemotePort());
            receivePacket = new DatagramPacket(buffer , buffer.length);
        }
        catch(UnknownHostException unknownHostException)
        {
            throw new TFTPClientException("IP address " + getServerIP()
                                          + " is invalid. The error is "
                                          + unknownHostException.toString());
        }
    }

    /*
     * This method builds a new TFTP packet by adding the appropriate fields
     * to the tftpSendPacket byte array.  Returns true if successful,
     * false if not.
     */
    private boolean buildTftpPacket()
    {
        Byte errorCode;

        tftpSendPacket.reset();

        switch (getOpCode())
        {
            case PACKET_RRQ:
            case PACKET_WRQ:
                tftpSendPacket.write(opCode, 0, opCode.length);
                tftpSendPacket.write(getFileName().getBytes(), 0,
                                     getFileName().getBytes().length);
                tftpSendPacket.write(PACKET_DATA_TERMINATOR);
                tftpSendPacket.write(getDataTransferMode().getBytes(), 0,
                                     getDataTransferMode().getBytes().length);
                tftpSendPacket.write(PACKET_DATA_TERMINATOR);
                return true;
            case PACKET_ACK:
                tftpSendPacket.write(opCode, 0, opCode.length);
                tftpSendPacket.write(blockNumber, 0, blockNumber.length);
                return true;
            case PACKET_DATA:
                tftpSendPacket.write(opCode, 0, opCode.length);
                tftpSendPacket.write(blockNumber, 0, blockNumber.length);
                tftpSendPacket.write(data, 0, data.length);
                return true;
            case PACKET_ERROR:
                tftpSendPacket.write(opCode, 0, opCode.length);
                tftpSendPacket.write(getErrorCode(), 0, getErrorCode().length);
                errorCode = new Byte(getErrorCode()[1]);
                tftpSendPacket.write(ERROR_MESSAGES[errorCode.intValue()].getBytes(),
                          0, ERROR_MESSAGES[errorCode.intValue()].getBytes().length);
                tftpSendPacket.write(PACKET_DATA_TERMINATOR);
                return true;
            default:
                return false;
        }
    }

    /*
     * The method parses a tftp packet, disassembles it into its components
     * and returns the opcode.
     */
    private int parseTftpPacket()
    {
        String errorMessage;
        byte[] blockNumber = new byte[2];
        byte[] oldBlockNumber;

        tftpReceivedPacket = new ByteArrayInputStream(receivePacket.getData(),
                                                      0, receivePacket.getLength());
        tftpReceivedPacket.read(opCode, 0, 2);

        switch (opCode[1])
        {
            case PACKET_ACK:
                tftpReceivedPacket.read(blockNumber, 0, 2);
                setBlockNumber(blockNumber);
                return PACKET_ACK;
            case PACKET_DATA:
                oldBlockNumber = getBlockNumber();
                tftpReceivedPacket.read(blockNumber, 0, 2);
                setBlockNumber(blockNumber);
                // set current packet length
                setDataLength(tftpReceivedPacket.available());
                // If block numbers are different then it is a new data packet
                if (! Arrays.equals(blockNumber, oldBlockNumber))
                {
                    // At this time these steps are unnecessary but may be useful in
                    // the future
                    data = new byte[getDataLength()];
                    tftpReceivedPacket.read(data, 0, data.length);
                    // Add new data chunk length to the old value
                    setTotalDataLength(getFetchedDataLength() + getDataLength());
                    // Add new data chunk length to the total overall download length
                    setTotalActualDataLength(getActualDownloadedBytes() + getDataLength());
                }
                else
                {
                    // If it is a retransmit just add its length to the overall download length
                    setTotalActualDataLength(getActualDownloadedBytes() + getDataLength());
                }
                return PACKET_DATA;
            case PACKET_ERROR:
                tftpReceivedPacket.read(errorCode, 0, 2);
                setDataLength(tftpReceivedPacket.available() - 1);
                data = new byte[getDataLength()];
                tftpReceivedPacket.read(data, 0, data.length);
                errorMessage = new String(data);
                setErrorMessage(errorMessage);
                return PACKET_ERROR;
            case PACKET_RRQ:
                return PACKET_RRQ;
            case PACKET_WRQ:
                return PACKET_WRQ;
            default:
                return PACKET_UNDEFINED;
        }
    } // End parseTftpPacket()
    

    /*
     * This method tries to send the TFTP packet over UDP.  If it cannot
     * an exception is thrown.
     */
    private void sendTftpPacket() throws TFTPClientException
    {
        byte[] data = tftpSendPacket.toByteArray();

        try
        {
            sendPacket.setData(data, 0, data.length);
            //if (sendPacket.getPort() != getRemotePort())
            //    sendPacket.setPort(getRemotePort());
            socket.send(sendPacket);
        }
        catch(IOException exception)
        {
            if (socket != null && ! socket.isClosed())
                socket.close();
            throw new TFTPClientException("Could not send packet.  Error is "
                                          + exception.toString());
        }
    } // End sendTftpPacket()

    /*
     * This method tries to read a TFTP packet from the network.  If a socket
     * time out occurs this method returns false without reading any packet
     */
    private boolean readTftpPacket() throws TFTPClientException
    {
        boolean packetRead = false;

        try
        {
            socket.receive(receivePacket);
            System.out.println("Received data on address " + socket.getLocalAddress()  + ", port " + socket.getLocalPort() + ", receive packet port " + receivePacket.getPort());
            if (getRemotePort() != receivePacket.getPort())
               setRemotePort(receivePacket.getPort());
            System.out.println("Received packet size = " + receivePacket.getLength());
            packetRead = true;
        }
        catch(SocketTimeoutException socketTimeoutException) { }
        catch(IOException exception)
        {
            // If problems raise an exception
            if (socket != null && ! socket.isClosed())
                socket.close();
            throw new TFTPClientException("Could not read packet.  Error is "
                                          + exception.toString());
        }
        return packetRead;
        
    } // End readTftpPacket()

    /**
     * Downloads the file specified by the <CODE>filename</CODE> field from the
     * TFTP server whose address is obtained from the <CODE>serverIP</CODE>
     * field.  Method returns an integer to flag success or not.  
     *
     * @return     0 if everything was successful, 1 if an error packet has been
     *            received, and 2 if the maximum number of retransmits has been
     *            reached and an error packet has been sent.
     *
     * @throws    TFTPClientException        if there are errors during IO.
     */
   public int getFile(String outputFileName) throws TFTPClientException, FileNotFoundException, IOException {

      boolean done = false;
      int returnCode = 1;
      int retriesCounter = 0;
      int opcode;

      try {
         
    	 // Initialize socket and packets
         setLocalPort(initializeSocket());
         initializePackets();
         receivedData.reset();       // Clear any previous received data

         File outputFile = new File(outputFileName);
      int outputFileTotalBytes = 0;
      int outputFileBytesReceived = 0;
      byte[] outputFileBuffer = new byte[MAX_DATA_FIELD_SIZE];
         FileOutputStream outputFileStream = new FileOutputStream(outputFile);
         if (!outputFile.exists()) {
            outputFile.createNewFile();
         }
      
         System.out.println("Trying to fetch file " + getFileName() + " from server " + getServerIP());
         // Set op code to read request and set remote port to 69
         setOpCode(PACKET_RRQ);
         setRemotePort(DEFAULT_TFTP_SERVER_PORT);
         setErrorMessage(ERROR_MESSAGES[8]);
         // Build TFTP Read request packet
         if (buildTftpPacket()) {

            sendTftpPacket();
            // Loop until we are finished
            while (!done) {
            
               // Try to read packet from the network
               if (readTftpPacket()) {
                  // If successful, parse packet
                  retriesCounter = 1;
                  opcode = parseTftpPacket();
                  switch (opcode) {
                            
                     case PACKET_DATA: 
                        outputFileStream.write(data);
                    	setOpCode(PACKET_ACK);  // Block number is set when data packet is parsed
                        if (buildTftpPacket()) {
                           sendTftpPacket();
                        }
                     
                        // TODO - Logic is flawed if file is exact multiple of data field size
                        done = (getDataLength() != MAX_DATA_FIELD_SIZE);
                        if (done) returnCode = 0;
                        break;
                     case PACKET_ERROR:
                        // Error packet has been received
                        done = true;
                        returnCode = 1;
                        break;
                     default:
                	    done = true;
                        returnCode = 1;
                        System.out.println("Put file failure. Unexpected opcode " + opcode + " received.");
                        break;
                  } // End opcode switch
               } // End if read TFTP packet
               else {
                  // A socket timeout has occurred.  Need to resend last packet
                  // First check that the client has not exceeded the number of
                  // retries
                  if (retriesCounter < getNumberOfRetries()) {
                     sendTftpPacket();
                     retriesCounter++;
                  }
                  else {
                     // If number of retries has been exceeded send an error packet
                     setOpCode(PACKET_ERROR);
                     setErrorCode(ERROR_NOT_DEFINED);
                     if (buildTftpPacket()) {
                        sendTftpPacket();
                        // After sending the error packet terminate
                        done = true;
                        returnCode = 2;
                     }
                  } // End if exceeded max retires 
                  break;
               } // End if didn't failed to read TFTP packet
            } // End while not done
         } // End if built packet 

         // Close socket.  Ignore any thrown exceptions
         socket.close();
         outputFileStream.flush();
         outputFileStream.close();
        
      }
      catch (Exception e) {
         e.printStackTrace();
      }
      
   return returnCode;
 
} // End getFile()


/**
 * Sends the file specified by the <CODE>filename</CODE> field to the
 * TFTP server whose address is obtained from the <CODE>serverIP</CODE>
 * field.  Method returns an integer to flag success or not.  
 *
 * @return     0 if everything was successful, 1 if an error packet has been
 *            received, and 2 if the maximum number of retransmits has been
 *            reached and an error packet has been sent.
 *
 * @throws    TFTPClientException        if there are errors during IO.
 */
public int putFile(String inputFileName) throws TFTPClientException, FileNotFoundException
{

   boolean done = false;
   int returnCode = 1;
   int retriesCounter = 1;
   int opcode;
   int blockCount = 0;
   byte[] blockNumber = new byte[2];
   
   // Initialize socket and packets
   setLocalPort(initializeSocket());
   initializePackets();
   receivedData.reset();  // Clear any previous received data
   
   int inputFileTotalBytes = 0;
   int inputFileBytesRead = 0;
   byte[] inputFileBuffer = new byte[MAX_DATA_FIELD_SIZE];
   FileInputStream inputFileStream = new FileInputStream(inputFileName);

   System.out.println("Trying to send file " + inputFileName + " to server " + getServerIP());
   
   setOpCode(PACKET_WRQ);
   setRemotePort(DEFAULT_TFTP_SERVER_PORT);
   setErrorMessage(ERROR_MESSAGES[8]);
    
   // Build TFTP Write request packet
   if (buildTftpPacket()) {
   
      sendTftpPacket();
      while (!done) {
    	  
         if (readTftpPacket()) {
            
            retriesCounter = 1;
            opcode = parseTftpPacket(); 
            System.out.println("Read TFTP Packet. Opcode = " + opcode);
            switch (opcode) {
               case PACKET_ACK:
                  try {
                     inputFileBytesRead = inputFileStream.read(inputFileBuffer);
                     System.out.println("Input files read = " + inputFileBytesRead);
                  }
                  catch (Exception e) {
                	  e.printStackTrace();
                  }
            	  if (inputFileBytesRead != -1) {
            	     inputFileTotalBytes += inputFileBytesRead;
            		 data = Arrays.copyOf(inputFileBuffer, inputFileBytesRead);
                     setOpCode(PACKET_DATA);
                     blockCount++;  
                     blockNumber[1] = (byte)(blockCount & 0x00FF);
                     blockNumber[0] = (byte)((blockCount & 0x00FF)>>8);
                     setBlockNumber(blockNumber);
                     if (buildTftpPacket()) {
                	    sendTftpPacket();
                	    System.out.println("Sent data packet");
                     }
            	  } // End if bytes read
            	  else {
                     done = true;
                     returnCode = 0;
                  }
                  break;
               case PACKET_ERROR:
                  done = true;
                  break;
               default:
            	  done = true;
                  System.out.println("Put file failure. Unexpected opcode " + opcode + " received.");
                  break;
            } // End opcode switch
         } // End if read TFTP packet
         else {
            // A socket timeout has occurred.  Need to re-send last packet
            // First check that the client has not exceeded the number of
            // retries
        	System.out.println("Failed to read TFTP packet");
            if (retriesCounter < getNumberOfRetries()) {
               // Send packet
               sendTftpPacket();
               retriesCounter++;
            }
            else {
               // If number of retries has been exceeded send an error packet
               setOpCode(PACKET_ERROR);
               setErrorCode(ERROR_NOT_DEFINED);
               if (buildTftpPacket()) {
                  sendTftpPacket();
                  // After sending the error packet terminate
                  
                  returnCode = 2;
               }
               done = true;
           } // End if exceeded max retures
         } // End if didn't read TFTP packet
      } // End while not finished
   } // End if built WRQ packet
   try {
       
      // Close socket.  Ignore any thrown exceptions
      socket.close();
      inputFileStream.close();
    
   }
   catch (Exception e) {}
    
   return returnCode;
 
} // End putFile()


} // End class

