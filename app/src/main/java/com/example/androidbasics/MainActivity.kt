package com.example.androidbasics

import android.Manifest
import android.os.Bundle
import android.provider.ContactsContract
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.database.getStringOrNull
import com.example.androidbasics.ui.theme.AndroidBasicsTheme

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<ContactViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), 1)

        val projection = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
            ContactsContract.Contacts.LOOKUP_KEY
        )

        contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            projection,
            null,
            null,
            null
        )?.use { cursor ->

            if (cursor.count > 0) {
                val idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID)
                val nameColumn =
                    cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)
                val lookupColumn =
                    cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY)

                val contacts = mutableListOf<Contact>()

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val name = cursor.getString(nameColumn)
                    val lookup = cursor.getStringOrNull(lookupColumn)


//                    val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    val selection = "${ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY} = ?"
                    val args = arrayOf(lookup)

                    contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        selection,
                        args,
                        null
                    )?.use { innerCursor ->
                        val phoneColumn =
                            innerCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

                        if (innerCursor.moveToNext()) {
                            val phoneNumber = innerCursor.getString(phoneColumn)
                            contacts.add(Contact(id, name, phoneNumber))
                        }
                    }

                }

//                println(contacts)

                viewModel.updateContacts(contacts)
            }

        }



        enableEdgeToEdge()
        setContent {
            AndroidBasicsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LazyColumn(
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(
                            space = 16.dp,
                            alignment = Alignment.CenterVertically
                        )
                    ) {

                        items(viewModel.contacts) {

                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Text(text = "name: ${it.name}")
                                Text(text = "number: ${it.phoneNumber}")
                            }

                        }

                    }
                }
            }
        }
    }
}