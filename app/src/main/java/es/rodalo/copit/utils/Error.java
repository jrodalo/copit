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

import es.rodalo.copit.R;

/**
 * Errores lanzados por la aplicación
 */
public class Error extends Exception {

    public int getMessageKey() {
        return R.string.copy_error;
    }

    public static class LowBatteryException extends Error {

        @Override
        public int getMessageKey() {
            return R.string.copy_error_battery;
        }
    }


    public static class IsChildException extends Error {

        @Override
        public int getMessageKey() {
            return R.string.copy_error_samefolder;
        }
    }


    public static class SameDirectoryException extends Error {

        @Override
        public int getMessageKey() {
            return R.string.copy_error_samefolder;
        }
    }


    public static class NoDestinationException extends Error {

        @Override
        public int getMessageKey() {
            return R.string.copy_error_dest;
        }
    }


    public static class NoSourceException extends Error {

        @Override
        public int getMessageKey() {
            return R.string.copy_error_source;
        }
    }


    public static class CantCreateBackupFolderException extends Error {

        @Override
        public int getMessageKey() {
            return R.string.copy_error_dest;
        }
    }
}
