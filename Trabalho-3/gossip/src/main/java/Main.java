/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;

/**
 *
 * @author vitor
 */
public class Main {

    public static int id;
    public static int qtdIteracoes = 0;
    public static ArrayList<Variavel> valores = new ArrayList<>();

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
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
//                File f = new File(args[i].toString());
//                Class toRun = Class.forName(f.getAbsolutePath());
//
//                System.out.println(" " + f.getAbsolutePath());
//                System.out.println(" " + f.getCanonicalPath());
////                System.out.println(" "+args[i].);
//                k++;
//                i++;

//                try {
//
//                    File file = new File(args[i]);
//
//                    //convert the file to URL format
//                    URL url = file.toURI().toURL();
//                    URL[] urls = new URL[]{url};
//
//                    //load this folder into Class loader
//                    ClassLoader cl = new URLClassLoader(urls);
//
//                    //load the Address class in 'c:\\other_classes\\'
//                    Class cls = cl.loadClass("sd.gossip.Min");
//
//                    //print the location from where this class was loaded
//                    ProtectionDomain pDomain = cls.getProtectionDomain();
//                    CodeSource cSource = pDomain.getCodeSource();
//                    URL urlfrom = cSource.getLocation();
//                    System.out.println(urlfrom.getFile());
//
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
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
