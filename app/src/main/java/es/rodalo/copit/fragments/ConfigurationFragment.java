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
import android.preference.CheckBoxPreference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;

import java.util.List;

import es.rodalo.copit.R;
import es.rodalo.copit.utils.Preferences;
import es.rodalo.copit.utils.Sources;

/**
 * Fragmento encargado de generar la pantalla de configuración
 */
public class ConfigurationFragment extends PreferenceFragment {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.configuration);
    }


    @Override
    public void onResume() {
        super.onResume();

        loadSources();
    }


    /**
     * Carga los checkbox asociados a las carpetas de fotos
     */
    private void loadSources() {

        PreferenceCategory sourcesCategory = (PreferenceCategory) getPreferenceScreen().findPreference("SOURCES_CATEGORY");

        sourcesCategory.removeAll();

        List<Sources> selectedSources = Preferences.getSelectedSources();

        for (Sources source : Sources.values()) {

            CheckBoxPreference check = new CheckBoxPreference(getActivity());

            boolean exists = source.exists();

            check.setTitle(getString(source.getStringId()));
            check.setEnabled(exists);
            check.setChecked(exists && selectedSources.contains(source));
            check.setKey(source.getKey());
            check.setDefaultValue(false);

            sourcesCategory.addPreference(check);
        }
    }

}
