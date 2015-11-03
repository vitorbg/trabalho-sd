/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servidor;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
        81 . bd.data 20
        PORTASV DIRETORIOARQUIVO ARQUIVO TAMANHOREGISTRO
        Obs.: . retorna diretorio padrao do projeto

*/



public class Servidor implements Runnable {
    Socket connect;
    static int porta;
    static int cont;
    static String arquivobd; 
    static String dirbd; 
    static int tam_registro;
    static Map<String,Integer> mapa = new HashMap<String,Integer>();
    static int seekpos=0;
    static ArrayList<Integer> pos_deletados = new ArrayList<Integer>();
    static ArrayList<String> chaves_a_deletar = new ArrayList<String>();
    static String infoUser[] = {"idade","endereco","telefone"};
    /* Info Roteador */
    static String iprot = "localhost";
    static String portarot = "80";
    
    
    
    public Servidor(Socket connect)
    {
        this.connect = connect;
        cont++;
        System.out.println("[Servidor] Cliente conectado: " + connect.getInetAddress().getHostAddress() + ".");
    }
    
    
    public static void main(String[] args)
    {
        try{
            porta = Integer.parseInt(args[0]); 
            dirbd = args[1];
            arquivobd = args[2];
            tam_registro = Integer.parseInt(args[3]);
            ServerSocket serverConnect = new ServerSocket(porta);
            System.out.println("[Servidor] Escutando a porta "+porta + ".");
            AddRoteador();
            System.out.println("[Servidor] Quantidade de roteadores: "+Roteadores.rts.size() + ".");            
            while(true)
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

    public void run() {
        try {
            BufferedReader in = null;
            PrintWriter out = null;
            out = new PrintWriter(connect.getOutputStream());
            in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
            String msg = in.readLine();
            System.out.println("[Conexao" +cont + "]" + " "+msg);
            OperacaoHandler(msg,out);
            out.println("Resposta enviada ao cliente "+cont + ".");
            out.flush(); 
            in.close();
            out.close();
            connect.close();
            System.out.println("[Servidor] Fechando conexao "+cont + ".");
        }catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void OperacaoHandler(String msg, PrintWriter out) throws FileNotFoundException, IOException{
        File file = new File(Servidor.dirbd, Servidor.arquivobd);
        RandomAccessFilePlus rafp = new RandomAccessFilePlus(new RandomAccessFile(file, "rw"));
        RandomAccessFile rafpdel = new RandomAccessFile(file, "rw");
        String action;
        
        StringTokenizer actiontoken = new StringTokenizer(msg);
        action = actiontoken.nextToken();
        msg = msg.replaceFirst(action.toString(), action.toString().toLowerCase());
        action = action.toLowerCase();
        System.out.println("action: "+action);
        switch(action){
                case "adduser":     
                    String data = msg.split("adduser ")[1];
                    String key = data.split(",")[0];
                    String linha = null;  
                    String valor;
                    String newdata;
                    int tamanholinha;
                    if(pos_deletados.size()!=0){
                        rafpdel.seek(pos_deletados.get(0));
                        msg = data;                                                   
                        if(!mapa.containsKey(key)){
                            linha = rafpdel.readLine();
                            tamanholinha = linha.length();
                            rafpdel.seek(pos_deletados.get(0));
                            if(data.length()==tamanholinha){
                                rafpdel.writeBytes(data);  
                                mapa.put(key, pos_deletados.get(0));
                                broadcast(key,"add");
                                out.println("Chave adicionada.");
                                out.flush();
                                rafpdel.seek(pos_deletados.get(0));
                                linha = rafpdel.readLine(); // Nao necessario ler aqui, mas eh uma verif.
                                pos_deletados.remove(0); // Necessario
                                rafpdel.close();
                            }
                            else{
                                rafp.seek(seekpos);
                                msg = data;
                                rafp.writeBytes(data);                   
                                rafp.writeBytes(System.getProperty("line.separator").toString());
                                mapa.put(key, seekpos);
                                out.println("Chave adicionada.");
                                out.flush();
                                rafp.seek(seekpos);
                                linha = rafp.readLine(); 
                                seekpos += data.length()+2; 
                                rafp.seek(seekpos);
                                rafp.close();
                            }
                        }
                        else{
                      //      System.out.println("Key ja existe.. fazer @@ aqui!");
                            /* Key ja existe, entao concatena key+value da key q ja existe..
                            verifica se novakey ja existe, se sim concatena com o valor corresponde dela..
                            verifica se..
                            */
                             while(mapa.containsKey(key)){
                                int pont = mapa.get(key);
                                rafp.seek(pont);
                                linha = rafp.readLine();
                                valor = linha.split(",")[1];
                                key = key.concat("@").concat(valor);
                            } //
                            rafpdel.seek(pos_deletados.get(0));
                            msg = data;
                            newdata = key.concat(",").concat(data.split(",")[1]);
                            linha = rafpdel.readLine();
                            tamanholinha = linha.length();
                            rafpdel.seek(pos_deletados.get(0));
                            if(newdata.length()==tamanholinha){
                                rafpdel.writeBytes(newdata);                                               
                                mapa.put(newdata.split(",")[0], pos_deletados.get(0));
                                out.println("Chave adicionada.");
                                out.flush();
                                rafpdel.seek(pos_deletados.get(0));
                                linha = rafpdel.readLine(); 
                                pos_deletados.remove(0); 
                                rafpdel.close();
                            }
                            else{
                                rafp.seek(seekpos);
                                msg = data;
                                rafp.writeBytes(newdata);                   
                                rafp.writeBytes(System.getProperty("line.separator").toString());
                                mapa.put(newdata.split(",")[0], seekpos);
                                out.println("Chave adicionada.");
                                out.flush();
                                rafp.seek(seekpos);
                                linha = rafp.readLine(); 
                                seekpos += newdata.length()+2; 
                                rafp.seek(seekpos);
                                rafp.close();
                            }
                            
                        }
                    }
                    else{
                        if(!mapa.containsKey(key)){
                            rafp.seek(seekpos);
                            msg = data;
                            rafp.writeBytes(data);                   
                            rafp.writeBytes(System.getProperty("line.separator").toString());
                            mapa.put(msg.split(",")[0], seekpos);
                            broadcast(key,"add");
                            out.println("Chave adicionada.");
                            out.flush();
                            rafp.seek(seekpos);
                            linha = rafp.readLine(); 
                            seekpos += msg.length()+2; 
                            rafp.seek(seekpos);
                            rafp.close();
                        }
                        else{
                   //         System.out.println("Key ja existe.. fazer @@ aqui!"); 
                            /* Key ja existe, entao concatena key+value da key q ja existe..
                            verifica se novakey ja existe, se sim concatena com o valor corresponde dela..
                            verifica se..
                            */
                            while(mapa.containsKey(key)){
                                System.out.println("Mapa ja contem chave: "+key);
                                int pont = mapa.get(key);
                                rafp.seek(pont);
                                linha = rafp.readLine();
                                valor = linha.split(",")[1];
                                key = key.concat("@").concat(valor);
                            } //
                            rafp.seek(seekpos);
                            msg = data;
                            newdata = key.concat(",").concat(data.split(",")[1]);
                            rafp.writeBytes(newdata);                   
                            rafp.writeBytes(System.getProperty("line.separator").toString());
                            mapa.put(newdata.split(",")[0], seekpos);
                            out.println("Chave adicionada.");
                          //  out.println(key); chama o broadcast
                            out.flush();
                            rafp.seek(seekpos);
                            linha = rafp.readLine(); 
                            seekpos += newdata.length()+2; 
                            rafp.seek(seekpos);
                            rafp.close();
                            
                        }
                    }
                    break;
                case "deluser":
                    int pos_delete;
                    data = msg.split("deluser ")[1];
                    System.out.println("chave a ser deletada: "+data);
                    if(mapa.containsKey(data)){
                        System.out.println("Chave existe e pode ser deletada.");
                        pos_delete = mapa.get(data);
                        pos_deletados.add(pos_delete);
                        mapa.remove(data);
                        broadcast(data,"del");
                        out.println("Chave removida.");
                        out.flush();
                        rafpdel.seek(pos_delete);
                        int lengthdata = rafpdel.readLine().length();
                        rafpdel.seek(pos_delete);
                        rafpdel.write(createString(' ',lengthdata).getBytes());  
                        // Removendo as outras instancias...  
                        String keyset = mapa.keySet().toString();
                        keyset = keyset.substring(1, keyset.length()-1); // tirando []
                        String keys[] = keyset.split(", ");
                        for(int i = 0;i<keys.length;i++){                            
                            if(keys[i].startsWith(data.concat("@"))){
                                chaves_a_deletar.add(keys[i]);      
                            }
                        }
                        while(chaves_a_deletar.size()!=0){
                            pos_delete = mapa.get(chaves_a_deletar.get(0));
                            pos_deletados.add(pos_delete);
                            mapa.remove(chaves_a_deletar.get(0));
                            out.println("Chave removida.");
                            out.flush();
                            rafpdel.seek(pos_delete);
                            lengthdata = rafpdel.readLine().length();
                            rafpdel.seek(pos_delete);
                            rafpdel.write(createString(' ',lengthdata).getBytes());                                
                            chaves_a_deletar.remove(0);
                        }                                                
                    }
                    else{
                        System.out.println("Chave n existe e n pode ser deletada.");
                    }
                    break;
                case "getuser":
                    linha = "";
                    System.out.println(msg);
                    String dados = msg.split("getuser ")[1];
                    StringTokenizer parse = new StringTokenizer(dados);
                    String campo = parse.nextToken().toString(); 
                    String chave = parse.nextToken(); 
                    int qtd_arroba = procura_pos(infoUser,campo);
                    String keyset = mapa.keySet().toString();
                    keyset = keyset.substring(1, keyset.length()-1); // tirando []
                    String keys[] = keyset.split(", ");            
                    if(qtd_arroba == 0){
                        if(mapa.containsKey(chave)){
                            int pos = mapa.get(chave);
                            rafp.seek(pos);
                            linha = rafp.readLine();
                            linha = linha.split(",")[1];
                            System.out.println(linha);                                                            
                        }                                     
                    }
                    else{                       
                    for(int i = 0;i<keys.length;i++){                            
                        if(keys[i].startsWith(chave.concat("@")) && keys[i].split("@").length-1 == qtd_arroba){
                     //       System.out.println("key: "+keys[i]);
                     //       System.out.println("qtd+arroba: "+qtd_arroba);
                            if(mapa.containsKey(keys[i])){
                                int pos = mapa.get(keys[i]);
                                rafp.seek(pos);
                                linha = rafp.readLine();
                                linha = linha.split(",")[1];
                                System.out.println(linha);                                                            
                            }                                             
                        }
                    }
                    }
                    out.println(linha);
                    break;
                   
                default:
                    System.out.println("Acao nao encontrada!");
                    break;
        }
    }
    
    
    public static String createString(char character, int length) {
    char[] chars = new char[length];
    Arrays.fill(chars, character);
    return new String(chars);
}
    
    public int procura_pos(String[] str_array,String x){       
        int pos = -1;
        for(int i=0;i<str_array.length;i++){
            if(x.equals(str_array[i])){
                pos = i;
            }            
        }
        return pos;
    }
    
    public static void ImprimeMapa(){
        System.out.println("Map Size: "+mapa.size());
        System.out.println("####################");
        System.out.println(" Chave     Servidor ");
        for (Map.Entry pairs : mapa.entrySet()) {
            System.out.println(pairs.getKey() + "  #  " + pairs.getValue());
            // it.remove();
        }
        
        System.out.println("####################");
    }
    
    public void broadcast(String chave, String mode) throws UnknownHostException, IOException{
        Socket to_roteador;    
        String msg_env;
        switch(mode){
            case "add":
                msg_env = "broadcast ".concat(mode).concat(" ").concat(chave);
                for(int i=0;i<Roteadores.rts.size();i++){
                    to_roteador = new Socket(InetAddress.getByName(Roteadores.rts.get(i).getHost()), Integer.parseInt(Roteadores.rts.get(i).getPorta()));
                    PrintWriter pw_rot = new PrintWriter(to_roteador.getOutputStream());
                    pw_rot.println(msg_env);
                    pw_rot.flush();
                    pw_rot.close();
                    to_roteador.close();
                }
                break;
            case "del":
                msg_env = "broadcast ".concat(mode).concat(" ").concat(chave);
                for(int i=0;i<Roteadores.rts.size();i++){
                    to_roteador = new Socket(InetAddress.getByName(Roteadores.rts.get(i).getHost()), Integer.parseInt(Roteadores.rts.get(i).getPorta()));
                    PrintWriter pw_rot = new PrintWriter(to_roteador.getOutputStream());
                    pw_rot.println(msg_env);
                    pw_rot.flush();
                    pw_rot.close();
                    to_roteador.close();
                }
                break;
            default:
                System.out.println("Modo errado!");
                break;
        }
    }
    
     public static void AddRoteador(){
        Roteadores x1 = new Roteadores("179.104.168.115","80");
        Roteadores x2 = new Roteadores("54.94.210.183","80");
        Roteadores.rts.add(x1);
        Roteadores.rts.add(x2);
    }
}
       