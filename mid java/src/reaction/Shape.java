package reaction;

import graphics.G;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;
import music.I;
import music.UC;
import reaction.Shape.Prototype.List;

public class Shape implements Serializable {
  public static Shape.Database DB = Shape.Database.load();
  public static Shape DOT = DB.get("DOT");
  public static Trainer TRAINER = new Trainer();
  // List up-to-date with DB
  public static Collection<Shape> LIST = DB.values();
  public Prototype.List prototypeList = new Prototype.List();
  public String name;

  public Shape(String name){
    this.name = name;
  }

  public static Shape recognize(Ink ink){ // can return null
    if(ink.vs.size.x < UC.dotThreshold && ink.vs.size.y < UC.dotThreshold){return DOT;}
    Shape bestMatch = null;
    int bestSoFar = UC.noMatchDist;
    for(Shape s : LIST){
      int d = s.prototypeList.bestDist(ink.norm);
      if(d < bestSoFar){
        bestMatch = s;
        bestSoFar = d;
      }
    }
    return bestMatch;
  }

  //----------------Database-----------------
  public static class Database extends TreeMap<String, Shape> implements Serializable {
    private static final String fileName = UC.shapeDatabaseFileName;

    private Database(){
      super();
      String dot = "DOT";
      put(dot, new Shape(dot));
    }

    private Shape forceGet(String name){
      if(!DB.containsKey(name)){
        DB.put(name, new Shape(name));
      }
      return DB.get(name);
    }

    public void train(String name, Ink.Norm norm){
      if(isLegal(name)){
        forceGet(name).prototypeList.train(norm);
      }
    }
    public static Database load(){
      Database res;
      try {
        System.out.println("Loading Database:" + fileName);
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName));
        res = (Database) ois.readObject();
        System.out.println("Successful loaded: " + res.keySet());
        ois.close();
      } catch (Exception e) {
        res = new Database();
        System.out.println("Failed to load Database:" + fileName);
        System.out.println(e);
      }
      return res;
    }

    // stub
    public static void save(){
      try{
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName));
        oos.writeObject(DB);
        System.out.println("Saving Database:" + fileName);
        oos.close();
      }catch (Exception e){
        System.out.println("Failed to save Database:" + fileName);
        System.out.println(e);
      }
    }
    public boolean isKnown(String name){return containsKey(name);}
    public boolean isUnknown(String name){return !containsKey(name);}
    public boolean isLegal(String name){return !name.equals("") && !name.equals("DOT");}
  }

  //----------------Prototype----------------
  public static class Prototype extends Ink.Norm implements Serializable{
    public int nBland;

    public void blend(Ink.Norm norm){
      blend(norm, nBland++);
    }


    //--------------------List-------------
    public static class List extends ArrayList<Prototype> implements I.Show, Serializable{
      // set as side effect of bestDist
      public static Prototype bestMatch;

      public void train(Ink.Norm norm){
        if(bestDist(norm) < UC.noMatchDist){
          bestMatch.blend(norm);
        }else{
          add(new Shape.Prototype());
        }
      }

      public int bestDist(Ink.Norm norm){
        bestMatch = null;
        int bestSoFar = UC.noMatchDist;
        for(Prototype p : this){
          int d = p.dist(norm);
          if(d < bestSoFar){
            bestMatch = p;
            bestSoFar = d;
          }
        }
        return bestSoFar;
      }

      private static int m = 10, w = 60, showBoxHeight = m+w;
      private static G.VS showBox = new G.VS(m, m, w, w);
      @Override
      public void show(Graphics g) {
        g.setColor(Color.ORANGE);
        for(int i = 0; i < size(); i++){
          Prototype p = get(i);
          int x = m+i*(m+w);
          showBox.loc.set(x, m);
          p.drawAt(g, showBox);
          g.drawString(""+p.nBland, x, 20);
        }
      }
    }
  }
  //-----------------------Trainer-------------------
  public static class Trainer implements I.Show, I.Area{
    public static String UNKNOWN = " <- currently unknown";
    public static String ILLEGAL = " <- this name NOT legal";
    public static String KNOWN = "<- known name";
    public static Shape.Prototype.List pList = null;
    public static String curName = "";
    public static String curState = ILLEGAL;

    private Trainer(){}

    public void setState(){
      curState = (!Shape.DB.isLegal(curName)) ? ILLEGAL : UNKNOWN;
      if(curState.equals(UNKNOWN)){
        if(Shape.DB.isKnown(curName)){
          curState = KNOWN;
          pList = Shape.DB.get(curName).prototypeList;
        }else{
          pList = null;
        }
      }
    }

    private boolean removePrototype(int x, int y){
      int H = List.showBoxHeight;
      if(y < H){
        int ndx = x/H;
        Prototype.List pList = TRAINER.pList;
        if(pList != null && ndx < pList.size()){
          pList.remove(ndx);
          Ink.BUFFER.clear();
          return true;
        }
      }
      return false;
    }

    public void keyTyped(KeyEvent ke) {
      char c = ke.getKeyChar();
      System.out.println("typed "+c);
      if(c == 0x0D || c == 0x0A){Shape.Database.save();}
      curName = (c == ' ' || c == 0x0D || c == 0x0A) ? "" : curName+c;
      setState();
    }

    @Override
    public void dn(int x, int y) {
      Ink.BUFFER.dn(x, y);
    }

    @Override
    public void drag(int x, int y) {
      Ink.BUFFER.drag(x, y);
    }

    @Override
    public void up(int x, int y) {
      if(removePrototype(x, y)){return;}
      Ink.BUFFER.up(x, y);
      Ink ink = new Ink();
      Shape.DB.train(curName, ink.norm);
      setState();
    }

    @Override
    public boolean hit(int x, int y) {
      return true;
    }

    @Override
    public void show(Graphics g) {
      G.bkWhite(g);
      g.setColor(Color.BLACK);
      g.drawString(curName, 600, 30);
      g.drawString(curState, 700, 30);
      g.setColor(Color.RED);
      Ink.BUFFER.show(g);
      if(pList != null){
        pList.show(g);
      }
    }
  }
}
