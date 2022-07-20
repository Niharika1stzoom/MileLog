package com.firstzoom.milelog.ui;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.firstzoom.milelog.R;
import com.firstzoom.milelog.databinding.ListItemTripBinding;
import com.firstzoom.milelog.model.LocationPath;
import com.firstzoom.milelog.model.Trip;
import com.firstzoom.milelog.repository.Repository;
import com.firstzoom.milelog.room.AppDatabase;
import com.firstzoom.milelog.util.AddressUtil;
import com.firstzoom.milelog.util.AppConstants;
import com.firstzoom.milelog.util.AppExecutors;
import com.firstzoom.milelog.util.AppUtil;
import com.firstzoom.milelog.util.DistanceUtil;
import com.firstzoom.milelog.util.SharedPrefUtil;
import com.firstzoom.milelog.util.TrackerUtil;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {
    ListItemTripBinding mBinding;
    Context mContext;
    private List<Trip> mTripList;

    public TripAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mBinding = ListItemTripBinding.inflate(LayoutInflater.from(mContext), parent, false);
        return new TripViewHolder(mBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        Trip TripItem = mTripList.get(position);
        holder.bind(TripItem);
    }

    @Override
    public int getItemCount() {
        if (mTripList == null) {
            return 0;
        }
        return mTripList.size();
    }

    public void setList(List<Trip> TripList) {
        mTripList = TripList;
        notifyDataSetChanged();
    }

    public class TripViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {
        private ListItemTripBinding mBinding;

        public TripViewHolder(@NonNull ListItemTripBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
            binding.getRoot().setOnClickListener(this);
        }

        public void bind(Trip TripItem) {
            if (TripItem.getLongitudeStart() != null)
                mBinding.startLabel.setText(AddressUtil.getCompleteAddressString
                        (mContext,TripItem.getLatitudeStart(),TripItem.getLongitudeStart()));
            else
                mBinding.startLabel.setVisibility(GONE);
            if (TripItem.getLatitudeStop() != null)
                mBinding.endLabel.setText(AddressUtil.getCompleteAddressString
                        (mContext,TripItem.getLatitudeStop(),TripItem.getLongitudeStop()));
            else
                mBinding.endLabel.setVisibility(GONE);
            if(TripItem.getLatitudeStart()!=null && TripItem.getLatitudeStop()!=null){
                if(TripItem.getDistance()!=null && TripItem.getDistance()>0.0)
                {
                    mBinding.distanceLabel.setText("Distance(km): "+String.format("%.2f",TripItem.getDistance()));
                }
                else {
                    Double dist= DistanceUtil.calDistance(TripItem.getLatitudeStart(),TripItem.getLongitudeStart()
                            ,TripItem.getLatitudeStop(),TripItem.getLongitudeStop());
                    mBinding.distanceLabel.setText("Distance(km): " + String.format("%.2f", dist));
                }
            }
            if(TripItem.getStartTime()!=null)
                mBinding.startTime.setText(AppUtil.getDisplayDate(TripItem.getStartTime()));
            else
                mBinding.startTime.setText("");
            if(TripItem.getEndTime()!=null)
                mBinding.endTime.setText(AppUtil.getDisplayDate(TripItem.getEndTime()));
            else
                mBinding.endTime.setText("");
           if(TripItem.getStartTime()!=null && TripItem.getEndTime()!=null) {
               long []time=AppUtil.getDifference(TripItem.getStartTime(),TripItem.getEndTime());
               mBinding.durationLabel.setText("Duration(mins): "+time[0]+ " :"+time[1]);
           }

        }

        @Override
        public void onClick(View view) {
            Trip Trip = mTripList.get(getAdapterPosition());
            Bundle bundle =new Bundle();
            bundle.putInt(AppConstants.KEY_TRIP_ID,Trip.getId());
            Navigation.findNavController(view).navigate(R.id.editTripFragment,bundle);

        }
    }
}
