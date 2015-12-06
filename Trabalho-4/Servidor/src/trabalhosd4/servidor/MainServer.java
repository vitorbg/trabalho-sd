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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author vitor
 */
public class MainServer implements Runnable {

    private static int porta = 1024;
    private Socket connect;
    public static ArrayList<Cliente> clientes = new ArrayList<Cliente>();

    public MainServer(Socket connect) {
        this.connect = connect;
        System.out.println("[Servidor] Cliente conectado: " + connect.getInetAddress().getHostAddress() + ".");
        System.out.println("[Servidor] Cliente conectado: " + connect.getInetAddress().getCanonicalHostName() + ".");
    }

    public static void main(String[] args) {
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
            dOut = new BufferedOutputStream(connect.getOutputStream());

            String input = in.readLine();

            System.out.println("CHEGOU: " + input);
            System.out.println(" ");
            Cliente cliente = new Cliente();
            cliente.setId(input);
            clientes.add(cliente);

            mostraTodosCliente();
            
        } catch (IOException ioe) {
            System.err.println("Server Error: " + ioe);
        } finally {

//            GrafoPersist.mostraTodos();
//            close(in); //fecha character input stream
//            close(out); //fecha character output stream
//            close(dOut); //fecha binary output stream
//            close(connect); //fecha socket connection
        }
    }

    public static void mostraTodosCliente() {
        System.out.println("trabalhosd4.servidor.MainServer.mostraTodosCliente()");
        for (int i = 0; i < clientes.size(); i++) {
            System.out.println(clientes.get(i).getId());
            System.out.println(clientes.get(i).getSaldo());
            System.out.println("-------------------");
        }
    }
}
