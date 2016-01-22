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

package es.rodalo.copit.views.adapters;

import com.bumptech.glide.Glide;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.io.File;
import java.util.List;

import es.rodalo.copit.views.widgets.SquaredImageView;

/**
 * Muestra las fotos en grid usando la librería Picasso
 */
public class ImageAdapter extends BaseAdapter {

    private final Context context;
    private final List<File> files;

    public ImageAdapter(Context context, List<File> files) {
        this.context = context;
        this.files = files;
    }

    @Override
    public int getCount() {
        return files.size();
    }

    @Override
    public File getItem(int position) {
        return files.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View recycled, ViewGroup parent) {

        SquaredImageView view = (SquaredImageView) recycled;

        if (view == null) {
            view = new SquaredImageView(context);
        }

        File image = getItem(position);

        Glide.with(context)
                .load(image)
                .centerCrop()
                .crossFade()
                .into(view);

        return view;
    }

}
