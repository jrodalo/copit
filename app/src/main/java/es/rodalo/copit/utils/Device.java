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

import java.io.File;
import java.util.Date;


/**
 * Utilidades relacionadas con el dispositivo
 */
public class Device {


    public static String getRandomId() {
        return "" + new Date().getTime();
    }



    /**
     * Intenta obtener una posible carpeta de destino
     */
    public static String guessDestFolder() {

        String[] possiblePaths = new String[]{"/storage/usbdisk/", "/storage/usbdrive"};

        for (String path : possiblePaths) {

            File usb = new File(path);

            if (usb.exists() && usb.isDirectory()) {
                return usb.getAbsolutePath();
            }
        }

        return "";
    }

}
