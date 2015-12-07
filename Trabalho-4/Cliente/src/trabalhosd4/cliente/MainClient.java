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
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author vitor
 */
public class MainClient {

    private static final int PORTA = 1024;
    private static final String SERVIDOR = "127.0.0.1";
    private static Socket server;
    public static Thread threadRecebeMultiCast = new Thread(new ThreadRecebeMultiCast());
    public static boolean fimPrograma = false;
    public static boolean fimVotacao = false;
    public static int id;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {

        threadRecebeMultiCast.start();

        int op = 0;

        String entrada;
        double valor;
        Scanner scanner = new Scanner(System.in);
        Scanner scannerDois = new Scanner(System.in);
        Scanner scannerTres = new Scanner(System.in);

        System.out.print("Informe ID: ");
        id = scannerTres.nextInt();

        do {

            System.out.println("1 - Consultar no Servidor ");
            System.out.println("2 - Entrar na Região Crítica - Adicionar");
            System.out.println("3 - Entrar na Região Crítica - Resgatar");
            System.out.println("4 -");
            System.out.println("0 - SAIR");
            System.out.print("Operacao: ");
            op = scanner.nextInt();

            switch (op) {

                case 1: {
                    System.out.print("Consulta ");
                    enviarMensagem("1|" + id);
                    System.out.print("Saldo recebido do Servidor: " + recebeMensagem());
                    fechaSocket();
                    break;
                }
                case 2: {
                    System.out.println("Confirme para entrar da região crítica. ");
                    System.out.print("Valor a Adicionar: ");
                    valor = scannerDois.nextDouble();
                    MainClient.fimVotacao = false;
                    enviarMensagem("2|" + id + "|" + String.valueOf(valor));

                    break;
                }
                case 3: {
                    System.out.println("Confirme para entrar da região crítica. ");
                    System.out.print("Valor a Resgatar: ");
                    valor = scannerDois.nextDouble();
                    enviarMensagem("3|" + String.valueOf(valor));
                    break;
                }
            }

        } while (op != 0);

        fimPrograma = true;

    }

    public static void enviarMensagem(String msg) throws IOException {

        server = new Socket(InetAddress.getByName(SERVIDOR), PORTA);

        PrintWriter pw = new PrintWriter(server.getOutputStream());
        pw.println(msg);
        pw.println("");
        pw.flush();

    }

    public static String recebeMensagem() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(server.getInputStream()));
        String t;
        while ((t = br.readLine()) != null) {
            System.out.println(t);
        }
        br.close();
        return t;
    }

    public static void fechaSocket() throws IOException {
        server.close();
    }

}
