package sandbox;
import graphics.WinApp;
import java.awt.*;

public class RedRect extends WinApp{

  public RedRect(){super("Red Rect",1000,700);} // Win Title, Win width, Win height

  @Override
  public void paintComponent(Graphics g){  // called by OS whenever it needs to show this window
    g.setColor(Color.RED);         // use the color red..
    g.fillRect(100,100,100,100);   // to fill in a rectangle
  }

  public static void main(String[] args){
    PANEL = new RedRect();  // PANEL is where the paintComponent code lives
    WinApp.launch();        // fire up the WinApp thread the the OS manages
  }

}