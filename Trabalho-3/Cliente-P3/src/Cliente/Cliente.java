/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/*

    179.104.168.115 80 "adduser joao,14"
    IPDESTINO PORTA REQUISICAO

*/


public class Cliente {
    public static void main(String[] args) throws IOException{
    Socket server;
    if(args[0].equals("localhost") || args[0].equals("127.0.0.1"))
    {
        server = new Socket(InetAddress.getLocalHost(), Integer.parseInt(args[1]));
    }
    else
    {
        server = new Socket(InetAddress.getByName(args[0]), Integer.parseInt(args[1]));
    }
    PrintWriter pw = new PrintWriter(server.getOutputStream());
    pw.println(args[2]);
    pw.flush();
    BufferedReader br = new BufferedReader(new InputStreamReader(server.getInputStream()));
    String msg;
    msg = br.readLine(); //tratar pra se a msg for null
    System.out.println(msg);
    br.close();
    pw.close();
    server.close();
    }
    
}
