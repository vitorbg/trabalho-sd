
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author vitor
 */
public class TUDPReceiver implements Runnable {

    @Override
    public void run() {
        while (!Main.fimPrograma) {
            try {
                Main.recebeMSG();
            } catch (IOException ex) {
                Logger.getLogger(TUDPReceiver.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
