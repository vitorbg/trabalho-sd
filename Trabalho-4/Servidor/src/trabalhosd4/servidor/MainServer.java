/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalhosd4.servidor;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author vitor
 */
public class MainServer implements Runnable {

    private static int porta = 1024;
    private Socket connect;
    public static boolean fimPrograma = false;
    public static boolean fimVotacao = false;
    public static Thread threadRecebeMultiCast = new Thread(new ThreadRecebeMultiCast());

    public MainServer(Socket connect) {
        this.connect = connect;
        System.out.println("[Servidor] Cliente conectado: " + connect.getInetAddress().getHostAddress() + ".");
        System.out.println("[Servidor] Cliente conectado: " + connect.getInetAddress().getCanonicalHostName() + ".");
    }

    public static void main(String[] args) {
        povoaClientes();
        threadRecebeMultiCast.start();
        System.out.println("Sistemas Distribuidos: Projeto 4");
        System.out.println("[Servidor] Iniciando..");
        try {
            ServerSocket serverConnect = new ServerSocket(porta);
            System.out.println("[Servidor] Escutando a porta " + porta + ".");
            while (true) {
                MainServer server = new MainServer(serverConnect.accept());
                Thread threadRunner = new Thread(server);
                threadRunner.start();
            }
        } catch (IOException e) {
            System.err.println("Erro: " + e);
        }
    }

    @Override
    public void run() {
        BufferedReader in = null;
        PrintWriter out = null;
        BufferedOutputStream dOut = null;
        String fileRequested = null;
        String input2;
        try {
            in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
            out = new PrintWriter(connect.getOutputStream());
//            dOut = new BufferedOutputStream(connect.getOutputStream());

            String input = in.readLine();

            System.out.println("CHEGOU: " + input);
            System.out.println(" ");

            //Responde saldo
            if (input.charAt(0) == '1') {

                String a = input.substring(2);
//                System.out.println("VALOR DE A = " + a);
                int id = Integer.valueOf(a);
                for (int i = 0; i < RegiaoCritica.clientes.size(); i++) {
                    if (RegiaoCritica.clientes.get(i).getId() == id) {
                        out.println(RegiaoCritica.clientes.get(i).getSaldo());
                        out.flush();
                        out.close();
                    }
                }
            }

            //Envia multicast solicitando acesso a região crítica
            if (input.charAt(0) == '2') {
                String sID = input.substring(2, 3);
                String sValor = input.substring(5, input.length());

                int id = Integer.valueOf(sID);
                double valor = Double.valueOf(sValor);

                enviaMulticast(sID + "|erc");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ie) {
                }

                if (RegiaoCritica.ID == 0) {
//                if (Votacao.retornaVencedor() == id) {
                    System.out.println("*****************");
                    System.out.println("PEGA A REGIAO CRITICA ID: " + id);
                    System.out.println("*****************");
                    out.println("1");
                    out.flush();
                    out.close();

                    RegiaoCritica.ID = id;
                    Votacao.reiniciarVotacao();
                    RegiaoCritica.adicionarSaldo(id, valor);
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ie) {
                    }

                    enviaMulticast(id + "|src");
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ie) {
                    }
                    if (Votacao.retornaVencedor() == id) {

                    }
                    System.out.println("*****************");
                    System.out.println("SAI DA REGIAO CRITICA ID: " + id);
                    System.out.println("*****************");

                    RegiaoCritica.ID = 0;

                } else {
                    System.out.println("---------------------------------------");
                    System.out.println("Regiao Critica em Uso." + "Quem tentou acessa-la: " + id);
                    System.out.println("---------------------------------------");

//                    RegiaoCritica.filaDeAcesso.add(id);
                    while (RegiaoCritica.ID != 0) {
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException ie) {
                        }
                        System.out.println("---------------------------------------");
                        System.out.println("ESPERANDO !!!! ID: " + id);
                        System.out.println("---------------------------------------");
                    }
                    System.out.println("*****************");
                    System.out.println("PEGA A REGIAO CRITICA ID: " + id);
                    System.out.println("*****************");

                    RegiaoCritica.ID = id;
                    Votacao.reiniciarVotacao();
                    RegiaoCritica.adicionarSaldo(id, valor);
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ie) {
                    }
                    System.out.println("*****************");
                    System.out.println("SAI DA REGIAO CRITICA ID: " + id);
                    System.out.println("*****************");
                    RegiaoCritica.ID = 0;
                }
            }

            mostraTodosCliente();

        } catch (IOException ioe) {
            System.err.println("Server Error: " + ioe);
            fimPrograma = true;
        } finally {

//            close(in); //fecha character input stream
//            close(out); //fecha character output stream
//            close(dOut); //fecha binary output stream
//            close(connect); //fecha socket connection
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

    public static void mostraTodosCliente() {
        System.out.println("trabalhosd4.servidor.MainServer.mostraTodosCliente()");
        for (int i = 0; i < RegiaoCritica.clientes.size(); i++) {
            System.out.println("ID: " + RegiaoCritica.clientes.get(i).getId()
                    + " Saldo: "
                    + RegiaoCritica.clientes.get(i).getSaldo()
            );

        }
        System.out.println("-----------------");
    }

    public static void povoaClientes() {
        Cliente cliente = new Cliente(1, 400, " ");
        Cliente cliente2 = new Cliente(2, 400, " ");
        Cliente cliente3 = new Cliente(3, 400, " ");
        Cliente cliente4 = new Cliente(4, 400, " ");

        RegiaoCritica.clientes.add(cliente);
        RegiaoCritica.clientes.add(cliente2);
        RegiaoCritica.clientes.add(cliente3);
        RegiaoCritica.clientes.add(cliente4);
    }
}
