package it.antonino.palco.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import it.antonino.palco.`interface`.NetworkInterface
import it.antonino.palco.common.SingletonHolder
import it.antonino.palco.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HuaweiRepository(
    private val networkInterface: NetworkInterface
) {

    companion object : SingletonHolder<HuaweiRepository,NetworkInterface>(::HuaweiRepository)
    private val TAG = HuaweiRepository::class.java.simpleName

    fun doRegister(user: User) : MutableLiveData<Pair<Int?,String?>> {
        var responseCode = MutableLiveData<Pair<Int?,String?>>()
        val response = networkInterface.registration(user).enqueue(
            object : Callback<JsonObject> {
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.d(TAG, "FAILURE")
                    Log.d(TAG, "${t.toString()}")
                    responseCode.postValue(Pair(null,null))
                }

                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    Log.d(TAG, "SUCCESS")
                    Log.d(TAG, "${response}")
                    responseCode.postValue(Pair(response.code(),response.body().toString()))
                }

            }
        )
        Log.d(TAG, "$response")
        return responseCode
    }

    fun uploadHuaweiToken(user: User): MutableLiveData<String?> {

        var responseCode = MutableLiveData<String?>()
        val response = networkInterface.uploadHuaweiToken(user).enqueue(
            object : Callback<JsonObject> {
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.d(TAG, "FAILURE")
                    Log.d(TAG, "${t.toString()}")
                    responseCode.postValue(null)
                }

                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    Log.d(TAG, "SUCCESS")
                    Log.d(TAG, "${response}")
                    responseCode.postValue(response.body().toString())
                }
            }
        )
        Log.d(TAG, "$response")
        return responseCode
    }

}