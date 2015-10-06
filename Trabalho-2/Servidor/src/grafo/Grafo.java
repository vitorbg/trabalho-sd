package grafo;

import java.util.ArrayList;

public class Grafo {

    private String nome;
    public ArrayList<Aresta> aresta = new ArrayList<Aresta>();
    public ArrayList<String> vertice = new ArrayList<String>();

    public Grafo() {

    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

}
