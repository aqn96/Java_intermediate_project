package music;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import reaction.Gesture;
import reaction.Reaction;

public class Stem extends Duration implements Comparable<Stem> {
  public Staff staff;
  public Head.List heads = new Head.List();
  public boolean isUp = false;
  public Beam beam = null;

  public Stem(Staff staff, Head.List heads, boolean up) {
    this.staff = staff;
    isUp = up;
    for(Head head : heads) {
      head.unStem();
      head.stem = this;
    }
    this.heads = heads;
    staff.sys.stems.addStem(this);
    setWrongSide();

    addReaction(new Reaction("E-E"){
      public int bid(Gesture g) {
        int y = g.vs.yM(), x1 = g.vs.xL(), x2 = g.vs.xH();
        int xS = Stem.this.x();
        if(x1 > xS || x2 < xS){return UC.noBid;}
        int y1 = Stem.this.yLo(), y2 = Stem.this.yHi();
        if(y < y1 || y > y2){return UC.noBid;}
        return Math.abs(y-(y1+y2)/2) + 60;
      }
      public void act(Gesture g) {
        Stem.this.incFlag();
      }
    });

    addReaction(new Reaction("W-W"){
      public int bid(Gesture g) {
        int y = g.vs.yM(), x1 = g.vs.xL(), x2 = g.vs.xH();
        int xS = Stem.this.x();
        if(x1 > xS || x2 < xS){return UC.noBid;}
        int y1 = Stem.this.yLo(), y2 = Stem.this.yHi();
        if(y < y1 || y > y2){return UC.noBid;}
        return Math.abs(y-(y1+y2)/2);
      }
      public void act(Gesture g) {
        Stem.this.decFlag();
        if(nFlag == 0 && beam != null){
          beam.deleteBeam();
        }
      }
    });
  }

  public static Stem getStem(Staff staff, Time time, int y1, int y2, boolean up) {
    Head.List heads = new Head.List();
    for(Head h: time.heads){
      int yH = h.y();
      if(yH > y1 && yH < y2){
        heads.add(h);
      }
    }
    if(heads.size() == 0){return  null;}
    Beam b = internalStem(staff.sys, time.x, y1, y2); // possible this is internal stem in beam group.
    Stem res = new Stem(staff, heads, up);
    if(b!=null){
      b.addStem(res);
      res.nFlag = 1;
    }
    return res;
  }

  public static Beam internalStem(Sys sys, int x, int y1, int y2) {
    for (Stem s: sys.stems) {
      if(s.beam != null && s.x() < x && s.yLo() < y2 && s.yHi() > y1){
        int bx = s.beam.first().x(), by = s.beam.first().yBeamEnd();
        int ex = s.beam.last().x(), ey = s.beam.last().yBeamEnd();
        if(Beam.verticalLineCrossSegment(x, y1, y2, bx, by, ex, ey)) {return s.beam;}
      }
    }
    return null;
  }
  @Override
  public void show(Graphics g) {
//    System.out.println("stem show"+ nFlag + " "+ heads.size());
    if(nFlag >= -1 && heads.size() > 0){
      int x = x(), H = staff.fmt.H, yH = yFirstHead(), yB = yBeamEnd();
      g.setColor(Color.BLACK);
      g.drawLine(x, yH, x, yB);
      if(nFlag > 0 && beam == null){
        if(nFlag == 1){(isUp ? Glyph.FLAG1D : Glyph.FLAG1U).showAt(g, H, x(), yBeamEnd());}
        if(nFlag == 2){(isUp ? Glyph.FLAG2D : Glyph.FLAG2U).showAt(g, H, x(), yBeamEnd());}
        if(nFlag == 3){(isUp ? Glyph.FLAG3D : Glyph.FLAG3U).showAt(g, H, x(), yBeamEnd());}
        if(nFlag == 4){(isUp ? Glyph.FLAG4D : Glyph.FLAG4U).showAt(g, H, x(), yBeamEnd());}
      }
    }
  }

  public Head firstHead(){
    return heads.get(isUp ? heads.size()-1 : 0);
  }

  public Head lastHead(){
    return heads.get(isUp ? 0 : heads.size()-1);
  }

  public int yLo(){return isUp ? yBeamEnd() : yFirstHead();}
  public int yHi(){return isUp ? yFirstHead() : yBeamEnd();}

  public int yFirstHead(){
    if(heads.size() == 0){return 200;}
    Head head = firstHead();
    return head.staff.yOfLine(head.line);
  }

  public int yBeamEnd(){
    if(heads.size() == 0){return 100;}
    if(isInternalStem()){beam.setMasterBeam(); return Beam.yOfX(x());}
    Head head = lastHead();
    int line = head.line;
    line += isUp ? -7 : 7; // default one octeve
    int flagIncrement = nFlag > 2 ? 2*(nFlag-2) : 0;
    line += isUp ? -flagIncrement : flagIncrement;
    if((isUp && line > 4) || (!isUp && line < 4)){ // hit center line if possible
      line = 4;
    }
    return staff.yOfLine(line);
  }

  public boolean isInternalStem(){
    if(beam == null){return false;}
    if(this == beam.first() || this == beam.last()){return false;}
    return true;
  }

  public int x(){
    if(heads.size() == 0){return 100;}
    Head head = firstHead();
    return head.time.x+(isUp ? head.w() : 0);
  }

  public void deleteStem() {
    staff.sys.stems.remove(this);
    if(beam != null) beam.removeStem(this);
    deleteMass();
  }

  public void setWrongSide() {
    Collections.sort(heads);
    int i, last, next;
    if(isUp){i = heads.size()-1; last = 0; next = -1;} else{i = 0; last = heads.size()-1; next = 1;}
    Head ph = heads.get(i);
    ph.wrongSide = false;
    while(i != last){
      i += next;
      Head nh = heads.get(i);
      nh.wrongSide = (ph.staff == nh.staff && (Math.abs(nh.line-ph.line) <= 1) && !ph.wrongSide);
      ph = nh;
    }
  }

  public int compareTo(Stem s) {
    return x()-s.x();
  }

  //-------------List-------------------
  public static class List extends ArrayList<Stem> {
    public int yMin = 1_000_000, yMax = -1_000_000;
    public void addStem(Stem s){
      add(s);
      if(s.yLo() < yMin){yMin = s.yLo();}
      if(s.yHi() > yMax){yMax = s.yHi();}
    }

    public boolean fastReject(int y){return y > yMax || y < yMin;}
    public ArrayList<Stem> allIntersectors(int x1, int y1, int x2, int y2){
      ArrayList<Stem> res = new ArrayList<>();
      for(Stem s : this){
        if(Beam.verticalLineCrossSegment(s.x(), s.yLo(), s.yHi(), x1, y1, x2, y2)){
          res.add(s);
        }
      }
      return res;
    }
    public void sort(){Collections.sort(this);}
  }

}
