
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor. 
 */
/**
 *
 * @author vitor
 */
public class TMulticastReceiver implements Runnable {

    @Override
    public void run() {
        while (!Main.instanciaDescoberta) {
            Main.recebeMulticast();
        }
    }
}
