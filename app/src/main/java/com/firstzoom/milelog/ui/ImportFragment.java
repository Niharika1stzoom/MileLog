package com.firstzoom.milelog.ui;

import static android.app.Activity.RESULT_OK;

import static com.firstzoom.milelog.util.AppConstants.TAG;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firstzoom.milelog.R;
import com.firstzoom.milelog.databinding.FragmentImportBinding;
import com.firstzoom.milelog.model.LocationPath;
import com.firstzoom.milelog.model.Tag;
import com.firstzoom.milelog.model.Trip;
import com.firstzoom.milelog.util.AppConstants;
import com.firstzoom.milelog.util.AppUtil;
import com.firstzoom.milelog.util.ImportExportUtil;
import com.firstzoom.milelog.util.PermissionUtil;
import com.firstzoom.milelog.util.RealPathUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ImportFragment extends Fragment {
    private static final int FILE_SELECT_CODE = 45;
    FragmentImportBinding mBinding;
    public TripViewModel mViewModel;
    private File mFile = null;
    JSONObject dbBackup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentImportBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(TripViewModel.class);
        mBinding.importButton.setOnClickListener(view1 -> {
            if(PermissionUtil.hasLocationBackgroundPermissions(getActivity().getApplicationContext()))
            showFileChooser();
        });

        mBinding.exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(PermissionUtil.hasLocationBackgroundPermissions(getActivity().getApplicationContext()))
                getTrips();
            }
        });
    }

    private void getTrips() {
        displayLoader();
        mViewModel.getTripList().observe(getViewLifecycleOwner(), tripList -> {
              hideLoader();
                dbBackup=new JSONObject();
                dbBackup= ImportExportUtil.getJsonTrip(tripList,dbBackup);
                getLocationData();
        });
    }

    void getLocationData() {
        displayLoader();
        mViewModel.getLocationPaths().observe(getViewLifecycleOwner(), locList -> {
              hideLoader();
            dbBackup = ImportExportUtil.getJsonPath(locList,dbBackup);
            Log.d(TAG, "Copying db to file " + dbBackup.toString());
            if(writeToStorage(dbBackup.toString()))
                mBinding.msg.setText(getString(R.string.export_msg));
            else
                AppUtil.showSnackbar(getView(),getString(R.string.exportFail));
        });
    }

    private Boolean writeToStorage(String jsonContent) {
        try {
            if (mFile == null)
                mFile = getFile();
            Log.d(TAG, "Path of export" + mFile.getAbsolutePath());
            FileWriter writer = new FileWriter(mFile);
            writer.append(jsonContent);
            writer.flush();
            writer.close();
            return true;

        } catch (Exception e) {
            Log.d(TAG, "Err Writing to file" + e.getLocalizedMessage());
            e.printStackTrace();
            return false;
        }
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/plain");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select the backup file."),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getContext(), "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    // Get the path
                    if (uri != null) {
                        fillWithStartingData(getContext(), uri);
                    }
                    break;
                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private static JSONArray loadJSONArray(Context context, Uri filename, String name) {
        String file="";
        try {
            file= readTextFromUri(filename,context);
            JSONObject json = new JSONObject(file);
            return new JSONArray(json.getString(name));
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG,"Reading from file Error"+e.getLocalizedMessage());
        }

            return null;
    }
    public static String readTextFromUri(Uri uri,Context context) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream inputStream =
                     context.getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(Objects.requireNonNull(inputStream)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }
        Log.d(AppConstants.TAG,"Contents of a file"+stringBuilder.toString());
        return stringBuilder.toString();
    }
    private void fillWithStartingData(Context context, Uri filename) {
        //RepoDao dao = getDatabase(context).repoDao();
        ArrayList<Trip> list = new ArrayList<>();
        JSONArray trips = loadJSONArray(context, filename, AppConstants.TRIP_TABLE);
        if(trips!=null){
        try {
            for (int i = 0; i < trips.length(); i++) {
                JSONObject tripsJSONObject = trips.getJSONObject(i);
                String contactName = tripsJSONObject.getString("tags");
                Gson gson = new Gson();
                Type type = new TypeToken<List<Tag>>() {
                }.getType();
                Type typeTrip = new TypeToken<Trip>() {
                }.getType();
                Trip trip = gson.fromJson(tripsJSONObject.toString(), typeTrip);
                mViewModel.insertTrip(trip);


            }

        } catch (JSONException e) {

        }}
        else
            AppUtil.showSnackbar(getView(),getString(R.string.import_fail));
        //Location
        JSONArray paths = loadJSONArray(context, filename, AppConstants.LOCATION_PATH_TABLE);
        if(paths!=null)
        try {
            for (int i = 0; i < paths.length(); i++) {
                JSONObject pathJSONObject = paths.getJSONObject(i);
                Gson gson = new Gson();
                Type typePath = new TypeToken<LocationPath>() {
                }.getType();
                LocationPath path = gson.fromJson(pathJSONObject.toString(), typePath);
                mViewModel.insertPath(path);
            }
            mBinding.msg.setText(getString(R.string.import_success));
        } catch (JSONException e) {

        }
    }
    File getFile() {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues values = new ContentValues();
            String fileName = AppConstants.FILE_NAME + timeStamp + ".txt";
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);   // file name
            values.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/" + getContext().getString(R.string.app_name));
            Uri extVolumeUri = MediaStore.Files.getContentUri("external");
            Uri fileUri = getContext().getContentResolver().insert(extVolumeUri, values);
            try {
                OutputStream outputStream = getContext().getContentResolver().openOutputStream(fileUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.d(TAG,"Creating new file issue"+e.getLocalizedMessage());
            }
            File file=new File(RealPathUtil.getRealPath(getContext(),fileUri));
            return file;
        }
        else {
            File storageDir = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                            + "/" + getContext().getString(R.string.app_name));
            boolean success = false;
            if (!storageDir.exists()) {
                success = storageDir.mkdir();
            }
            String fileName = AppConstants.FILE_NAME + timeStamp + ".txt";
            File tripFile = null;
            tripFile = new File(storageDir, fileName);
            return tripFile;
        }

    }
    private void hideLoader() {
        mBinding.viewLoader.rootView.setVisibility(View.GONE);
        mBinding.group.setVisibility(View.VISIBLE);
    }

    private void displayLoader() {
        mBinding.viewLoader.rootView.setVisibility(View.VISIBLE);
        mBinding.group.setVisibility(View.GONE);
    }
}