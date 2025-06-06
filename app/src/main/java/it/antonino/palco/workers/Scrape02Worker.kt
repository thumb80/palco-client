package it.antonino.palco.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import it.antonino.palco.viewmodel.SharedViewModel
import org.koin.core.component.KoinComponent
import org.koin.java.KoinJavaComponent

private val viewModel: SharedViewModel by KoinJavaComponent.inject(SharedViewModel::class.java)

class Scrape02Worker(
    appContext: Context,
    workerParams: WorkerParameters
): CoroutineWorker(appContext, workerParams), KoinComponent
{
    val context = appContext

    override suspend fun doWork(): Result {
        viewModel.scrape02(context)
        return Result.success()
    }

}