package es.rodalo.copit;

import com.nononsenseapps.filepicker.FilePickerActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import es.rodalo.copit.fragments.DestFragment;
import es.rodalo.copit.fragments.SourceFragment;
import es.rodalo.copit.services.CopyService;
import es.rodalo.copit.utils.Device;
import es.rodalo.copit.utils.Message;
import es.rodalo.copit.utils.Preferences;

/**
 * Actividad principal de la aplicación
 */
public class MainActivity extends FragmentActivity {

    private static final int REQUEST_SOURCE_DIRECTORY = 1;
    private static final int REQUEST_DEST_DIRECTORY = 2;
    private static final float MINIMUM_BATTERY_LEVEL = 5f; // 5%

    private FloatingActionButton mFabCopy;
    private SourceFragment mSourceFragment;
    private DestFragment mDestFragment;
    private Dialog mChooseFoldersDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        firstTimeChecks();

        loadFragments();

        mFabCopy = (FloatingActionButton) findViewById(R.id.main_fab_copy);

        mFabCopy.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startCopy();
            }

        });

        Button openFolderDialogButton = (Button) findViewById(R.id.main_open_dialog);

        openFolderDialogButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                openFolderDialog();
            }

        });
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

        checkUsb();
    }


    /**
     * Abre el menú inferior que permite seleccionar las carpetas de origen/destino
     */
    private void openFolderDialog() {

        View view = getLayoutInflater().inflate(R.layout.dialog_choose_folders, null);

        TextView chooseSourceButton = (TextView) view.findViewById(R.id.text_choose_source);
        TextView chooseDestButton = (TextView) view.findViewById(R.id.text_choose_dest);

        mChooseFoldersDialog = new Dialog(MainActivity.this, R.style.MaterialDialogSheet);
        mChooseFoldersDialog.setContentView(view);
        mChooseFoldersDialog.setCancelable(true);
        mChooseFoldersDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mChooseFoldersDialog.getWindow().setGravity(Gravity.BOTTOM);
        mChooseFoldersDialog.show();

        chooseSourceButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                chooseSource();
                mChooseFoldersDialog.dismiss();
            }
        });

        chooseDestButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                chooseDest();
                mChooseFoldersDialog.dismiss();
            }
        });
    }


    /**
     * Muestra el selector de directorios para la carpeta de origen
     */
    private void chooseSource() {

        Intent i = new Intent(MainActivity.this, FilePickerActivity.class);

        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_DIR);
        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());

        startActivityForResult(i, REQUEST_SOURCE_DIRECTORY);
    }


    /**
     * Muestra el selector de directorios para la carpeta de destino
     */
    private void chooseDest() {

        Intent i = new Intent(MainActivity.this, FilePickerActivity.class);

        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, true);
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_DIR);
        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());

        startActivityForResult(i, REQUEST_DEST_DIRECTORY);
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
     * Comprueba si es posible ejecutar la copia de archivos entre el origen y destino
     */
    public boolean canExecuteCopy(File source, File dest) throws IOException {

        if (!source.exists() || !source.isDirectory()) {
            throw new IOException(getString(R.string.copy_error_source));
        }

        if (!dest.exists() || !dest.isDirectory() || !dest.canWrite()) {
            throw new IOException(getString(R.string.copy_error_dest));
        }

        if (source.equals(dest)) {
            throw new IOException(getString(R.string.copy_error_samefolder));
        }

        if (Device.getBatteryLevel() < MINIMUM_BATTERY_LEVEL) {
            throw new IOException(getString(R.string.copy_error_battery));
        }

        return true;
    }


    /**
     * Inicia el servicio que realiza la copia entre el origen y destino
     */
    private void startCopy() {

        try {

            File source = new File(Preferences.getSourceFolder());
            File dest = new File(Preferences.getDestFolder());

            if (canExecuteCopy(source, dest)) {

                File backup = new File(dest, getBackupFolderName());

                Intent intent = new Intent(this, CopyService.class);
                intent.putExtra(CopyService.PARAM_SOURCE, source);
                intent.putExtra(CopyService.PARAM_DEST, backup);
                startService(intent);
            }

        } catch (IOException ex) {
            Message.error(mDestFragment.getView(), ex.getMessage());
        }
    }


    /**
     * Gestiona los eventos lanzados desde el servicio de copia
     */
    private final BroadcastReceiver onCopyEvent = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (CopyService.ACTION_START.equals(intent.getAction())) {

                hideCopyButton();

                if (mDestFragment.isAdded()) {
                    mDestFragment.showProgress();
                }

            } else if (CopyService.ACTION_PROGRESS.equals(intent.getAction())) {

                hideCopyButton();

                if (mDestFragment.isAdded()) {

                    int progress = intent.getIntExtra(CopyService.RESPONSE_PROGRESS, 0);
                    int total = intent.getIntExtra(CopyService.RESPONSE_TOTAL, 0);

                    mDestFragment.showProgress();
                    mDestFragment.updateProgress(progress, total);
                }

            } else if (CopyService.ACTION_END.equals(intent.getAction())) {

                boolean result = intent.getBooleanExtra(CopyService.RESPONSE_RESULT, false);

                if (mDestFragment.isAdded()) {
                    mDestFragment.hideProgress();
                }

                showCopyButton();

                if (result) {
                    Message.success(mDestFragment.getView(), getString(R.string.copy_success));
                } else {
                    Message.error(mDestFragment.getView(), getString(R.string.copy_error));
                }
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


    /**
     * Obtiene el nombre de la carpeta donde se guardarán los archivos copiados
     */
    private String getBackupFolderName() {

        String appId = BuildConfig.APPLICATION_ID;

        String appName = appId.substring(appId.lastIndexOf(".") + 1, appId.length());

        return appName.toLowerCase() + "_backup/" + new SimpleDateFormat("ddMMyyyy", Locale.US).format(new Date());
    }


    /**
     * Prueba para intentar obtener el nombre del pendrive
     */
    private void checkUsb() {

        Intent intent = getIntent();

        if (intent == null || !UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(intent.getAction())) {
            return;
        }

        UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

        if (device != null) {
            //mUsbManager.requestPermission(mDevice, mPermissionIntent);
            Message.success(mDestFragment.getView(), device.getDeviceName());
        }
    }


    /**
     * Gestiona la respuesta del selector de directorios
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (REQUEST_SOURCE_DIRECTORY == requestCode) {

            String path = data.getData().getPath();

            Preferences.setSourceFolder(path);

            mSourceFragment.changePath(path);

            return;
        }

        if (REQUEST_DEST_DIRECTORY == requestCode) {

            String path = data.getData().getPath();

            Preferences.setDestFolder(path);

            mDestFragment.changePath(path);

            return;
        }
    }

}
