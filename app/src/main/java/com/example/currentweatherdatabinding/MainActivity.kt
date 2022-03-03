package com.example.currentweatherdatabinding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.InputStream
import java.net.URL
import java.util.*
import org.json.JSONException
import org.json.JSONObject
import androidx.databinding.DataBindingUtil
import com.example.currentweatherdatabinding.databinding.ActivityMainBinding

class Weather()
{
    var temperature="NULL"
    var wind_speed="NULL"
    var main_weather= R.drawable.nothing
}

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.weather=Weather()
    }
    suspend fun loadWeather() {
        binding.error.text=""
        val API_KEY = getResources().getString(R.string.API_key);
        //val City="Irkutsk"
        val City=binding.City.text
        val weatherURL = "https://api.openweathermap.org/data/2.5/weather?q="+City+"&appid="+API_KEY+"&units=metric";
        try {
            val stream = URL(weatherURL).getContent() as InputStream
            val data = Scanner(stream).nextLine()
            var obj = JSONObject(data)
            if(obj["cod"].toString() != "200") {
                binding.error.text="Ошибка в названии города"
            }

            else {
                val main=obj.getJSONObject("main")
                val weather=obj.getJSONArray("weather").getJSONObject(0)
                val wind=obj.getJSONObject("wind")
                val temperature=main["temp"]
                val mainweather=weather["main"]
                Log.d("temperature",temperature.toString())
                Log.d("main",mainweather.toString())
                val class_weather=Weather()
                class_weather.temperature=temperature.toString()
                if(mainweather.toString()=="Clouds")
                    class_weather.main_weather=R.drawable.cloudly
                if(mainweather.toString()=="Clear")
                    class_weather.main_weather=R.drawable.sunny
                if(mainweather.toString()=="Rain")
                    class_weather.main_weather=R.drawable.rainy
                class_weather.wind_speed=wind["speed"].toString()
                binding.weather=class_weather
            }
        }
        catch(e:Exception)
        {
            binding.error.text="Ошибка в названии города"
        }

    }


    public fun onClick(v: View) {

        // Используем IO-диспетчер вместо Main (основного потока)
        GlobalScope.launch (Dispatchers.IO) {
            loadWeather()
        }
    }
}