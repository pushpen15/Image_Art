package imageart;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import view.frmPrincipal;
import view.frmStart;

public class ImageArt {

    public static void main(String[] args) throws UnsupportedLookAndFeelException {
       changeLookAndFeel();
        //new frmPrincipal("C:\\Users\\goura\\Pictures\\usherSmall.jpg").setVisible(true);
        new frmStart().setVisible(true);
       // new frmStart().setSize(100,100);
    }
    
    public static void changeLookAndFeel(){
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(frmStart.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
