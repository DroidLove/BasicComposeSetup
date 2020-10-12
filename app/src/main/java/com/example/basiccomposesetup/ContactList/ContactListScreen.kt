package com.example.basiccomposesetup.ContactList

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumnForIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ContactListScreen(viewModel: ContactListViewModel) {
    val apiResponse: ArrayList<String>? by viewModel.apiResponse.observeAsState()

    Surface(color = MaterialTheme.colors.background) {
        viewModel.getContactListing()
        handlingUI(apiResponse)
    }
}

@Composable
private fun handlingUI(result: ArrayList<String>?) {

    result?.let {
        LazyColumnForIndexed(
            items = it,
        ) { index, item ->
            ContactCard(item)
        }
    }?: showLoader()
}

@Composable
fun showLoader() {
    Column(
        modifier = Modifier.fillMaxWidth().fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        CircularProgressIndicator()
    }
}

@Composable
fun ContactCard(name: String) {
    Card(shape = RoundedCornerShape(8.dp),
        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp,
            start = 4.dp, end = 4.dp)) {
        Column(Modifier.fillMaxWidth()) {
            Text(text = "$name",
                modifier = Modifier.padding(all = 8.dp))
        }
    }
}