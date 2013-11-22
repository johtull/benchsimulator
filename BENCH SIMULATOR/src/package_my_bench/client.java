package package_my_bench;

//import java.awt.GraphicsEnvironment;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;

/**
 * JFrame handler
 * @author Robert Brestle
 */
public class client {

    public static void main(String[] args) {
        JFrame f = new BenchGame(640, 480);
        f.setTitle("Bench Simulator 2013 - BETA");
        f.setSize(640, 480);
        f.setLocationRelativeTo(null);//center frame
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
        f.setResizable(false);
        f.setFocusable(true);//focus the frame
        
        
        //hidden cursor:
        // Transparent 16 x 16 pixel cursor image.
        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        // Create a new blank cursor.
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
            cursorImg, new Point(0, 0), "blank cursor");
        // Set the blank cursor to the JFrame.
        f.getContentPane().setCursor(blankCursor);
        
        //full screen (needs extra configuration to work)
//        GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(f);
    }
}