package it.unibo.jnavy.view.utilities;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for loading, caching, and scaling images and icons.
 */
public final class ImageLoader {

    private static final Logger LOGGER = Logger.getLogger(ImageLoader.class.getName());
    private static final Map<String, Image> IMAGE_CACHE = new HashMap<>();

    private ImageLoader() {
    }

    /**
     * Loads an image from the specified classpath, utilizing an internal cache
     * to optimize performance and prevent redundant I/O disk operations.
     * 
     * @param path the absolute path to the image resource (e.g., "/images/ship.png").
     * @return the loaded {@link Image}, or {@code null} if the resource cannot be found or read.
     */
    public static Image getImage(final String path) {
        if (IMAGE_CACHE.containsKey(path)) {
            return IMAGE_CACHE.get(path);
        }
        try {
            final URL resourceUrl = ImageLoader.class.getResource(path);
            if (resourceUrl == null) {
                LOGGER.log(Level.WARNING, "Image resource not found: {0}", path);
                return null;
            }
            final Image loadedImage = ImageIO.read(resourceUrl);
            IMAGE_CACHE.put(path, loadedImage);
            return loadedImage;
        } catch (final IOException e) {
            LOGGER.log(Level.SEVERE, "Error reading image: " + path, e);
            return null;
        }
    }

    /**
     * Retrieves an image from the cache (or loads it if necessary) and wraps it
     * in an {@link ImageIcon}, ready to be used in Swing components like JLabel or JButton.
     * 
     * @param path the absolute path to the image resource.
     * @return an {@link ImageIcon} containing the requested image, or {@code null} if it cannot be loaded.
     */
    public static ImageIcon getIcon(final String path) {
        final Image img = getImage(path);
        return img != null ? new ImageIcon(img) : null;
    }

    /**
     * Retrieves an image, scales it to the specified dimensions using a smooth scaling algorithm,
     * and wraps it in an {@link ImageIcon}.
     * 
     * @param path the absolute path to the image resource.
     * @param width the desired width of the scaled icon.
     * @param height the desired height of the scaled icon.
     * @return a resized {@link ImageIcon}, or {@code null} if the original image cannot be loaded.
     */
    public static ImageIcon getScaledIcon(final String path, final int width, final int height) {
        final Image img = getImage(path);
        if (img != null) {
            final Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImg);
        }
        return null;
    }
}
