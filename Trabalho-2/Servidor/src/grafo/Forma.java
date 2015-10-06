/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grafo;

import java.awt.Graphics;

public abstract class Forma {
//    Ponto2D ancora;
    int dimensao;
    int indeX, indeY;
    
    public Forma () { }

   public Forma(int x, int y, int dim) {
//	 ancora = new Ponto2D(x, y);
         this.dimensao = dim;
   }
   public abstract void setDimensao(int dX, int dY);
   
//   public void setAncora (Ponto2D a) {
//       ancora = a;
//   }
//   
//   void setAncora (int x, int y) {
//       ancora = new Ponto2D(x,y) ;
//   }
//   
   public abstract void setDimensao (int raio);
//   
//   Ponto2D getAncora () {
//       return ancora;
//   }
//   @Override
//   public String toString() {
//	   return "(" + ancora.getX() + ", " + ancora.getY() + ", " + this.dimensao +")";
//   }
   public abstract void desenha(Graphics g);

    public int getIndeX() {
        return indeX;
    }

    public void setIndeX(int indeX) {
        this.indeX = indeX;
    }

    public int getIndeY() {
        return indeY;
    }

    public void setIndeY(int indeY) {
        this.indeY = indeY;
    }


}