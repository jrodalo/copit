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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;

import es.rodalo.copit.R;

/**
 * Fragmento encargado de la sección de destino
 */
public class DestFragment extends Fragment {

    private static final String ARG_DEST_PATH = "path";

    private String mPath;
    private TextView mTextTitle;
    private TextView mTextProgress;
    private ProgressBar mProgressBar;


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

        mProgressBar = (ProgressBar) view.findViewById(R.id.dest_progress);
        mTextProgress = (TextView) view.findViewById(R.id.dest_text_progress);
        mTextTitle = (TextView) view.findViewById(R.id.dest_text_title);

        updateLabels();

        return view;
    }


    /**
     * Muestra el nombre de la carpeta de destino o un aviso si no existe
     */
    private void updateLabels() {

        if (mPath == null || mPath.isEmpty()) {

            mTextTitle.setText(getString(R.string.select_folder));

        } else {

            File directory = new File(mPath);

            if (directory.exists() && directory.isDirectory()) {

                mTextTitle.setText(directory.getName());

            } else {

                mTextTitle.setText(getString(R.string.select_folder));
            }
        }
    }


    /**
     * Actualiza el valor de la barra de progreso según los valores indicados
     */
    public void updateProgress(int progress, int total) {

        progress = (progress >= 0) ? progress : 0;
        total = (total > 0) ? total : 1;

        mProgressBar.setProgress((progress * 100) / total);
        mTextProgress.setText(getString(R.string.copy_count, progress, total));
    }


    /**
     * Muestra la barra de progreso
     */
    public void showProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
        mTextProgress.setVisibility(View.VISIBLE);
    }


    /**
     * Oculta la barra de progreso
     */
    public void hideProgress() {
        mProgressBar.setProgress(0);
        mProgressBar.setVisibility(View.INVISIBLE);
        mTextProgress.setVisibility(View.INVISIBLE);
    }


    /**
     * Cambia la ruta asociada a este fragmento
     */
    public void changePath(String path) {

        mPath = path;

        updateLabels();
    }

}
