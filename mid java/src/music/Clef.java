package music;

import graphics.G;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;

import reaction.Gesture;
import reaction.Mass;
import reaction.Reaction;

public class Clef extends Mass implements Comparable<Clef>{
  public Glyph glyph;
  public int x;
  public Staff staff;

  public Clef (Staff staff, int x, Glyph glyph) {
    super("NOTE");
    this.staff = staff;
    this.x = x;
    this.glyph = glyph;

    addReaction(new Reaction("S-N") {
      @Override
      public int bid(Gesture g) {
        int x = g.vs.xM(), y = g.vs.yL();
        int ax = Clef.this.x + staff.fmt.H*2, ay = staff.yOfLine(4);
        int dx = Math.abs(x - ax), dy = Math.abs(y - ay), dist = dx+dy;
        return dist > 50 ? UC.noBid : dist;
      }

      @Override
      public void act(Gesture g) {
        Clef.this.deleteClef();
      }
    });
  }

  public void deleteClef(){
    staff.clefs.remove(this);
    if(staff.clefs.size() == 0){staff.clefs = null;} else {Collections.sort(staff.clefs);}
    deleteMass();
  }

  public void show(Graphics g) {
    glyph.showAt(g, staff.fmt.H, x, staff.yOfLine(4));
  }

  @Override
  public int compareTo(Clef c) {
    return  x - c.x;
  }

  //---------------List---------------
  public static class List extends ArrayList<Clef> {
  }
}
