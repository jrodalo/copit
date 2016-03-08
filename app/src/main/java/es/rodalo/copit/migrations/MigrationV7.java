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

package es.rodalo.copit.migrations;

import android.content.SharedPreferences;

import es.rodalo.copit.utils.Device;
import es.rodalo.copit.utils.Preferences;
import es.rodalo.copit.utils.Sources;

/**
 * Migración asociada a la versión 7
 */
public class MigrationV7 extends Migration {

    @Override
    public void migrate(SharedPreferences.Editor preferenceEditor, int from) {

        if (from < 7) {
            super.migrate(preferenceEditor, from);
        }

        preferenceEditor.clear();

        initSources(preferenceEditor);
        initBackupFolderName(preferenceEditor);
    }


    /**
     * Inicializa las preferencias con los origenes existentes
     */
    private void initSources(SharedPreferences.Editor preferenceEditor) {

        for (Sources source : Sources.values()) {
            preferenceEditor.putBoolean(source.getKey(), source.checkedByDefault());
        }
    }


    /**
     * Inicializa el nombre de la carpeta del backup
     */
    private void initBackupFolderName(SharedPreferences.Editor preferenceEditor) {

        preferenceEditor.putString(Preferences.PREF_BACKUP_FOLDER_NAME, Device.getUserId());
    }
}
