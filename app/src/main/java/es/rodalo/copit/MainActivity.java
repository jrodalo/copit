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

import android.app.Dialog;
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
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.rodalo.copit.fragments.DestFragment;
import es.rodalo.copit.fragments.SourceFragment;
import es.rodalo.copit.services.CopyService;
import es.rodalo.copit.utils.Device;
import es.rodalo.copit.utils.Error;
import es.rodalo.copit.utils.Message;
import es.rodalo.copit.utils.Preferences;

/**
 * Actividad principal de la aplicación
 */
public class MainActivity extends FragmentActivity {

    private static final float MINIMUM_BATTERY_LEVEL = 5f; // 5%

    private SourceFragment mSourceFragment;
    private DestFragment mDestFragment;
    private Dialog mChooseFoldersDialog;

    @Bind(R.id.main_fab_copy) FloatingActionButton mFabCopy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        firstTimeChecks();

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
        filter.addAction(CopyService.ACTION_ERROR);

        LocalBroadcastManager.getInstance(this).registerReceiver(onCopyEvent, filter);
    }


    /**
     * Abre el menú inferior que permite seleccionar las carpetas de origen/destino
     */
    @OnClick(R.id.main_open_dialog)
    public void openFolderDialog() {

        View view = getLayoutInflater().inflate(R.layout.dialog_choose_folders, null);

        mChooseFoldersDialog = new Dialog(MainActivity.this, R.style.MaterialDialogSheet);
        mChooseFoldersDialog.setContentView(view);
        mChooseFoldersDialog.setCancelable(true);
        mChooseFoldersDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mChooseFoldersDialog.getWindow().setGravity(Gravity.BOTTOM);
        mChooseFoldersDialog.show();

        TextView chooseSourceButton = ButterKnife.findById(view, R.id.text_choose_source);
        TextView chooseDestButton = ButterKnife.findById(view, R.id.text_choose_dest);

        chooseSourceButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mSourceFragment.chooseSource();
                mChooseFoldersDialog.dismiss();
            }
        });

        chooseDestButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mDestFragment.chooseDest();
                mChooseFoldersDialog.dismiss();
            }
        });
    }


    /**
     * Realiza algunas comprobaciones si es la primera vez que se ejecuta la app
     */
    private void firstTimeChecks() {

        if (Preferences.isTheFirstTime()) {

            String sourceFolder = Device.guessSourceFolder();

            if (!sourceFolder.isEmpty()) {
                Preferences.setSourceFolder(sourceFolder);
            }

            String destFolder = Device.guessDestFolder();

            if (!destFolder.isEmpty()) {
                Preferences.setDestFolder(destFolder);
            }

            Preferences.setFirstTime(false);
        }
    }


    /**
     * Carga los fragmentos para el origen y destino
     */
    private void loadFragments() {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        mSourceFragment = SourceFragment.newInstance(Preferences.getSourceFolder());
        mDestFragment = DestFragment.newInstance(Preferences.getDestFolder());

        ft.replace(R.id.main_source_fragment, mSourceFragment);
        ft.replace(R.id.main_dest_fragment, mDestFragment);

        ft.commit();
    }


    /**
     * Inicia el servicio que realiza la copia entre el origen y destino
     */
    @OnClick(R.id.main_fab_copy)
    public void startCopy() {

        try {

            File source = new File(Preferences.getSourceFolder());
            File dest = new File(Preferences.getDestFolder());

            Intent intent = new Intent(this, CopyService.class);
            intent.putExtra(CopyService.PARAM_SOURCE, source);
            intent.putExtra(CopyService.PARAM_DEST, dest);
            startService(intent);

        } catch (Exception ex) {
            Message.error(mDestFragment.getView(), ex.getMessage());
        }
    }


    /**
     * Gestiona los eventos lanzados desde el servicio de copia
     */
    private final BroadcastReceiver onCopyEvent = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {


            switch (intent.getAction()) {

                case CopyService.ACTION_START:

                    hideCopyButton();

                    if (mDestFragment.isAdded()) {
                        mDestFragment.showProgressPanel();
                    }

                    break;

                case CopyService.ACTION_PROGRESS:

                    if (mDestFragment.isAdded()) {

                        int progress = intent.getIntExtra(CopyService.RESPONSE_PROGRESS, 0);
                        int total = intent.getIntExtra(CopyService.RESPONSE_TOTAL, 0);

                        mDestFragment.showProgressPanel();
                        mDestFragment.updateProgress(progress, total);
                    }

                    break;

                case CopyService.ACTION_END:

                    boolean result = intent.getBooleanExtra(CopyService.RESPONSE_RESULT, false);

                    if (result) {
                        Message.success(mDestFragment.getView(), getString(R.string.copy_success));
                        Preferences.setLastTime(new Date());
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

                    break;

                case CopyService.ACTION_ERROR:

                    Exception exception = (Exception) intent.getSerializableExtra(CopyService.RESPONSE_ERROR);

                    String message = (exception instanceof Error) ?
                            getString(((Error) exception).getMessageKey()) :
                            getString(R.string.copy_error);

                    Message.error(mDestFragment.getView(), message);

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
