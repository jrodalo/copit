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

import es.rodalo.copit.utils.Preferences;

/**
 * Clase encargada de la migración de preferencias entre versiones
 */
public class MigrationManager {

    private int currentVersion = 0;
    private int newVersion = 0;

    public MigrationManager from(int currentVersion) {
        this.currentVersion = currentVersion;
        return this;
    }


    public MigrationManager to(int newVersion) {
        this.newVersion = newVersion;
        return this;
    }


    /**
     * Inicia la migración entre la versión actual y la nueva
     */
    public void migrate() {

        Migration migration = getMigration(newVersion, currentVersion);

        if (migration != null) {
            SharedPreferences.Editor preferenceEditor = Preferences.getSharedPreferences().edit();
            migration.migrate(preferenceEditor, currentVersion);
            preferenceEditor.apply();
        }

        Preferences.setVersion(newVersion);
    }


    /**
     * Intenta obtener una Migration asociada a la nueva versión
     */
    private Migration getMigration(int newVersion, int currentVersion) {

        try {

            if (currentVersion >= newVersion || newVersion == 0) {
                return null;
            }

            return (Migration) Class.forName(this.getClass().getPackage().getName() + ".MigrationV" + newVersion).newInstance();

        } catch (Exception ignore) {

            return getMigration(newVersion - 1, currentVersion);
        }
    }
}
