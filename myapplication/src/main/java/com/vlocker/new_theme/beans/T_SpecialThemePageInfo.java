package com.vlocker.new_theme.beans;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.vlocker.theme.model.b;
import com.vlocker.theme.model.d;

public class T_SpecialThemePageInfo implements Parcelable, b {
    public static final Creator CREATOR = new j();
    public d a = null;
    public String b = null;
    public String c = null;
    public String d;
    public int e;
    public int f;
    public int g;

    public T_SpecialThemePageInfo(Parcel parcel) {
        this.b = parcel.readString();
        this.c = parcel.readString();
        this.d = parcel.readString();
        this.e = parcel.readInt();
        this.f = parcel.readInt();
        this.g = parcel.readInt();
        this.a = new d();
        parcel.readTypedList(this.a, T_SpecialThemeInfo.CREATOR);
    }

    public d a() {
        return this.a;
    }

    public void a(int i) {
        this.e = i;
    }

    public void a(d dVar) {
        this.a = dVar;
    }

    public void a(String str) {
        this.b = str;
    }

    public void b(int i) {
        this.f = i;
    }

    public void b(String str) {
        this.c = str;
    }

    public void c(int i) {
        this.g = i;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.b);
        parcel.writeString(this.c);
        parcel.writeString(this.d);
        parcel.writeInt(this.e);
        parcel.writeInt(this.f);
        parcel.writeInt(this.g);
        parcel.writeTypedList(this.a);
    }
}
