/*
 * Copyright 2016, Jose Luis Rodriguez Alonso
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package es.rodalo.copit.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

import es.rodalo.copit.BuildConfig;

/**
 * Facilita el uso de las preferencias usadas en la aplicación
 */
public class Preferences {

    private static final String PREFS_NAME = BuildConfig.APPLICATION_ID + "_preferences";
    private static final String PREF_SOURCE_DIRECTORY = "sourceDir";
    private static final String PREF_DEST_DIRECTORY = "destDir";
    private static final String PREF_FIRST_TIME = "firtTime";
    private static final String PREF_LAST_TIME = "lastTime";

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


    /**
     * Obtiene la fecha de la última copia correcta
     */
    public static long getLastTime() {
        return getSharedPreferences().getLong(PREF_LAST_TIME, 0);
    }


    /**
     * Establece la fecha de la última copia correcta
     */
    public static void setLastTime(Date time) {

        SharedPreferences.Editor  editor = getSharedPreferences().edit();

        editor.putLong(PREF_LAST_TIME, time.getTime());

        editor.apply();
    }
}
