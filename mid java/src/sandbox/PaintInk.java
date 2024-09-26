package sandbox;

import graphics.G;
import graphics.WinApp;
import music.UC;
import reaction.Ink;
import java.awt.*;
import java.awt.event.MouseEvent;

public class PaintInk extends WinApp {
    public static Ink.List inkList = new Ink.List();

    public PaintInk(){
        super("paint ink", UC.screenWidth, UC.screenHeight);
        //TEST: inkList.add(new Ink());
    }

    public void paintComponent(Graphics g){
        G.bkWhite(g);
//        g.setColor(Color.RED);
//        g.fillRect(100,100,100,100);
        g.setColor(Color.RED);
        inkList.show(g);
        Ink.BUFFER.show(g);
    }

    public void mousePressed(MouseEvent me){Ink.BUFFER.dn(me.getX(), me.getY());repaint();}
    public void mouseDragged(MouseEvent me){Ink.BUFFER.drag(me.getX(), me.getY());repaint();}
    public void mouseReleased(MouseEvent me){
        Ink.BUFFER.up(me.getX(), me.getY());
        inkList.add(new Ink());
        repaint();
    }

    public static void main(String[] args){
        PANEL = new PaintInk();
        WinApp.launch();
    }
}
