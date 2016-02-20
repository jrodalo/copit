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

package es.rodalo.copit.services;


import android.app.ActivityManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import java.io.File;

import es.rodalo.copit.BuildConfig;
import es.rodalo.copit.utils.ApplicationContext;
import es.rodalo.copit.utils.Error;
import es.rodalo.copit.utils.Files;


/**
 * Servicio encargado de realizar la copia de archivos
 */
public class CopyService extends IntentService implements Files.CopyProgressCallback {

    public static final String ACTION_START = "es.rodalo.copit.intent.copy.start";
    public static final String ACTION_PROGRESS = "es.rodalo.copit.intent.copy.progress";
    public static final String ACTION_END = "es.rodalo.copit.intent.copy.end";
    public static final String ACTION_ERROR = "es.rodalo.copit.intent.copy.error";

    public static final String PARAM_SOURCE = "source";
    public static final String PARAM_DEST = "dest";

    public static final String RESPONSE_PROGRESS = "progress";
    public static final String RESPONSE_TOTAL = "total";
    public static final String RESPONSE_RESULT = "result";
    public static final String RESPONSE_ERROR = "error";


    public CopyService() {
        super("CopyService");
    }


    /**
     * Inicia el proceso de copia de archivos
     */
    @Override
    protected void onHandleIntent(Intent intent) {

        onStart();

        try {

            File source = (File) intent.getExtras().get(PARAM_SOURCE);
            File dest = (File) intent.getExtras().get(PARAM_DEST);
            File backup = createBackupFolder(source, dest);

            Files.copyFolder(source, backup, this);

            onEnd(true);

        } catch (Exception e) {

            onError(e);
            onEnd(false);
        }
    }


    /**
     * Notifica el inicio del proceso de copia
     */
    private void onStart() {
        publish(new Intent(ACTION_START));
    }


    /**
     * Notifica el progreso actual del proceso de copia
     */
    @Override
    public void onProgress(int progress, int total) {
        Intent intent = new Intent(ACTION_PROGRESS);
        intent.putExtra(RESPONSE_PROGRESS, progress);
        intent.putExtra(RESPONSE_TOTAL, total);
        publish(intent);
    }


    /**
     * Notifica el final del proceso de copia
     */
    private void onEnd(boolean result) {
        Intent intent = new Intent(ACTION_END);
        intent.putExtra(RESPONSE_RESULT, result);
        publish(intent);
    }


    /**
     * Notifica un error durante el proceso de copia
     */
    private void onError(Exception exception) {
        Intent intent = new Intent(ACTION_ERROR);
        intent.putExtra(RESPONSE_ERROR, exception);
        publish(intent);
    }


    /**
     * Publica el intent indicado para que lo pueda recuperar la clase principal
     */
    private void publish(Intent intent) {
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


    /**
     * Obtiene el nombre de la carpeta donde se guardarán los archivos copiados
     */
    private File createBackupFolder(File source, File dest) throws Error.CantCreateBackupFolder {

        String appId = BuildConfig.APPLICATION_ID;

        String appName = appId.substring(appId.lastIndexOf(".") + 1, appId.length());

        String folderName = appName.toLowerCase() + "_backup/" + source.getName();

        File backupFolder = new File(dest, folderName);

        if ( ! backupFolder.exists() && ! backupFolder.mkdirs()) {
            throw new Error.CantCreateBackupFolder();
        }

        return backupFolder;
    }


    /**
     * Comprueba si este servicio esta ejecutándose
     */
    public static boolean isRunning() {

        ActivityManager manager = (ActivityManager) ApplicationContext.getAppContext().getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (CopyService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }

        return false;
    }

}
