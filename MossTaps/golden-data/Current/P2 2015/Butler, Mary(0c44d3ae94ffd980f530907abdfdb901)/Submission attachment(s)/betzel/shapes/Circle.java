package ravensproject.betzel.shapes;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * Created by scottbetzel on 6/21/15.
 */
public class Circle
        extends Ellipse2D.Double {

    private boolean fill;

    private double rotation;

    public Circle(double x,
                  double y,
                  double w,
                  double h,
                  boolean theFill,
                  double theRotation) {
        super(x, y, w, h);

        this.fill = theFill;
        this.rotation = theRotation;
    }

    public void draw(final Graphics2D g) {

        BufferedImage temp = new BufferedImage((int)this.getWidth(),
                                               (int)this.getHeight(),
                                               BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D tempGraphics = (Graphics2D)temp.getGraphics();

        tempGraphics.drawOval((int)this.getCenterX(),
                    (int)this.getCenterY(),
                    (int)this.getWidth(),
                    (int)this.getHeight());

        if (fill) {
            tempGraphics.fillOval((int)this.getCenterX(),
                        (int)this.getCenterY(),
                        (int)this.getWidth(),
                        (int)this.getHeight());
        }

        tempGraphics.rotate(this.rotation);



    }

}
