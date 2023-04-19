package my.edu.tarc.letsfund

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import my.edu.tarc.letsfund.Contact
import my.edu.tarc.letsfund.ContactDao

class ContactRepository(private val contactDao: ContactDao){
    //Room execute all queries on a separate thread
    val allContacts: LiveData<List<Contact>> = contactDao.getAllContact()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun add(contact: Contact){
        contactDao.insert(contact)
    }

    @WorkerThread
    suspend fun delete(contact: Contact){
        contactDao.delete(contact)
    }

    @WorkerThread
    suspend fun update(contact: Contact){
        contactDao.update(contact)
    }

    @WorkerThread
    suspend fun deleteAll() {
        contactDao.deleteAll()
    }

    @WorkerThread
    suspend fun uploadContacts(id: String){
        //TODO: Sync local contact to the Cloud Database
        if (allContacts.isInitialized) {
            val database = Firebase.database("https://contact-c7ad7-default-rtdb.asia-southeast1.firebasedatabase.app/").reference

            if (!allContacts.value!!.isEmpty()) {
                allContacts.value!!.forEach {
                    database.child(id).child(it.phone).setValue(it)
                }
            }
        }
    }

}