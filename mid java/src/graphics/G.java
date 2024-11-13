package graphics;

import java.awt.*;
import java.io.Serializable;
import java.util.Random;

public class G{
  public static Random RND = new Random();
  public static final VS BACKGROUND_RECT = new VS(0,0,3000,3000);
  public static G.V LEFT = new G.V(-1, 0);
  public static G.V RIGHT = new G.V(1, 0);
  public static G.V UP = new G.V(0, -1);
  public static G.V DOWN = new G.V(0, 1);
  public static int rnd(int max){return RND.nextInt(max);}
  public static Color rndColor(){return new Color(rnd(256),rnd(256),rnd(256)); }
  public static void fillBack(Graphics g){g.setColor(Color.WHITE); g.fillRect(0,0,3000,3000);}
  public static void fillBackground(Graphics g, Color c){BACKGROUND_RECT.fill(g,c);}
  public static void bkWhite(Graphics g){fillBackground(g, Color.WHITE);;}
  public static void spline(Graphics g, int ax, int ay, int bx, int by, int cx, int cy, int n){
    if(n == 0){g.drawLine(ax, ay, cx, cy); return;}
    int abx = (ax+bx)/2, aby = (ay+by)/2;
    int bcx = (bx+cx)/2, bcy = (by+cy)/2;
    int abcx = (abx+bcx)/2, abcy = (aby+bcy)/2;
    spline(g, ax, ay, abx, aby, abcx, abcy, n-1);
    spline(g, abcx, abcy, bcx, bcy, cx, cy, n-1);
  }
  //-----------------------V------------------------
  public static class V implements Serializable {
    public static Transform T = new Transform();
    public int x,y;
    public V(int x, int y){this.set(x,y);}
    public V(V v){this.set(v);}
    public void set(int x, int y){this.x = x; this.y = y;}
    public void set(V v){this.x = v.x; this.y = v.y;}
    public void add(V v){x += v.x; y += v.y;} // vector addition
    public void blend(V v, int k){set((k*x+v.x)/(k+1), (k*y+v.y)/(k+1));}
    // Transform
    public void setT(V v){set(v.tx(), v.ty());}
    public int tx(){return x*T.n/T.d+T.dx;}
    public int ty(){return y*T.n/T.d+T.dy;}
    //------------------------Transform-------------
    public static class Transform{
      int dx, dy, n, d;
      public void set(VS oVS, VS nVS){
        setScale(oVS.size.x, oVS.size.y, nVS.size.x, nVS.size.y);
        dx = setOff(oVS.loc.x, nVS.loc.x);
        dy = setOff(oVS.loc.y, nVS.loc.y);
      }
      public void set(BBox oB, VS nVS){
        setScale(oB.h.size(), oB.v.size(), nVS.size.x, nVS.size.y);
        dx = setOff(oB.h.lo, nVS.loc.x);
        dy = setOff(oB.v.lo, nVS.loc.y);
      }
      public void setScale(int oW, int oH, int nW, int nH){
        n = (nW > nH)? nW : nH;
        d = (oW > oH)? oW : oH;
      }
      public int setOff(int oX, int nX){return -oX*n/d+nX;}
    }
  }
  //-----------------------VS-----------------------
  public static class VS implements Serializable {
    public V loc, size;
    public VS(int x, int y, int w, int h){loc = new V(x,y); size = new V(w,h);}
    public void fill(Graphics g, Color c){g.setColor(c); g.fillRect(loc.x,loc.y,size.x,size.y);}
    public boolean hit(int x, int y){return loc.x<=x && loc.y <=y && x<=(loc.x+size.x) && y<=(loc.y+size.y);}
    public int xL(){return loc.x;}
    public int xM(){return loc.x+size.x/2;}
    public int xH(){return loc.x+ size.x;}
    public int yL(){return loc.y;}
    public int yM(){return loc.y+size.y/2;}
    public int yH(){return loc.y+size.y;}
  }
  //-----------------------LoHi---------------------
  public static class LoHi implements Serializable {
    public int lo, hi;
    public LoHi(int lo, int hi){this.lo = lo; this.hi = hi;}
    public void set(int v){this.lo = v; this.hi = v;}
    public void add(int v){if(v < lo){lo = v;} if(v > hi){hi = v;}}
    public int size(){return (hi - lo) > 0 ? (hi-lo) : 1;}
  }
  //-----------------------BBox---------------------
  public static class BBox implements Serializable {
    public LoHi h, v; // horizontal vertical
    public BBox(){this.h = new LoHi(0,0); this.v = new LoHi(0,0);}
    public void set(int x, int y){h.set(x); v.set(y);}
    public void add(int x, int y){h.add(x); v.add(y);}
    public void add(V v){add(v.x,v.y);}
    public VS getNewVS(){return new VS(h.lo, v.lo, h.size(), v.size());}
    public void draw(Graphics g){g.drawRect(h.lo, v.lo, h.size(), v.size());}
  }
  //-----------------------PL-----------------------
  public static class PL implements Serializable {
    //poly line
    public V[] points;

    public PL(int count){
      points = new V[count];
      for(int i = 0; i < count; i++){points[i] = new V(0,0);}
    }
    public int size(){return points.length;}
    public void drawN(Graphics g, int n){
      for(int i = 1; i < n; i++){
        g.drawLine(points[i-1].x,points[i-1].y,points[i].x,points[i].y);
      }
      drawNDots(g, n);
    }
    public void drawNDots(Graphics g, int n){
      g.setColor(Color.BLUE);
      for(int i = 0; i < n; i++){
        g.drawOval(points[i].x-2, points[i].y-2, 4,4);
      }
    }
    public void draw(Graphics g){drawN(g, points.length);}
    public void transform(){for(int i = 0; i < points.length; i++){points[i].setT(points[i]);}}
  }
  //---------------------HC-------------------
  public static class HC{
    public static HC ZERO = new HC(null, 0);
    public HC dad;
    public int dv;

    public HC(HC dad, int dv){
      this.dad = dad;
      this.dv = dv;
    }

    public int v(){
      return dad == ZERO ? dv : dad.v()+dv;
    }
  }
}