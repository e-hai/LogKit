package com.kit.log.ui

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.kit.log.R
import com.orhanobut.logger.Logger
import com.orhanobut.logger.Utils

class LogAdapter : RecyclerView.Adapter<LogAdapter.VH>() {

    private var dataList: List<LogInfo> = emptyList()

    fun update(data: List<LogInfo>) {
        dataList = data
        notifyDataSetChanged()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val context = recyclerView.context
        // 设置长按监听器
        recyclerView.setOnTouchListener(object : View.OnTouchListener {
            val gestureDetector =
                GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                    override fun onLongPress(e: MotionEvent) {
                        super.onLongPress(e)
                        val childView = recyclerView.findChildViewUnder(e.x, e.y)
                        if (childView != null) {
                            val position = recyclerView.getChildAdapterPosition(childView)
                            if (position != RecyclerView.NO_POSITION) {
                                val itemText = dataList.get(position).message
                                copyToClipboard(recyclerView, itemText)
                            }
                        }
                    }
                })

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                return gestureDetector.onTouchEvent(event)
            }
        })
    }

    private fun copyToClipboard(recyclerView: RecyclerView, text: String) {
        val clipboardManager =
            recyclerView.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("Copied Text", text)
        clipboardManager.setPrimaryClip(clipData)
        // 可以在这里添加一个 Toast 提示用户文本已复制
        Toast.makeText(recyclerView.context, "Log 已复制到粘贴板", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding =
            LayoutInflater.from(parent.context).inflate(R.layout.adapter_logs, parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.updateItemView(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }


    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun updateItemView(data: LogInfo) {
            itemView.findViewById<TextView>(R.id.tv_tag).text = data.tag
            itemView.findViewById<TextView>(R.id.tv_date).text = data.dateString
            itemView.findViewById<TextView>(R.id.tv_msg).text = data.message
            val color = when (Utils.logLevel(data.priority)) {
                Logger.VERBOSE -> {
                    itemView.resources.getColor(R.color.verbose)
                }

                Logger.DEBUG -> {
                    itemView.resources.getColor(R.color.debug)
                }

                Logger.INFO -> {
                    itemView.resources.getColor(R.color.info)
                }

                Logger.WARN -> {
                    itemView.resources.getColor(R.color.warning)
                }

                Logger.ERROR -> {
                    itemView.resources.getColor(R.color.error)
                }

                else -> {
                    Color.BLACK
                }
            }
            itemView.setBackgroundColor(color)
        }
    }
}
