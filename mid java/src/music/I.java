package music;

import java.awt.*;

public interface I {
    public interface Draw{public void draw(Graphics g);}
    public interface Show{public void show(Graphics g);}
    public interface Hit{public boolean hit(int x, int y);}
    public interface Area extends Hit{
        public void dn(int x, int y);
        public void drag(int x, int y);
        public void up(int x, int y);
    }
}
