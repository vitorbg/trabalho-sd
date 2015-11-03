/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Servidor;

import java.util.ArrayList;


public class Roteadores {
    public static ArrayList<Roteadores> rts = new ArrayList<>();
    private String host;
    private String porta;

    
    public Roteadores(String host, String porta){
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
    
}
