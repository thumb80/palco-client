package it.antonino.palco.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Concerto(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "artist") val artist: String?,
    @ColumnInfo(name = "city") val city: String?,
    @ColumnInfo(name = "place") val place: String?,
    @ColumnInfo(name = "time") val time: String?
)