package es.rodalo.copit.utils;

import android.app.Application;
import android.content.Context;

/**
 * ApplicationContext
 */
public class ApplicationContext extends Application {

    private static Context context;

    @Override
    public void onCreate() {

        super.onCreate();

        ApplicationContext.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return ApplicationContext.context;
    }
}
