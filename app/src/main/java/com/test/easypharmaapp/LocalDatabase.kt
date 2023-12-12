package com.test.easypharmaapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class LocalDatabase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "EasyPharma.db"
        const val DATABASE_VERSION = 1
        const val TABLE_MEDICINES = "tbl_medicines"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"

        const val TABLE_CART = "tbl_cart"
        const val COLUMN_MEDICINE_ID = "medicine_id"
        const val COLUMN_MEDICINE_NAME = "medicine_name"

    }



    override fun onCreate(db: SQLiteDatabase) {
        val create_medicines_table = """CREATE TABLE $TABLE_MEDICINES (  $COLUMN_ID INTEGER PRIMARY KEY,  $COLUMN_NAME TEXT )""".trimIndent()
        val create_cart_table = """ CREATE TABLE $TABLE_CART ( $COLUMN_MEDICINE_ID TEXT , $COLUMN_MEDICINE_NAME TEXT )  """.trimIndent()

        db.execSQL(create_cart_table)
        db.execSQL(create_medicines_table)

        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_MEDICINES", null)
        if (cursor != null && cursor.moveToFirst()) {
            val count = cursor.getInt(0)
            if (count == 0) {
                db.execSQL("INSERT INTO $TABLE_MEDICINES ($COLUMN_ID, $COLUMN_NAME) VALUES (1, 'Aspirin')")
                db.execSQL("INSERT INTO $TABLE_MEDICINES ($COLUMN_ID, $COLUMN_NAME) VALUES (2, 'Paracetamol')")
                db.execSQL("INSERT INTO $TABLE_MEDICINES ($COLUMN_ID, $COLUMN_NAME) VALUES (3, 'Ibuprofen')")
                db.execSQL("INSERT INTO $TABLE_MEDICINES ($COLUMN_ID, $COLUMN_NAME) VALUES (4, 'Amoxicillin')")
                db.execSQL("INSERT INTO $TABLE_MEDICINES ($COLUMN_ID, $COLUMN_NAME) VALUES (5, 'Metformin')")
                db.execSQL("INSERT INTO $TABLE_MEDICINES ($COLUMN_ID, $COLUMN_NAME) VALUES (6, 'Amlodipine')")
                db.execSQL("INSERT INTO $TABLE_MEDICINES ($COLUMN_ID, $COLUMN_NAME) VALUES (7, 'Simvastatin')")
                db.execSQL("INSERT INTO $TABLE_MEDICINES ($COLUMN_ID, $COLUMN_NAME) VALUES (8, 'Omeprazole')")
                db.execSQL("INSERT INTO $TABLE_MEDICINES ($COLUMN_ID, $COLUMN_NAME) VALUES (9, 'Losartan')")
                db.execSQL("INSERT INTO $TABLE_MEDICINES ($COLUMN_ID, $COLUMN_NAME) VALUES (10, 'Acetaminophen')")
                db.execSQL("INSERT INTO $TABLE_MEDICINES ($COLUMN_ID, $COLUMN_NAME) VALUES (11, 'Hydrochlorothiazide')")
                db.execSQL("INSERT INTO $TABLE_MEDICINES ($COLUMN_ID, $COLUMN_NAME) VALUES (12, 'Gabapentin')")
                db.execSQL("INSERT INTO $TABLE_MEDICINES ($COLUMN_ID, $COLUMN_NAME) VALUES (13, 'Sertraline')")
                db.execSQL("INSERT INTO $TABLE_MEDICINES ($COLUMN_ID, $COLUMN_NAME) VALUES (14, 'Metoprolol')")
                db.execSQL("INSERT INTO $TABLE_MEDICINES ($COLUMN_ID, $COLUMN_NAME) VALUES (15, 'Atorvastatin')")
                db.execSQL("INSERT INTO $TABLE_MEDICINES ($COLUMN_ID, $COLUMN_NAME) VALUES (16, 'Albuterol')")
                db.execSQL("INSERT INTO $TABLE_MEDICINES ($COLUMN_ID, $COLUMN_NAME) VALUES (17, 'Lisinopril')")
                db.execSQL("INSERT INTO $TABLE_MEDICINES ($COLUMN_ID, $COLUMN_NAME) VALUES (18, 'Fluoxetine')")
                db.execSQL("INSERT INTO $TABLE_MEDICINES ($COLUMN_ID, $COLUMN_NAME) VALUES (19, 'Pantoprazole')")
                db.execSQL("INSERT INTO $TABLE_MEDICINES ($COLUMN_ID, $COLUMN_NAME) VALUES (20, 'Ciprofloxacin')")


            }
        }
        cursor?.close()
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MEDICINES")
        onCreate(db)
    }

    fun get_all_medicines(): List<Medicine> {
        val m_list = mutableListOf<Medicine>()
        val db = this.readableDatabase
        val my_cursor = db.query(TABLE_MEDICINES, arrayOf(COLUMN_ID, COLUMN_NAME), null, null, null, null, null)
        with(my_cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(COLUMN_ID)).toString()
                val name = getString(getColumnIndexOrThrow(COLUMN_NAME))
                m_list.add(Medicine(id, name))
            }
        }
        my_cursor.close()
        return m_list
    }
    fun addToCart(medicineId: String,medicineName: String) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_MEDICINE_ID, medicineId)
            put(COLUMN_MEDICINE_NAME,medicineName)
        }
        db.insert(TABLE_CART, null, values)
        db.close()
    }

    fun get_cart(): List<String> {
        val db = this.readableDatabase
        val my_cursor = db.query("tbl_cart", arrayOf("medicine_id"), null, null, null, null, null)

        val ids = mutableListOf<String>()
        with(my_cursor) {
            while (moveToNext()) {
                val id = getString(getColumnIndexOrThrow("medicine_id"))
                ids.add(id)
            }
        }
        my_cursor.close()
        return ids
    }

    fun get_names_medicines(ids: List<String>): Map<String, String> {
        val db = this.readableDatabase
        val ids_holder = ids.joinToString(separator = ",", transform = { "?" })
        val my_cursor = db.rawQuery("SELECT id, name FROM $TABLE_MEDICINES WHERE id IN ($ids_holder)", ids.toTypedArray())
        val medicineNames = mutableMapOf<String, String>()
        with(my_cursor) {
            while (moveToNext()) {
                val id = getString(getColumnIndexOrThrow("id"))
                val name = getString(getColumnIndexOrThrow("name"))
                medicineNames[id] = name
            }
        }
        my_cursor.close()
        return medicineNames
    }


    fun deleteFromCart(medicineId: String) {
        val db = this.writableDatabase
        val whereClause = "$COLUMN_MEDICINE_ID=?"
        val whereArgs = arrayOf(medicineId)
        db.delete(TABLE_CART, whereClause, whereArgs)
        db.close()
    }


    fun get_cart_items(): List<Medicine> {
        val db = this.readableDatabase

        val query = "  SELECT * from $TABLE_CART ".trimIndent()
        val cursor = db.rawQuery(query, null)
        val cartItems = mutableListOf<Medicine>()

        with(cursor) {
            while (moveToNext()) {
                val id = getString(getColumnIndexOrThrow(COLUMN_MEDICINE_ID))
                val name = getString(getColumnIndexOrThrow(COLUMN_MEDICINE_NAME))
                cartItems.add(Medicine(id, name))
            }
        }
        cursor.close()
        return cartItems
    }

}

