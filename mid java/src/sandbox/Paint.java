package sandbox;

import graphics.WinApp;
import graphics.G;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;


public class Paint extends WinApp {
    public static int repaint = 0;
    public static int clicks = 0;
    public static Path thePath = new Path();
    public static Pic thePic = new Pic();
    public Paint() {super("paint", 1000, 750);}

    @Override
    public void paintComponent(Graphics g){
        repaint++;
        G.bkWhite(g);
        g.setColor(Color.BLACK);
        thePic.draw(g);

/*
        g.setColor(G.rndColor());
        g.fillOval(100,100,200,100);
        g.setColor(Color.BLACK);
        g.drawLine(100, 400, 400, 100);

        int x = 400, y = 200;
        String msg = "DUDE" + clicks;
        g.drawString(msg,x,y);
        g.setColor(Color.RED);
        g.drawOval(x,y,3,3);

        FontMetrics fm = g.getFontMetrics();
        int a = fm.getAscent(), h = fm.getHeight(), w = fm.stringWidth(msg);
        g.drawRect(x,y-a,w,h);
 */
    }

    @Override
    public void mousePressed(MouseEvent me){
        clicks++;
        thePath = new Path();
        thePic.add(thePath);
        thePath.add(me.getPoint());
        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent me){
        thePath.add(me.getPoint());
        repaint();
    }

    public static void main (String[] args){
        PANEL = new Paint();
        WinApp.launch();
    }

    //------------------------------path-----------------------------------

    public static class Path extends ArrayList<Point> {
        public void draw(Graphics g){
            for(int i = 1; i < size(); i++){
                Point p = get(i-1), n = get(i);
                g.drawLine(p.x, p.y, n.x, n.y);
            }
        }

    }

    public static class Pic extends ArrayList<Path> {
        public void draw(Graphics g){
            for(Path p: this){           // Another way to do for loop.
                p.draw(g);
            }
        }
    }

}
