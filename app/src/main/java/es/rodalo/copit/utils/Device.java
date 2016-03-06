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

import android.accounts.Account;
import android.accounts.AccountManager;
import android.provider.Settings;

import java.io.File;

/**
 * Utilidades relacionadas con el dispositivo
 */
public class Device {


    /**
     * Obtiene un identificador del usuario. Puede ser o bien el nombre de su cuenta de google
     * más el identificador único de su dispositivo o bien solo el identificador del dispositivo
     * si no tenía una cuenta de email configurada
     */
    public static String getUserId() {

        String email = getUserEmail();

        String userId = (email != null) ? email.substring(0, email.indexOf("@") + 1) : "";

        String deviceId = Settings.Secure.getString(ApplicationContext.getAppContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        return userId + deviceId;
    }


    /**
     * Obtiene el email del usuario o null si no tiene cuentas configuradas
     */
    private static String getUserEmail() {

        Account[] accounts = AccountManager.get(ApplicationContext.getAppContext()).getAccounts();

        String email = null;

        for (Account account : accounts) {
            if ("com.google".equalsIgnoreCase(account.type)) {
                email = account.name;
                break;
            }
        }

        return email;
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
