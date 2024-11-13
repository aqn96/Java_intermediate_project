package sandbox;

import graphics.G;
import graphics.WinApp;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import music.UC;
import reaction.Gesture;
import reaction.Ink;
import reaction.Layer;
import reaction.Mass;
import reaction.Reaction;

public class ReactionTest extends WinApp {
  static {
    new Layer("BACK");
    new Layer("FRONT");
  }

  public ReactionTest() {
    super("ReactionTest", UC.screenWidth, UC.screenHeight);
    Reaction.initialReactions.addReaction(new Reaction("SW-SW"){
      public int bid(Gesture g){return 0;}
      public void act(Gesture g){new Box(g.vs);}
    });
  }

  public void paintComponent(Graphics g) {
    G.fillBackground(g, Color.WHITE);
    g.setColor(Color.BLUE);
    Layer.ALL.show(g);
    Ink.BUFFER.show(g);
    g.drawString(Gesture.recognized, 900, 30);
  }

  public void mousePressed(MouseEvent me) {Gesture.AREA.dn(me.getX(), me.getY()); repaint();}
  public void mouseDragged(MouseEvent me) {Gesture.AREA.drag(me.getX(), me.getY()); repaint();}
  public void mouseReleased(MouseEvent me) {Gesture.AREA.up(me.getX(), me.getY()); repaint();}


  //------------------Box-------------------
  public static class Box extends Mass {
    public G.VS vs;
    public Color c = G.rndColor();

    public Box(G.VS vs) {
      super("BACK");
      this.vs = vs;

      addReaction(new Reaction("S-S") {
        public int bid(Gesture g) {
          int x = g.vs.xM(), y = g.vs.yL();
          if(Box.this.vs.hit(x, y)){
            return Math.abs(x-Box.this.vs.xM());
          } else{
            return UC.noBid;
          }
        }
        public void act(Gesture g) {Box.this.deleteMass();}
      });
    }

    public void show(Graphics g) {vs.fill(g, c);}
  }

  public static void main(String[] args) {PANEL = new ReactionTest(); WinApp.launch();}
}
