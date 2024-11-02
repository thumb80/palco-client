package it.antonino.palco.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import it.antonino.palco.PalcoApplication.Companion.file
import it.antonino.palco.model.Concerto
import it.antonino.palco.network.NetworkRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.reflect.Type
import java.nio.charset.Charset
import kotlin.collections.ArrayList

class SharedViewModel(private val networkRepository: NetworkRepository): ViewModel() {

    private var _batchEnded: MutableLiveData<Boolean> = MutableLiveData()
    val batchEnded: LiveData<Boolean> = _batchEnded

    private var _cities: MutableLiveData<ArrayList<String>> = MutableLiveData()
    val cities: LiveData<ArrayList<String>> = _cities

    private var _artists: MutableLiveData<ArrayList<String>> = MutableLiveData()
    val artists: LiveData<ArrayList<String>> = _artists

    private var _concertiFilterCity: MutableLiveData<ArrayList<it.antonino.palco.model.Concerto>> = MutableLiveData()
    val concertiFilterCity: LiveData<ArrayList<it.antonino.palco.model.Concerto>> = _concertiFilterCity

    private var _concertiFilterArtist: MutableLiveData<ArrayList<it.antonino.palco.model.Concerto>> = MutableLiveData()
    val concertiFilterArtist: LiveData<ArrayList<it.antonino.palco.model.Concerto>> = _concertiFilterArtist

    fun setBatchEnded(value: Boolean) {
        _batchEnded.postValue(value)
    }

    fun getAllConcerti(): ArrayList<Concerto> {
        val itemType = object : TypeToken<List<Concerto>>() {}.type
        val concerti: ArrayList<Concerto> = Gson().fromJson(file?.readText(), itemType)
        return concerti
    }

    fun getAllCities(): ArrayList<String> {
        val itemType = object : TypeToken<List<Concerto>>() {}.type
        val concerti: ArrayList<Concerto> = Gson().fromJson(file?.readText(), itemType)
        val ret: ArrayList<String> = arrayListOf()
        concerti.forEach {
            ret.add(it.city)
        }
        _cities.postValue(ret)
        return ret
    }

    fun getAllArtist(): ArrayList<String> {
        val itemType = object : TypeToken<List<Concerto>>() {}.type
        val concerti: ArrayList<Concerto> = Gson().fromJson(file?.readText(), itemType)
        val ret: ArrayList<String> = arrayListOf()
        concerti.forEach {
            if (!ret.contains(it.artist))
                ret.add(it.artist)
        }
        _artists.postValue(ret)
        return ret
    }

    fun getAllByCity(city: String): ArrayList<Concerto> {
        val itemType = object : TypeToken<List<Concerto>>() {}.type
        val concerti: ArrayList<Concerto> = Gson().fromJson(file?.readText(), itemType)
        val ret: ArrayList<Concerto> = arrayListOf()
        concerti.forEach {
            if (it.city == city)
                ret.add(it)
        }
        _concertiFilterCity.postValue(ret)
        return ret
    }

    fun getAllByArtist(artist: String): ArrayList<Concerto> {
        val itemType = object : TypeToken<List<Concerto>>() {}.type
        val concerti: ArrayList<Concerto> = Gson().fromJson(file?.readText(), itemType)
        var ret: ArrayList<Concerto> = arrayListOf()
        concerti.forEach {
            if (it.artist == artist)
                ret.add(it)
        }
        _concertiFilterArtist.postValue(ret)
        return ret
    }

    fun scrape(context: Context) {
        runBlocking(Dispatchers.IO) {
            if (!Python.isStarted())
                Python.start(AndroidPlatform(context))
            val py = Python.getInstance()
            val pyObj = py.getModule("batch")
            val message = pyObj.callAttr("scrape")
            println(message)
            val gson: Gson = Gson().newBuilder().create()
            val collectionType: Type =
                object : TypeToken<ArrayList<it.antonino.palco.model.Concerto?>?>() {}.type
            val concerti: Collection<it.antonino.palco.model.Concerto> = gson.fromJson(String(message.repr().replaceRange(0, 1, "").replaceRange(message.repr().length - 2 , message.repr().length - 1, "").toByteArray(
                Charset.forName("UTF-8"))), collectionType)
            println(concerti)
            if (file?.exists() == true)
                file?.writeText("")
            file?.writeText(Gson().toJson(concerti))
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