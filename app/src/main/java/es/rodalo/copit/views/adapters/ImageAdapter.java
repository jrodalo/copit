package es.rodalo.copit.views.adapters;

import com.squareup.picasso.Picasso;

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
    public View getView(int position, View convertView, ViewGroup parent) {

        SquaredImageView view = (SquaredImageView) convertView;

        if (view == null) {
            view = new SquaredImageView(context);
            view.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        File image = getItem(position);

        Picasso.with(context)
                .load(image)
                .centerCrop()
                .fit()
                .into(view);

        return view;
    }

}
