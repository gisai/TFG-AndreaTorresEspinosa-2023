package com.ate.alergiapp;

import android.os.Parcel;
import android.os.Parcelable;

public class CustomObject implements Parcelable {

    public String mName;
    public String mAddress;
    public String mPagWeb;
    public String mRating;

    public CustomObject(String name, String address, String pagweb, String rating) {
        mName = name;
        mAddress = address;
        mPagWeb = pagweb;
        mRating = rating;
    }

    private CustomObject(Parcel parcel) {
        readFromParcel(parcel);
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public String getPagWeb() {
        return mPagWeb;
    }

    public void setPagweb(String pagweb) {
        mPagWeb = pagweb;
    }

    public String getRating() {
        return mRating;
    }

    public void setRating(String rating) {
        mRating = rating;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mAddress);
        dest.writeString(mPagWeb);
        dest.writeString(mRating);
    }

    private void readFromParcel(Parcel parcel) {
        mName = parcel.readString();
        mAddress = parcel.readString();
        mPagWeb = parcel.readString();
        mRating = parcel.readString();
    }

    public static final Creator<CustomObject> CREATOR = new Creator<CustomObject>() {
        @Override
        public CustomObject createFromParcel(Parcel source) {
            return new CustomObject(source);
        }

        @Override
        public CustomObject[] newArray(int size) {
            return new CustomObject[0];
        }
    };
}
