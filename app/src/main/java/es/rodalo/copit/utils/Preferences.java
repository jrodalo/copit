package es.rodalo.copit.utils;

import android.content.Context;
import android.content.SharedPreferences;

import es.rodalo.copit.BuildConfig;

/**
 * Facilita el uso de las preferencias usadas en la aplicación
 */
public class Preferences {

    private static final String PREFS_NAME = BuildConfig.APPLICATION_ID + "_preferences";
    private static final String PREF_SOURCE_DIRECTORY = "sourceDir";
    private static final String PREF_DEST_DIRECTORY = "destDir";
    private static final String PREF_FIRST_TIME = "firtTime";

    private static SharedPreferences getSharedPreferences() {
        Context ctx = ApplicationContext.getAppContext();
        return ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }


    /**
     * Obtiene la carpeta de origen
     */
    public static String getSourceFolder() {
        return getSharedPreferences().getString(PREF_SOURCE_DIRECTORY, "");
    }


    /**
     * Establece la carpeta de origen
     */
    public static void setSourceFolder(String path) {

        SharedPreferences.Editor editor = getSharedPreferences().edit();

        editor.putString(PREF_SOURCE_DIRECTORY, path);

        editor.apply();
    }


    /**
     * Obtiene la carpeta de destino
     */
    public static String getDestFolder() {
        return getSharedPreferences().getString(PREF_DEST_DIRECTORY, "");
    }


    /**
     * Establece la carpeta de destino
     */
    public static void setDestFolder(String path) {

        SharedPreferences.Editor editor = getSharedPreferences().edit();

        editor.putString(PREF_DEST_DIRECTORY, path);

        editor.apply();
    }


    /**
     * Comprueba si es la primera vez que se ejecuta la app
     */
    public static boolean isTheFirstTime() {
        return getSharedPreferences().getBoolean(PREF_FIRST_TIME, true);
    }


    /**
     * Establece si es la primera vez que se ejecuta la app
     */
    public static void setFirstTime(boolean first) {

        SharedPreferences.Editor editor = getSharedPreferences().edit();

        editor.putBoolean(PREF_FIRST_TIME, first);

        editor.apply();
    }
}
