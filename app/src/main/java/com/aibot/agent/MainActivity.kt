package com.aibot.agent

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var messagesRecyclerView: RecyclerView
    private lateinit var inputEditText: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var apiKeyTextView: TextView
    
    private val messageList = mutableListOf<Message>()
    private lateinit var adapter: MessageAdapter
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
    
    // ⚠️ 请在此处配置你的 AI API Key
    private var apiKey = "YOUR_API_KEY_HERE"
    private val apiEndpoint = "https://api.openai.com/v1/chat/completions"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initViews()
        setupRecyclerView()
        setupListeners()
        
        // 添加欢迎消息
        addMessage("你好！我是你的 AI 助手。请配置 API Key 后开始对话。", false)
    }
    
    private fun initViews() {
        messagesRecyclerView = findViewById(R.id.messagesRecyclerView)
        inputEditText = findViewById(R.id.inputEditText)
        sendButton = findViewById(R.id.sendButton)
        loadingProgressBar = findViewById(R.id.loadingProgressBar)
        apiKeyTextView = findViewById(R.id.apiKeyTextView)
        
        // 显示 API Key 提示
        if (apiKey == "YOUR_API_KEY_HERE") {
            apiKeyTextView.visibility = View.VISIBLE
            apiKeyTextView.text = "⚠️ 请在 MainActivity.kt 中配置你的 API Key"
        } else {
            apiKeyTextView.visibility = View.GONE
        }
    }
    
    private fun setupRecyclerView() {
        adapter = MessageAdapter(messageList)
        messagesRecyclerView.layoutManager = LinearLayoutManager(this)
        messagesRecyclerView.adapter = adapter
    }
    
    private fun setupListeners() {
        sendButton.setOnClickListener { sendMessage() }
        
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage()
                true
            } else {
                false
            }
        }
    }
    
    private fun sendMessage() {
        val text = inputEditText.text.toString().trim()
        if (text.isEmpty()) return
        
        if (apiKey == "YOUR_API_KEY_HERE") {
            Toast.makeText(this, "请先配置 API Key", Toast.LENGTH_LONG).show()
            return
        }
        
        addMessage(text, true)
        inputEditText.text.clear()
        
        loadingProgressBar.visibility = View.VISIBLE
        sendButton.isEnabled = false
        
        lifecycleScope.launch {
            try {
                val response = callAI_API(text)
                withContext(Dispatchers.Main) {
                    addMessage(response, false)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    addMessage("错误：${e.message}", false)
                    Toast.makeText(this@MainActivity, "请求失败：${e.message}", Toast.LENGTH_SHORT).show()
                }
            } finally {
                withContext(Dispatchers.Main) {
                    loadingProgressBar.visibility = View.GONE
                    sendButton.isEnabled = true
                }
            }
        }
    }
    
    private suspend fun callAI_API(message: String): String = withContext(Dispatchers.IO) {
        val json = JSONObject()
        json.put("model", "gpt-3.5-turbo")
        
        val messages = JSONArray()
        messageList.filter { it.isUser }.forEach {
            messages.put(JSONObject().apply {
                put("role", "user")
                put("content", it.text)
            })
        }
        messages.put(JSONObject().apply {
            put("role", "user")
            put("content", message)
        })
        json.put("messages", messages)
        json.put("max_tokens", 1000)
        
        val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
        
        val request = Request.Builder()
            .url(apiEndpoint)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(requestBody)
            .build()
        
        val response = client.newCall(request).execute()
        
        if (!response.isSuccessful) {
            throw Exception("API 返回错误：${response.code}")
        }
        
        val responseBody = response.body?.string() ?: throw Exception("空响应")
        val jsonResponse = JSONObject(responseBody)
        val choices = jsonResponse.getJSONArray("choices")
        
        if (choices.length() > 0) {
            val choice = choices.getJSONObject(0)
            val messageObj = choice.getJSONObject("message")
            messageObj.getString("content")
        } else {
            throw Exception("无返回内容")
        }
    }
    
    private fun addMessage(text: String, isUser: Boolean) {
        messageList.add(Message(text, isUser))
        adapter.notifyItemInserted(messageList.size - 1)
        messagesRecyclerView.scrollToPosition(messageList.size - 1)
    }
}

data class Message(val text: String, val isUser: Boolean)
