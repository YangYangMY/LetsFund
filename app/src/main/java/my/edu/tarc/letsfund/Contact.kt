package my.edu.tarc.letsfund

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contact")
data class Contact (val name: String, @PrimaryKey val phone: String) {
    override fun toString(): String {
        return "$name : $phone"
    }
}
