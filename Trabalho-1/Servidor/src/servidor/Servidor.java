package servidor;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.StringTokenizer;

/*  Passe tres argumentos: pastaraiz + porta + nome do arquivo da pagina contido na pasta raiz
*   Exemplos:
*   C:/httdocs 8089 index.html
*   . 80 pagina.html                          // . como parametro retorna a pasta do projeto
*   C:/Servidor 80 index.html
*/

public class Servidor implements Runnable
{

  static File raiz;
  static String arq_pagina;
  static int porta; 
  Socket connect;


  public Servidor(Socket connect)
  {
    this.connect = connect;
    System.out.println("[Servidor] Cliente conectado: " + connect.getInetAddress().getHostAddress() + ".");
  }

  public static void main(String[] args)
  {
    raiz = new File(args[0]);
    porta = Integer.parseInt(args[1]);  
    arq_pagina = args[2];
    System.out.println("Sistemas Distribuidos: Projeto 1");
    System.out.println("[Servidor] Iniciando..");
    try
    {
      ServerSocket serverConnect = new ServerSocket(porta);
      System.out.println("[Servidor] Escutando a porta "+porta + ".");
      while (true)
      {
        Servidor server = new Servidor(serverConnect.accept());     
        Thread threadRunner = new Thread(server);
        threadRunner.start(); 
      }
    }
    catch (IOException e)
    {
      System.err.println("Erro: " + e);
    }
  }

  public void run()
  {
    BufferedReader in = null;
    PrintWriter out = null;
    BufferedOutputStream dOut = null;
    String fileRequested = null;
    try
    {
      in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
      out = new PrintWriter(connect.getOutputStream()); /* Enviar cabecalhos ao cliente */
      dOut = new BufferedOutputStream(connect.getOutputStream()); /* Enviar dados ao cliente */

      String input = in.readLine(); /* GET / HTTP/1.1 */
      StringTokenizer parse = new StringTokenizer(input);
      String method = parse.nextToken().toUpperCase(); /* GET */

      fileRequested = parse.nextToken().toLowerCase(); /* / */

      if (fileRequested.endsWith("/"))
      {
        fileRequested += arq_pagina;  // fileReq: concatena / com a index.html.. Resultado: /index.html
      }
      File file = new File(raiz, fileRequested);
      int fileLength = (int)file.length(); 
      

      String content = getContentType(fileRequested); 

      if(method.equals("GET"))
      {
        FileInputStream fileIn = null;
        byte[] fileData = new byte[fileLength];

        try
        {
          fileIn = new FileInputStream(file);
          fileIn.read(fileData);
        }
        finally
        {
          close(fileIn); 
        }
        /* Cabecalho */
        out.println("HTTP/1.0 200 OK");
        out.println("Date: " + new Date());
        out.println("Content-type: " + content);
        out.println("Content-length: " + file.length());
        out.println(); 
        out.flush(); 

        dOut.write(fileData,0,fileLength); 
        dOut.flush(); 
      }

    }
    catch (FileNotFoundException fnfe)
    {
      fileNotFound(out, fileRequested);
    }
    catch (IOException ioe)
    {
      System.err.println("Server Error: " + ioe);
    }
    finally
    {
      close(in); //fecha character input stream
      close(out); //fecha character output stream
      close(dOut); //fecha binary output stream
      close(connect); //fecha socket connection

    }
  }

  private void fileNotFound(PrintWriter out, String file)
  {
    out.println("HTTP/1.0 404 File Not Found");
    out.println("Date: " + new Date());
    out.println("Content-Type: text/html");
    out.println();
    out.println("<HTML>");
    out.println("<HEAD><TITLE>File Not Found</TITLE>" + "</HEAD>");
    out.println("<BODY>");
    out.println("<H2>404 File Not Found: " + file + "</H2>");
    out.println("</BODY>");
    out.println("</HTML>");
    out.flush();
  }


   /*
        Retorna o conteudo de acordo com a extensao do arquivo
   */

  private String getContentType(String fileRequested)
  {
    if (fileRequested.endsWith(".htm") || fileRequested.endsWith(".html"))
    {
      return "text/html";
    }
    else
    {
      return "text/plain";
    }
  }


   /*
        da .close() na stream
   */
  public void close(Object stream)
  {
    if (stream == null)
      return;

    try
    {
      if (stream instanceof Reader)
      {
        ((Reader)stream).close();
      }
      else if (stream instanceof Writer)
      {
        ((Writer)stream).close();
      }
      else if (stream instanceof InputStream)
      {
        ((InputStream)stream).close();
      }
      else if (stream instanceof OutputStream)
      {
        ((OutputStream)stream).close();
      }
      else if (stream instanceof Socket)
      {
        ((Socket)stream).close();
      }
      else
      {
        System.err.println("Tentativa falha de fechar a stream: " + stream);
      }
    }
    catch (Exception e)
    {
      System.err.println("Falha fechando a stream: " + e);
    }
  }
}