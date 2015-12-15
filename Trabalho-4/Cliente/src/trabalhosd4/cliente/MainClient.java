/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalhosd4.cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 *
 * @author vitor
 */
public class MainClient {

    public static ArrayList<Processo> processos = new ArrayList<>();
    private static final int PORTA = 1024;
    private static final int PORTA_TCP = 9010;
    private static final String SERVIDOR = "127.0.0.1";
    public static Thread threadRecebeMultiCast = new Thread(new ThreadRecebeMultiCast());
    public static boolean fimPrograma = false;
    public static boolean estaComRegiaoCritica = false;
    public static int id;
    public static final int QUANTIDADE_PROCESSOS = 2;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {

        threadRecebeMultiCast.start();

        int op = 0;
        int opDois = 0;

        String entrada;
        double valor;
        Scanner scanner = new Scanner(System.in);
        Scanner scannerDois = new Scanner(System.in);
        Scanner scannerTres = new Scanner(System.in);

        System.out.print("Informe ID: ");
        id = scannerTres.nextInt();

        int porta_socket = PORTA_TCP + id;
        System.out.println("PORTA " + porta_socket);
        ServerSocket serverConnect = new ServerSocket(porta_socket);
        System.out.println("Servidor iniciado na porta " + porta_socket);

        do {

            System.out.println("1 - Entrar na Região Crítica");
            System.out.println("2 - ");
            System.out.println("3 - ");
            System.out.println("4 - ");
            System.out.println("0 - SAIR");
            System.out.print("Operacao: ");
            op = scanner.nextInt();

            switch (op) {

                case 1: {
                    System.out.println("Confirmar para entrar na Região Crítica ");
                    System.out.println("Operacao: ");
                    opDois = scannerDois.nextInt();
                    enviaMulticast("erc|" + String.valueOf(id) + "|" + SERVIDOR + "|" + String.valueOf(porta_socket));

                    Socket server = serverConnect.accept();
                    BufferedReader br = new BufferedReader(new InputStreamReader(server.getInputStream()));
                    String t;
                    String idOutro = null;
                    int contador = 0;
                    boolean regiaoCriticaEmUso = false;
                    while (contador < QUANTIDADE_PROCESSOS - 1) {

//                        while ((t = br.readLine()) != null) {
                        t = br.readLine();
                        System.out.println("Permissao: " + t);
                        StringTokenizer parse = new StringTokenizer(t, "|");
                        String permissao = parse.nextToken();
                        idOutro = parse.nextToken();
                        System.out.println("-+----------------------");
                        System.out.println(permissao);
                        System.out.println(idOutro);

                        System.out.println("-+----------------------");
                        if (permissao.equals("n")) {
                            regiaoCriticaEmUso = true;
                        }
                        contador++;
                        System.out.println(contador);
                        br.close();
//                        }

                    }

                    if (regiaoCriticaEmUso) {
                        if (processos.isEmpty()) {
                            Processo proc = new Processo();
                            proc.id = Integer.valueOf(idOutro);
                            proc.dataEntrada = new Date();
                            MainClient.processos.add(proc);
                        }
                        System.out.println("Região Crítica em uso =/ ");
                    } else {

//                        if(regi)
                        MainClient.estaComRegiaoCritica = true;
                        System.out.println("Está na região Crítica " + MainClient.id);
                        try {
                            Thread.sleep(20000);
                        } catch (InterruptedException ie) {
                        }
                        System.out.println("Saiu da região Crítica " + MainClient.id);
                        MainClient.estaComRegiaoCritica = false;

                    }

                    break;
                }
                case 2: {
                }
                case 3: {
                }
            }

        } while (op != 0);

        fimPrograma = true;

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
//            System.out.println("Pedido de acesso à região crítica: " + msg);
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ie) {
            }
        } catch (IOException ioe) {
            System.out.println(ioe);
        }
    }

    public void recebeMulticast() {

        DatagramPacket outPacket = null;
        DatagramPacket inPacket = null;
        MulticastSocket multicastSocket = null;
        byte[] inBuf = new byte[256];
        byte[] outBuf;
        int PORT = 8888;

        try {
            //Prepare to join multicast group
            multicastSocket = new MulticastSocket(8888);
            InetAddress address = InetAddress.getByName("224.2.2.3");
            multicastSocket.joinGroup(address);
//            multicastSocket.setSoTimeout(10000);

            try {
                inPacket = new DatagramPacket(inBuf, inBuf.length);
                multicastSocket.receive(inPacket);
                String msg = new String(inBuf, 0, inPacket.getLength());

            } catch (SocketTimeoutException e) {
                System.out.println("Timeout reached!!! " + e);
            }

        } catch (IOException ioe) {
            System.out.println(ioe);
        }

    }

    public static void enviarMensagem(String msg, String endereco, int port) throws IOException {
        Socket s;

        s = new Socket(SERVIDOR, port);

        PrintWriter pw = new PrintWriter(s.getOutputStream());

        pw.println(msg);

        pw.println("");
        pw.flush();
        s.close();

    }

//    public static String recebeMensagem() throws IOException {
//        BufferedReader br = new BufferedReader(new InputStreamReader(server.getInputStream()));
//        String t;
//        while ((t = br.readLine()) != null) {
//            System.out.println(t);
//        }
//        br.close();
//        return t;
//    }
//
//    public static void fechaSocket() throws IOException {
//        server.close();
//    }
}
