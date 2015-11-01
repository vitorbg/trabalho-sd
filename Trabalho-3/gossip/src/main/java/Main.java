/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;

/**
 *
 * @author vitor
 */
public class Main {

    public static int id;
    public static int qtdIteracoes = 0;
    public static ArrayList<Variavel> valores = new ArrayList<>();
    public static Thread multicastReceiver = new Thread(new TMulticastReceiver());
    public static Thread multicastSender = new Thread(new TMulticastSender());

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        id = Integer.valueOf(args[0]);
        qtdIteracoes = Integer.valueOf(args[1]);

        int k = 0; //Guarda o indice do array para a funcao de agregacao respectiva
        int j = 0; //Guarda o indice do array para receber o nome e valor
        int i = 2; //Indice no agrs
        while (i < args.length) {

            if (args[i].endsWith(".class")) {
//                Class<?> forName = Class.forName(args[i]);
//                System.out.println(" "+forName);
//                valores.get(k).funcaoAgregacao = (Agregavel) forName.newInstance();
//                System.out.println(" " + args[i].toString());
            } else {
                Variavel var = new Variavel();

                var.valor = Double.valueOf(args[i]);
                i++;
                var.nome = args[i];
                i++;
                j++;
                valores.add(var);
            }

            System.out.println("ID: " + id);
            System.out.println("ITERACOES: " + qtdIteracoes);
            System.out.println(valores.get(0).nome);
            System.out.println(valores.get(0).valor);
            System.out.println(valores.get(0).funcaoAgregacao);
        }

    }

}
