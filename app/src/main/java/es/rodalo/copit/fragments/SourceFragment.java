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
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.rodalo.copit.R;
import es.rodalo.copit.utils.Files;
import es.rodalo.copit.utils.Preferences;
import es.rodalo.copit.utils.Sources;
import es.rodalo.copit.views.adapters.ImageAdapter;

/**
 * Fragmento encargado de la sección de origen
 */
public class SourceFragment extends Fragment {

    private List<Sources> mSelectedSources;

    @Bind(R.id.source_select_sources_panel) LinearLayout mSelectSourcesPanel;
    @Bind(R.id.source_grid_panel) RelativeLayout mMainPanel;
    @Bind(R.id.source_text_subtitle) TextView mTextSubtitle;
    @Bind(R.id.source_grid_photos) GridView mGridPhotos;


    /**
     * Obtiene una nueva instancia de este fragmento para la carpeta indicada
     */
    public static SourceFragment newInstance() {

        return new SourceFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_source, container, false);

        ButterKnife.bind(this, view);

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

        mSelectedSources = Preferences.getSelectedSources();

        updateLabels();
        loadPhotos();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        ButterKnife.unbind(this);
    }


    /**
     * Muestra el panel principal
     */
    private void showMainPanel() {
        mMainPanel.setVisibility(View.VISIBLE);
        mSelectSourcesPanel.setVisibility(View.GONE);
    }


    /**
     * Muestra el panel para seleccionar la carpeta de origen
     */
    private void showSelectSourcesPanel() {
        mMainPanel.setVisibility(View.GONE);
        mSelectSourcesPanel.setVisibility(View.VISIBLE);
    }



    /**
     * Muestra el nombre de la carpeta de origen y el contador de archivos
     */
    private void updateLabels() {

        if (mSelectedSources.isEmpty()) {

            showSelectSourcesPanel();

        } else {

            int imageCount = 0;
            int videoCount = 0;

            for (Sources source : mSelectedSources) {

                List<File> paths = source.getActivePaths();

                for (File path : paths) {
                    imageCount += Files.getImages(path).size();
                    videoCount += Files.getVideos(path).size();
                }
            }

            String photosCountText = getResources().getQuantityString(R.plurals.photos_count, imageCount, imageCount);
            String videosCountText = getResources().getQuantityString(R.plurals.videos_count, videoCount, videoCount);

            mTextSubtitle.setText(getString(R.string.file_count, photosCountText, videosCountText));

            showMainPanel();
        }
    }


    /**
     * Inicia la carga de fotos ubicadas en las carpetas seleccionadas
     */
    private void loadPhotos() {

        if (mSelectedSources.isEmpty()) {
            return;
        }

        List<File> recentImages = new ArrayList<>();

        for (Sources source : mSelectedSources) {

            List<File> paths = source.getActivePaths();

            for (File path : paths) {
                recentImages.addAll(getRecentImages(path));
            }
        }

        mGridPhotos.setVisibility(View.VISIBLE);
        mGridPhotos.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        mGridPhotos.setAdapter(new ImageAdapter(getActivity().getApplicationContext(), recentImages));
    }


    /**
     * Obtiene las imagenes de la carpeta indicada ordenadas por fecha de modificación
     */
    private List<File> getRecentImages(File path) {

        List<File> images = new ArrayList<>(Files.getImages(path));

        Collections.sort(images, Files.lastModifiedComparator);

        return images;
    }
}
