package com.example.water_app.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.water_app.R
import com.example.water_app.databinding.ActivityJoinBinding
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

class JoinActivity : AppCompatActivity() {

    val TAG = "JoinActivity"
    private lateinit var binding: ActivityJoinBinding
    private var preferenceHelper: PreferenceHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)

        binding = ActivityJoinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            super.onBackPressed()
        }

        binding.btnJoin!!.setOnClickListener {
            registerMe()
        }

    }

    private fun registerMe() {
        val mbr_id = binding!!.edtId.text.toString()
        val mbr_password = binding!!.edtPass.text.toString()
        val mbr_nm = binding!!.edtName.text.toString()
        val mbr_ncnm = binding!!.edtNick.text.toString()
        val mbr_gen = 'Y'
        val mbr_tel = binding!!.edtTel.text.toString()
        val mbr_birth = binding!!.edtBirth.text.toString()
        val mbr_email = binding!!.edtEmail.text.toString()

        val retrofit = Retrofit.Builder()
            .baseUrl(UserInterface.USER_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
        val api = retrofit.create(UserInterface::class.java)
        val call = api.getUserRegist(
            mbr_id, mbr_password, mbr_nm, mbr_ncnm, mbr_gen, mbr_tel, mbr_birth, mbr_email)
        call!!.enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if (response.isSuccessful && response.body() != null) {
                    Log.e("onSuccess", response.body()!!)
                    val jsonResponse = response.body()
                    try {
                        parseRegData(jsonResponse)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                Log.e(TAG, "에러 = " + t.message)
            }
        })
    }

    @Throws(JSONException::class)
    private fun parseRegData(response: String?) {
        val jsonObject = JSONObject(response)
        if (jsonObject.optString("status") == "true") {
            saveInfo(response)
            Toast.makeText(this@JoinActivity, "회원가입 성공", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this@JoinActivity, jsonObject.getString("message"), Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun saveInfo(response: String?) {
        preferenceHelper!!.putIsLogin(true)
        try {
            val jsonObject = JSONObject(response)
            if (jsonObject.getString("status") == "true") {
                val dataArray = jsonObject.getJSONArray("data")
                for (i in 0 until dataArray.length()) {
                    val dataobj = dataArray.getJSONObject(i)
                    preferenceHelper!!.putId(dataobj.getString("mbr_id"))
                    preferenceHelper!!.putPass(dataobj.getString("mbr_pass"))
                    preferenceHelper!!.putPass(dataobj.getString("mbr_nm"))
                    preferenceHelper!!.putPass(dataobj.getString("mbr_ncnm"))
                    preferenceHelper!!.putPass(dataobj.getString("mbr_tel"))
                    preferenceHelper!!.putPass(dataobj.getString("mbr_brthdy"))
                    preferenceHelper!!.putPass(dataobj.getString("mbr_email"))
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}