package music;

import java.util.ArrayList;

public class Time {
  public int x;
  public Head.List heads = new Head.List();

  private Time(Sys sys, int x){
    this.x = x;
    sys.times.add(this);
  }

  public void unStemHeads(int y1, int y2){
    for(Head h: heads){
      int y = h.y();
      if(y > y1 && y < y2){
        h.unStem();
      }
    }
  }

  public void stemHeads(Staff staff, boolean up, int y1, int y2){
    Stem s = new Stem(staff, heads, up);
//    System.out.println(heads.size()+ " " +y1+ " " + y2);
    for(Head h: heads){
      int y = h.y();
//      System.out.println(y);
      if(y > y1 && y < y2){
        h.joinStem(s);
      }
    }
    if(s.heads.size() == 0){
      System.out.println("empty stem heads after stemming");
    }else{
      s.setWrongSide();
      s.staff.sys.stems.addStem(s);
    }

  }

  //--------------------List-------------------
  public static class List extends ArrayList<Time> {
    public Sys sys;

    public List(Sys sys){this.sys = sys;}
    public Time getTime(int x){
      if(size() == 0){return new Time(sys, x);}
      Time t = getClosestTime(x);
      return Math.abs(x-t.x) < UC.snapTime ? t : new Time(sys, x);
    }

    public Time getClosestTime(int x){
      Time res = get(0);
      int bestSoFar = Math.abs(res.x - x);
      for(Time t: this){
        int dist = Math.abs(x - t.x);
        if(dist < bestSoFar){
          res = t;
          bestSoFar = dist;
        }
      }
      return res;
    }
  }
}
