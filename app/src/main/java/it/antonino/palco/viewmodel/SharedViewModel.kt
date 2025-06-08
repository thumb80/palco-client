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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.threeten.bp.LocalDateTime
import java.io.File
import java.lang.reflect.Type
import java.nio.charset.Charset

class SharedViewModel(private val networkRepository: NetworkRepository): ViewModel() {

    private val _isTargetHour = MutableStateFlow(false)
    val isTargetHour: StateFlow<Boolean> = _isTargetHour

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

    private val targetHour = 4
    private val tagetMinute = 20

    init {
        scheduleNextCheck()
    }

    fun scheduleNextCheck() {
        viewModelScope.launch {
            checkHour()
            delay(60_000L)
            scheduleNextCheck()
        }
    }

    fun checkHour() {
        val currentHour = LocalDateTime.now().hour
        val currntMinute = LocalDateTime.now().minute
        _isTargetHour.value = currentHour == targetHour && currntMinute == tagetMinute
    }

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

    fun scrape01(context: Context) {
        runBlocking(Dispatchers.IO) {
            if (!Python.isStarted())
                Python.start(AndroidPlatform(context))
            val py = Python.getInstance()
            val pyObj = py.getModule("batch")
            val message = pyObj.callAttr("scrape01")
            println(message)
            val gson: Gson = Gson().newBuilder().create()
            val collectionType: Type =
                object : TypeToken<ArrayList<Concerto?>?>() {}.type
            val concerti: Collection<Concerto> = gson.fromJson(String(message.repr().replaceRange(0, 1, "").replaceRange(message.repr().length - 2 , message.repr().length - 1, "").toByteArray(
                Charset.forName("UTF-8"))), collectionType)
            PalcoApplication.concerti = concerti as ArrayList<Concerto>
            if (file?.exists() == true)
                file?.writeText("")
            file?.writeText(Gson().toJson(concerti))
        }
    }

    fun scrape02(context: Context) {
        runBlocking(Dispatchers.IO) {
            if (!Python.isStarted())
                Python.start(AndroidPlatform(context))
            val py = Python.getInstance()
            val pyObj = py.getModule("batch")
            val message = pyObj.callAttr("scrape02")
            val gson: Gson = Gson().newBuilder().create()
            val collectionType: Type =
                object : TypeToken<ArrayList<Concerto?>?>() {}.type
            val concerti: ArrayList<Concerto> = gson.fromJson(String(message.repr().replaceRange(0, 1, "").replaceRange(message.repr().length - 2 , message.repr().length - 1, "").toByteArray(
                Charset.forName("UTF-8"))), collectionType)
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
            val content = file?.readText()
            val gson = Gson()
            val itemType = object : TypeToken<List<Concerto>>() {}.type
            val jsonMap: ArrayList<Concerto> = gson.fromJson(content, itemType) // Parse JSON as a map

            jsonMap.forEach {
                if (it.artist.contains(requiredValues.artist, true) || (it.artist.split(" ")[0].contains(requiredValues.artist.split(" ")[0], true)))
                    return false
            }

            return true
        } catch (e: JsonSyntaxException) {
            false
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