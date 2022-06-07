package com.italkutalk.lab17

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.gson.Gson
import com.google.gson.JsonParser
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var btn_query: Button
    //定義資料結構存放 Server 回傳的資料
    class MyObject (val id:String, val name:String )





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_query = findViewById(R.id.btn_query)
        btn_query.setOnClickListener {
            //關閉按鈕避免再次查詢
            btn_query.isEnabled = false
            //發送請求
            sendRequest()
        }
    }
    //發送請求
    private fun sendRequest() {

        //本書原內容採用環保署空氣品質指標 API，但近期對方修改資料的取得方式，故範例提供更穩定的資料來源
        val url = "https://jsonplaceholder.typicode.com/comments?postId=1"

        //建立 Request.Builder 物件，藉由 url()將網址傳入，再建立 Request 物件
        val req = Request.Builder()
            .url(url)
            .build()
        //建立 OkHttpClient 物件，藉由 newCall()發送請求，並在 enqueue()接收回傳
        OkHttpClient().newCall(req).enqueue(object : Callback {
            //發送成功執行此方法
            override fun onResponse(call: Call, response: Response) {
                //使用 response.body?.string()取得 JSON 字串
                val json = response.body?.string()
                //建立 Gson 並使用其 fromJson()方法，將 JSON 字串以 MyObject 格式輸出

                //顯示結果
                val parser = JsonParser()
                //将Json字符串转换成JsonArray对象
                val JsonArray = parser.parse(json).asJsonArray

                //**********Gson进行数据解析与合并*************
                //Gson
                val gson = Gson()
                val userList = mutableListOf<MyObject>()
                //将数据添加到userList中
                for (user in JsonArray) {
                    val MyObject = gson.fromJson(user, MyObject::class.java)
                    userList.add(MyObject)
                }
                println(userList)
                //顯示結果
                //建立一個字串陣列，用於存放 SiteName 與 Status 資訊
                val items = arrayOfNulls<String>(userList.size)
                userList.forEachIndexed { index, data ->
                    items[index] = "名子：${data.id}, 編號：${data.name}"
                }
                //切換到主執行緒將畫面更新
                runOnUiThread {
                    //開啟按鈕可再次查詢
                    btn_query.isEnabled = true
                    //建立 AlertDialog 物件並顯示字串陣列
                    AlertDialog.Builder(this@MainActivity)
                        .setTitle("臺北市空氣品質")
                        .setItems(items, null)
                        .show()
                }

            }
                //發送失敗執行此方法
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread {
                        //開啟按鈕可再次查詢
                        btn_query.isEnabled = true
                        Toast.makeText(
                            this@MainActivity,
                            "查詢失敗$e", Toast.LENGTH_SHORT
                        ).show()
                    }
                }

        })
    }

}
