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

import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import es.rodalo.copit.R;

/**
 * Ubicaciones de las distintas carpetas de fotos disponibles
 */
public enum Sources {

    CAMERA {

        @Override
        public List<File> getPossiblePaths() {
            return Collections.singletonList(new File(dcimFolder, "Camera"));
        }

        @Override
        public int getStringId() {
            return R.string.label_source_camera;
        }

        @Override
        public boolean checkedByDefault() {
            return true;
        }
    },

    SCREENSHOTS {

        @Override
        public List<File> getPossiblePaths() {
            return Collections.singletonList(new File(picturesFolder, "Screenshots"));
        }

        @Override
        public int getStringId() {
            return R.string.label_source_screenshots;
        }

        @Override
        public boolean checkedByDefault() {
            return false;
        }
    },

    WHATSAPP {

        @Override
        public List<File> getPossiblePaths() {
            return Collections.singletonList(new File(rootFolder, "WhatsApp/Media/WhatsApp Images"));
        }

        @Override
        public int getStringId() {
            return R.string.label_source_whatsapp;
        }

        @Override
        public boolean checkedByDefault() {
            return false;
        }
    },

    FACEBOOK {

        @Override
        public List<File> getPossiblePaths() {
            return Collections.singletonList(new File(picturesFolder, "Messenger"));
        }

        @Override
        public int getStringId() {
            return R.string.label_source_facebook;
        }

        @Override
        public boolean checkedByDefault() {
            return false;
        }
    };

    public static final String SOURCE_KEY_PREFIX = "source_";

    private static final File rootFolder = Environment.getExternalStorageDirectory();
    private static final File dcimFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
    private static final File picturesFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

    protected abstract List<File> getPossiblePaths();
    public abstract int getStringId();
    public abstract boolean checkedByDefault();


    /**
     * Obtiene una clave asociada a esta ubicación
     */
    public String getKey() {
        return SOURCE_KEY_PREFIX + name().toLowerCase();
    }


    /**
     * Comprueba si esta ubicación existe
     */
    public boolean exists() {
        return getActivePaths().size() > 0;
    }


    /**
     * Obtiene una lista con las ubicaciones existentes
     */
    public List<File> getActivePaths() {

        List<File> activePaths = new ArrayList<>();

        for (File path : getPossiblePaths()) {

            if (path.exists() && path.isDirectory()) {
                activePaths.add(path);
            }
        }

        return activePaths;
    }
}

