package my.edu.tarc.letsfund

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import my.edu.tarc.letsfund.ContactRepository

class ContactViewModel (application: Application): AndroidViewModel(application) {
    //LiveData gives us updated contacts when they change
    var contactList : LiveData<List<Contact>>
    private val repository: ContactRepository
    var selectedIndex : Int = -1

    init {
        val contactDao = ContactDatabase.getDatabase(application).contactDao()
        repository = ContactRepository(contactDao)
        contactList = repository.allContacts
    }

    fun addContact(contact: Contact) = viewModelScope.launch{
         repository.add(contact)
    }

    fun updateContact(contact : Contact) = viewModelScope.launch {
        repository.update(contact)
    }

    fun deleteContact(contact : Contact) = viewModelScope.launch {
        repository.delete(contact)
    }

    fun deleteAll() = viewModelScope.launch{
        repository.deleteAll()
    }
}
