package net.glowstone.util;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Todo: Javadoc for ConsoleMissing.
 */
public final class ConsoleMissing {

    private static final String TITLE = "Glowstone";
    private static final String MESSAGE = "Glowstone requires a console to run.\n\nPlease visit the official documentation for information\non how to start Glowstone in a console.";
    private static final String[] OPTIONS = {"Documentation", "Exit"};
    private static URI uri = null;

    private ConsoleMissing() {
    }

    public static void display() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ReflectiveOperationException | UnsupportedLookAndFeelException e) {
            // fall back to default look and feel
        }

        try {
            uri = new URI("https://github.com/GlowstoneMC/Glowstone/wiki/Installation");
        } catch (URISyntaxException e) {
            // uri is invalid and shall remain null
        }

        // see if we're capable of opening a browser window
        if (uri != null && Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            display(new JOptionPane(MESSAGE, JOptionPane.ERROR_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, OPTIONS));
        } else {
            // otherwise just warn
            display(new JOptionPane(MESSAGE, JOptionPane.ERROR_MESSAGE));
        }
    }

    private static void display(final JOptionPane pane) {
        final JFrame frame = new JFrame(TITLE);
        frame.setContentPane(pane);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);

        pane.addPropertyChangeListener(JOptionPane.VALUE_PROPERTY, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (OPTIONS[0].equals(evt.getNewValue())) {
                    try {
                        Desktop.getDesktop().browse(uri);
                    } catch (IOException e) {
                        // give up
                    }
                }
                frame.dispose();
                System.exit(0);
            }
        });
    }
}
