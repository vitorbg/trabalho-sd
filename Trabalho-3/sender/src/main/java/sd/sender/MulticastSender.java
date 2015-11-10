/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sd.sender;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 *
 * @author vitor
 */
public class MulticastSender {

    public static void main(String[] args) throws SocketException, IOException {
        DatagramSocket serverSocket = new DatagramSocket(9876);
        byte[] receiveData = new byte[1024];
        byte[] sendData = new byte[1024];

        while (true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            String sentence = new String(receivePacket.getData());
            System.out.println("RECEIVED: " + sentence);
            InetAddress IPAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();
            String capitalizedSentence = sentence.toUpperCase();
            sendData = capitalizedSentence.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
            serverSocket.send(sendPacket);
        }

//        DatagramSocket socket = null;
//        DatagramPacket outPacket = null;
//        byte[] outBuf;
//        final int PORT = 8888;
//
//        try {
//            socket = new DatagramSocket();
//            long counter = 0;
//            String msg;
//
//            while (true) {
//                msg = "This is multicast! " + counter;
//                counter++;
//                outBuf = msg.getBytes();
//
//                //Send to multicast IP address and port
//                InetAddress address = InetAddress.getByName("224.2.2.3");
//                outPacket = new DatagramPacket(outBuf, outBuf.length, address, PORT);
//
//                socket.send(outPacket);
//
//                System.out.println("Server sends : " + msg);
//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException ie) {
//                }
//            }
//        } catch (IOException ioe) {
//            System.out.println(ioe);
//        }
//    }
    }
}
