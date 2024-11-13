package sandbox;

import graphics.G;
import graphics.WinApp;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Paint extends WinApp{
  public static Path thePath = new Path();
  public static Pic thePic = new Pic();  // we add a single Picture - list of path

  public Paint(){super("Paint", 1000, 700);}

  @Override
  public void paintComponent(Graphics g){
    g.setColor(Color.WHITE); g.fillRect(0,0,9000,9000);
    Color c = G.rndColor();
    g.setColor(c);
    g.fillOval(100,50,200,300);
    g.setColor(Color.BLACK);
    thePic.draw(g);  // draw the whole picture not just the last path
    g.drawLine(100,600,600,100);
    int x = 400, y = 200; String msg = "Clicks = "+clicks;
    FontMetrics fm = g.getFontMetrics();
    int a = fm.getAscent(), d = fm.getDescent();
    int w = fm.stringWidth(msg);
    g.setColor(c);
    g.drawRect(x,y-a,w,a+d);
    g.setColor(Color.BLACK);
    g.drawString(msg,x,y);
  }

  public static int clicks = 0;

  @Override
  public void mousePressed(MouseEvent me){
    clicks++;
    thePath = new Path(); // this clears the path by creating a new one..
    thePic.add(thePath);  //.. then adds that new Path to the growing Picture
    repaint();
  }

  @Override
  public void mouseDragged(MouseEvent me){
    thePath.add(me.getPoint());
    repaint();
  }

  public static void main(String[] args){PANEL=new Paint(); WinApp.launch();}

  //--------------------PATH----------------------------
  public static class Path extends ArrayList<Point>{
    public void draw(Graphics g){
      for(int i = 1; i<size(); i++){
        Point p = get(i-1), n = get(i); // the previous and the next point
        g.drawLine(p.x,p.y,n.x,n.y);
      }
    }
  }

  //--------------------Pic----------------------------
  public static class Pic extends ArrayList<Path>{
    public void draw(Graphics g){for(Path p:this){p.draw(g);}}
  }
}