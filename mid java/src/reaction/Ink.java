package reaction;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import graphics.*;
import music.*;

public class Ink extends G.PL implements I.Show {
    public static Buffer BUFFER = new Buffer();
    public Ink() {
        super(BUFFER.n);
        for (int i = 0; i < BUFFER.n; i++) {
            points[i] = new G.V(BUFFER.points[i]);
        }
    }

    public void show(Graphics g){
        g.setColor(Color.BLUE);
        this.draw(g);
    }

    //---------------------------------Buffer------------------------------------
    public static class Buffer extends G.PL implements I.Show, I.Area{
        public static final int MAX = UC.inkBufferMax;

        public int n;
        public G.BBox bbox = new G.BBox();
        private Buffer(){super(MAX);} // Don't want anyone else to make another (singleton)
        public void add(int x, int y){if(n < MAX){points[n++].set(x,y); bbox.add(x,y);}}
        public void clear(){n = 0;}

        public void dn(int x, int y){clear(); bbox.set(x,y); add(x,y);}
        public void drag(int x, int y){add(x,y);}
        public void up(int x, int y){add(x,y);}
        public void show(Graphics g){drawN(g,n); bbox.draw(g);}
        public boolean hit(int x, int y){return true;}

    }


    //----------------------------------List-------------------------------------
    public static class List extends ArrayList<Ink> implements I.Show{

        @Override
        public void show(Graphics g) {
            for(Ink ink:this){ink.show(g);}
        }
    }




}
