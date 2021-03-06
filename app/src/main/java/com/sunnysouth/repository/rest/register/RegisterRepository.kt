package com.sunnysouth.repository.rest.register

import android.content.Context
import com.sunnysouth.config.BASE_URL
import com.sunnysouth.repository.rest.RetrofitClient
import com.sunnysouth.repository.models.RegisterSuccess
import com.sunnysouth.repository.models.User
import com.sunnysouth.viewmodel.RegisterViewModel
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RegisterRepository(private val viewModel: RegisterViewModel) {

    fun getRegister(user: User, context: Context){

        val retrofit = RetrofitClient().getClient(BASE_URL)
        val service = retrofit?.create<RegisterService>(RegisterService::class.java)

        service?.goRegister(user)?.enqueue(object : Callback<RegisterSuccess>{
            override fun onFailure(call: Call<RegisterSuccess>, t: Throwable) {
                var messages = mutableListOf<String>()
                messages.add("Register error")
                viewModel.setRegisterError(messages)
            }

            override fun onResponse(call: Call<RegisterSuccess>, response: Response<RegisterSuccess>) {

                if(response.code() != 200 && response.code() != 201){
                    var messages = mutableListOf<String>()
                    val jObjError = JSONObject(response.errorBody()?.string())
                    jObjError.keys().forEach  {
                        val errors: JSONArray = jObjError.getJSONArray(it)
                        for (i in 0 until errors.length()) {
                            messages.add("${it}: ${errors[i].toString()}")
                        }
                    }
                    viewModel.setRegisterError(messages)
                }else{
                    val registerSuccess: RegisterSuccess? = response.body()
                    viewModel.setRegisterSuccess(registerSuccess)
                }
            }
        })
    }
}