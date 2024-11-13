package music;

import reaction.Gesture;
import reaction.Mass;
import reaction.Reaction;

import java.awt.*;

public class Accid extends Mass { // accidental
    public static Glyph[] GLYPHS = {Glyph.D_FLAT, Glyph.FLAT, Glyph.NATURAL, Glyph.SHARP, Glyph.D_SHARP};
    public static final int FLAT  = 1, NATURAL = 2, SHARP = 3;

    public int iGlyph;
    public Head head;
    public int left = 0;

    public Accid(int iGlyph, Head head){
        super("NOTE");
        this.iGlyph = iGlyph;
        this.head = head;

        addReaction(new Reaction("DOT") {
            @Override
            public int bid(Gesture g) {
                int x = g.vs.xM(), y = g.vs.yM();
                int accidX = Accid.this.x(), accidY = head.y();
                int dx = Math.abs(x-accidX), dy = Math.abs(y-accidY), dist = dx + dy;
                return dist > 50 ? UC.noBid:dist;
            }

            @Override
            public void act(Gesture g) {
                left+= 10;
                if(left > 50){left = 0;}
            }
        });

        addReaction(new Reaction("S-N") {
            @Override
            public int bid(Gesture g) {
                int x = g.vs.xM(), y = g.vs.yL();
                int ax = Accid.this.x() + head.w()/2, ay = Accid.this.head.y();
                int dx = Math.abs(x -ax), dy = Math.abs(y-ay), dist = dx + dy;
                return dist > 50? UC.noBid : dist;
            }

            @Override
            public void act(Gesture g) {
                Accid.this.deleteAccid();
            }
        });
    }

    public void deleteAccid() {
        head.accid = null;
        deleteMass();
    }

    public void show(Graphics g){
        GLYPHS[iGlyph].showAt(g, head.staff.fmt.H, x(), head.y());
    }

    public int x(){return head.x() - left - UC.accidHeadOffSet;}

}
