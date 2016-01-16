package es.rodalo.copit.fragments;

import com.nononsenseapps.filepicker.FilePickerActivity;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import es.rodalo.copit.R;
import es.rodalo.copit.utils.Preferences;
import es.rodalo.copit.views.adapters.ImageAdapter;

/**
 * Fragmento encargado de la sección de origen
 */
public class SourceFragment extends Fragment {

    private static final String ARG_SOURCE_PATH = "path";
    private static final int REQUEST_SOURCE_DIRECTORY = 1;
    private static final String[] imageExtensions = new String[]{"jpg", "jpeg", "png", "gif", "bmp"};
    private static final String[] videoExtensions = new String[]{"avi", "mpg", "mpeg", "mov"};

    private TextView mTextTitle;
    private TextView mTextSubtitle;
    private GridView mGridPhotos;
    private String mPath;


    /**
     * Obtiene una nueva instancia de este fragmento para la carpeta indicada
     */
    public static SourceFragment newInstance(String path) {

        SourceFragment fragment = new SourceFragment();

        Bundle args = new Bundle();

        args.putString(ARG_SOURCE_PATH, path);

        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mPath = getArguments().getString(ARG_SOURCE_PATH, "");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_source, container, false);

        mTextTitle = (TextView) view.findViewById(R.id.source_text_title);
        mTextSubtitle = (TextView) view.findViewById(R.id.source_text_subtitle);
        mGridPhotos = (GridView) view.findViewById(R.id.source_grid_photos);

        Button chooseSourceButton = (Button) view.findViewById(R.id.source_button_directory);

        chooseSourceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(), FilePickerActivity.class);

                i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
                i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
                i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_DIR);
                i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());

                startActivityForResult(i, REQUEST_SOURCE_DIRECTORY);
            }
        });

        updateLabels();

        loadPhotos();

        return view;
    }


    /**
     * Muestra el nombre de la carpeta de origen y el contador de archivos
     */
    private void updateLabels() {

        if (mPath == null || mPath.isEmpty()) {

            mGridPhotos.setVisibility(View.INVISIBLE);
            mTextTitle.setText(getString(R.string.select_folder));

        } else {

            File directory = new File(mPath);

            if (directory.exists() && directory.isDirectory()) {

                mTextTitle.setText(directory.getName());

                int imageCount = getImages(directory).size();
                int videoCount = getVideos(directory).size();

                String photosCountText = getResources().getQuantityString(R.plurals.photos_count, imageCount, imageCount);
                String videosCountText = getResources().getQuantityString(R.plurals.videos_count, videoCount, videoCount);

                mTextSubtitle.setText(getString(R.string.file_count, photosCountText, videosCountText));

            } else {

                mGridPhotos.setVisibility(View.INVISIBLE);
                mTextTitle.setText(getString(R.string.select_folder));
            }
        }
    }


    /**
     * Inicia la carga de fotos ubicadas en la carpeta de origen
     */
    private void loadPhotos() {

        if (mPath != null && !mPath.isEmpty()) {
            mGridPhotos.setVisibility(View.VISIBLE);
            mGridPhotos.setAdapter(new ImageAdapter(getActivity().getApplicationContext(), getRecentImages(mPath)));
        }
    }


    /**
     * Obtiene las imagenes de la carpeta indicada ordenadas por fecha de modificación
     */
    private List<File> getRecentImages(String path) {

        File directory = new File(path);

        if (!directory.exists()) {
            return Collections.emptyList();
        }

        List<File> images = new ArrayList<>(getImages(directory));

        Collections.sort(images, LastModifiedFileComparator.LASTMODIFIED_REVERSE);

        return images;
    }


    /**
     * Obtiene las imagenes de la carpeta indicada
     */
    private Collection<File> getImages(File directory) {

        return FileUtils.listFiles(
                directory,
                new SuffixFileFilter(imageExtensions, IOCase.INSENSITIVE),
                FalseFileFilter.FALSE);
    }


    /**
     * Obtiene los videos de la carpeta indicada
     */
    private Collection<File> getVideos(File directory) {

        return FileUtils.listFiles(
                directory,
                new SuffixFileFilter(videoExtensions, IOCase.INSENSITIVE),
                FalseFileFilter.FALSE);
    }


    /**
     * Gestiona la respuesta del selector de directorios
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (REQUEST_SOURCE_DIRECTORY == requestCode && Activity.RESULT_OK == resultCode) {

            mPath = data.getData().getPath();

            Preferences.setSourceFolder(mPath);

            updateLabels();

            loadPhotos();
        }
    }

}
