package it.antonino.palco.model.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import it.antonino.palco.viewmodel.SharedViewModel
import org.koin.java.KoinJavaComponent

private val viewModel: SharedViewModel by KoinJavaComponent.inject(SharedViewModel::class.java)
class ScrapeRockolWorker(
    appContext: Context,
    workerParams: WorkerParameters
): CoroutineWorker(appContext, workerParams)
{
    val context = appContext
    override suspend fun doWork(): Result {
        viewModel.scrapeRockShock(context)
        return Result.success()
    }

}