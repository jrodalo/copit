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
     * Copia archivos entre las carpetas indicadas realizando
     * algunas validaciones para comprobar si es posible
     */
    public static boolean copyFolder(File srcDir, File destDir, CopyProgressCallback callback) throws Exception {

        validateCopy(srcDir, destDir);

        return doCopyFolder(srcDir, destDir, callback);
    }


    /**
     * Copia archivos entre las carpetas indicadas
     */
    private static boolean doCopyFolder(File srcDir, File destDir, CopyProgressCallback callback) throws Exception {

        File[] srcFiles = srcDir.listFiles();

        int count = 0;
        int total = srcFiles.length;

        for (File srcFile : srcFiles) {

            File destFile = new File(destDir, srcFile.getName());

            if (srcFile.isDirectory()) {

                doCopyFolder(srcFile, destFile, callback);

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

        return true;
    }


    /**
     * Comprueba si es posible ejecutar la copia de archivos entre el origen y destino
     */
    private static void validateCopy(File source, File dest) throws Exception {

        if (!source.exists() || !source.isDirectory()) {
            throw new Error.SourceDontExists();
        }

        if (!dest.exists()) {
            throw new Error.DestDontExists();
        }

        if (!dest.isDirectory()) {
            throw new Error.DestDontExists();
        }

        if (!dest.canWrite()) {
            throw new Error.DestDontExists();
        }

        if (source.equals(dest)) {
            throw new Error.SameDirectoryException();
        }

        if (isChild(dest, source)) {
            throw new Error.IsChildException();
        }
    }


    /**
     * Comprueba si un directorio es hijo de otro
     */
    public static boolean isChild(File maybeChild, File possibleParent) throws IOException {

        final File parent = possibleParent.getCanonicalFile();

        if (!parent.exists() || !parent.isDirectory()) {
            return false;
        }

        File child = maybeChild.getCanonicalFile();

        while (child != null) {

            if (child.equals(parent)) {
                return true;
            }

            child = child.getParentFile();
        }

        return false;
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
