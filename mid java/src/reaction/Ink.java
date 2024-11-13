package reaction;

import graphics.G;
import graphics.G.V;
import java.io.Serializable;
import music.I;
import music.UC;

import java.awt.*;
import java.util.ArrayList;

public class Ink implements I.Show {
  public static final Buffer BUFFER = new Buffer();
  public Norm norm;
  public G.VS vs;
//  public static G.VS temp = new G.VS(100, 100, 100, 100);
  public Ink() {
    norm = new Norm();
    vs = BUFFER.bbox.getNewVS();
  }

  @Override
  public void show(Graphics g) {
    g.setColor(Color.BLUE);
    norm.drawAt(g, vs);
  }
  //---------------------------Norm---------------------
  public static class Norm extends G.PL implements Serializable {
    public static final int N = UC.normSampleSize, MAX = UC.normCoordMax;
    public static final G.VS NCS = new G.VS(0, 0, MAX, MAX);
    public Norm(){
      super(N);
      BUFFER.subSample(this);
      G.V.T.set(BUFFER.bbox, NCS);
      transform();
    }
    public void drawAt(Graphics g, G.VS vs){
      G.V.T.set(NCS, vs);
      for(int i = 1; i < N; i++){
        g.drawLine(points[i-1].tx(), points[i-1].ty(), points[i].tx(), points[i].ty());
      }
    }
    public int dist(Norm n){
      int res = 0;
      for(int i = 0; i < N; i++){
        int dx = points[i].x - n.points[i].x, dy = points[i].y - n.points[i].y;
        res += dx*dx + dy*dy;
      }
      return res;
    }
    public void blend(Norm norm, int n){
      for(int i = 0; i < N; i++){
        points[i].blend(norm.points[i], n);
      }
    }
  }
  //---------------------------Buffer-------------------
  public static class Buffer extends G.PL implements I.Show, I.Area {

    public int n;
    public static final int MAX = UC.intBufferMax;
    public G.BBox bbox = new G.BBox();

    private Buffer(){super(MAX);}

    public void add(int x, int y){if(n < MAX){points[n++].set(x, y); bbox.add(x, y);}}
    public void clear(){n = 0;}
    public void dn(int x, int y) {clear(); bbox.set(x,y); add(x, y);}
    public void drag(int x, int y) {add(x, y);}
    public void up(int x, int y) {add(x, y);}
    public void show(Graphics g) {drawN(g, n); /*bbox.draw(g);*/}
    public boolean hit(int x, int y) {return true;}
    public void subSample(G.PL pl){
      int k = pl.size();
      for(int i = 0; i < k; i++){
        pl.points[i].set(this.points[i*(n-1)/(k-1)]);
      }
    }
  }

  //---------------------------List---------------------
  public static class List extends ArrayList<Ink> implements I.Show{
    @Override
    public void show(Graphics g) {for(Ink ink: this){ink.show(g);}}
  }
}
