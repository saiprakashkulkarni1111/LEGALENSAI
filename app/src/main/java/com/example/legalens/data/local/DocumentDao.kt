package com.example.legalens.data.local

import com.example.legalens.data.model.ClauseEntity
import com.example.legalens.data.model.DocumentEntity
import com.example.legalens.data.model.TimelineEventEntity
import kotlinx.coroutines.flow.Flow

interface DocumentDao {
    fun getAllDocuments(): Flow<List<DocumentEntity>>
    suspend fun getDocumentById(id: String): DocumentEntity?
    fun getClausesForDocument(documentId: String): Flow<List<ClauseEntity>>
    suspend fun getClausesForDocumentDirect(documentId: String): List<ClauseEntity>
    fun getTimelineEventsForDocument(documentId: String): Flow<List<TimelineEventEntity>>
    fun getAllTimelineEvents(): Flow<List<TimelineEventEntity>>
    suspend fun insertDocument(document: DocumentEntity)
    suspend fun insertClauses(clauses: List<ClauseEntity>)
    suspend fun insertTimelineEvents(events: List<TimelineEventEntity>)
    suspend fun deleteDocumentById(id: String)
}
