package it.antonino.palco.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import it.antonino.palco.database.Concerti
import it.antonino.palco.database.model.Concerto
import it.antonino.palco.ext.toConcertoFilter
import it.antonino.palco.network.NetworkRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.reflect.Type
import java.nio.charset.Charset
import java.util.ArrayList
import kotlin.random.Random

class SharedViewModel(
    private val concerti: Concerti,
    private val networkRepository: NetworkRepository): ViewModel() {

    private var _scrapeResult: MutableLiveData<PyObject> = MutableLiveData()
    val scrapeResul: LiveData<PyObject> = _scrapeResult

    private var _isInit: MutableLiveData<Boolean> = MutableLiveData(false)
    val isInit: LiveData<Boolean> = _isInit

    private var _cities: MutableLiveData<ArrayList<String>> = MutableLiveData()
    val cities: LiveData<ArrayList<String>> = _cities

    private var _artists: MutableLiveData<ArrayList<String>> = MutableLiveData()
    val artists: LiveData<ArrayList<String>> = _artists

    private var _concertiFilter: MutableLiveData<ArrayList<it.antonino.palco.model.Concerto>> = MutableLiveData()
    val concertiFilter: LiveData<ArrayList<it.antonino.palco.model.Concerto>> = _concertiFilter

    fun setInit(value: Boolean) {
        _isInit.postValue(value)
    }

    suspend fun insertAll(concerto: Collection<Concerto>) {
        withContext(Dispatchers.IO) {
            concerti.concertiDao().deleteAll()
            concerti.concertiDao().insertAll(concerto)
        }
    }

    suspend fun getAllConcerti(): ArrayList<Concerto> {
        var ret: ArrayList<Concerto>
        withContext(Dispatchers.IO) {
            ret = concerti.concertiDao().getAll() as ArrayList<Concerto>
        }
        return ret
    }

    suspend fun getAllCities(): ArrayList<String> {
        var ret: ArrayList<String> = arrayListOf()
        withContext(Dispatchers.IO) {
            ret = concerti.concertiDao().getAllCities() as ArrayList<String>
        }
        _cities.postValue(ret)
        return ret
    }

    suspend fun getAllArtist(): ArrayList<String> {
        var ret: ArrayList<String> = arrayListOf()
        withContext(Dispatchers.IO) {
            ret = concerti.concertiDao().getAllArtist() as ArrayList<String>
        }
        _artists.postValue(ret)
        return ret
    }

    suspend fun getAllByCity(city: String): ArrayList<Concerto> {
        var ret: ArrayList<Concerto> = arrayListOf()
        withContext(Dispatchers.IO) {
            ret = concerti.concertiDao().getAllByCity(city) as ArrayList<Concerto>
        }
        _concertiFilter.postValue(ret.toConcertoFilter())
        return ret
    }

    suspend fun getAllByArtist(artist: String): ArrayList<Concerto> {
        var ret: ArrayList<Concerto> = arrayListOf()
        withContext(Dispatchers.IO) {
            ret = concerti.concertiDao().getAllByArtist(artist) as ArrayList<Concerto>
        }
        _concertiFilter.postValue(ret.toConcertoFilter())
        return ret
    }

    suspend fun deleteAll() {
        withContext(Dispatchers.IO) {
            concerti.concertiDao().deleteAll()
        }
    }

    fun scrape() {
        viewModelScope.launch {
            val py = Python.getInstance()
            val pyObj = py.getModule("batch")
            val message = pyObj.callAttr("scrape")
            println(message)
            _scrapeResult.postValue(message)
            val gson: Gson = Gson().newBuilder().create()
            val collectionType: Type =
                object : TypeToken<ArrayList<it.antonino.palco.model.Concerto?>?>() {}.type
            val concerti: Collection<it.antonino.palco.model.Concerto> = gson.fromJson(String(message.repr().replaceRange(0, 1, "").replaceRange(message.repr().length - 2 , message.repr().length - 1, "").toByteArray(
                Charset.forName("UTF-8"))), collectionType)
            println(concerti)
            val concertiDb: ArrayList<Concerto>  = arrayListOf()
            concerti.forEachIndexed { index, concerto ->
                concertiDb.add(
                    Concerto(
                        Random.nextInt(),
                        concerto.artist,
                        concerto.city,
                        concerto.place,
                        concerto.time
                    )
                )
            }
            insertAll(concertiDb)
        }
    }

    fun getArtistInfos(artist: String?): LiveData<JsonObject?> {
        var responseObject = MutableLiveData<JsonObject?>()
        viewModelScope.launch {
            responseObject = networkRepository.getArtistInfos(artist)
        }
        return responseObject
    }

}