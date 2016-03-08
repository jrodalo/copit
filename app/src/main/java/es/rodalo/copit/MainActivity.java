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

package es.rodalo.copit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;

import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.rodalo.copit.fragments.DestFragment;
import es.rodalo.copit.fragments.SourceFragment;
import es.rodalo.copit.migrations.MigrationManager;
import es.rodalo.copit.services.CopyService;
import es.rodalo.copit.utils.Error;
import es.rodalo.copit.utils.Message;
import es.rodalo.copit.utils.Preferences;

/**
 * Actividad principal de la aplicación
 */
public class MainActivity extends FragmentActivity {

    private SourceFragment mSourceFragment;
    private DestFragment mDestFragment;

    @Bind(R.id.main_fab_copy) FloatingActionButton mFabCopy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        checkMigrations();

        loadFragments();

        mFabCopy.setVisibility(CopyService.isRunning() ? View.INVISIBLE : View.VISIBLE);
    }


    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onCopyEvent);
    }


    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter();

        filter.addAction(CopyService.ACTION_START);
        filter.addAction(CopyService.ACTION_PROGRESS);
        filter.addAction(CopyService.ACTION_END);

        LocalBroadcastManager.getInstance(this).registerReceiver(onCopyEvent, filter);
    }


    /**
     * Realiza algunas comprobaciones si es la primera vez que se ejecuta la app
     */
    private void checkMigrations() {

        int prefsVersion = Preferences.getVersion();
        int buildVersion = BuildConfig.VERSION_CODE;

        if (prefsVersion < buildVersion) {
            new MigrationManager().from(prefsVersion).to(buildVersion).migrate();
        }
    }


    /**
     * Carga los fragmentos para el origen y destino
     */
    private void loadFragments() {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        mSourceFragment = SourceFragment.newInstance();
        mDestFragment = DestFragment.newInstance(Preferences.getDestFolder());

        ft.replace(R.id.main_source_fragment, mSourceFragment);
        ft.replace(R.id.main_dest_fragment, mDestFragment);

        ft.commit();
    }


    /**
     * Abre la pantalla de configuración
     */
    @OnClick(R.id.main_config_button)
    public void onConfigButtonClick() {
        startActivity(new Intent(this, ConfigurationActivity.class));
    }


    /**
     * Inicia el servicio que realiza la copia entre el origen y destino
     */
    @OnClick(R.id.main_fab_copy)
    public void onFabButtonClick() {

        try {

            Intent intent = new Intent(this, CopyService.class);

            startService(intent);

        } catch (Exception ex) {
            Message.error(mDestFragment.getView(), ex.getMessage());
        }
    }


    /**
     * Ejecuta las acciones necesarias al iniciar el proceso de copia
     */
    private void onCopyStarted() {

        hideCopyButton();

        if (mDestFragment.isAdded()) {
            mDestFragment.showProgressPanel();
        }
    }


    /**
     * Ejecuta las acciones necesarias al avanzar en el proceso de copia
     */
    private void onCopyProgress(int progress, int total) {

        if (mDestFragment.isAdded()) {
            mDestFragment.showProgressPanel();
            mDestFragment.updateProgress(progress, total);
        }
    }


    /**
     * Ejecuta las acciones necesarias al finalizar el proceso de copia
     */
    private void onCopyEnded(boolean success, Exception exception) {

        if (success) {

            Message.success(mDestFragment.getView(), getString(R.string.copy_success));
            Preferences.setLastTime(new Date());

        } else {

            String message = (exception != null && exception instanceof Error) ?
                    getString(((Error) exception).getMessageKey()) :
                    getString(R.string.copy_error);

            Message.error(mDestFragment.getView(), message);
        }

        if (mDestFragment.isAdded()) {
            mDestFragment.hideProgress();
            mDestFragment.updateLabels();
        }

        // Necesario para evitar un problema cuando el servicio termina muy rápido
        new Handler().postDelayed(new Runnable() {
            public void run() {
                showCopyButton();
            }
        }, 500);
    }


    /**
     * Gestiona los eventos lanzados desde el servicio de copia
     */
    private final BroadcastReceiver onCopyEvent = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            switch (intent.getAction()) {

                case CopyService.ACTION_START:

                    onCopyStarted();
                    break;

                case CopyService.ACTION_PROGRESS:

                    int progress = intent.getIntExtra(CopyService.RESPONSE_PROGRESS, 0);
                    int total = intent.getIntExtra(CopyService.RESPONSE_TOTAL, 0);

                    onCopyProgress(progress, total);
                    break;

                case CopyService.ACTION_END:

                    boolean result = intent.getBooleanExtra(CopyService.RESPONSE_RESULT, false);
                    Exception exception = (Exception) intent.getSerializableExtra(CopyService.RESPONSE_ERROR);

                    onCopyEnded(result, exception);
                    break;
            }
        }
    };


    /**
     * Muestra el botón para iniciar la copia de archivos
     */
    private void showCopyButton() {

        if (!mFabCopy.isShown()) {
            mFabCopy.animate().cancel();
            mFabCopy.show();
        }
    }


    /**
     * Oculta el botón para iniciar la copia de archivos
     */
    private void hideCopyButton() {

        if (mFabCopy.isShown()) {
            mFabCopy.animate().cancel();
            mFabCopy.hide();
        }
    }

}
