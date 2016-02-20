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

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;

/**
 * Métodos para trabajar con ficheros
 */
public class Files {


    /**
     * Comparador que permite ordenar ficheros por fecha de modificación (los más recientes primero)
     */
    public static final Comparator<File> lastModifiedComparator = new Comparator<File>() {

        public int compare(File file1, File file2) {
            long result = file1.lastModified() - file2.lastModified();
            if (result < 0) {
                return 1;
            } else if (result > 0) {
                return -1;
            } else {
                return 0;
            }
        }
    };


    /**
     * Copia archivos entre las carpetas indicadas
     */
    public static void copyFolder(File srcDir, File destDir, CopyProgressCallback callback) throws IOException {

        File[] srcFiles = srcDir.listFiles();

        int count = 0;
        int total = srcFiles.length;

        for (File srcFile : srcFiles) {

            File destFile = new File(destDir, srcFile.getName());

            if (srcFile.isDirectory()) {

                copyFolder(srcFile, destFile, callback);

            } else {

                if (isNotTheSameFile(srcFile, destFile)) {
                     FileUtils.copyFile(srcFile, destFile, true);
                }
            }

            count += 1;

            if (callback != null) {
                callback.onProgress(count, total);
            }
        }
    }


    /**
     * Comprueba si los archivos indicados son iguales o no
     */
    private static boolean isNotTheSameFile(File srcFile, File destFile) {

        if ( ! destFile.exists()) {
            return true;
        }

        if (srcFile.lastModified() != destFile.lastModified()) {
            return true;
        }

        if (srcFile.length() != destFile.length()) {
            return true;
        }

        return false;
    }


    public interface CopyProgressCallback {

        void onProgress(int progress, int total);

    }
}
