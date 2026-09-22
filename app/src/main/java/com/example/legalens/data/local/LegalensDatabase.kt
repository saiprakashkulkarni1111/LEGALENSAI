package com.example.legalens.data.local

import android.content.Context

open class LegalensDatabase(
    private val documentDao: DocumentDao,
    private val legalDao: LegalDao
) {
    fun documentDao(): DocumentDao = documentDao
    fun legalDao(): LegalDao = legalDao

    companion object {
        @Volatile
        private var INSTANCE: LegalensDatabase? = null

        fun getInstance(context: Context): LegalensDatabase {
            return INSTANCE ?: synchronized(this) {
                val docDao = LocalDocumentDao()
                val legDao = LocalLegalDao()
                val instance = LegalensDatabase(docDao, legDao)
                INSTANCE = instance
                instance
            }
        }
    }
}
