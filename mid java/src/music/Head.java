package music;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import reaction.Gesture;
import reaction.Mass;
import reaction.Reaction;

public class Head extends Mass implements Comparable<Head> {
  public Staff staff;
  public int line;
  public Time time;
  public Glyph forcedGlyph;
  public Stem stem = null;
  public boolean  wrongSide = false;
  public Accid accid = null;

  public Head(Staff staff, int x, int y) {
    super("NOTE");
    this.staff = staff;
    time = staff.sys.getTime(x);
    time.heads.add(this);
    line = staff.lineOfy(y);

    // stem or un stem heads
    addReaction(new Reaction("S-S") {
      public int bid(Gesture g) {
        int x = g.vs.xM(), y1 = g.vs.yL(), y2 = g.vs.yH();
        int W = Head.this.w(), y = Head.this.y();
        if(y1 > y || y2 < y){return UC.noBid;}
        int hL = Head.this.time.x, hR = hL+W;
        if(x < hL-W || x > hR+W){return UC.noBid;}
        if(x < hL+W/2) {return hL-x;}
        if(x > hR-W/2) {return x-hR;}
        return UC.noBid;
      }

      public void act(Gesture g) {
        int x = g.vs.xM(), y1 = g.vs.yL(), y2 = g.vs.yH();
        Staff staff = Head.this.staff;
        Time time = Head.this.time;
        int w = Head.this.w();
        boolean up = x>(time.x+w/2);
        if(Head.this.stem == null){
          Stem.getStem(staff, time, y1, y2, up);
        }else{
          time.unStemHeads(y1, y2);
        }
      }
    });

    addReaction(new Reaction("DOT") {
      public int bid(Gesture g) {
        int xH = x(), yH = y(), H = staff.fmt.H, W = Head.this.w();
        int x = g.vs.xM(), y = g.vs.yM();
        if(x < xH || x > xH+2*W || y < yH-H || y > yH+H){return UC.noBid;}
        return Math.abs(xH+W-x)+Math.abs(yH-y);
      }

      public void act(Gesture g) {
        if(Head.this.stem != null){
          Head.this.stem.cycleDot();
        }
      }
    });

    addReaction(new Reaction("NE-SE") {
      @Override
      public int bid(Gesture g) {
        int x = g.vs.xM(), y = g.vs.yL(); // center top for gesture up arrow
        int hX = Head.this.x() + Head.this.w() / 2, hY = Head.this.y();
        int dx = Math.abs(x - hX), dy = Math.abs(y - hY), diff = dx + dy;
        return diff < 50 ? diff:UC.noBid;
      }

      @Override
      public void act(Gesture g) {
        Head.this.accidUp();
      }
    });

    addReaction(new Reaction("SE-NE") {
      @Override
      public int bid(Gesture g) {
        int x = g.vs.xM(), y = g.vs.yH(); // center top for gesture down arrow
        int hX = Head.this.x() + Head.this.w() / 2, hY = Head.this.y();
        int dx = Math.abs(x - hX), dy = Math.abs(y - hY), diff = dx + dy;
        return diff < 50 ? diff:UC.noBid;
      }

      @Override
      public void act(Gesture g) {
        Head.this.accidDn();
      }
    });

    addReaction(new Reaction("S-N") {
      @Override
      public int bid(Gesture g) {
        int x = g.vs.xM(), y = g.vs.yL();
        int hx = Head.this.x() + w()/2, hy = Head.this.y();
        int dx = Math.abs(x -hx), dy = Math.abs(y-hy), dist = dx + dy;
        return dist > 50? UC.noBid : dist;
      }

      @Override
      public void act(Gesture g) {
        Head.this.deleteHead();
      }
    });
  }

  private void deleteHead() {
    if(this.accid != null){
      accid.deleteAccid();
    }
    unStem();
    deleteMass();
  }

  public void accidUp() {
    if (accid == null) {
      accid = new Accid( Accid.SHARP,this);
      return;
    }
    if (accid.iGlyph < 4) {
      accid.iGlyph++;
    }
  }

  public void accidDn() {
    if (accid == null) {
      accid = new Accid( Accid.FLAT,this);
      return;
    }
    if (accid.iGlyph < 4) {
      accid.iGlyph--;
    }
  }

  public int w(){
    return staff.fmt.H*24/10;
  }

  public void show(Graphics g) {
//    g.setColor(wrongSide ? Color.GREEN : Color.BLUE);
//    if(stem != null && stem.heads.size() != 0 && this == stem.firstHead()){
//      g.setColor(Color.RED);
//    }
    int H = staff.fmt.H;
    g.setColor(stem == null ? Color.BLUE : Color.BLACK);
    (forcedGlyph != null ? forcedGlyph : normalGlyph()).showAt(g, H, x(), y());
    if(stem != null){
      int off = UC.argDotOffset, sp = UC.argDotSpacing;
      for(int i = 0; i < stem.nDot; i++){
        g.fillOval(time.x+off+i*sp, y()-3*H/2, H*2/3, H*2/3);
      }
    }
    g.setColor(Color.BLACK);
  }

  public Glyph normalGlyph() {
    if(stem == null){return Glyph.HEAD_Q;}
    if(stem.nFlag == -1){return Glyph.HEAD_HALF;}
    if(stem.nFlag == -2){return Glyph.HEAD_W;}
    return Glyph.HEAD_Q;
  }

  public int y(){return staff.yOfLine(line);}
  public int x(){
    int res = time.x;
    if(wrongSide){res += (stem != null && stem.isUp) ? w() : -w();}
    return res;
  }

  // stub
  public void delete(){
    time.heads.remove(this);
  }

  public void unStem() {
    if(stem != null) {
      stem.heads.remove(this);
      if(stem.heads.size() == 0) {
        stem.deleteStem();
        stem = null;
        wrongSide = false;
      }
    }
  }

  public void joinStem(Stem s) {
    if(stem != null) {unStem();}
    s.heads.add(this);
    stem = s;
  }

  public int compareTo(Head h) {
    return (staff.iStaff != h.staff.iStaff) ? staff.iStaff-h.staff.iStaff : line-h.line;
  }

  //------------------List-----------------
  public static class List extends ArrayList<Head> {
  }
}
