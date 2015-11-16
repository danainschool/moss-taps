package ravensproject.betzel.agents;

import ravensproject.RavensFigure;
import ravensproject.RavensProblem;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;

/**
 * Created by scott betzel on 6/22/15.
 *
 * This agent will return a list of possible
 * solutions based solely on pixel count.
 */
public class PixelCountAgent
    extends SimpleCountAgentBase {

    public PixelCountAgent(String theName) {
        super(theName);

    }

    @Override
    protected Map<String, Integer> getCounts(RavensProblem problem) throws Exception {

        HashMap<String, Integer> pixelCounts = new HashMap<>();

        for (String figureKey: problem.getFigures().keySet()) {
            RavensFigure figure = problem.getFigures().get(figureKey);

            BufferedImage image = ImageIO.read(new File(figure.getVisual()));

            pixelCounts.put(figure.getName(), this.getPixelCount(image));
        }

        return pixelCounts;
    }

    @Override
    protected EqualityComparisonMode getEqualityComparisonMode() {
        return EqualityComparisonMode.ERROR_INTERVAL_MATCH;
    }

    private int getPixelCount(BufferedImage bi) {
        int toRet = 0;

        for (int x = 0; x < bi.getWidth(); x++) {
            for (int y = 0; y < bi.getHeight(); y++) {

                Color c = new Color(bi.getRGB(x, y));
                if (!c.equals(Color.WHITE)) {
                    toRet++;
                }
            }
        }

        return toRet;
    }
}
