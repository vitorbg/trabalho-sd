/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor. 
 */

/**
 *
 * @author vitor
 */
public class TMulticastSender implements Runnable {

    @Override
    public void run() {
        while (!Main.instanciaDescoberta) {
            Main.enviaMulticast();
            try {
                Thread.sleep(10000);
            } catch (InterruptedException interrupted) {
                System.out.println(interrupted);
            }

        }
    }
}
