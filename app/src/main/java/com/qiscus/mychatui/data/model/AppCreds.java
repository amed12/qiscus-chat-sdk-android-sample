package com.qiscus.mychatui.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class AppCreds implements Parcelable, Comparable<AppCreds>{
    String appId,customServerUrl;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getCustomServerUrl() {
        return customServerUrl;
    }

    public void setCustomServerUrl(String customServerUrl) {
        this.customServerUrl = customServerUrl;
    }

    public Boolean getCustomServer() {
        return isCustomServer;
    }

    public void setCustomServer(Boolean customServer) {
        isCustomServer = customServer;
    }

    Boolean isCustomServer;

    @Override
    public String toString() {
        return "AppCres{" +
                "appId='" + appId + '\'' +
                ", customServerUrl='" + customServerUrl + '\'' +
                ", isCustomServer='" + isCustomServer + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        AppCreds user = (AppCreds) o;

        return appId != null ? appId.equals(user.appId) : user.appId == null;
    }

    @Override
    public int hashCode() {
        return appId != null ? appId.hashCode() : 0;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(appId);
        dest.writeString(customServerUrl);
        dest.writeBoolean(isCustomServer);
    }

    @Override
    public int compareTo(@NonNull AppCreds o) {
        return appId.compareTo(o.appId);
    }
}
