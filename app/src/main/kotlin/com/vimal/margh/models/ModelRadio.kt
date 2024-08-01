package com.vimal.margh.models

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_radio")
data class ModelRadio(
    @PrimaryKey
    var radio_id: Int,
    var radio_name: String,
    var radio_image: String,
    var radio_url: String,
    var radio_type: String,
    var view_count: String,
    var category_name: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(radio_id)
        parcel.writeString(radio_name)
        parcel.writeString(radio_image)
        parcel.writeString(radio_url)
        parcel.writeString(radio_type)
        parcel.writeString(view_count)
        parcel.writeString(category_name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ModelRadio> {
        override fun createFromParcel(parcel: Parcel): ModelRadio {
            return ModelRadio(parcel)
        }

        override fun newArray(size: Int): Array<ModelRadio?> {
            return arrayOfNulls(size)
        }
    }
}
