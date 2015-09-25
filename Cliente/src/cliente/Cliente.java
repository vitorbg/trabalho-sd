package cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/*  Passe dois argumentos: url + porta
*   Exemplos:
*   localhost 80
*   www.uol.com.br 80
*   localhost 8089
*/

public class Cliente {
    public static void main(String[] args) throws IOException {
    Socket server;
    if(args[0].equals("localhost") || args[0].equals("127.0.0.1"))
    {
        server = new Socket(InetAddress.getLocalHost(), Integer.parseInt(args[1]));
    }
    else{
        server = new Socket(InetAddress.getByName(args[0]), Integer.parseInt(args[1]));
    }

    PrintWriter pw = new PrintWriter(server.getOutputStream());
    pw.println("GET / HTTP/1.1");
    pw.println("Host: "+args[0]);
    pw.println("");
    pw.flush();
    BufferedReader br = new BufferedReader(new InputStreamReader(server.getInputStream()));
    String t;
    while((t = br.readLine()) != null) 
        System.out.println(t);
    br.close();
    }
 
}
