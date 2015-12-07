/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalhosd4.servidor;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author vitor
 */
public class RegiaoCritica {

    public static int ID = 0;

    public static ArrayList<Cliente> clientes = new ArrayList<Cliente>();
    public static List<Integer> filaDeAcesso = new ArrayList<>();

    public static void adicionarSaldo(int ID, double valor) {
        for (int i = 0; i < clientes.size(); i++) {
            if (clientes.get(i).getId() == ID) {
                clientes.get(i).setSaldo(
                        clientes.get(i).getSaldo() + valor
                );
            }
        }
    }

    public static void adicionaFilaDeAcesso() {

    }
}
