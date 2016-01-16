package es.rodalo.copit.fragments;

import com.nononsenseapps.filepicker.FilePickerActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;

import es.rodalo.copit.R;
import es.rodalo.copit.utils.Preferences;

/**
 * Fragmento encargado de la sección de destino
 */
public class DestFragment extends Fragment {

    private static final String ARG_DEST_PATH = "path";
    private static final int REQUEST_DEST_DIRECTORY = 2;

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
        mTextTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(), FilePickerActivity.class);

                i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
                i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, true);
                i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_DIR);
                i.putExtra(FilePickerActivity.EXTRA_START_PATH, mPath.isEmpty() ? Environment.getExternalStorageDirectory().getPath() : mPath);

                startActivityForResult(i, REQUEST_DEST_DIRECTORY);
            }
        });

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
     * Gestiona la respuesta del selector de directorios
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (REQUEST_DEST_DIRECTORY == requestCode && Activity.RESULT_OK == resultCode) {

            mPath = data.getData().getPath();

            Preferences.setDestFolder(mPath);

            updateLabels();
        }
    }

}
