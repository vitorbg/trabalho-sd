/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistencia;

import grafo.Grafo;
import java.util.ArrayList;

/**
 *
 * @author vitor
 */
public class GrafoPersist {

    public static ArrayList<Grafo> grafo;

    static {
        grafo = (ArrayList<Grafo>) Persist.recuperar("grafo.dat");
        if (grafo == null) {
            grafo = new ArrayList<Grafo>();
        }
    }

    public static void cadastrar(Grafo a) {
        grafo.add(a);
//        boolean r = Persist.gravar(grafo, "grafo.dat");
    }

    public static void mostraTodos() {
        System.out.println("Grafos !!");
        for (int i = 0; i < grafo.size(); i++) {
            System.out.println("ID: " + grafo.get(i).getNome());
            for (int j = 0; j < grafo.get(i).vertice.size(); j++) {
                System.out.println("Vertice: " + grafo.get(i).vertice.get(j));
            }
            for (int j = 0; j < grafo.get(i).aresta.size(); j++) {
                System.out.println("Aresta: " + grafo.get(i).aresta.get(j).getVertice1()
                        + " - " + grafo.get(i).aresta.get(j).getVertice2()
                        + " peso:" + grafo.get(i).aresta.get(j).getPeso()
                );
            }
        }
    }

}
