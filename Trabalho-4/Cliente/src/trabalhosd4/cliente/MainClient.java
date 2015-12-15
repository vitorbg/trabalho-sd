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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 *
 * @author vitor
 */
public class MainClient {

    public static ArrayList<Processo> processos = new ArrayList<>();
    public static ArrayList<FilaRegiaoCritica> filaRegiaoCritica = new ArrayList<>();
    private static final int PORTA = 1024;
    private static final int PORTA_TCP = 9010;
    private static final String SERVIDOR = "127.0.0.1";
    public static Thread threadRecebeMultiCast = new Thread(new ThreadRecebeMultiCast());
    public static boolean fimPrograma = false;
    public static boolean estaComRegiaoCritica = false;
    public static int id;
    public static final int QUANTIDADE_PROCESSOS = 3;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        povoarRelogio();
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
                    enviaMulticast("erc|" + String.valueOf(id) + "|" + SERVIDOR + "|" + String.valueOf(porta_socket) + "|" + processos.get(id - 1).contador);

                    String idOutro = null;
                    int contador = 0;
                    boolean regiaoCriticaEmUso = false;
                    while (contador < QUANTIDADE_PROCESSOS - 1) {
                        Socket server = serverConnect.accept();
                        BufferedReader br = new BufferedReader(new InputStreamReader(server.getInputStream()));
                        String t;
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
                        server.close();
//                        }

                    }

                    Collections.sort(processos, new Comparator<Processo>() {
                        @Override
                        public int compare(Processo p1, Processo p2) {

                            if (p1.contador < p2.contador) {
                                return -1;
                            }
                            if (p1.contador == p2.contador) {
                                return 0;
                            } else {
                                return 1;
                            }

                        }
                    });
                    boolean usarRegiaoCritica = false;

                    if (regiaoCriticaEmUso) {

                        while (!usarRegiaoCritica) {

                            System.out.println("Região Crítica em uso =/ ");
                            Socket server = serverConnect.accept();
                            BufferedReader br = new BufferedReader(new InputStreamReader(server.getInputStream()));
                            String t;
                            t = br.readLine();

                            br.close();
                            server.close();
                            System.out.println("Chegou: " + t);
                            StringTokenizer parse = new StringTokenizer(t, "|");
                            String operacao = parse.nextToken();
                            String permissao = parse.nextToken();
                            String idOutroDois = parse.nextToken();
                            if (permissao.equals("s")) {
                                System.out.println("Está na região Crítica " + MainClient.id);
                                MainClient.estaComRegiaoCritica = true;
                                usarRegiaoCritica = true;
                                try {
                                    Thread.sleep(30000);
                                } catch (InterruptedException ie) {
                                }
                                System.out.println("Saiu da região Crítica " + MainClient.id);
                                MainClient.estaComRegiaoCritica = false;
                                for (int k = 0; k < filaRegiaoCritica.size(); k++) {
                                    if (k == 0) {
                                        enviarMensagem("src|s|" + MainClient.id, filaRegiaoCritica.get(k).endereco, filaRegiaoCritica.get(k).port);

                                    } else {
                                        enviarMensagem("src|n|" + MainClient.id, filaRegiaoCritica.get(k).endereco, filaRegiaoCritica.get(k).port);
                                    }
                                }
                                filaRegiaoCritica.clear();

                            }

                        }
                    } else {
                        MainClient.estaComRegiaoCritica = true;
                        System.out.println("Está na região Crítica " + MainClient.id);
                        try {
                            Thread.sleep(30000);
                        } catch (InterruptedException ie) {
                        }
                        System.out.println("Saiu da região Crítica " + MainClient.id);
                        MainClient.estaComRegiaoCritica = false;

                        for (int k = 0; k < filaRegiaoCritica.size(); k++) {
                            if (k == 0) {
                                enviarMensagem("src|s|" + MainClient.id, filaRegiaoCritica.get(k).endereco, filaRegiaoCritica.get(k).port);
                            } else {
                                enviarMensagem("src|n|" + MainClient.id, filaRegiaoCritica.get(k).endereco, filaRegiaoCritica.get(k).port);
                            }

                        }
                        filaRegiaoCritica.clear();
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

    public static void povoarRelogio() {

        for (int i = 0; i < QUANTIDADE_PROCESSOS; i++) {
            Processo p = new Processo();
            p.id = i + 1;
            p.contador = 0;
            processos.add(p);
        }
        for (int j = 0; j < QUANTIDADE_PROCESSOS; j++) {
            System.out.println(processos.get(j).id);
            System.out.println(processos.get(j).contador);
        }
    }
}
