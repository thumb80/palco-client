package it.antonino.palco.ui

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.WorkerParameters
import it.antonino.palco.PalcoActivity
import it.antonino.palco.PalcoApplication
import it.antonino.palco.PalcoApplication.Companion.isBatchError
import it.antonino.palco.PalcoApplication.Companion.networkMonitor
import it.antonino.palco.R
import it.antonino.palco.databinding.FragmentAdviceBinding
import it.antonino.palco.viewmodel.SharedViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.java.KoinJavaComponent
import org.koin.java.KoinJavaComponent.inject
import java.util.UUID
import kotlin.system.exitProcess

private val viewModel: SharedViewModel by KoinJavaComponent.inject(SharedViewModel::class.java)

class AdviceFragment: Fragment() {

    private lateinit var binding: FragmentAdviceBinding
    private lateinit var workRequestId: UUID

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdviceBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (PalcoApplication.sharedPreferences?.getBoolean("ok_consent", false) == true) {
            checkIfBatchCanRun()
        } else {
            val dialog = AlertDialog.Builder(ContextThemeWrapper(requireContext(), R.style.AlertDialogCustom))
            dialog.setView(R.layout.dialog_advice)
            dialog.setPositiveButton(R.string.ok_consent, object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    PalcoApplication.sharedPreferences?.edit()?.putBoolean("ok_consent", true)?.apply()
                    p0?.cancel()
                    checkIfBatchCanRun()
                }

            })
            dialog.setNegativeButton(R.string.no_consent, object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    exitProcess(0)
                }
            })
            dialog.setCancelable(false)
            dialog.create()
            dialog.show()
        }
    }

    class ScrapeWorker(
        appContext: Context,
        workerParams: WorkerParameters
    ): CoroutineWorker(appContext, workerParams)
    {
        override suspend fun doWork(): Result {
            viewModel.scrape()
            return Result.success()
        }

    }

    private fun setScrapeBatch() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val scrapeWork = OneTimeWorkRequestBuilder<ScrapeWorker>()
            .setConstraints(constraints)
            .build()
        workRequestId = scrapeWork.id
        WorkManager.getInstance(requireContext()).enqueueUniqueWork(
            "it-antonino-scrape",
                ExistingWorkPolicy.REPLACE,
                scrapeWork
        )
        checkWorker()
    }

    private fun checkWorker() {

        WorkManager.getInstance(requireContext()).getWorkInfoByIdLiveData(workRequestId)
            .observe(requireActivity(), Observer { workInfo ->
                when (workInfo.state) {
                    WorkInfo.State.ENQUEUED -> Log.d(tag, "checkWorker enqueued in ${workInfo.runAttemptCount}")
                    WorkInfo.State.RUNNING -> {
                        Log.d(tag, "checkWorker running in ${workInfo.runAttemptCount}")
                    }
                    WorkInfo.State.SUCCEEDED -> {
                        isBatchError = false
                        binding.batchProgress.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), getString(R.string.db_initialized), Toast.LENGTH_LONG).show()
                        (activity as PalcoActivity).supportFragmentManager.beginTransaction()
                            .replace(R.id.container, ConcertiFragment())
                            .commit()
                        Log.d(tag, "checkWorker success in ${workInfo.runAttemptCount}")
                    }
                    WorkInfo.State.BLOCKED, WorkInfo.State.FAILED -> {
                        isBatchError = true
                        binding.batchProgress.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), getString(R.string.db_not_init), Toast.LENGTH_LONG).show()
                        (activity as PalcoActivity).supportFragmentManager.beginTransaction()
                            .replace(R.id.container, NoConnectionFragment())
                            .commit()
                        Log.d(tag, "checkWorker failed/blocked in ${workInfo.runAttemptCount}")
                    }
                    else -> Log.d(tag, "checkWorker canceled in ${workInfo.runAttemptCount}")
                }
            })
    }

    private fun checkIfBatchCanRun() {
        if (isBatchError == true) {
            Toast.makeText(requireContext(), getString(R.string.batch_error_string), Toast.LENGTH_LONG).show()
            (activity as PalcoActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.container, NoConnectionFragment())
                .commit()
        }
        else if (networkMonitor?.isNetworkAvailable() == false) {
            Toast.makeText(requireContext(), getString(R.string.no_connection_hint), Toast.LENGTH_LONG).show()
            (activity as PalcoActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.container, NoConnectionFragment())
                .commit()
        }
        else {
            Toast.makeText(requireContext(), getString(R.string.db_init), Toast.LENGTH_LONG).show()
            setScrapeBatch()
        }
    }

}