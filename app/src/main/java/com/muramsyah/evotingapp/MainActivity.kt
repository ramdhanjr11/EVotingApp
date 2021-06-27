package com.muramsyah.evotingapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.muramsyah.evotingapp.databinding.ActivityMainBinding
import com.muramsyah.evotingapp.model.CalonKahim
import com.muramsyah.evotingapp.model.Mahasiswa
import com.muramsyah.evotingapp.model.SetupSystem
import com.muramsyah.evotingapp.utils.FireBaseUtils
import com.muramsyah.evotingapp.viewModel.MainViewModel
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.CoroutineContext


class MainActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var binding: ActivityMainBinding

    private lateinit var chart: PieChart

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        chart = PieChart(this)

        binding.pieChart.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE

        viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(application))[MainViewModel::class.java]

        viewModel.dataUser.observe(this, { user ->
            binding.tvName.text = user.name
        })

        viewModel.dataCalonKahim.observe(this, { calonKahim ->
            binding.pieChart.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
            loadPieChartData(calonKahim)
        })

        viewModel.dataSystem.observe(this, { dataSystem ->
            setupSystem(dataSystem)
            Log.d("isVote", dataSystem.dateVote + " " + dataSystem.isVote)
            if (dataSystem.isVote) {
                binding.btn1.isEnabled = true
                binding.btn2.isEnabled = true
            } else {
                binding.btn1.isEnabled = false
                binding.btn2.isEnabled = false
            }
        })

        prepare()
        setupItemKahim()
        setupPieChart()
        setupExpandable()
    }

    private fun setupExpandable() {
        binding.include2.container.setOnClickListener {
            if (binding.expandableLayout.isExpanded) {
                binding.expandableLayout.collapse()
            } else {
                binding.expandableLayout.expand()
            }
        }

        binding.include3.container.setOnClickListener {
            if (binding.expandableLayout2.isExpanded) {
                binding.expandableLayout2.collapse()
            } else {
                binding.expandableLayout2.expand()
            }
        }

        binding.include4.container.setOnClickListener {
            if (binding.expandableLayout3.isExpanded) {
                binding.expandableLayout3.collapse()
                binding.include4.textView2.text = "Tekan untuk melihat.."
            } else {
                binding.expandableLayout3.expand()
                binding.include4.textView2.text = "Tekan untuk menutup.."
            }
        }
    }

    private fun setupPieChart() {
        chart.isDrawHoleEnabled = true
        chart.setUsePercentValues(true)
        chart.setEntryLabelTextSize(12f)
        chart.setEntryLabelColor(resources.getColor(R.color.colorTextTitle))
        chart.setCenterTextColor(resources.getColor(R.color.colorTextTitle))
        chart.centerText = "Calon Kahim"
        chart.setCenterTextSize(12f)
        chart.description.isEnabled = false

        val legend = chart.legend
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.setDrawInside(false)
        legend.isEnabled = true
    }

    private fun loadPieChartData(calonKahim: ArrayList<CalonKahim>) {
        val entries = ArrayList<PieEntry>(2)
        if (calonKahim[0].voteCount < 1 && calonKahim[1].voteCount < 1) {
            entries.add(PieEntry(1F, "Calon 1"))
            entries.add(PieEntry(1F, "Calon 2"))
        } else {
            entries.add(PieEntry((calonKahim[0].voteCount.toFloat()/300)*100, "Calon 1"))
            entries.add(PieEntry((calonKahim[1].voteCount.toFloat()/300)*100, "Calon 2"))
        }

        Log.d("calongKahim2", calonKahim[1].voteCount.toFloat().toString())

        val colors = ArrayList<Int>()
        for (i in ColorTemplate.MATERIAL_COLORS) {
            colors.add(i)
        }

        val dataSet = PieDataSet(entries, "")
        dataSet.setColors(colors)

        val data = PieData(dataSet)
        data.setDrawValues(true)
        data.setValueFormatter(PercentFormatter(chart))
        data.setValueTextSize(12f)
        data.setValueTextColor(resources.getColor(R.color.colorTextTitle))

        chart.data = data
        chart.invalidate()

        binding.pieChart.data = data
    }

    private fun setupItemKahim() {
        binding.include2.container.backgroundTintList = resources.getColorStateList(R.color.cardViewColor1)
        binding.include3.container.backgroundTintList = resources.getColorStateList(R.color.cardViewColor2)
        binding.include4.container.backgroundTintList = resources.getColorStateList(R.color.cardViewColor3)
    }

    private fun prepare() {
        binding.btn1.setOnClickListener(this)
        binding.btn2.setOnClickListener(this)
        binding.btnLogout.setOnClickListener(this)
    }

    private fun setupSystem(dataSystem: SetupSystem) {
        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        val countDate = dataSystem.dateVote
        val now = Date()

        val date: Date = sdf.parse(countDate)
        val currentTime: Long = now.getTime()
        val newYearDate: Long = date.getTime()
        val countDownToNewYear = newYearDate - currentTime

        binding.mycountdown.start(countDownToNewYear)
        binding.mycountdown.setOnCountdownEndListener {
            Toast.makeText(this, "Waktu Voting Selesai", Toast.LENGTH_SHORT).show()
            showEndNotification(this, "Pemilihan Kahim", "Waktu voting telah selesai!", 1)
            binding.btn1.isEnabled = false
            binding.btn2.isEnabled = false
        }
    }

    private fun logout() {
        FireBaseUtils.auth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
        Toast.makeText(this, "Kamu telah keluar!", Toast.LENGTH_SHORT).show()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_1 -> viewModel.voteCalonKahim1()
            R.id.btn_2 -> viewModel.voteCalonKahim2()
            R.id.btn_logout -> logout()
        }
    }

    private fun showEndNotification(context: Context, title: String, message: String, notifId: Int) {
        val channelId = "Channel_1"
        val channelName = "Reminder channel"

        val notificationManagerCompat = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_baseline_notifications_white)
            .setAutoCancel(false)
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setColor(ContextCompat.getColor(context, android.R.color.transparent))
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setSound(alarmSound)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT)
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)
            builder.setChannelId(channelId)
            notificationManagerCompat.createNotificationChannel(channel)
        }

        val notification = builder.build()
        notificationManagerCompat.notify(notifId, notification)
    }
}