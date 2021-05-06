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

class GoogleRepository(
    val networkInterface: NetworkInterface
) {

    companion object : SingletonHolder<GoogleRepository, NetworkInterface>(::GoogleRepository)

    val TAG = GoogleRepository::class.qualifiedName

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

    fun uploadFirebaseToken(user: User): MutableLiveData<String?> {

        var responseCode = MutableLiveData<String?>()
        val response = networkInterface.uploadFirebaseToken(user).enqueue(
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