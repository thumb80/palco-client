package it.antonino.palco.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import it.antonino.palco.database.model.Concerto

@Dao
interface ConcertoDao {

    @Query("SELECT * FROM `Concerto`")
    fun getAll(): List<Concerto>

    @Query("SELECT DISTINCT city FROM `Concerto`")
    fun getAllCities(): List<String>

    @Query("SELECT DISTINCT artist FROM `Concerto`")
    fun getAllArtist(): List<String>

    @Query("SELECT * FROM `Concerto` WHERE city = :city")
    fun getAllByCity(city: String): List<Concerto>

    @Query("SELECT * FROM `Concerto` WHERE artist = :artist")
    fun getAllByArtist(artist: String): List<Concerto>

    @Insert
    fun insertAll(concerti: Collection<Concerto>)

    @Query("DELETE FROM `Concerto`")
    fun deleteAll()

}