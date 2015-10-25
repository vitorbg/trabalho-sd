/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sd.gossip;

/**
 *
 * @author vitor
 */
public class Media implements Agregavel {

    @Override
    public double computa(double a, double b) {
        return ((a + b) / 2);
    }
}
