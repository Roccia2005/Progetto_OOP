package it.unibo.jnavy.view.utilities;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for loading, caching, and scaling images and icons.
 */
public final class ImageLoader {

    private static final Map<String, Image> imageCache = new HashMap<>();

    private ImageLoader() {
    }

    /**
     * Loads an image from the specified classpath, utilizing an internal cache 
     * to optimize performance and prevent redundant I/O disk operations.
     * * @param path the absolute path to the image resource (e.g., "/images/ship.png").
     * @return the loaded {@link Image}, or {@code null} if the resource cannot be found or read.
     */
    public static Image getImage(String path) {
        if (imageCache.containsKey(path)) {
            return imageCache.get(path);
        }
        try {
            URL resourceUrl = ImageLoader.class.getResource(path);
            if (resourceUrl == null) {
                return null;
            }
            Image loadedImage = ImageIO.read(resourceUrl);
            imageCache.put(path, loadedImage);
            return loadedImage;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Retrieves an image from the cache (or loads it if necessary) and wraps it 
     * in an {@link ImageIcon}, ready to be used in Swing components like JLabel or JButton.
     * * @param path the absolute path to the image resource.
     * @return an {@link ImageIcon} containing the requested image, or {@code null} if it cannot be loaded.
     */
    public static ImageIcon getIcon(String path) {
        Image img = getImage(path);
        return img != null ? new ImageIcon(img) : null;
    }

    /**
     * Retrieves an image, scales it to the specified dimensions using a smooth scaling algorithm, 
     * and wraps it in an {@link ImageIcon}.
     * * @param path the absolute path to the image resource.
     * @param width the desired width of the scaled icon.
     * @param height the desired height of the scaled icon.
     * @return a resized {@link ImageIcon}, or {@code null} if the original image cannot be loaded.
     */
    public static ImageIcon getScaledIcon(String path, int width, int height) {
        Image img = getImage(path);
        if (img != null) {
            Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImg);
        }
        return null;
    }
}