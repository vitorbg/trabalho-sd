/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 *
 * @author vitor
 */
public class Main {

    public static boolean instanciaDescoberta = false;
    public static boolean multicastEnviado = false;
    public static InetAddress ipOrigem;
    public static DatagramPacket outPacket = null;
    public static DatagramPacket inPacket = null;
    public static MulticastSocket multicastSocket = null;
    public static byte[] inBuf = new byte[256];
    public static byte[] outBuf;
    public static final int PORT = 8888;
    //--
    public static int id;
    public static int qtdIteracoes = 0;
    public static ArrayList<Variavel> valores = new ArrayList<>();
    public static Thread multicastReceiver = new Thread(new TMulticastReceiver());
    public static Thread multicastSender = new Thread(new TMulticastSender());

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        id = Integer.valueOf(args[0]);
        qtdIteracoes = Integer.valueOf(args[1]);

        int k = 0; //Guarda o indice do array para a funcao de agregacao respectiva
        int j = 0; //Guarda o indice do array para receber o nome e valor
        int i = 2; //Indice no agrs

        while (i < args.length) {
            Variavel var = new Variavel();

            var.nome = args[i];
            i++;

            var.valor = Double.valueOf(args[i]);
            i++;

            var.funcaAgregacao = args[i];
            i++;

            valores.add(var);
        }

        System.out.println("ID: " + id);
        System.out.println("ITERACOES: " + qtdIteracoes);
        System.out.println(valores.get(0).nome);
        System.out.println(valores.get(0).valor);

    }

    public static void recebeMulticast() {
        try {
            //Prepare to join multicast group
            Main.multicastSocket = new MulticastSocket(8888);
            InetAddress address = InetAddress.getByName("224.2.2.3");
            Main.multicastSocket.joinGroup(address);
//            Main.multicastSocket.setSoTimeout(10000);
            try {
                Main.inPacket = new DatagramPacket(Main.inBuf, Main.inBuf.length);
                Main.multicastSocket.receive(Main.inPacket);
                String msg = new String(Main.inBuf, 0, Main.inPacket.getLength());
                System.out.println("From " + Main.inPacket.getAddress() + " Msg : " + msg);
                Main.ipOrigem = Main.inPacket.getAddress();
            } catch (SocketTimeoutException e) {
                System.out.println("Timeout reached!!! " + e);
            }
        } catch (IOException ioe) {
            System.out.println(ioe);
        }
    }

    public static void enviaMulticast() {
        DatagramSocket socket = null;
        DatagramPacket outPacket = null;
        byte[] outBuf;
        final int PORT = 8888;
        try {
            socket = new DatagramSocket();
            String msg;
            msg = getEnderecoComputador();
            outBuf = msg.getBytes();

            //Send to multicast IP address and port
            InetAddress address = InetAddress.getByName("224.2.2.3");
            outPacket = new DatagramPacket(outBuf, outBuf.length, address, PORT);
            socket.send(outPacket);
            System.out.println("Server sends : " + msg);
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ie) {
            }
        } catch (IOException ioe) {
            System.out.println(ioe);
        }
    }

    public static String getEnderecoComputador() {

        InetAddress addr = null;

        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {
            System.out.println(ex.getMessage());
        }

        return addr.getHostAddress();
    }

}
