package com.homurax.activitytest

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

/*class Person : Serializable {
    var name = ""
    var age = 0
}*/

/*
class Person : Parcelable {
    var name = ""
    var age = 0

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        // 写出
        parcel.writeString(name)
        parcel.writeInt(age)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Person> {
        override fun createFromParcel(parcel: Parcel): Person {
            val person = Person()
            // 读取
            person.name = parcel.readString() ?: ""
            person.age = parcel.readInt()
            return person
        }

        override fun newArray(size: Int): Array<Person?> {
            return arrayOfNulls(size)
        }
    }
}*/

@Parcelize
class Person(var name: String, var age: Int) : Parcelable