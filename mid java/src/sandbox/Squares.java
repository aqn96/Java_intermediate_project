package sandbox;
import graphics.WinApp;
import graphics.G;
import music.I;
import music.UC;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;


public class Squares extends WinApp implements ActionListener {
  public static G.VS theVS = new G.VS(100,100,200,300);
  public static Color color = G.rndColor();
  public static boolean dragging = false;
  public static G.V mouseDelta = new G.V(0,0);
  public static boolean showSpine = false;
  public static Timer timer;
  public static G.V pressedLoc= new G.V(0,0);
  public static I.Area curArea;
  public static Square lastSquare;
  public static Square.List squares = new Square.List();

  public Squares(){
    super("Squares", UC.screenWidth, UC.screenHeight);
    timer = new Timer(30, this);
    timer.setInitialDelay(5000);
    timer.start();
  }

  @Override
  public void paintComponent(Graphics g){
    G.fillBack(g);
    squares.draw(g);
    if(showSpine && squares.size()>2){
      g.setColor(Color.BLACK);
      G.V a = squares.get(0).loc,  b = squares.get(1).loc, c = squares.get(2).loc;
      G.spline(g, a.x, a.y, b.x, b.y, c.x, c.y, 5);
    }
  }

  @Override
  public void mousePressed(MouseEvent me){
    int x = me.getX(), y = me.getY();
//    lastSquare = squares.hit(x,y);
//    if(lastSquare == null){
//      dragging = false;
//      lastSquare = new Square(x, y);
//      squares.add(lastSquare);
//    }else{
//      dragging = true;
//      lastSquare.dv.set(0, 0);
//      pressedLoc.set(x, y);
//      mouseDelta.set(lastSquare.loc.x - x, lastSquare.loc.y - y);
//    }
    curArea = squares.hit(x, y);
    curArea.dn(x, y);
    repaint();
  }

  @Override
  public void mouseDragged(MouseEvent me){
    int x = me.getX(), y = me.getY();
//    if(dragging){
//      lastSquare.moveto(x + mouseDelta.x, y + mouseDelta.y);
//    }else {
//      lastSquare.resize(x, y);
//    }
    curArea.drag(x, y);
    repaint();
  }

  @Override
  public void mouseReleased(MouseEvent me){
//    if(dragging){
//      lastSquare.dv.set(me.getX()-pressedLoc.x, me.getY()-pressedLoc.y);
//    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    repaint();
  }

  public static void main(String[] args){PANEL=new Squares();WinApp.launch();}


  //-----------------Square------------------------------
  public static class Square extends G.VS implements I.Draw, I.Area{
    public Color c = G.rndColor();
    // public G.V dv = new G.V(G.rnd(20)-10, G.rnd(20)-10);
    public G.V dv = new G.V(0, 0);
    public static Square BACKGROUND = new Square(){
      public void dn(int x, int y) {lastSquare = new Square(x, y); squares.add(lastSquare);}
      public void drag(int x, int y) {lastSquare.resize(x, y);}
    };

    public Square(int x, int y){super(x,y,50,50);}
    public Square(){super(0, 0, 5000, 5000); c = Color.WHITE;}

    public void draw(Graphics g){fill(g, c); moveAndBounce();}
    public void resize(int x, int y){if(x > loc.x && y > loc.y){size.set(x - loc.x, y - loc.y);}}
    public void moveto(int x, int y){loc.set(x,y);}
    public void moveAndBounce(){
      loc.add(dv);
      if(xL()<0 && dv.x<0){dv.x= - dv.x;}
      if(xH()>800 && dv.x>0){dv.x= - dv.x;}
      if(yL()<0 && dv.y<0){dv.y= - dv.y;}
      if(yH()>800 && dv.y>0){dv.y= - dv.y;}
    }

    @Override
    public void dn(int x, int y) {mouseDelta.set(loc.x - x, loc.y - y);}

    @Override
    public void drag(int x, int y) {loc.set(x+mouseDelta.x, y+mouseDelta.y);}

    @Override
    public void up(int x, int y) {}

    //------------------List----------------------------
    public static class List extends ArrayList<Square> {

      public List(){super(); add(Square.BACKGROUND);}
      public void draw(Graphics g){for(Square s : this){s.draw(g);}}
      public Square hit(int x, int y){
        Square res = null;
        for(Square s: this){
          if(s.hit(x, y)){res = s;}
        }
        return res;
      }
    }
  }
}