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

    public static class HiLo{}
    //-----------------------------------BBox--------------------------------------

    public static class BBox{}
    //-----------------------------------PL----------------------------------------

    public static class PL{}


}
