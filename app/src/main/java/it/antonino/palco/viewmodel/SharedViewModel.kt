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
import it.antonino.palco.PalcoApplication.Companion.file_1
import it.antonino.palco.PalcoApplication.Companion.file_2
import it.antonino.palco.model.Concerto
import it.antonino.palco.network.NetworkRepository
import it.antonino.palco.util.Constant.FIRST_BATCH
import it.antonino.palco.util.Constant.MODULE_BATCH
import it.antonino.palco.util.Constant.SECOND_BATCH
import it.antonino.palco.util.decryptJsonFromFile
import it.antonino.palco.util.encryptJsonToFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.lang.reflect.Type

class SharedViewModel(private val networkRepository: NetworkRepository): ViewModel() {

    private var _isNewDay: MutableLiveData<Boolean> = MutableLiveData()
    val isNewDay: LiveData<Boolean> = _isNewDay

    private var _batchEnded: MutableLiveData<Boolean> = MutableLiveData()
    val batchEnded: LiveData<Boolean> = _batchEnded

    private var _concerti: MutableLiveData<ArrayList<Concerto?>> = MutableLiveData()
    val concerti: LiveData<ArrayList<Concerto?>> = _concerti

    private var _cities: MutableLiveData<ArrayList<String>> = MutableLiveData()
    val cities: LiveData<ArrayList<String>> = _cities

    private var _artists: MutableLiveData<ArrayList<String>> = MutableLiveData()
    val artists: LiveData<ArrayList<String>> = _artists

    private var _concertsFilterCity: MutableLiveData<ArrayList<Concerto?>> = MutableLiveData()
    val concertsFilterCity: LiveData<ArrayList<Concerto?>> = _concertsFilterCity

    private var _concertsFilterArtist: MutableLiveData<ArrayList<Concerto?>> = MutableLiveData()
    val concertsFilterArtist: LiveData<ArrayList<Concerto?>> = _concertsFilterArtist

    private val gson = Gson().newBuilder().create()

    fun setIsNewDay(value: Boolean) {
        _isNewDay.postValue(value)
    }

    fun setBatchEnded(value: Boolean) {
        _batchEnded.postValue(value)
    }

    fun setConcerts(value: ArrayList<Concerto?>) {
        _concerti.postValue(value)
    }

    fun getAllConcerts(context: Context): ArrayList<Concerto?> {
        val itemType = object : TypeToken<List<Concerto>>() {}.type
        val json_1 = if (file_1.exists()) decryptJsonFromFile(context, file_1) else ""
        val json_2 = if (file_2.exists()) decryptJsonFromFile(context, file_2) else ""
        var file = File("")
        if (json_2.isNotEmpty())
            file = file_2
        else if (json_1.isNotEmpty())
            file = file_1
        val concerts: ArrayList<Concerto?> = gson.fromJson(decryptJsonFromFile(context, file), itemType)
        return concerts
    }

    fun getAllCities(context: Context): ArrayList<String> {
        val itemType = object : TypeToken<List<Concerto>>() {}.type
        var file = File("")
        val json_1 = if (file_1.exists()) decryptJsonFromFile(context, file_1) else ""
        val json_2 = if (file_2.exists()) decryptJsonFromFile(context, file_2) else ""
        if (json_2.isNotEmpty())
            file = file_2
        else if (json_1.isNotEmpty())
            file = file_1
        val concerts: ArrayList<Concerto> = gson.fromJson(decryptJsonFromFile(context, file), itemType)
        val ret: ArrayList<String> = arrayListOf()
        concerts.forEach {
            ret.add(it.city)
        }
        _cities.postValue(ret)
        return ret
    }

    fun getAllArtist(context: Context): ArrayList<String> {
        val itemType = object : TypeToken<List<Concerto>>() {}.type
        var file = File("")
        val json_1 = if (file_1.exists()) decryptJsonFromFile(context, file_1) else ""
        val json_2 = if (file_2.exists()) decryptJsonFromFile(context, file_2) else ""
        if (json_2.isNotEmpty())
            file = file_2
        else if (json_1.isNotEmpty())
            file = file_1
        val concerts: ArrayList<Concerto> = gson.fromJson(decryptJsonFromFile(context, file), itemType)
        val ret: ArrayList<String> = arrayListOf()
        concerts.forEach {
            if (!ret.contains(it.artist))
                ret.add(it.artist)
        }
        _artists.postValue(ret)
        return ret
    }

    fun getAllByCity(context: Context, city: String): ArrayList<Concerto?> {
        val itemType = object : TypeToken<List<Concerto>>() {}.type
        var file = File("")
        val json_1 = if (file_1.exists()) decryptJsonFromFile(context, file_1) else ""
        val json_2 = if (file_2.exists()) decryptJsonFromFile(context, file_2) else ""
        if (json_2.isNotEmpty())
            file = file_2
        else if (json_1.isNotEmpty())
            file = file_1
        val concerts: ArrayList<Concerto> = gson.fromJson(decryptJsonFromFile(context, file), itemType)
        val ret: ArrayList<Concerto?> = arrayListOf()
        concerts.forEach {
            if (it.city == city)
                ret.add(it)
        }
        _concertsFilterCity.postValue(ret)
        return ret
    }

    fun getAllByArtist(context: Context, artist: String): ArrayList<Concerto?> {
        val itemType = object : TypeToken<List<Concerto>>() {}.type
        var file = File("")
        val json_1 = if (file_1.exists()) decryptJsonFromFile(context, file_1) else ""
        val json_2 = if (file_2.exists()) decryptJsonFromFile(context, file_2) else ""
        if (json_2.isNotEmpty())
            file = file_2
        else if (json_1.isNotEmpty())
            file = file_1
        val concerts: ArrayList<Concerto> = gson.fromJson(decryptJsonFromFile(context, file), itemType)
        val ret: ArrayList<Concerto?> = arrayListOf()
        concerts.forEach {
            if (it.artist == artist)
                ret.add(it)
        }
        _concertsFilterArtist.postValue(ret)
        return ret
    }

    fun firstBatch(context: Context) {
        runBlocking(Dispatchers.IO) {
            if (!Python.isStarted())
                Python.start(AndroidPlatform(context))
            val py = Python.getInstance()
            val pyObj = py.getModule(MODULE_BATCH)
            val message = pyObj.callAttr(FIRST_BATCH)
            val collectionType: Type =
                object : TypeToken<ArrayList<Concerto?>?>() {}.type
            val concerts = message.repr().replaceRange(0, 1, "").replaceRange(message.repr().length - 2 , message.repr().length - 1, "")
            val jsonString: ArrayList<Concerto> = gson.fromJson(concerts, collectionType)
            encryptJsonToFile(context, file_1, gson.toJson(jsonString))
        }
    }

    fun secondBatch(context: Context) {
        runBlocking(Dispatchers.IO) {
            if (!Python.isStarted())
                Python.start(AndroidPlatform(context))
            val py = Python.getInstance()
            val pyObj = py.getModule(MODULE_BATCH)
            val message = pyObj.callAttr(SECOND_BATCH)
            val collectionType: Type =
                object : TypeToken<ArrayList<Concerto?>?>() {}.type
            val concertString = message.repr().replaceRange(0, 1, "").replaceRange(message.repr().length - 2 , message.repr().length - 1, "")
            val concerts: ArrayList<Concerto> = gson.fromJson(concertString, collectionType)
            val allConcerts : MutableList<Concerto> = mutableListOf()
            try {
                val itemType = object : TypeToken<List<Concerto>>() {}.type
                val json = decryptJsonFromFile(context, file_1)
                val jsonMap: ArrayList<Concerto> = gson.fromJson(json, itemType)
                concerts.forEach {
                    if (!jsonMap.contains(it))
                        jsonMap.add(it)
                }
                allConcerts.addAll(jsonMap)
            } catch (e: Exception) {
                println(e.message)
                allConcerts.addAll(concerts)
            }
            encryptJsonToFile(context, file_2, gson.toJson(allConcerts))
        }
    }

    fun containsSpecificJsonValues(context: Context, file: File, requiredValues: Concerto): Boolean {
        return try {
            val itemType = object : TypeToken<List<Concerto>>() {}.type
            val json = decryptJsonFromFile(context, file)
            val jsonMap: ArrayList<Concerto> = gson.fromJson(json, itemType) // Parse JSON as a map
            if (jsonMap.contains(requiredValues))
                return false
            return true
        } catch (_: JsonSyntaxException) {
            false
        }
    }

    fun getArtistInfos(labelId: String?): LiveData<JsonObject?> {
        var responseObject = MutableLiveData<JsonObject?>()
        viewModelScope.launch {
            responseObject = networkRepository.getArtistInfos(labelId)
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