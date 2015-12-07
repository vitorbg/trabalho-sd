/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalhosd4.servidor;

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
public class ThreadRecebeMultiCast implements Runnable {

    public static DatagramPacket outPacket = null;
    public static DatagramPacket inPacket = null;
    public static MulticastSocket multicastSocket = null;
    public static byte[] inBuf = new byte[256];
    public static byte[] outBuf;
    public static final int PORT = 8888;
    public static String ipInstancia;
    public static String ipInstanciaDescoberta;

    public void run() {
        try {
            //Prepare to join multicast group
            multicastSocket = new MulticastSocket(8888);
            InetAddress address = InetAddress.getByName("224.2.2.3");
            multicastSocket.joinGroup(address);
//            multicastSocket.setSoTimeout(10000);

            while (!MainServer.fimVotacao) {
                try {
                    inPacket = new DatagramPacket(inBuf, inBuf.length);
                    multicastSocket.receive(inPacket);
                    String msg = new String(inBuf, 0, inPacket.getLength());
                    if (msg.charAt(0) == 's') {
                        String sID = msg.substring(2, 3);
                        System.out.println("Processo ID: " + sID + " votou s.");
                        Votacao.computaVoto(Integer.valueOf(sID));
//                        return;
                    }

                } catch (SocketTimeoutException e) {
                    System.out.println("Timeout reached!!! " + e);
                }
            }
        } catch (IOException ioe) {
            System.out.println(ioe);
        }
    }

    public static void enviaMulticast(String msg) {
        DatagramSocket socket = null;
        DatagramPacket outPacket = null;
        byte[] outBuf;
        final int PORT = 8888;
        try {
            socket = new DatagramSocket();

            outBuf = msg.getBytes();

            //Send to multicast IP address and port
            InetAddress address = InetAddress.getByName("224.2.2.3");
            outPacket = new DatagramPacket(outBuf, outBuf.length, address, PORT);
            socket.send(outPacket);
            System.out.println("Pedido de acesso à região crítica: " + msg);
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ie) {
            }
        } catch (IOException ioe) {
            System.out.println(ioe);
        }
    }

}
