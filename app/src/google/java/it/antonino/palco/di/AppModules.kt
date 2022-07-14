package it.antonino.palco.di

import it.antonino.palco.BuildConfig
import it.antonino.palco.MainViewModel
import it.antonino.palco.`interface`.NetworkInterface
import it.antonino.palco.common.CustomTrust
import it.antonino.palco.network.DiscogsAPI
import it.antonino.palco.network.NetworkAPI
import it.antonino.palco.network.NetworkRepository
import it.antonino.palco.repository.GoogleRepository
import it.antonino.palco.ui.login.LoginViewModel
import it.antonino.palco.ui.viewmodel.SharedViewModel
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.*
import javax.security.cert.CertificateException

val appModule = module {

    // Network Dependencies
    single {
        NetworkRepository.getInstance(get(),get())
    }
    single {
        GoogleRepository.getInstance(get())
    }
    single {
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                //UnsafeOkHttpClient.unsafeOkHttpClient
                //OkHttpClient()
                CustomTrust(androidContext())
                .client
                .newBuilder()
                .callTimeout(2, TimeUnit.MINUTES)
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build())
            .baseUrl(BuildConfig.BASE_URL)
            .build()
    }
    // Interfaces
    single {
        get<Retrofit>()
            .create(NetworkAPI::class.java)
    }
    single {
        get<Retrofit>()
            .create(DiscogsAPI::class.java)
    }
    single {
        get<Retrofit>()
            .create(NetworkInterface::class.java)
    }

    // ViewModel Dependencies
    viewModel { LoginViewModel(get()) }
    viewModel { MainViewModel(get()) }
    viewModel { SharedViewModel(get()) }

}

// Test Client NO SSL
object UnsafeOkHttpClient {
    // Create a trust manager that does not validate certificate chains
    val unsafeOkHttpClient:

            OkHttpClient get() = try {
        // Create a trust manager that does not validate certificate chains
        val trustAllCerts: Array<TrustManager> = arrayOf<TrustManager>(
            object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(
                    chain: Array<X509Certificate?>?,
                    authType: String?
                ) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(
                    chain: Array<X509Certificate?>?,
                    authType: String?
                ) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }
            }
        )

        // Install the all-trusting trust manager
        val sslContext: SSLContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())

        // Create an ssl socket factory with our all-trusting manager
        val sslSocketFactory: SSLSocketFactory = sslContext.getSocketFactory()
        val builder = OkHttpClient.Builder()
        builder.sslSocketFactory(
            sslSocketFactory,
            trustAllCerts[0] as X509TrustManager
        )
        builder.hostnameVerifier(object : HostnameVerifier {
            override fun verify(hostname: String?, session: SSLSession?): Boolean {
                return true
            }
        })
        builder.build()
    } catch (e: Exception) {
        throw RuntimeException(e)
    }
}