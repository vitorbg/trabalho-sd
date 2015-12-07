/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalhosd4.servidor;

import java.util.ArrayList;

/**
 *
 * @author vitor
 */
public class Votacao {

    public static ArrayList<Voto> votacao = new ArrayList<Voto>();
    private static boolean votoNovo = false;

    public static void computaVoto(int ID) {
        if (votacao.isEmpty()) {
            Voto voto = new Voto(ID);
            voto.setNumVotos(1);
            votacao.add(voto);
        } else {
            for (int i = 0; i < votacao.size(); i++) {
                if (votacao.get(i).getID() == ID) {
                    votoNovo = true;
                }
            }
            if (votoNovo) {
                Voto voto = new Voto(ID);
                voto.setNumVotos(1);
            } else {
                for (int i = 0; i < votacao.size(); i++) {
                    if (votacao.get(i).getID() == ID) {
                        votacao.get(i).setNumVotos(
                                votacao.get(i).getNumVotos() + 1);
                    }
                }
            }

        }

    }

    public static int retornaVencedor() {
        Voto votoCampeao = votacao.get(0);
        for (int i = 0; i < votacao.size(); i++) {
            if (votacao.get(i).getNumVotos() > votoCampeao.getNumVotos()) {
                votoCampeao = votacao.get(i);
            }
        }
        return votoCampeao.getID();
    }

    public static void reiniciarVotacao() {
        votacao = null;
        votacao = new ArrayList<Voto>();
    }
}
