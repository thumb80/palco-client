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
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import it.antonino.palco.PalcoApplication
import it.antonino.palco.PalcoApplication.Companion.file
import it.antonino.palco.model.Concerto
import it.antonino.palco.network.NetworkRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.lang.reflect.Type
import java.nio.charset.Charset

class SharedViewModel(private val networkRepository: NetworkRepository): ViewModel() {

    private var _isNewDay: MutableLiveData<Boolean> = MutableLiveData()
    val isNewDay: LiveData<Boolean> = _isNewDay

    private var _batchEnded: MutableLiveData<Boolean> = MutableLiveData()
    val batchEnded: LiveData<Boolean> = _batchEnded

    private var _concerti: MutableLiveData<ArrayList<Concerto>> = MutableLiveData()
    val concerti: LiveData<ArrayList<Concerto>> = _concerti

    private var _cities: MutableLiveData<ArrayList<String>> = MutableLiveData()
    val cities: LiveData<ArrayList<String>> = _cities

    private var _artists: MutableLiveData<ArrayList<String>> = MutableLiveData()
    val artists: LiveData<ArrayList<String>> = _artists

    private var _concertiFilterCity: MutableLiveData<ArrayList<Concerto>> = MutableLiveData()
    val concertiFilterCity: LiveData<ArrayList<Concerto>> = _concertiFilterCity

    private var _concertiFilterArtist: MutableLiveData<ArrayList<Concerto>> = MutableLiveData()
    val concertiFilterArtist: LiveData<ArrayList<Concerto>> = _concertiFilterArtist

    fun setIsNewDay(value: Boolean) {
        _isNewDay.postValue(value)
    }

    fun setBatchEnded(value: Boolean) {
        _batchEnded.postValue(value)
    }

    fun setConcerti(value: ArrayList<Concerto>) {
        _concerti.postValue(value)
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

    fun scrapeCanzoni(context: Context) {
        runBlocking(Dispatchers.IO) {
            if (!Python.isStarted())
                Python.start(AndroidPlatform(context))
            val py = Python.getInstance()
            val pyObj = py.getModule("batch")
            val message = pyObj.callAttr("scrapeCanzoni")
            println(message)
            val gson: Gson = Gson().newBuilder().create()
            val collectionType: Type =
                object : TypeToken<ArrayList<Concerto?>?>() {}.type
            val concerti: Collection<Concerto> = gson.fromJson(String(message.repr().replaceRange(0, 1, "").replaceRange(message.repr().length - 2 , message.repr().length - 1, "").toByteArray(
                Charset.forName("UTF-8"))), collectionType)
            println(concerti)
            PalcoApplication.concerti = concerti as ArrayList<Concerto>
            if (file?.exists() == true)
                file?.writeText("")
            file?.writeText(Gson().toJson(concerti))
        }
    }

    fun scrapeGoth(context: Context) {
        runBlocking(Dispatchers.IO) {
            if (!Python.isStarted())
                Python.start(AndroidPlatform(context))
            val py = Python.getInstance()
            val pyObj = py.getModule("batch")
            val message = pyObj.callAttr("scrapeGoth")
            println(message)
            val gson: Gson = Gson().newBuilder().create()
            val collectionType: Type =
                object : TypeToken<ArrayList<Concerto?>?>() {}.type
            val concerti: ArrayList<Concerto> = gson.fromJson(String(message.repr().replaceRange(0, 1, "").replaceRange(message.repr().length - 2 , message.repr().length - 1, "").toByteArray(
                Charset.forName("UTF-8"))), collectionType)
            println(concerti)
            val temp : MutableList<Concerto> = mutableListOf()
            try {
                concerti.forEach {
                    if (containsSpecificJsonValues(file, it))
                        temp.add(it)
                }
                PalcoApplication.concerti.addAll(temp)
            } catch (e: Exception) {
                PalcoApplication.concerti.addAll(concerti)
            }
            file?.writeText(Gson().toJson(PalcoApplication.concerti))
        }
    }

    fun scrapeRockShock(context: Context) {
        runBlocking(Dispatchers.IO) {
            if (!Python.isStarted())
                Python.start(AndroidPlatform(context))
            val py = Python.getInstance()
            val pyObj = py.getModule("batch")
            val message = pyObj.callAttr("scrapeRockShock")
            println(message)
            val gson: Gson = Gson().newBuilder().create()
            val collectionType: Type =
                object : TypeToken<ArrayList<Concerto?>?>() {}.type
            val concerti: ArrayList<Concerto> = gson.fromJson(String(message.repr().replaceRange(0, 1, "").replaceRange(message.repr().length - 2 , message.repr().length - 1, "").toByteArray(
                Charset.forName("UTF-8"))), collectionType)
            println(concerti)
            val temp : MutableList<Concerto> = mutableListOf()
            try {
                concerti.forEach {
                    if (containsSpecificJsonValues(file, it))
                        temp.add(it)
                }
                PalcoApplication.concerti.addAll(temp)
            } catch (e: Exception) {
                PalcoApplication.concerti.addAll(concerti)
            }
            file?.writeText(Gson().toJson(PalcoApplication.concerti))
        }
    }

    fun containsSpecificJsonValues(file: File?, requiredValues: Concerto): Boolean {
        return try {
            val content = file?.readText() // Read file content as a string
            val gson = Gson()
            val itemType = object : TypeToken<List<Concerto>>() {}.type
            val jsonMap: ArrayList<Concerto> = gson.fromJson(content, itemType) // Parse JSON as a map

            jsonMap.forEach {
                if (it.artist.contains(requiredValues.artist, true) || (it.artist.split(" ")[0].contains(requiredValues.artist.split(" ")[0], true)))
                    return false
            }

            return true
        } catch (e: JsonSyntaxException) {
            false // Return false if parsing fails or keys don't match
        }
    }

    fun getArtistInfos(artist: String?): LiveData<JsonObject?> {
        var responseObject = MutableLiveData<JsonObject?>()
        viewModelScope.launch {
            responseObject = networkRepository.getArtistInfos(artist)
        }
        return responseObject
    }

    fun getArtistThumb(artist: String?): LiveData<JsonObject?> {
        var responseObject = MutableLiveData<JsonObject?>()
        viewModelScope.launch {
            responseObject = networkRepository.getArtistThumb(artist)
        }
        return responseObject
    }

}