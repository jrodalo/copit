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
