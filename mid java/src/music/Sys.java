package music;

import graphics.G;
import graphics.G.HC;
import java.awt.Graphics;
import java.util.ArrayList;
import reaction.Gesture;
import reaction.Mass;
import reaction.Reaction;

public class Sys extends Mass {
  public Page page;
  public int iSys;
  public Staff.List staffs;
  public Time.List times;
  public Stem.List stems = new Stem.List();
  public Key initialKey = new Key();

  public Sys(Page page, G.HC sysTop) {// stub
    super("BACK");
    this.page = page;
    iSys = page.sysList.size();
    staffs = new Staff.List(sysTop);
    times = new Time.List(this);
    if(iSys == 0){
      staffs.add(new Staff(this, 0, new G.HC(sysTop, 0), new Staff.Fmt(5, 8)));
    }else{
      Sys oldSys = page.sysList.get(0);
      for(Staff oldStaff : oldSys.staffs){
        Staff ns = oldStaff.copy(this);
        this.staffs.add(ns);
      }
    }
    addReaction(new Reaction("E-E"){

      public int bid(Gesture g) {
        int x1 = g.vs.xL(), y1 = g.vs.yL(), x2 = g.vs.xH(), y2 = g.vs.yH();
        if(stems.fastReject((y1+y2)/2)){return UC.noBid;}
        ArrayList<Stem> tmp = stems.allIntersectors(x1, y1, x2, y2);
        if(tmp.size() < 2) {return UC.noBid;}
        System.out.println("sys reaction crossed: " + tmp.size() + " stems");
        Beam b = tmp.get(0).beam;
        for(Stem s: tmp){
          if(s.beam != b){return UC.noBid;}
        }
        System.out.println("All stems share owner");
        if(b == null && tmp.size() != 2){return UC.noBid;}
        if(b == null && (tmp.get(0).nFlag != 0 || tmp.get(1).nFlag != 0)){return UC.noBid;}
        return 50;
      }

      public void act(Gesture g) {
        int x1 = g.vs.xL(), y1 = g.vs.yL(), x2 = g.vs.xH(), y2 = g.vs.yH();
        ArrayList<Stem> tmp = stems.allIntersectors(x1, y1, x2, y2);
        Beam b = tmp.get(0).beam;
        if(b == null){
          new Beam(tmp.get(0), tmp.get(1));
        }else{
          for(Stem s: tmp){
            s.incFlag();
          }
        }
      }
    });

    addReaction(new Reaction("E-E") {
      @Override
      public int bid(Gesture g) {
        int x = page.margins.left;
        int x1 = g.vs.xL(), x2 = g.vs.xH();
        if(x1 > x || x2 < x){return UC.noBid;}
        int y = g.vs.yM();
        if(y < yTop() || y > yBot()){
          return UC.noBid;
        }
        return Math.abs(x-(x1+x2)/2);
      }

      @Override
      public void act(Gesture g) {
        Sys.this.incKey();
      }
    });

    addReaction(new Reaction("W-W") {
      @Override
      public int bid(Gesture g) {
        int x = page.margins.left;
        int x1 = g.vs.xL(), x2 = g.vs.xH();
        if(x1 > x || x2 < x){return UC.noBid;}
        int y = g.vs.yM();
        if(y < yTop() || y > yBot()){
          return UC.noBid;
        }
        return Math.abs(x-(x1+x2)/2);
      }

      @Override
      public void act(Gesture g) {
        Sys.this.decKey();
      }
    });
  }

  public void incKey(){
    if(initialKey.n < 7){initialKey.n++;}
    initialKey.glyph = initialKey.n >= 0 ? Glyph.SHARP: Glyph.FLAT;
  }

  public void decKey(){
    if(initialKey.n > -7){initialKey.n--;}
    initialKey.glyph = initialKey.n >= 0 ? Glyph.SHARP: Glyph.FLAT;
  }


  public Time getTime(int x){return times.getTime(x);}

  public int yTop(){
    return staffs.sysTop();
  }

  public int yBot(){
    return staffs.get(staffs.size()-1).yBot();
  }

  public int height(){
    return yBot()-yTop();
  }

  public void addNewStaff(int y){
    int off = y-staffs.sysTop();
    G.HC staffTop = new HC(staffs.sysTop, off);
    staffs.add(new Staff(this, staffs.size(), staffTop, new Staff.Fmt(5, 8)));
    page.updateMaxH();
  }

  public void show(Graphics g){
    int x = page.margins.left;
    g.drawLine(x, yTop(), x, yBot());
    page.show(g);
    int xKey = x + UC.marginKeyOffset;
    initialKey.drawOnSys(g, this, xKey);
  }

  //---------------------List------------------
  public static class List extends ArrayList<Sys>{

  }
}
