package it.antonino.palco.database

import androidx.room.Database
import androidx.room.RoomDatabase
import it.antonino.palco.database.dao.ConcertoDao
import it.antonino.palco.database.model.Concerto

@Database(entities = [Concerto::class], version = 1)
abstract class Concerti : RoomDatabase() {
    abstract fun concertiDao(): ConcertoDao
}