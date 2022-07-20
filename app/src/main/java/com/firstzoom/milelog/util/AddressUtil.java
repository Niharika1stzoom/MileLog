package com.firstzoom.milelog.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;
import java.util.Locale;

public class AddressUtil {
    public static String getCompleteAddressString(Context context, double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {

            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");
                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                if(TextUtils.isEmpty(strAdd))
                    strAdd="Address not found.";
            } else {
                strAdd="Address not found.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(AppConstants.TAG, "Cannot get Address!");
        }
        return strAdd;
    }


}
