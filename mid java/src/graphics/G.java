package graphics;

import music.I;

import java.awt.*;
import java.util.Random;

public class G {
    public static Random RND = new Random();
    public static int rnd(int max) {return RND.nextInt(max);}
    public static Color rndColor(){return new Color(rnd(256), rnd(256), rnd(256));}
    public static void bkWhite(Graphics g){
        g.setColor(Color.WHITE);
        g.fillRect(0,0,5000,5000);
    }
    public static G.V LEFT = new G.V(-1,0);
    public static G.V RIGHT = new G.V(1,0);
    public static G.V UP = new G.V(0,-1);
    public static G.V DOWN = new G.V(0,1);


    //------------------------------------------------------------------------------------------------------------
    public static void spline(Graphics g, int ax, int ay, int bx, int by, int cx, int cy, int n){
        if (n == 0){g.drawLine(ax,ay,cx,cy);return;}
        int abx = (ax+bx)/2, aby = (ay+by)/2;
        int bcx = (bx+cx)/2, bcy = (by+cy)/2;
        int abcx = (abx+bcx)/2, abcy = (aby+bcy)/2;
        spline(g,ax,ay,abx,aby,abcx,abcy, n-1);
        spline(g, abcx, abcy, bcx, bcy, cx, cy, n-1);
    }
    //-----------------------------------V----------------------------------------

    public static class V{
        public int x,y;
        public V(int x, int y){this.set(x,y);}
        public V(V v){this.set(v);}
        public void set(int x, int y){this.x=x; this.y=y;}
        public void set(V v){this.x=v.x; this.y=v.y;}
        public void add(V v){x+=v.x; y+=v.y;}

    }
    //-----------------------------------VS----------------------------------------

    public static class VS implements I.Hit{
        public V loc,size;
        public VS(int x, int y, int w, int h){loc = new V(x,y); size = new V(w,h);}
        public void fill(Graphics g, Color c){g.setColor(c); g.fillRect(loc.x,loc.y,size.x,size.y);}
        public boolean hit(int x, int y){
            return loc.x <= x && loc.y <= y && x <= (loc.x + size.x) && y <= (loc.y + size.y);
        }
        public int xL(){return loc.x;}
        public int xM(){return loc.x+size.x/2;}
        public int xH(){return loc.x+size.x;}
        public int yL(){return loc.x;}
        public int yM(){return loc.y+size.y/2;}
        public int yH(){return loc.y+size.y;}
    }
    //-----------------------------------HiLo--------------------------------------
    public static class Lohi{
        public int lo, hi;
        public Lohi(int lo, int hi){this.lo = lo; this. hi = hi;}
        public void set(int v){lo = v; hi = v;}
        public void add(int v){
            if(v < lo){lo = v;}
            if(v > hi){hi = v;}
        }
        public int size(){return ((hi-lo) > 0)? hi-lo: 1;}
    }
    //-----------------------------------BBox--------------------------------------
    // Bounding Box
    public static class BBox{
        public Lohi h, v; // horizontal, vertical
        public BBox(){h = new Lohi(0,0); v = new Lohi(0,0);}
        public void set(int x, int y){h.set(x); v.set(y);}
        public void add(int x, int y){h.add(x); v.add(y);}
        public void add(V v){add(v.x,v.y);}
        public VS getNewVs(){return new VS(h.lo, v.lo, h.size(), v.size());}
        public void draw(Graphics g){g.drawRect(h.lo, v.lo, h.size(), v.size());}
    }
    //-----------------------------------PL----------------------------------------

    public static class PL{
        // Poly line multiple points
        public V[] points;
        public PL(int count){
            points = new V[count];
            for(int i=0; i<count; i++){points[i] = new V(0, 0);}
        }
        public int size(){return points.length;}

        // subset of lines point (standardized the point number)
        public void drawN(Graphics g, int n){
            for(int i = 1; i<n; i++){
                g.drawLine(points[i-1].x,points[i-1].y,points[i].x,points[i].y);}
        }
        public void draw(Graphics g){drawN(g,points.length);}

    }


}
