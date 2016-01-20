package es.rodalo.copit.services;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;

import android.app.ActivityManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import es.rodalo.copit.utils.ApplicationContext;


/**
 * Servicio encargado de realizar la copia de archivos
 */
public class CopyService extends IntentService {

    public static final String ACTION_START = "es.rodalo.copit.intent.copy.start";
    public static final String ACTION_PROGRESS = "es.rodalo.copit.intent.copy.progress";
    public static final String ACTION_END = "es.rodalo.copit.intent.copy.end";

    public static final String PARAM_SOURCE = "source";
    public static final String PARAM_DEST = "dest";

    public static final String RESPONSE_PROGRESS = "progress";
    public static final String RESPONSE_TOTAL = "total";
    public static final String RESPONSE_RESULT = "result";


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

            copyFiles(source, dest);

            onEnd(true);

        } catch (Exception e) {

            onEnd(false);
        }
    }


    /**
     * Copia archivos entre las carpetas indicadas
     */
    private void copyFiles(File from, File to) throws IOException {

        if (!to.exists() && !to.mkdirs()) {
            throw new IOException("Can't create backup folder :(");
        }

        Collection<File> files = FileUtils.listFiles(from, FileFileFilter.FILE, FalseFileFilter.FALSE);

        int count = 0;
        int total = files.size();

        for (File file : files) {

            if (file.isDirectory() || !file.canRead()) {
                continue;
            }

            FileUtils.copyFileToDirectory(file, to, true);

            count += 1;

            onProgress(count, total);
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
    private void onProgress(int progress, int total) {
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
     * Publica el intent indicado para que lo pueda recuperar la clase principal
     */
    private void publish(Intent intent) {
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
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
