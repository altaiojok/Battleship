package battleship.controller;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

class Labels {
    private static final ResourceBundle labelResource;

    static {
        try {
            labelResource = new PropertyResourceBundle(ClassLoader.getSystemResourceAsStream("battleship/controller/labels.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String get(final String labelKey) {
        return get(labelKey, (String) null);
    }

    public static String get(final String labelKey, Object... args) {
        return MessageFormat.format(labelResource.getString(labelKey), args);
    }

    private Labels() {
    }
}
