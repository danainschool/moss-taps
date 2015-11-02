package ravensproject.betzel;

import ravensproject.betzel.helpers.LoggingHelper;

/**
 * Created by scottbetzel on 6/21/15.
 */
public class DataGenerator {

    private static final int WIDTH = 184;
    private static final int HEIGHT = 184;

    public static void main(String[] args) {
        try {
            DataGenerator dg = new DataGenerator();
            dg.generateRandomImages();
        } catch (Exception ex) {
            LoggingHelper.logError(String.format("%s", ex.getMessage()));
        }
    }

    private void generateRandomImages() throws Exception {
//        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_BYTE_GRAY);
//
//        Graphics g = image.getGraphics();
//        Graphics2D graphics = (Graphics2D) g;
//        graphics.setColor(Color.WHITE);
//        graphics.fillRect ( 0, 0, image.getWidth(), image.getHeight() );
//
//        graphics.setColor(Color.BLACK);
//        graphics.setStroke(new BasicStroke(4));

//        Circle c = new Circle();
//        c.draw(graphics, 92, 92, 32, false);
//        c.draw(graphics, 32, 32, 32, false);

//        graphics.setColor(Color.WHITE);

//        ImageIO.write(image, "png", new File("01.png"));
    }
}
