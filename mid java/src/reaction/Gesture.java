package reaction;

import graphics.G;
import java.util.ArrayList;
import music.I;
import music.I.Area;

public class Gesture {
  private static List UNDO = new List();
  public static I.Area AREA = new Area() {
    public boolean hit(int x, int y) {return true;}
    public void dn(int x, int y) {Ink.BUFFER.dn(x, y);}
    public void drag(int x, int y) {Ink.BUFFER.drag(x, y);}
    public void up(int x, int y) {
      Ink.BUFFER.up(x, y);
      Ink ink = new Ink();
      Gesture gest = Gesture.getNew(ink); // can fail if unrecognized
      Ink.BUFFER.clear();
      recognized = gest == null ? "NULL" : gest.shape.name;
      if(gest != null){
        if(gest.shape.name.equals("N-N")){
          undo();
        } else{
          gest.doGesture();
        }
      }
    }
  };
  public static String recognized = "NULL";

  public Shape shape;
  public G.VS vs;

  private Gesture(Shape shape, G.VS vs) {
    this.shape = shape;
    this.vs = vs;
  }

  private void redoGesture() {
    Reaction r = Reaction.best(this);
    if(r != null){r.act(this);}
  }

  private void doGesture() {
    Reaction r = Reaction.best(this);
    if(r != null){UNDO.add(this); r.act(this);}
    else{recognized += " no bids";}
  }

  public static Gesture getNew(Ink ink){
    Shape s = Shape.recognize(ink);
    return s == null ? null : new Gesture(s, ink.vs);
  }

  public static void undo(){
    if(UNDO.size() > 0){
      UNDO.remove(UNDO.size() - 1);
      Layer.nuke(); // eliminating all masses.
      Reaction.nuke(); // clears byShape then reloads initial reactions.
      UNDO.redo();
    }
  }
  //------------------------List--------------------------
  public static class List extends ArrayList<Gesture> {

    private void redo(){for(Gesture gest: this){gest.redoGesture();}}
  }
}
