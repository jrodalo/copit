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

package es.rodalo.copit.fragments;

import com.nononsenseapps.filepicker.FilePickerActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.rodalo.copit.R;
import es.rodalo.copit.utils.Preferences;

/**
 * Fragmento encargado de la sección de destino
 */
public class DestFragment extends Fragment {

    private static final String ARG_DEST_PATH = "path";
    private static final int REQUEST_DEST_DIRECTORY = 2;

    private String mPath;

    @Bind(R.id.dest_select_folder_panel) LinearLayout mSelectFolderPanel;
    @Bind(R.id.dest_main_panel) LinearLayout mMainPanel;
    @Bind(R.id.dest_main_title) TextView mMainTitle;
    @Bind(R.id.dest_main_subtitle) TextView mMainSubtitle;
    @Bind(R.id.dest_progress_panel) LinearLayout mProgressPanel;
    @Bind(R.id.dest_progress_bar) ProgressBar mProgressBar;
    @Bind(R.id.dest_progress_subtitle) TextView mProgressSubtitle;


    /**
     * Obtiene una nueva instancia de este fragmento para la carpeta indicada
     */
    public static DestFragment newInstance(String path) {

        DestFragment fragment = new DestFragment();

        Bundle args = new Bundle();

        args.putString(ARG_DEST_PATH, path);

        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mPath = getArguments().getString(ARG_DEST_PATH, "");
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dest, container, false);

        ButterKnife.bind(this, view);

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        updateLabels();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    /**
     * Muestra el selector de directorios para la carpeta de destino
     */
    @OnClick(R.id.dest_select_folder_button)
    public void chooseDest() {

        Intent i = new Intent(getContext(), FilePickerActivity.class);

        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, true);
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_DIR);
        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());

        startActivityForResult(i, REQUEST_DEST_DIRECTORY);
    }


    /**
     * Muestra el nombre de la carpeta de destino o un aviso si no existe
     */
    public void updateLabels() {

        if (mPath == null || mPath.isEmpty()) {

            showSelectFolderPanel();

        } else {

            File directory = new File(mPath);

            if (directory.exists() && directory.isDirectory()) {

                mMainTitle.setText(directory.getName());

                long lastTime = Preferences.getLastTime();
                long now = System.currentTimeMillis();

                if (lastTime > 0) {
                    mMainSubtitle.setVisibility(now - lastTime > DateUtils.MINUTE_IN_MILLIS ? View.VISIBLE : View.GONE);
                    mMainSubtitle.setText(DateUtils.getRelativeTimeSpanString(lastTime, now, DateUtils.SECOND_IN_MILLIS, 0));
                }

                showMainPanel();

            } else {

                showSelectFolderPanel();
            }
        }
    }


    /**
     * Muestra el panel principal
     */
    private void showMainPanel() {
        mProgressPanel.setVisibility(View.GONE);
        mMainPanel.setVisibility(View.VISIBLE);
        mSelectFolderPanel.setVisibility(View.GONE);
    }


    /**
     * Muestra el panel para seleccionar la carpeta de destino
     */
    private void showSelectFolderPanel() {
        mProgressPanel.setVisibility(View.GONE);
        mMainPanel.setVisibility(View.GONE);
        mSelectFolderPanel.setVisibility(View.VISIBLE);
    }


    /**
     * Muestra la barra de progreso
     */
    public void showProgressPanel() {
        mProgressPanel.setVisibility(View.VISIBLE);
        mMainPanel.setVisibility(View.GONE);
        mSelectFolderPanel.setVisibility(View.GONE);
    }


    /**
     * Actualiza el valor de la barra de progreso según los valores indicados
     */
    public void updateProgress(int progress, int total) {

        progress = (progress >= 0) ? progress : 0;
        total = (total > 0) ? total : 1;

        mProgressBar.setProgress((progress * 100) / total);
        mProgressSubtitle.setText(getString(R.string.copy_count, progress, total));
    }


    /**
     * Oculta la barra de progreso
     */
    public void hideProgress() {
        mProgressBar.setProgress(0);
        mProgressPanel.setVisibility(View.GONE);
        mMainPanel.setVisibility(View.VISIBLE);
        mSelectFolderPanel.setVisibility(View.GONE);
    }


    /**
     * Gestiona la respuesta del selector de directorios
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_DEST_DIRECTORY) {

            mPath = data.getData().getPath();

            Preferences.setDestFolder(mPath);
        }
    }

}
