package es.rodalo.copit.utils;

import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;

import es.rodalo.copit.R;

/**
 * Permite mostrar mensajes en la aplicación usando un Snackbar de Android
 */
public class Message {

    /**
     * Tipos de mensajes disponibles
     */
    public enum Types {

        SUCCESS(R.color.colorSuccess),
        WARNING(R.color.colorWarning),
        ERROR(R.color.colorError);

        final int color;

        Types(int color) {
            this.color = color;
        }
    }


    /**
     * Muestra un mensaje con los datos indicados
     */
    private static void show(View view, String text, Types type) {

        Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_LONG);

        snackbar.getView().setBackgroundColor(ContextCompat.getColor(view.getContext(), type.color));

        snackbar.show();
    }


    /**
     * Muestra un mensaje de éxito
     */
    public static void success(View view, String text) {
        show(view, text, Types.SUCCESS);
    }


    /**
     * Muestra un mensaje de aviso
     */
    public static void warning(View view, String text) {
        show(view, text, Types.WARNING);
    }


    /**
     * Muestra un mensaje de error
     */
    public static void error(View view, String text) {
        show(view, text, Types.ERROR);
    }

}
