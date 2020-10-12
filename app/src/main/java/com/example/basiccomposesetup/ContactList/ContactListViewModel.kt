/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.basiccomposesetup.ContactList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONObject

class ContactListViewModel() : ViewModel() {

    private val _apiResponse = MutableLiveData<ArrayList<String>>()
    val apiResponse: LiveData<ArrayList<String>> = _apiResponse

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    var parent: Job? = Job()

    fun getContactListing() {
        parent = GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
            val result = async(Dispatchers.IO) {
                _isLoading.postValue(true)
                apiCall()
            }
            val deferredValue = result.await()
            _isLoading.postValue(false)
            _apiResponse.value = mappingResult(deferredValue?.body()?.string())
        }
    }

    private fun apiCall(): Response? {
        var url = "https://limitless-lake-93364.herokuapp.com/hello"

        val request = Request.Builder()
            .url(url)
            .build()

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        var client = OkHttpClient.Builder()
            .addInterceptor(logging).build()

        return client.newCall(request).execute()
    }

    private fun mappingResult(result: String?): ArrayList<String> {
        var listEmployee: ArrayList<String> = ArrayList()
        var jsonObj = JSONObject(result)
        var jsonArray = JSONArray(jsonObj.get("employee").toString())

        for (i in 0 until jsonArray.length()) {
            val gsonObj = Gson().fromJson<EmployeeModel>(jsonArray.get(i)?.toString(), EmployeeModel::class.java)

            listEmployee.add(gsonObj.name)
        }

        return listEmployee
    }
}

@Suppress("UNCHECKED_CAST")
class ContactListViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactListViewModel::class.java)) {
            return ContactListViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
