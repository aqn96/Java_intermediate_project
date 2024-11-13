package music;

import java.awt.Graphics;
import reaction.Gesture;
import reaction.Mass;
import reaction.Reaction;

public class Bar extends Mass {
  /*
  0 - single,
  1 - double,
  2 - fine,
  if either left or right set, it repeats
   */
  private static final int FAT = 0x2, RIGHT = 0x4, LEFT = 0x8;
  public Sys sys;
  public int x, barType = 0;
  public Key key = null;


  public Bar(Sys sys, int x) {
    super("BACK");
    this.sys = sys;
    int right = sys.page.margins.right;
    this.x = Math.abs(right - x) < UC.barToMarginSnap? right : x;

    addReaction(new Reaction("S-S") { // cycle bar type
      public int bid(Gesture g) {
        int x = g.vs.xM();
        if(Math.abs(x-Bar.this.x) > UC.barToMarginSnap){return UC.noBid;}
        int y1 = g.vs.yL(), y2 = g.vs.yH();
        if(y1 < Bar.this.sys.yTop() - 20 || y2 > Bar.this.sys.yBot() - 20){ return UC.noBid;}
        return Math.abs(x - Bar.this.x);
      }

      public void act(Gesture g) {
        Bar.this.cycleType();
      }
    });

    addReaction(new Reaction("DOT") { // toggle repeat
      public int bid(Gesture g) {
        int x = g.vs.xM(), y = g.vs.yM();
        if(y < Bar.this.sys.yTop() || y > Bar.this.sys.yBot()){return UC.noBid;}
        int dist = Math.abs(x - Bar.this.x);
        if(dist > 3*sys.page.maxH) return UC.noBid;
        return dist;
      }
      public void act(Gesture g) {
        if(g.vs.xM() < Bar.this.x){Bar.this.toggleLeft();} else{Bar.this.toggleRight();}
      }
    });

    addReaction(new Reaction("E-E") { // increments key on double bar
      @Override
      public int bid(Gesture g) {
        if(barType != 1){return UC.noBid;}
        int x1 = g.vs.xL(), x2 = g.vs.xH();
        if(x1 > x || x2 < x){return UC.noBid;}
        int y = g.vs.yM();
        if(y < sys.yTop() || y > sys.yBot()){return UC.noBid;}
        return Math.abs(x - (x1+x2)/2);
      }

      @Override
      public void act(Gesture g) {
        Bar.this.incKey();
      }
    });

    addReaction(new Reaction("W-W") { // decrement key on double bar
      @Override
      public int bid(Gesture g) {
        if(barType != 1){return UC.noBid;}
        int x1 = g.vs.xL(), x2 = g.vs.xH();
        if(x1 > x || x2 < x){return UC.noBid;}
        int y = g.vs.yM();
        if(y < sys.yTop() || y > sys.yBot()){return UC.noBid;}
        return Math.abs(x - (x1+x2)/2);
      }

      @Override
      public void act(Gesture g) {
        Bar.this.decKey();
      }
    });

    addReaction(new Reaction("S-N") {
      @Override
      public int bid(Gesture g) {
        int x = g.vs.xM(), y = g.vs.yL();
        int y1 = Bar.this.sys.yTop(), y2 = Bar.this.sys.yBot();
        if(y < y1 || y > y2){return UC.noBid;}
        int ax = Bar.this.x;
        int dx = Math.abs(x - ax), dist = dx + 16;
        return dist > 50 ? UC.noBid : dist;
      }

      @Override
      public void act(Gesture g) {
        Bar.this.deleteBar();
      }
    });
  }

  public void deleteBar(){
    deleteMass();
  }

  private void incKey(){
    if(key == null){
      key = new Key();
    }
    if(key.glyph == Glyph.NATURAL){key.glyph = Glyph.SHARP; key.n = 1; return;}
    if(key.glyph == Glyph.FLAT){key.glyph = Glyph.NATURAL; return;}
    if(key.n < 7){key.n++;}
  }

  private void decKey(){
    if(key == null){
      key = new Key();
    }
    if(key.glyph == Glyph.NATURAL){key.glyph = Glyph.FLAT; key.n = -1; return;}
    if(key.glyph == Glyph.SHARP){key.glyph = Glyph.NATURAL; return;}
    if(key.n > -7){key.n--;}
  }

  public void cycleType(){barType++; if(barType > 2) barType = 0;}
  public void toggleLeft(){barType = barType^LEFT;}
  public void toggleRight(){barType = barType^RIGHT;}

  public void show(Graphics g){
//    g.setColor(barType == 1 ? Color.RED : Color.BLACK);
//    for(Staff staff: sys.staffs){
//      g.drawLine(x, staff.yTop(), x, staff.yBottom());
//    }
    int sysTop = sys.yTop(), y1 = 0, y2 = 0;
    boolean justSawBreak = true;
    for(int i = 0; i < sys.staffs.size(); i++){
      Staff staff = sys.staffs.get(i);
      int staffTop = staff.yTop();
      if (justSawBreak) {y1 = staffTop;}
      y2 = staff.yBot();
      justSawBreak = !staff.fmt.barContinues;
      if(justSawBreak){drawLines(g, x, y1, y2);}
      if(barType > 3){drawDots(g, x, staffTop);}
    }
    if(barType == 1 && key != null){
      key.drawOnSys(g, sys, x+UC.barKeyOffSet);
    }
  }

  public void drawLines(Graphics g, int x, int y1, int y2){
    int H = sys.page.maxH;
    if(barType == 0){thinBar(g, x, y1, y2);}
    if(barType == 1){thinBar(g, x, y1, y2); thinBar(g, x-H, y1, y2);}
    if(barType == 2){fatBar(g, x-H, y1, y2, H); thinBar(g, x-2*H, y1, y2);}
    if(barType >= 4){
      fatBar(g, x-H, y1, y2, H);
      if((barType & LEFT) != 0){
        thinBar(g, x-2*H, y1, y2); wings(g, x-2*H, y1, y2, -H, H);}
      if((barType & RIGHT) != 0){
        thinBar(g, x+H, y1, y2); wings(g, x+H, y1, y2, H, H);}
    }
  }

  public static void wings(Graphics g, int x, int y1, int y2, int dx, int dy){
    g.drawLine(x, y1, x+dx, y1-dy);
    g.drawLine(x, y2, x+dx, y2+dy);
  }

  public static void fatBar(Graphics g, int x, int y1, int y2, int dx){
    g.fillRect(x, y1, dx, y2-y1);
  }

  public static void thinBar(Graphics g, int x, int y1, int y2){
    g.drawLine(x, y1, x, y2);
  }
  public void drawDots(Graphics g, int x, int top){
    int H = sys.page.maxH;
    if((barType & LEFT) != 0){
      g.fillOval(x-3*H, top+11*H/4, H/2, H/2);
      g.fillOval(x-3*H, top+19*H/4, H/2, H/2);
    }

    if((barType & RIGHT) != 0){
      g.fillOval(x+3*H/2, top+11*H/4, H/2, H/2);
      g.fillOval(x+3*H/2, top+19*H/4, H/2, H/2);
    }
  }
}
