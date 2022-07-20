package com.firstzoom.milelog.ui.trip;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.firstzoom.milelog.R;
import com.firstzoom.milelog.databinding.FragmentEditTripBinding;
import com.firstzoom.milelog.databinding.TripFragmentBinding;
import com.firstzoom.milelog.model.LocationPath;
import com.firstzoom.milelog.model.Tag;
import com.firstzoom.milelog.model.Trip;
import com.firstzoom.milelog.room.AppDatabase;
import com.firstzoom.milelog.ui.TripViewModel;
import com.firstzoom.milelog.util.AddressUtil;
import com.firstzoom.milelog.util.AppConstants;
import com.firstzoom.milelog.util.AppExecutors;
import com.firstzoom.milelog.util.AppUtil;
import com.firstzoom.milelog.util.DistanceUtil;
import com.firstzoom.milelog.util.PermissionUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EditTripFragment extends Fragment {
    FragmentEditTripBinding mBinding;
    int id;
    Trip mTrip;
    private TripViewModel mViewModel;
    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer;
    private  String mFileName = null;
    private boolean recording = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getInt(AppConstants.KEY_TRIP_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = FragmentEditTripBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(TripViewModel.class);
        getTrip();
    }


    private void getTrip() {
        displayLoader();
        mViewModel.getTrip(id).observe(getViewLifecycleOwner(), trip -> {
            hideLoader();
            if (trip == null) {
                displayEmptyView();
            } else {
                mTrip = trip;
                updateUI(trip);
            }
        });

    }

    private void startRecording() {
        if (CheckPermissions()) {
            showRecording();
            if(mFileName==null) {
                File dir = getActivity().getExternalFilesDir(Environment.DIRECTORY_MUSIC);
                String suffix = ".3gp";
                mFileName += "/AudioRecording.3gp";
                try {
                    File audio = File.createTempFile(
                            "AudioRecording",  /* prefix */
                            suffix,         /* suffix */
                            dir      /* directory */
                    );
                    mFileName = audio.getAbsolutePath();

                    Log.d("MileDebug", "file " + mFileName);
                } catch (IOException e) {
                    Log.d(AppConstants.TAG, "Audii File creation err " + e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }
            else{
                Log.d(AppConstants.TAG, "File exists" + mFileName);
            }
            //below method is used to initialize the media recorder clss
            mRecorder = new MediaRecorder();
            //below method is used to set the audio source which we are using a mic.
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            //below method is used to set the output format of the audio.
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            //below method is used to set the audio encoder for our recorded audio.
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            //below method is used to set the output file location for our recorded audio
            mRecorder.setOutputFile(mFileName);
            try {
                //below mwthod will prepare our audio recorder class
                mRecorder.prepare();
            } catch (IOException e) {
                Log.e(AppConstants.TAG, "prepare() failed" + e.getLocalizedMessage());
            }
            // start method will start the audio recording.
            mRecorder.start();
            //statusTV.setText("Recording Started");
        } else {
            //if audio recording permissions are not granted by user below method will ask for runtime permission for mic and storage.
            //RequestPermissions();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mPlayer!=null && mPlayer.isPlaying())
            pausePlaying();
        if(mRecorder!=null)
            pauseRecording();
    }

    public void playAudio() {
        if(mFileName==null && mTrip.getAudioFilePath()==null) {
         AppUtil.showSnackbar(getView(),getString(R.string.recordAudio));
            return;
        }
        String audioFile;
        if(mFileName!=null && !TextUtils.isEmpty(mFileName))
        {
            audioFile=mFileName;
        }
        else
            audioFile= mTrip.getAudioFilePath();

        mBinding.play.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_stop));
        mPlayer = new MediaPlayer();
        try {
            //below method is used to set the data source which will be our file name
            mPlayer.setDataSource(audioFile);
            //below method will prepare our media player
            mPlayer.prepare();
            //below method will start our media player.
            mPlayer.start();
            mBinding.seekbar.setMax(mPlayer.getDuration());
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mBinding.seekbar.setProgress(0);
                    mediaPlayer.release();
                    mediaPlayer=null;
                    mPlayer=null;
                    mBinding.play.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_play));
                }
            });
            new Timer().scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    try {
                        mBinding.seekbar.setProgress(mPlayer.getCurrentPosition());
                    }catch (Exception e){

                    }

                }
            },0,1000);
            //statusTV.setText("Recording Started Playing");
        } catch (IOException e) {
            Log.e("TAG", "prepare() failed");
        }
    }

    //stop recording
    public void pauseRecording() {
        showStopRecording();
        //mBinding.mike.setBackgroundColor(getResources().getColor(R.color.teal_200));
        //below method will stop the audio recording.
        mRecorder.stop();
        //below method will release the media recorder class.
        mRecorder.release();
        mRecorder = null;
        //statusTV.setText("Recording Stopped");

    }
//stop playing
    public void pausePlaying() {
        //this method will release the media player class and pause the playing of our recorded audio.
        mPlayer.release();
        mPlayer = null;
        mBinding.play.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_play));
    }

    private boolean CheckPermissions() {
        return true;
    }

    private void updateUI(Trip trip) {
       // mFileName = trip.getAudioFilePath();
        if(trip.getLatitudeStart()!=null && trip.getLongitudeStart()!=null )
        mBinding.startLabel.setText(AddressUtil.getCompleteAddressString(getContext(), trip.getLatitudeStart(), trip.getLongitudeStart()));
       if(trip.getLatitudeStop()!=null && trip.getLongitudeStop()!=null )
        mBinding.endLabel.setText(AddressUtil.getCompleteAddressString(getContext(), trip.getLatitudeStop(), trip.getLongitudeStop()));
        if(trip.getTags()!=null && trip.getTags().size()>0){
            String s="";
            for(Tag t:trip.getTags()){
                s+=t.text+"  ";
            }
            mBinding.tagInput.setText(s);
        }
        if (trip.getDistance() != null)
            mBinding.distanceLabel.setText("Distance(km): " + trip.getDistance());
        if (trip.getStartTime() != null)
            mBinding.startTime.setText(AppUtil.getDisplayDate(trip.getStartTime()));
        else
            mBinding.startTime.setText("");
        if (trip.getEndTime() != null)
            mBinding.endTime.setText(AppUtil.getDisplayDate(trip.getEndTime()));
        else
            mBinding.endTime.setText("");
        if (trip.getStartTime() != null && trip.getEndTime() != null) {
            long diff = trip.getEndTime().getTime() - trip.getStartTime().getTime();
            // long diff = d2.getTime() - d1.getTime();//as given
            long seconds = TimeUnit.MILLISECONDS.toSeconds(diff);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
            long mins = diff / 60;
            mBinding.durationLabel.setText("Duration(mins): " + minutes);
        }
        if(mTrip.getDescription()!=null)
            mBinding.notesInput.setText(mTrip.getDescription());
        if(trip.getLatitudeStart()!=null && trip.getLatitudeStop()!=null) {
            //  Double dist= DistanceUtil.calDistance(TripItem.getLatitudeStart(),TripItem.getLongitudeStart(),TripItem.getLatitudeStop(),TripItem.getLongitudeStop());
            if (trip.getDistance() != null && trip.getDistance() > 0.0) {
                mBinding.distanceLabel.setText("Distance(km): " + String.format("%.2f", trip.getDistance()));
            } else {
                Double dist = DistanceUtil.calDistance(trip.getLatitudeStart(), trip.getLongitudeStart()
                        , trip.getLatitudeStop(), trip.getLongitudeStop());
                mBinding.distanceLabel.setText("Distance(km): " + String.format("%.2f", dist));
            }
        }
        mBinding.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPlayer != null)
                    pausePlaying();
                else
                    if(mFileName!=null || (mTrip!=null && mTrip.getAudioFilePath()!=null && !TextUtils.isEmpty(mTrip.getAudioFilePath())))
                    playAudio();
                    else
                        AppUtil.showSnackbar(getView(),getString(R.string.recordAudio));
            }
        });
        mBinding.mike.setOnClickListener(view -> {
         if(!PermissionUtil.hasAudioPermissions(getActivity()))
             return;
            if (!recording) {
                startRecording();
                recording = true;
            } else {
                pauseRecording();
                recording = false;
            }
        });
        mBinding.save.setOnClickListener(view -> {
            String oldPath=mTrip.getAudioFilePath();
            if(mFileName!=null)
            mTrip.setAudioFilePath(mFileName);
            if (mBinding.notesInput.getText() != null)
                mTrip.setDescription(mBinding.notesInput.getText().toString());
            if (mBinding.tagInput.getText() != null) {
               mTrip.addTags(mBinding.tagInput.getText().toString());
            }
            mViewModel.updateTrip(mTrip);
            if(oldPath!=null)
            delFile(oldPath);
            navigateTo();
        });
        mBinding.delButton.setOnClickListener(view -> {
            mViewModel.delTrip(mTrip);
            navigateTo();
        });
        mBinding.cancelButton.setOnClickListener(view -> {
          navigateTo();
        });
        mBinding.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                if(mPlayer!=null){
                    mPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }
    public void navigateTo(){
        NavHostFragment.findNavController(getParentFragment()).popBackStack();
        NavHostFragment.findNavController(this).navigate(R.id.tripFragment);
    }

    private void delFile(String oldPath) {
        File file=new File(oldPath);
        file.delete();
        if(!file.exists())
        {
            Log.d(AppConstants.TAG,"Old file deleted"+oldPath);
        }

    }

    private void hideLoader() {
        mBinding.viewLoader.rootView.setVisibility(View.GONE);
        mBinding.group.setVisibility(View.VISIBLE);
    }

    private void displayEmptyView() {
        mBinding.group.setVisibility(View.GONE);
        mBinding.viewEmpty.emptyText.setText(getString(R.string.empty_trip));
        mBinding.viewEmpty.emptyContainer.setVisibility(View.VISIBLE);
    }

    private void displayLoader() {
        mBinding.viewLoader.rootView.setVisibility(View.VISIBLE);
        mBinding.group.setVisibility(View.GONE);
    }
    void showRecording()
    {
        mBinding.mike.setBackgroundColor(getResources().getColor(R.color.darkGreen));
    }
    void showStopRecording()
    {
        mBinding.mike.setBackgroundColor(Color.DKGRAY);
    }
}