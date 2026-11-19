package com.simpletv.player

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    // Change this URL to use a different playlist.
    private val playlistUrl = "https://romaxa55.github.io/world_ip_tv/output/index.m3u"

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private lateinit var adapter: ChannelAdapter
    private var all: List<Channel> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val list = findViewById<RecyclerView>(R.id.list)
        val progress = findViewById<ProgressBar>(R.id.progress)
        val status = findViewById<TextView>(R.id.status)
        val search = findViewById<EditText>(R.id.search)

        adapter = ChannelAdapter { channel ->
            startActivity(
                Intent(this, PlayerActivity::class.java).apply {
                    putExtra(PlayerActivity.EXTRA_URL, channel.url)
                    putExtra(PlayerActivity.EXTRA_NAME, channel.name)
                }
            )
        }
        list.layoutManager = LinearLayoutManager(this)
        list.adapter = adapter
        list.setHasFixedSize(true)

        search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = filter(s?.toString().orEmpty())
            override fun beforeTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
            override fun onTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
        })

        status.setOnClickListener { load(progress, status) }

        load(progress, status)
    }

    private fun filter(q: String) {
        val query = q.trim().lowercase()
        val filtered = if (query.isEmpty()) {
            all
        } else {
            all.filter {
                it.name.lowercase().contains(query) ||
                    (it.group?.lowercase()?.contains(query) == true)
            }
        }
        adapter.submit(filtered)
    }

    private fun load(progress: ProgressBar, status: TextView) {
        progress.visibility = View.VISIBLE
        status.visibility = View.GONE
        scope.launch {
            try {
                val text = withContext(Dispatchers.IO) {
                    val conn = (URL(playlistUrl).openConnection() as HttpURLConnection).apply {
                        connectTimeout = 15000
                        readTimeout = 15000
                        instanceFollowRedirects = true
                    }
                    conn.inputStream.bufferedReader().use { it.readText() }
                }
                all = withContext(Dispatchers.Default) { M3uParser.parse(text) }
                adapter.submit(all)
                progress.visibility = View.GONE
                if (all.isEmpty()) {
                    status.text = getString(R.string.no_channels)
                    status.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                progress.visibility = View.GONE
                status.text = getString(R.string.load_failed, e.message ?: "")
                status.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
