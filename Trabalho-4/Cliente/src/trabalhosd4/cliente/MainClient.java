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

/**
 *
 * @author vitor
 */
public class MainClient {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {

        Socket server;

//        String servidor = "192.168.1.12";
        String servidor = "127.0.0.1";

        int porta = 1024;
        server = new Socket(InetAddress.getByName(servidor), porta);

        PrintWriter pw = new PrintWriter(server.getOutputStream());
        pw.println("1005");
//        pw.println("Host: " + args[0]);
        pw.println("");
        pw.flush();
        BufferedReader br = new BufferedReader(new InputStreamReader(server.getInputStream()));
        String t;
        while ((t = br.readLine()) != null) {
            System.out.println(t);
        }
        br.close();
    }

}
