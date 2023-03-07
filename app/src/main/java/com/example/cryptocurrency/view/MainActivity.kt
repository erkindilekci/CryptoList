package com.example.cryptocurrency.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptocurrency.adapter.RecyclerViewAdapter
import com.example.cryptocurrency.databinding.ActivityMainBinding
import com.example.cryptocurrency.model.CryptoModel
import com.example.cryptocurrency.service.CryptoAPI
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.*
import retrofit2.*
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), RecyclerViewAdapter.Listener{
    private lateinit var binding: ActivityMainBinding

    private val BASE_URL = "https://raw.githubusercontent.com/"
    private lateinit var cryptoModals: ArrayList<CryptoModel>
    private var recyclerViewAdapter : RecyclerViewAdapter? = null
    private var compositeDisposable = CompositeDisposable()
    private var job: Job? = null

    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        println("Error: ${throwable.localizedMessage}")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        loadData()
    }

    private fun loadData() {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build().create(CryptoAPI::class.java)

        job = CoroutineScope(Dispatchers.IO).launch {
            val response = retrofit.getData()

            withContext(Dispatchers.Main + exceptionHandler) {
                if (response.isSuccessful) {
                    response.body().let {
                        cryptoModals = ArrayList(it)
                        cryptoModals.let {
                            recyclerViewAdapter = RecyclerViewAdapter(it, this@MainActivity)
                            binding.recyclerView.adapter = recyclerViewAdapter
                        }
                    }
                }
            }
        }


    }


    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
    }

    override fun onItemClick(cryptoModel: CryptoModel) {
        Toast.makeText(this,"Clicked : ${cryptoModel.currency}", Toast.LENGTH_LONG ).show()
    }

}