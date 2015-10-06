package servidor;

import grafo.Aresta;
import grafo.Grafo;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import persistencia.GrafoPersist;
import static persistencia.GrafoPersist.grafo;

/*  Passe tres argumentos: pastaraiz + porta + nome do arquivo da pagina contido na pasta raiz
 *   Exemplos:
 *   C:/httdocs 8089 index.html
 *   . 80 pagina.html                          // . como parametro retorna a pasta do projeto
 *   C:/Servidor 80 index.html
 */
public class Servidor implements Runnable {

    static File raiz;
    static String arq_pagina;
    static int porta;
    Socket connect;
    boolean fimPost = true;

    public Servidor(Socket connect) {
        this.connect = connect;
        System.out.println("[Servidor] Cliente conectado: " + connect.getInetAddress().getHostAddress() + ".");
        System.out.println("[Servidor] Cliente conectado: " + connect.getInetAddress().getCanonicalHostName() + ".");
    }

    public static void main(String[] args) {
        raiz = new File(args[0]);
        porta = Integer.parseInt(args[1]);
        arq_pagina = args[2];
        System.out.println("Sistemas Distribuidos: Projeto 2");
        System.out.println("[Servidor] Iniciando..");
        try {
            ServerSocket serverConnect = new ServerSocket(porta);
            System.out.println("[Servidor] Escutando a porta " + porta + ".");
            while (true) {
                Servidor server = new Servidor(serverConnect.accept());
                Thread threadRunner = new Thread(server);
                threadRunner.start();
            }
        } catch (IOException e) {
            System.err.println("Erro: " + e);
        }
    }

    public void run() {
        BufferedReader in = null;
        PrintWriter out = null;
        BufferedOutputStream dOut = null;
        String fileRequested = null;
        String input2;
        try {
            in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
            out = new PrintWriter(connect.getOutputStream()); /* Enviar cabecalhos ao cliente */

            dOut = new BufferedOutputStream(connect.getOutputStream()); /* Enviar dados ao cliente */

            String input = in.readLine(); /* GET / HTTP/1.1 */

            System.out.println("Cabeçalho: " + input);
            System.out.println(" ");

            StringTokenizer parse = new StringTokenizer(input);
            String method = parse.nextToken().toUpperCase(); /* GET */

            String caminho = parse.nextToken(); /* /../../ */

            System.out.println("caminho: " + caminho);

            if (method.equals("POST")) {
                //Pega o body do POST ******************************************    
                input2 = in.readLine();

                //cria grafo ***************************************************
                if (caminho.contains("/criag/")) {

                    //Pega o nome do grafo a ser criado
                    String nome = caminho.substring(7, caminho.length());

                    //Verifica se o grafo já existe
                    int i;
                    boolean grafoExiste = false;
                    for (i = 0; i < GrafoPersist.grafo.size(); i++) {
                        //existe o grafo, retorna BAD REQUEST
                        if (GrafoPersist.grafo.get(i).getNome().equals(nome)) {
                            grafoExiste = true;
                        }
                    }

                    if (grafoExiste) {
                        badRequest(out);
                        out.println();
                        out.flush();
                        dOut.flush();
                    } else {
                        //Se o grafo não existe, cria ele
                        Grafo g = new Grafo();
                        g.setNome(nome);
                        System.out.println("NOME DO GRAFO: " + nome);
                        GrafoPersist.cadastrar(g);

                        /* Cabecalho */
                        out.println("HTTP/1.0 201 CREATED");
                        out.println("Date: " + new Date());
                        out.println();
                        out.flush();
                        dOut.flush();
                    }
                }
                //cria vertice *************************************************
                if (caminho.contains("/criav/")) {
                    boolean verticeExiste = false;
                    int i = 0;
                    int j = 0;
                    int indiceGrafo = 0;

                    String parte = caminho.substring(7, caminho.length());
                    while (parte.charAt(i) != '/') {
                        i++;
                        j++;
                    }
                    String nomeGrafo = parte.substring(0, j);

                    j++;
                    String nomeVertice = parte.substring(j, parte.length());
                    System.out.println("parte: " + parte);
                    System.out.println("nome grafo: " + nomeGrafo);
                    System.out.println("nome vertice: " + nomeVertice);
                    //Verifica se o grafo já existe
                    for (i = 0; i < GrafoPersist.grafo.size(); i++) {
                        //existe o grafo
                        if (GrafoPersist.grafo.get(i).getNome().equals(nomeGrafo)) {
                            indiceGrafo = i;
                            //verifica se o vertice existe
                            for (j = 0; j < GrafoPersist.grafo.get(i).vertice.size(); j++) {
                                if (GrafoPersist.grafo.get(i).vertice.get(j).equals(nomeVertice)) {
                                    verticeExiste = true;
                                }
                            }

                        }
                    }
                    if (verticeExiste) {
                        badRequest(out);
                    } else {
                        GrafoPersist.grafo.get(indiceGrafo).vertice.add(nomeVertice);
                        ok200(out);
                    }

                }

                //cria aresta **************************************************
                if (caminho.contains("/criaa/")) {
                    boolean arestaExiste = false;
                    int i = 0;
                    int j = 0;
                    int indiceGrafo = 0;
                    int indiceFixo;

                    String parte = caminho.substring(7, caminho.length());
                    System.out.println("parte: " + parte);

                    while (parte.charAt(i) != '/') {
                        j++;
                        i++;
                    }

                    String nomeGrafo = parte.substring(0, j);
                    System.out.println("nome grafo: " + nomeGrafo);
                    indiceFixo = j + 1;

                    while (parte.charAt(i) != '-') {
                        i++;
                        j++;
                    }

                    String nomeVertice1 = parte.substring(indiceFixo, j);
                    System.out.println("Vertice1: " + nomeVertice1);
                    indiceFixo = j + 1;

                    while (parte.charAt(i) != '&') {
                        i++;
                        j++;
                    }

                    String nomeVertice2 = parte.substring(indiceFixo, j);
                    System.out.println("Vertice2: " + nomeVertice2);
                    indiceFixo = j + 1;

                    while (parte.charAt(i) != '=') {
                        i++;
                        j++;
                    }

                    String atributo = parte.substring(indiceFixo, j);
                    System.out.println("Atributo: " + atributo);

                    j++;

                    String valorAtributo = parte.substring(j, parte.length());
                    System.out.println("Valor: " + valorAtributo);

                    //Verifica se o grafo já existe
                    for (i = 0; i < GrafoPersist.grafo.size(); i++) {
                        //existe o grafo
                        if (GrafoPersist.grafo.get(i).getNome().equals(nomeGrafo)) {
                            indiceGrafo = i;
                            //verifica se o vertice existe
                            for (j = 0; j < GrafoPersist.grafo.get(i).aresta.size(); j++) {
                                if (GrafoPersist.grafo.get(i).aresta.get(j).getVertice1().equals(nomeVertice1)
                                        && GrafoPersist.grafo.get(i).aresta.get(j).getVertice2().equals(nomeVertice2)
                                        || GrafoPersist.grafo.get(i).aresta.get(j).getVertice1().equals(nomeVertice2)
                                        && GrafoPersist.grafo.get(i).aresta.get(j).getVertice2().equals(nomeVertice1)) {
                                    arestaExiste = true;
                                }
                            }

                        }
                    }
                    if (arestaExiste) {
                        badRequest(out);
                    } else {
                        Aresta a = new Aresta();
                        a.setVertice1(nomeVertice1);
                        a.setVertice2(nomeVertice2);
                        a.setPeso(valorAtributo);
                        GrafoPersist.grafo.get(indiceGrafo).aresta.add(a);
                        ok200(out);
                    }
                }

//
//            fileRequested = parse.nextToken().toLowerCase(); /* / */
//
//            if (fileRequested.endsWith("/")) {
//                fileRequested += arq_pagina;  // fileReq: concatena / com a index.html.. Resultado: /index.html
//            }
//            File file = new File(raiz, fileRequested);
//            int fileLength = (int) file.length();
//
//            String content = getContentType(fileRequested);
//
                if (method.equals("DELETE")) {
//                FileInputStream fileIn = null;
//                byte[] fileData = new byte[fileLength];
//
//                try {
//                    fileIn = new FileInputStream(file);
//                    fileIn.read(fileData);
//                } finally {
//                    close(fileIn);
//                }
//                /* Cabecalho */
//                out.println("HTTP/1.0 200 OK");
//                out.println("Date: " + new Date());
//                out.println("Content-type: " + content);
//                out.println("Content-length: " + file.length());
//                out.println();
//                out.flush();
//
//                dOut.write(fileData, 0, fileLength);
//                dOut.flush();
                }
            }

            if (method.equals("PUT")) {
//                FileInputStream fileIn = null;
//                byte[] fileData = new byte[fileLength];
//
//                try {
//                    fileIn = new FileInputStream(file);
//                    fileIn.read(fileData);
//                } finally {
//                    close(fileIn);
//                }
//                /* Cabecalho */
//                out.println("HTTP/1.0 200 OK");
//                out.println("Date: " + new Date());
//                out.println("Content-type: " + content);
//                out.println("Content-length: " + file.length());
//                out.println();
//                out.flush();
//
//                dOut.write(fileData, 0, fileLength);
//                dOut.flush();
            }

            //            fileRequested = parse.nextToken().toLowerCase(); /* / */
//
//            if (fileRequested.endsWith("/")) {
//                fileRequested += arq_pagina;  // fileReq: concatena / com a index.html.. Resultado: /index.html
//            }
//            File file = new File(raiz, fileRequested);
//            int fileLength = (int) file.length();
//
//            String content = getContentType(fileRequested);
            if (method.equals("GET")) {
                if (caminho.contains("/grafo/")) {
                    //Pega o nome do grafo a ser criado
                    String nome = caminho.substring(7, caminho.length());

                    //Verifica se o grafo já existe
                    int i;
                    int indiceGrafo = 0;
                    boolean grafoExiste = false;
                    for (i = 0; i < GrafoPersist.grafo.size(); i++) {
                        //existe o grafo, retorna BAD REQUEST
                        if (GrafoPersist.grafo.get(i).getNome().equals(nome)) {
                            grafoExiste = true;
                            indiceGrafo = i;
                        }
                    }

                    if (grafoExiste) {
                        getGrafo(out, GrafoPersist.grafo.get(indiceGrafo));
                    } else {
                        badRequest(out);
                        out.println();
                        out.flush();
                        dOut.flush();

                    }
                }

//                FileInputStream fileIn = null;
//                byte[] fileData = new byte[fileLength];
//
//                try {
//                    fileIn = new FileInputStream(file);
//                    fileIn.read(fileData);
//                } finally {
//                    close(fileIn);
//                }
//                /* Cabecalho */
//                out.println("HTTP/1.0 200 OK");
//                out.println("Date: " + new Date());
//                out.println("Content-type: " + content);
//                out.println("Content-length: " + file.length());
//                out.println();
//                out.flush();
//
//                dOut.write(fileData, 0, fileLength);
//                dOut.flush();
            }
        } catch (FileNotFoundException fnfe) {
            fileNotFound(out, fileRequested);
        } catch (IOException ioe) {
            System.err.println("Server Error: " + ioe);
        } finally {

            GrafoPersist.mostraTodos();
            close(in); //fecha character input stream
            close(out); //fecha character output stream
            close(dOut); //fecha binary output stream
            close(connect); //fecha socket connection

        }
    }

    private void fileNotFound(PrintWriter out, String file) {
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

    private void badRequest(PrintWriter out) {
        out.println("HTTP/1.0 400 Bad Request");
        out.println("Date: " + new Date());
        out.println("Content-Type: text/html");
        out.println();
        out.println("<HTML>");
        out.println("<HEAD><TITLE>Bad Request</TITLE>" + "</HEAD>");
        out.println("<BODY>");
        out.println("<H2>400 The request had bad syntax or was inherently impossible to be satisfied.</H2>");
        out.println("</BODY>");
        out.println("</HTML>");
        out.flush();
    }

    private void ok200(PrintWriter out) {
        out.println("HTTP/1.0 200 OK");
        out.println("Date: " + new Date());
        out.println("Content-Type: text/html");
        out.println();
        out.println("<HTML>");
        out.println("<HEAD><TITLE>200 OK</TITLE>" + "</HEAD>");
        out.println("<BODY>");
        out.println("<H2></H2>");
        out.println("</BODY>");
        out.println("</HTML>");
        out.flush();
    }

    private void created201(PrintWriter out) {
        out.println("HTTP/1.0 201 Created");
        out.println("Date: " + new Date());
        out.println("Content-Type: text/html");
        out.println();
        out.println("<HTML>");
        out.println("<HEAD><TITLE>Created</TITLE>" + "</HEAD>");
        out.println("<BODY>");
        out.println("<H2></H2>");
        out.println("</BODY>");
        out.println("</HTML>");
        out.flush();
    }

    private void getGrafo(PrintWriter out, Grafo g) {
        out.println("HTTP/1.0 201 Created");
        out.println("Date: " + new Date());
        out.println("Content-Type: text/html");
        out.println();
        out.println("<HTML>");
        out.println("<HEAD><TITLE>Created</TITLE>" + "</HEAD>");
        out.println("<BODY>");
        out.println("<H2>");

        out.println("GRAFO: " + g.getNome());
        out.println("<br>");

        for (int j = 0; j < g.vertice.size(); j++) {
            out.println("Vertice: " + g.vertice.get(j));
            out.println("<br>");
        }
        out.println("<br>");
        for (int j = 0; j < g.aresta.size(); j++) {
            out.println("Aresta: " + g.aresta.get(j).getVertice1()
                    + " - " + g.aresta.get(j).getVertice2()
                    + " peso:" + g.aresta.get(j).getPeso()
            );
            out.println("<br>");
        }

        out.println("</H2>");
        out.println("</BODY>");
        out.println("</HTML>");
        out.flush();
    }

    /*
     Retorna o conteudo de acordo com a extensao do arquivo
     */
    private String getContentType(String fileRequested) {
        if (fileRequested.endsWith(".htm") || fileRequested.endsWith(".html")) {
            return "text/html";
        } else {
            return "text/plain";
        }
    }


    /*
     da .close() na stream
     */
    public void close(Object stream) {
        if (stream == null) {
            return;
        }

        try {
            if (stream instanceof Reader) {
                ((Reader) stream).close();
            } else if (stream instanceof Writer) {
                ((Writer) stream).close();
            } else if (stream instanceof InputStream) {
                ((InputStream) stream).close();
            } else if (stream instanceof OutputStream) {
                ((OutputStream) stream).close();
            } else if (stream instanceof Socket) {
                ((Socket) stream).close();
            } else {
                System.err.println("Tentativa falha de fechar a stream: " + stream);
            }
        } catch (Exception e) {
            System.err.println("Falha fechando a stream: " + e);
        }
    }
}
