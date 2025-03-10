package com.kit.log.ui

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kit.log.R
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

internal class LogActivity : ComponentActivity() {

    private val viewModel by viewModels<LogViewModel>()
    private val logAdapter = LogAdapter()
    private lateinit var tagAdapter: TagAdapter
    private lateinit var rcyLog: RecyclerView
    private lateinit var rcyTag: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)
        initView()
        initData()
    }

    private fun initView() {
        tagAdapter = TagAdapter {
            clickTag(it)
        }
        rcyLog = findViewById(R.id.rcy_log)
        rcyTag = findViewById(R.id.rcy_tag)
        rcyLog.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = logAdapter
        }
        rcyTag.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = tagAdapter
        }
        findViewById<ImageView>(R.id.iv_back).setOnClickListener { finish() }
        findViewById<ImageView>(R.id.iv_menu).setOnClickListener { clickMenu() }
    }



    private fun clickTag(tag: String) {
        viewModel.loadLog(tag)
    }

    private fun clickMenu() {
        if (rcyTag.visibility == View.VISIBLE) {
            rcyTag.visibility = View.GONE
        } else {
            rcyTag.visibility = View.VISIBLE
        }
    }

    private fun initData() {
        lifecycleScope.launch {
            viewModel.tagMSF.collectLatest {
                tagAdapter.update(it)
            }
        }
        lifecycleScope.launch {
            viewModel.logMSF.collectLatest {
                logAdapter.update(it)
            }
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(
            0,
            R.anim.slide_out_top_top_bottom
        )
    }

    companion object {
        fun startActivity(activity: Activity) {
            val intent = Intent(activity, LogActivity::class.java)
            activity.startActivity(intent)
            activity.overridePendingTransition(
                R.anim.slide_in_bottom_to_top,
                0
            )
        }
    }
}

