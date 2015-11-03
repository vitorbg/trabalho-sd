/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Roteador;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Servidores {
    public static ArrayList<Servidores> svs = new ArrayList<>();
    private String host;
    private String porta;
    private int qtde_tuplas;
      
    /**
     * @return the host
     */
    
    public Servidores(String host, String porta){
        this.host=host;
        this.porta=porta;
    }
    
    public String getHost() {
        return host;
    }

    /**
     * @param host the host to set
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @return the porta
     */
    public String getPorta() {
        return porta;
    }

    /**
     * @param porta the porta to set
     */
    public void setPorta(String porta) {
        this.porta = porta;
    }

    /**
     * @return the qtde_tuplas
     */
    public int getQtde_tuplas() {
        return qtde_tuplas;
    }

    /**
     * @param qtde_tuplas the qtde_tuplas to set
     */
    public void setQtde_tuplas(int qtde_tuplas) {
        this.qtde_tuplas = qtde_tuplas;
    }
    
}
