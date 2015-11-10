/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.StringTokenizer;

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
    public static ArrayList<Variavel> valoresInstanciaDescoberta = new ArrayList<>();
    public static Thread multicastThreadReceiver = new Thread(new TMulticastReceiver());
    public static Thread multicastThreadSender = new Thread(new TMulticastSender());
    public static Thread udpReceiver = new Thread(new TUDPReceiver());

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnknownHostException, SocketException, IOException {
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
            if (args[i] == "Max") {
                var.funcaoAgregavel = new Max();
            }
            if (args[i] == "Min") {
                var.funcaoAgregavel = new Min();
            }
            if (args[i] == "Media") {
                var.funcaoAgregavel = new Media();
            }

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
            msg = msg + "|";
            msg = msg + String.valueOf(valores.get(i).valor);
            msg = msg + "|";
        }
        System.out.println(msg);

        for (i = 0; i < qtdIteracoes; i++) {
            enviaMSG(msg);
        }

        fimPrograma = true;

    }

    public static void recebeMSG() throws SocketException, IOException {

        String clientSentence;
        String capitalizedSentence = null;

        ServerSocket welcomeSocket = new ServerSocket(8889);
        Socket connectionSocket = welcomeSocket.accept();
        while (true) {
            BufferedReader inFromClient
                    = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

            DataOutputStream outToClient
                    = new DataOutputStream(connectionSocket.getOutputStream());

            //Leitura da String que virá do Cliente
            clientSentence = inFromClient.readLine();

            System.out.println("Chegou da instancia " + ipInstanciaDescoberta + " = " + clientSentence);

            StringTokenizer parse = new StringTokenizer(clientSentence, "|");
            while (parse.hasMoreElements()) {
                String var = (String) parse.nextElement();
                String valor = (String) parse.nextElement();

                for (int i = 0; i < valores.size(); i++) {
                    if (var.equals(valores.get(i).nome)) {
                        valores.get(i).valor
                                = valores.get(i).funcaoAgregavel.computa(
                                        valores.get(i).valor, Double.valueOf(valor));
                        System.out.println("Variavel Local " + valores.get(i).nome);
                        System.out.println("Novo Valor " + valores.get(i).valor);
                    }

                }

            }

            capitalizedSentence = "MSG DO SERVIDOR" + "\n";
            //Responde ao cliente
            outToClient.writeBytes(capitalizedSentence);
        }
    }

    public static void enviaMSG(String msg) throws SocketException, IOException {
        //Declaro a Stream de saida de dados  
        PrintStream ps = null;

        String sentence;
        String modifiedSentence;
        String ipTratado = ipInstanciaDescoberta.substring(1);
        Socket clientSocket = new Socket(ipTratado, 8889);

        DataOutputStream outToServer
                = new DataOutputStream(clientSocket.getOutputStream());

        BufferedReader inFromServer
                = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        outToServer.writeBytes(msg + "\n");

        modifiedSentence = inFromServer.readLine();

        System.out.println("FROM SERVER: " + modifiedSentence);

        clientSocket.close();

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
                    if (iface.getName().equals("wlan0") || iface.getName().equals("eth0")) {
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
