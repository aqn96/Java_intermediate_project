package music;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import reaction.Mass;

public class Beam extends Mass {
  public Stem.List stems = new Stem.List();

  public Beam(Stem first, Stem last) {
    super("NOTE");
    addStem(first);
    addStem(last);
  }

  public Stem first(){return stems.get(0);}
  public Stem last(){return stems.get(stems.size()-1);}
  public void deleteBeam(){
    for(Stem s: stems){
      s.beam = null;
    }
    deleteMass();
  }
  public void addStem(Stem stem){
    if(stem.beam == null){
      stems.add(stem);
      stem.beam = this;
      stem.nFlag = 1;
      stems.sort();
    }
  }

  public void setMasterBeam(){
    mx1 = first().x();
    my1 = first().yBeamEnd();
    mx2 = last().x();
    my2 = last().yBeamEnd();
  }

  public void show(Graphics g){
    g.setColor(Color.BLACK);
    drawBeamGroup(g);
  }

  private void drawBeamGroup(Graphics g){
    setMasterBeam();
    Stem firstStem = first();
    int H = firstStem.staff.fmt.H;
    int sH = firstStem.isUp? H : -H;
    int nPrev = 0;
    int nCur = firstStem.nFlag, nNext = stems.get(1).nFlag;
    int px, cx = firstStem.x(), bx = cx+3*H; // px=location of prev stem, cx = location of prev stem, bx = location of beamEnd
    if(nCur > nNext){
      drawBeamStack(g, nNext, nCur, cx, bx, sH); // draw beams first stems
    }
    for(int cur = 1; cur < stems.size(); cur++){
      Stem sCur = stems.get(cur);
      px = cx;
      cx = sCur.x();
      nPrev = nCur;
      nCur = nNext;
      nNext = cur < stems.size()-1 ? stems.get(cur+1).nFlag : 0;
      int nBack = Math.min(nPrev, nCur);
      drawBeamStack(g, 0, nBack, px, cx, sH);
      if (nCur > nPrev && nCur > nNext){
        if(nPrev < nNext){
          bx = cx+3*H;
          drawBeamStack(g, nNext, nCur, cx, bx, sH);
        }else{
          bx = cx-3*H;
          drawBeamStack(g, nPrev, nCur, bx, cx, sH);
        }
      }
    }
  }


  public void removeStem(Stem stem) {
    if(stem == first() || stem == last()){
      deleteBeam();
    }else{
      stems.remove(stem);
      stems.sort();
    }
  }

  public static int yOfX(int x, int x1, int y1, int x2, int y2){
    int dy = y2 - y1;
    int dx = x2 - x1;
    return (x - x1)*dy/dx + y1;
  }

  public static int mx1, my1, mx2, my2; // coordinates for master beam
  public static int yOfX(int x){
    int dy = my2 - my1, dx = mx2 - mx1;
    return (x - mx1)*dy/dx + my1;
  }

  public static void setMasterBeam(int x1, int y1, int x2, int y2){mx1 = x1; my1 = y1; mx2 = x2; my2 = y2;}

  private static final int[] points = {0,0,0,0};
  public static Polygon poly = new Polygon(points, points, 4);
  public static void setPoly(int x1, int y1, int x2, int y2, int H){
    int[] a = poly.xpoints;
    a[0] = x1; a[1] = x2; a[2] = x2; a[3] = x1;
    a = poly.ypoints;
    a[0] = y1; a[1] = y2; a[2] = y2+H; a[3] = y1+H;
  }

  public static void drawBeamStack(Graphics g, int n1, int n2, int x1, int x2, int H){
    int y1 = yOfX(x1), y2 = yOfX(x2);
    for(int i = n1; i < n2; i++){
      setPoly(x1, y1+i*2*H, x2, y2+i*2*H, H);
      g.fillPolygon(poly);
    }
  }

  public static boolean verticalLineCrossSegment(int x, int y1, int y2, int bx, int by, int ex, int ey) {
    if(x < bx || x > ex){
      return false;
    }
    int y = yOfX(x, bx, by, ex, ey);
    if(y1 < y2){
      return y1 < y && y < y2;
    }else{
      return y1 > y && y > y2;
    }
  }

}
