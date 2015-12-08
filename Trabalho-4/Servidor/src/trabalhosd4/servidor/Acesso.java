/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalhosd4.servidor;

import java.util.Date;

/**
 *
 * @author vitor
 */
public class Acesso {

    private int ID;
    private Date dataCriacao;

    public Acesso(int ID, Date dataCriacao) {
        this.ID = ID;
        this.dataCriacao = dataCriacao;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

}
