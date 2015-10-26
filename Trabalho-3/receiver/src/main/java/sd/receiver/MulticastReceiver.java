/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sd.receiver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;

/**
 *
 * @author vitor
 */
public class MulticastReceiver {

    public static InetAddress ipOrigem;
    public static DatagramSocket twosocket = null;
    public static DatagramPacket outPacket = null;
    public static DatagramPacket inPacket = null;

    public static byte[] outBuf;
    public static final int PORT = 8888;

    public static void main(String[] args) throws IOException {

        MulticastSocket socket = null;
        byte[] inBuf = new byte[256];
        try {
            //Prepare to join multicast group
            socket = new MulticastSocket(8888);
            InetAddress address = InetAddress.getByName("224.2.2.3");
            socket.joinGroup(address);

            //while (true) {
            socket.setSoTimeout(10000);
            try {
                inPacket = new DatagramPacket(inBuf, inBuf.length);
                socket.receive(inPacket);
                String msg = new String(inBuf, 0, inPacket.getLength());
                System.out.println("From " + inPacket.getAddress() + " Msg : " + msg);

                ipOrigem = inPacket.getAddress();
            } catch (SocketTimeoutException e) {
                System.out.println("Timeout reached!!! " + e);
                enviaMulticast();
            }

            //}
        } catch (IOException ioe) {
            System.out.println(ioe);
        }

        System.out.println("IP DESCOBERTO: " + ipOrigem);

        String msg = "This is multicast! ";
        outBuf = msg.getBytes();
        outPacket = new DatagramPacket(outBuf, outBuf.length, ipOrigem, PORT);
        socket.send(outPacket);

    }

    public static void enviaMulticast() {
        DatagramSocket socket = null;
        DatagramPacket outPacket = null;
        byte[] outBuf;
        final int PORT = 8888;

        try {
            socket = new DatagramSocket();
            long counter = 0;
            String msg;

//            while (true) {
                msg = "This is multicast! " + counter;
                counter++;
                outBuf = msg.getBytes();

                //Send to multicast IP address and port
                InetAddress address = InetAddress.getByName("224.2.2.3");
                outPacket = new DatagramPacket(outBuf, outBuf.length, address, PORT);

                socket.send(outPacket);

                System.out.println("Server sends : " + msg);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ie) {
                }
//            }
        } catch (IOException ioe) {
            System.out.println(ioe);
        }
    }

}
