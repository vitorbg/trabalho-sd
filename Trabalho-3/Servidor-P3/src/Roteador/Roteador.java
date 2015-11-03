/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Roteador;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Roteador implements Runnable {
    Socket clientesoc;
    public static int porta = 80;
    static Map<String,Integer> mapatuplas = new HashMap<String, Integer>();   
    
    
    public static void main (String argv[]) throws IOException{
    System.out.println("Sistemas Distribuidos: Projeto 3");
    System.out.println("[Roteador] Iniciando..");
    try
    {
      ServerSocket serverConnect = new ServerSocket(porta);
      System.out.println("[Roteador] Escutando a porta "+porta + ".");
      System.out.println("[Roteador] Svs size: "+Servidores.svs.size());
      AddServer();
      System.out.println("[Roteador] Svs size apos addserver(): "+Servidores.svs.size());
      while (true)
      {
        Roteador b = new Roteador(serverConnect.accept());     
        Thread threadRunner = new Thread(b);
        threadRunner.start(); 
      }
    }
    catch (IOException e)
    {
      System.err.println("Erro: " + e);
    }
    }
    
    public Roteador(Socket c){
        this.clientesoc = c;
        System.out.println("[Roteador] Cliente conectado: " + clientesoc.getInetAddress().getHostAddress() + ".");
    }
    
    public void run(){
    BufferedReader in = null;
    PrintWriter out = null;
    BufferedOutputStream dOut = null;
    try
    {
      in = new BufferedReader(new InputStreamReader(clientesoc.getInputStream())); // ler requisicao do cliente
      out = new PrintWriter(clientesoc.getOutputStream()); /* Enviar cabecalhos ao cliente */

      String input = in.readLine(); 
      StringTokenizer parse = new StringTokenizer(input);
      String method = parse.nextToken().toLowerCase(); 
      switch (method) {
          case "adduser":
              System.out.println("adduser");
              String data = input.split("adduser ")[1];
              String key = data.split(",")[0];
              String sv_to_send = ChaveToIp(key);
             // System.out.println(sv_to_send);
              Socket conexao_servidor = new Socket(sv_to_send,81);
              PrintWriter escreve_servidor = new PrintWriter(conexao_servidor.getOutputStream());              
              escreve_servidor.println(input);
              escreve_servidor.flush();
              BufferedReader le_servidor = new BufferedReader(new InputStreamReader(conexao_servidor.getInputStream()));
              String msg_recebida, nova_chave_mapa;
              msg_recebida = le_servidor.readLine();
              out.println(msg_recebida);
              out.flush();
              out.close();
              escreve_servidor.close();
              le_servidor.close();
              break;
          case "deluser":
              System.out.println("deluser");
              data = input.split("deluser ")[1];
              key = data;
              sv_to_send = ChaveToIp(key);
              System.out.println("Enviando para o servidor: "+sv_to_send +".");
              conexao_servidor = new Socket(sv_to_send,81);
              escreve_servidor = new PrintWriter(conexao_servidor.getOutputStream());              
              escreve_servidor.println(input);
              escreve_servidor.flush();
              le_servidor = new BufferedReader(new InputStreamReader(conexao_servidor.getInputStream()));              
              msg_recebida = le_servidor.readLine();
              out.println(msg_recebida);
              out.flush();
              out.close();
              escreve_servidor.close();
              le_servidor.close();          
              break;
          case "getuser":
              System.out.println("getuser");
              data = input.split("getuser ")[1];
              StringTokenizer parseget = new StringTokenizer(data);
              String campo = parse.nextToken().toLowerCase(); 
              String chave = parse.nextToken().toLowerCase(); 
              sv_to_send = ChaveToIp(chave);
              System.out.println("Enviando para o servidor: "+sv_to_send + ".");
              conexao_servidor = new Socket(sv_to_send,81);
              escreve_servidor = new PrintWriter(conexao_servidor.getOutputStream());              
              escreve_servidor.println(input);
              escreve_servidor.flush();
              le_servidor = new BufferedReader(new InputStreamReader(conexao_servidor.getInputStream()));              
              msg_recebida = le_servidor.readLine();
              out.println(msg_recebida);
              out.flush();
              out.close();
              escreve_servidor.close();
              le_servidor.close();   
              break;
          case "broadcast":
              System.out.println("Broadcast recebido do servidor "+SvIpToNrVet(String.valueOf(clientesoc.getInetAddress().getHostAddress())) + " ("+String.valueOf(clientesoc.getInetAddress().getHostAddress()) + ").");
              String add_or_del = parse.nextToken().toLowerCase();
              if(add_or_del.equals("add")){
                  atualiza_mapa_add(parse.nextToken(),SvIpToNrVet(String.valueOf(clientesoc.getInetAddress().getHostAddress())));                  
              }
              else if(add_or_del.equals("del")){
                  atualiza_mapa_del(parse.nextToken());
                  decr_qtdetuplas(SvIpToNrVet(String.valueOf(clientesoc.getInetAddress().getHostAddress())));
              }
              ImprimeMapa();
              break;
          default:
              System.out.println("Acao nao encontrada!");
              break;
      }
    }   
        catch (IOException ex) {
            Logger.getLogger(Roteador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
     
      
  
      
      
      
    
    
    public int DetServer(String s){
        int qtd_svs = Servidores.svs.size();
        int svescolhido = 0,randomNum=0;
        System.out.println("[Balanceador] Quantidade de servidores online: "+qtd_svs);
        if(s.substring(s.length()-1).matches("[0-9]") && !s.contains(":")){
            svescolhido = Integer.valueOf(s.substring(s.length()-1))%qtd_svs;
        }
        else{ 
            randomNum = (int)(Math.random()*qtd_svs);
            svescolhido = randomNum;
            System.out.println("Chamada aleatoria de servidor.");
        }

        System.out.println("[Balanceador] Sv escolhido: "+svescolhido);
        return svescolhido;
    }
    
    
    public static void inc_qtdetuplas(int sv_index){        
        Servidores x = Servidores.svs.get(sv_index);
        int qtde_atual = x.getQtde_tuplas();
        qtde_atual++;
        x.setQtde_tuplas(qtde_atual);        
    }
    
    public static void decr_qtdetuplas(int sv_index){        
        Servidores x = Servidores.svs.get(sv_index);
        int qtde_atual = x.getQtde_tuplas();
        qtde_atual--;
        x.setQtde_tuplas(qtde_atual);        
    }
    
    public static void atualiza_mapa_add(String chave, int sv_index){
        System.out.println(chave);
        if((!mapatuplas.containsKey(chave))){
            mapatuplas.put(chave, sv_index);
        }
        inc_qtdetuplas(sv_index);
        
    }
    
    public static void atualiza_mapa_del(String chave){
        if(mapatuplas.containsKey(chave)){
            mapatuplas.remove(chave);
        }        
    }
    
    public static int SvIpToNrVet(String ip){
        int num_sv = -1;

       for(int i=0;i<Servidores.svs.size();i++){
                Servidores x = Servidores.svs.get(i);                
       //         System.out.println("get("+i + " )host: "+x.getHost() + " e ip: "+ip);
                if(x.getHost().equals(ip)){
                    num_sv = i;
                }         
    }
        return num_sv; 
        }
    
    public static String ChaveToIp(String chave){
        if(!mapatuplas.containsKey(chave)){
            int menor_carga = Integer.MAX_VALUE;
            int ind_menor_carga_sv = 0;
            for(int i=0;i<Servidores.svs.size();i++){
                Servidores x = Servidores.svs.get(i);
                if(x.getQtde_tuplas()<=menor_carga){
                    ind_menor_carga_sv = i;
                    menor_carga = x.getQtde_tuplas();
                }
            }
            return Servidores.svs.get(ind_menor_carga_sv).getHost();
        }
        else{
            int nr_sv = mapatuplas.get(chave);
            return Servidores.svs.get(nr_sv).getHost();
        }        
    }
    
    public static void ImprimeMapa(){
        System.out.println("Map Size: "+mapatuplas.size());
        System.out.println("####################");
        System.out.println(" Chave     Servidor ");
        Iterator it = mapatuplas.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            System.out.println(pairs.getKey() + "  #  " + pairs.getValue());           
    }
        
        System.out.println("####################");
    }
    
    
    public static void AddServer(){
        Servidores x1 = new Servidores("179.104.168.115","81");
        Servidores x2 = new Servidores("54.94.210.183","81");
        Servidores.svs.add(x1);
        Servidores.svs.add(x2);
    }
  

}
