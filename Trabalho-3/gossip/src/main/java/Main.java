/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 *
 * @author vitor
 */
public class Main {

    public static boolean instanciaDescoberta = false;
    public static boolean multicastRecebido = false;
    public static boolean fimPrograma = false;
    public static InetAddress ipOrigem;
    public static DatagramPacket outPacket = null;
    public static DatagramPacket inPacket = null;
    public static MulticastSocket multicastSocket = null;
    public static byte[] inBuf = new byte[256];
    public static byte[] outBuf;
    public static final int PORT = 8888;
    public static final int PORT_TWO = 8889;
    public static String ipInstancia;
    public static String ipInstanciaDescoberta;
    //--
    public static int id;
    public static int qtdIteracoes = 0;
    public static ArrayList<Variavel> valores = new ArrayList<>();
    public static Thread multicastThreadReceiver = new Thread(new TMulticastReceiver());
    public static Thread multicastThreadSender = new Thread(new TMulticastSender());
    public static Thread udpReceiver = new Thread(new TUDPReceiver());

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnknownHostException, SocketException {
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

        ipInstancia = prop_rede();

        multicastThreadReceiver.start();
        multicastThreadSender.start();
        udpReceiver.start();
        while (!instanciaDescoberta) {
            System.out.println("Tentando descobrir instancias...");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ie) {
            }
        }

        System.out.println("Instancia descoberta em: " + ipInstanciaDescoberta);
        System.out.println("Enviando msg");
        String msg = "";
        for (i = 0; i < valores.size(); i++) {
            msg = msg + (valores.get(i).nome);
            msg = " " + msg + String.valueOf(valores.get(i).valor);
        }
        System.out.println(msg);
        enviaMSG(msg);
        fimPrograma = true;

    }

    public static void recebeMSG() throws SocketException, IOException {

        DatagramSocket ds = new DatagramSocket(PORT_TWO);
        byte[] msg = new byte[256];
        DatagramPacket pkg = new DatagramPacket(msg, msg.length);
        ds.receive(pkg);
        String modifiedSentence = new String(pkg.getData());
        System.out.println("veio UDP: " + modifiedSentence);

    }

    public static void enviaMSG(String msg) throws SocketException {
        DatagramSocket socket = null;
        DatagramPacket outPacket = null;
        byte[] outBuf;
        try {
            socket = new DatagramSocket();
            outBuf = msg.getBytes();

            //Send to multicast IP address and port
            InetAddress address = InetAddress.getByName(ipInstanciaDescoberta);
            outPacket = new DatagramPacket(outBuf, outBuf.length, address, PORT_TWO);
            socket.send(outPacket);
            System.out.println("MSG ENVIADA: " + msg);
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ie) {
            }
        } catch (IOException ioe) {
            System.out.println(ioe);
        }

    }

    public static void recebeMulticast() {
        try {
            //Prepare to join multicast group
            multicastSocket = new MulticastSocket(8888);
            InetAddress address = InetAddress.getByName("224.2.2.3");
            multicastSocket.joinGroup(address);
            multicastSocket.setSoTimeout(10000);
            try {
                inPacket = new DatagramPacket(Main.inBuf, Main.inBuf.length);
                multicastSocket.receive(Main.inPacket);
                String msg = new String(Main.inBuf, 0, Main.inPacket.getLength());
                ipOrigem = Main.inPacket.getAddress();
                if (ipInstancia.equals(msg)) {
                    System.out.println("Pacote da própria instancia descartado.");
                } else {
                    System.out.println("From: " + Main.inPacket.getAddress() + " Msg: " + msg);
                    ipInstanciaDescoberta = msg;
                    instanciaDescoberta = true;

                }
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
            msg = ipInstancia;
            outBuf = msg.getBytes();

            //Send to multicast IP address and port
            InetAddress address = InetAddress.getByName("224.2.2.3");
            outPacket = new DatagramPacket(outBuf, outBuf.length, address, PORT);
            socket.send(outPacket);
            System.out.println("MSG ENVIADA: " + msg);
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ie) {
            }
        } catch (IOException ioe) {
            System.out.println(ioe);
        }
    }

    public static String prop_rede() throws UnknownHostException, SocketException {
        System.out.println("Obtendo Informacões da Rede desta Instancia");
        String IP = null;
        try {
            Enumeration ifaces = NetworkInterface.getNetworkInterfaces();

            while (ifaces.hasMoreElements()) {
                NetworkInterface iface = (NetworkInterface) ifaces.nextElement();

                System.out.println("Obtendo Informacões da interface: " + iface.getName());
                for (InterfaceAddress address : iface.getInterfaceAddresses()) {
                    System.out.println("IP........: " + address.getAddress().toString());
                    if (iface.getName().equals("wlan0")) {
                        IP = address.getAddress().toString();
                    }
                    Object bc = address.getBroadcast();
                    System.out.println("Broadcast.: " + bc);
                    System.out.println("Máscara...: " + address.getNetworkPrefixLength());

                }

            }

        } catch (SocketException ifaces) {
        }
        return IP;
    }

}
