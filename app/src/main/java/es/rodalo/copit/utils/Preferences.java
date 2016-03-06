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

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import es.rodalo.copit.BuildConfig;
import es.rodalo.copit.R;

/**
 * Facilita el uso de las preferencias usadas en la aplicación
 */
public class Preferences {

    private static final String PREFS_NAME = BuildConfig.APPLICATION_ID + "_preferences";
    private static final String PREF_DEST_DIRECTORY = "destDir";
    private static final String PREF_DEST_BACKUP_FOLDER_NAME = "destBackupFolderName";
    private static final String PREF_LAST_TIME = "lastTime";

    private static SharedPreferences getSharedPreferences() {
        Context ctx = ApplicationContext.getAppContext();
        return ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }


    public static void init() {
        initSources();
        initBackupFolderName();
    }


    /**
     * Inicializa las preferencias con los origenes existentes
     */
    private static void initSources() {

        SharedPreferences.Editor editor = getSharedPreferences().edit();

        for (Sources source : Sources.values()) {
            editor.putBoolean(source.getKey(), source.checkedByDefault());
        }

        editor.apply();
    }


    private static String initBackupFolderName() {

        String appName = ApplicationContext.getAppContext().getString(R.string.app_name).toLowerCase();

        String folderName = appName + "_backup" +
                File.separatorChar +
                Device.getUserId();

        SharedPreferences.Editor editor = getSharedPreferences().edit();

        editor.putString(PREF_DEST_BACKUP_FOLDER_NAME, folderName);

        editor.apply();

        return folderName;
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
     * Obtiene el nombre de la carpeta donde se guardarán las fotos
     */
    public static String getBackupFolderName() {

        String backupFolderName = getSharedPreferences().getString(PREF_DEST_BACKUP_FOLDER_NAME, "");

        if (backupFolderName.isEmpty()) {
            backupFolderName = initBackupFolderName();
        }

        return backupFolderName;
    }


    /**
     * Comprueba si es la primera vez que se ejecuta la app
     */
    public static boolean isTheFirstTime() {
        return getSharedPreferences().getAll().size() == 0;
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

        SharedPreferences.Editor editor = getSharedPreferences().edit();

        editor.putLong(PREF_LAST_TIME, time.getTime());

        editor.apply();
    }


    /**
     * Obtiene una lista de origenes seleccionados en la pantalla de configuración
     */
    public static List<Sources> getSelectedSources() {

        Map<String, ?> allPreferences = getSharedPreferences().getAll();

        List<Sources> sources = new ArrayList<>();

        for (Map.Entry<String, ?> entry : allPreferences.entrySet()) {

            String key = entry.getKey();
            Object value = entry.getValue() != null ? entry.getValue() : false;

            if (key.startsWith(Sources.SOURCE_KEY_PREFIX) && Boolean.valueOf(value.toString())) {

                String sourceName = key.substring(Sources.SOURCE_KEY_PREFIX.length(), key.length()).toUpperCase();

                try {

                    sources.add(Sources.valueOf(sourceName));

                } catch (IllegalArgumentException ignore) {

                    // Si falla aquí es porque se habrá cambiado el nombre del enum... podemos eliminarla

                    getSharedPreferences().edit().remove(key).apply();
                }
            }
        }

        return sources;
    }
}
