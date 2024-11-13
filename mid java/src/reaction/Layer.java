package reaction;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import music.I;

public class Layer extends ArrayList<I.Show> implements I.Show{
  public static HashMap<String, Layer> byName = new HashMap<String, Layer>();
  public static Layer ALL = new Layer("ALL");

  public String name;

  public Layer(String name) {
    this.name = name;
    if(!name.equals("ALL")){
      ALL.add(this);
    }
    byName.put(name, this);
  }

  @Override
  public void show(Graphics g) {
    for(I.Show item : this){
      item.show(g);
    }
  }

  public static void nuke(){
    for(I.Show item : ALL){((Layer)item).clear();}
  }
}
