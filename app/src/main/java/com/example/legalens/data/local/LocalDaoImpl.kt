package com.example.legalens.data.local

import com.example.legalens.data.model.CaseRecordEntity
import com.example.legalens.data.model.ClauseEntity
import com.example.legalens.data.model.DocumentEntity
import com.example.legalens.data.model.LegalProvisionEntity
import com.example.legalens.data.model.TimelineEventEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import java.util.concurrent.ConcurrentHashMap

class LocalDocumentDao : DocumentDao {
    private val _documents = MutableStateFlow<List<DocumentEntity>>(emptyList())
    private val clausesStore = ConcurrentHashMap<String, MutableList<ClauseEntity>>()
    private val _clausesFlowMap = ConcurrentHashMap<String, MutableStateFlow<List<ClauseEntity>>>()
    private val _timelineEvents = MutableStateFlow<List<TimelineEventEntity>>(emptyList())

    override fun getAllDocuments(): Flow<List<DocumentEntity>> = _documents.asStateFlow()

    override suspend fun getDocumentById(id: String): DocumentEntity? {
        return _documents.value.find { it.id == id }
    }

    override fun getClausesForDocument(documentId: String): Flow<List<ClauseEntity>> {
        return _clausesFlowMap.getOrPut(documentId) {
            MutableStateFlow(clausesStore[documentId]?.toList() ?: emptyList())
        }.asStateFlow()
    }

    override suspend fun getClausesForDocumentDirect(documentId: String): List<ClauseEntity> {
        return clausesStore[documentId]?.toList() ?: emptyList()
    }

    override fun getTimelineEventsForDocument(documentId: String): Flow<List<TimelineEventEntity>> {
        return _timelineEvents.map { list -> list.filter { it.documentId == documentId } }
    }

    override fun getAllTimelineEvents(): Flow<List<TimelineEventEntity>> = _timelineEvents.asStateFlow()

    override suspend fun insertDocument(document: DocumentEntity) {
        val current = _documents.value.toMutableList()
        val index = current.indexOfFirst { it.id == document.id }
        if (index >= 0) {
            current[index] = document
        } else {
            current.add(0, document)
        }
        _documents.value = current
    }

    override suspend fun insertClauses(clauses: List<ClauseEntity>) {
        clauses.groupBy { it.documentId }.forEach { (docId, clauseList) ->
            val existing = clausesStore.getOrPut(docId) { mutableListOf() }
            clauseList.forEach { newClause ->
                val idx = existing.indexOfFirst { it.id == newClause.id }
                if (idx >= 0) {
                    existing[idx] = newClause
                } else {
                    existing.add(newClause)
                }
            }
            val flow = _clausesFlowMap.getOrPut(docId) { MutableStateFlow(emptyList()) }
            flow.value = existing.toList()
        }
    }

    override suspend fun insertTimelineEvents(events: List<TimelineEventEntity>) {
        val current = _timelineEvents.value.toMutableList()
        events.forEach { ev ->
            val idx = current.indexOfFirst { it.id == ev.id }
            if (idx >= 0) {
                current[idx] = ev
            } else {
                current.add(ev)
            }
        }
        _timelineEvents.value = current
    }

    override suspend fun deleteDocumentById(id: String) {
        _documents.value = _documents.value.filter { it.id != id }
        clausesStore.remove(id)
        _clausesFlowMap.remove(id)
        _timelineEvents.value = _timelineEvents.value.filter { it.documentId != id }
    }
}

class LocalLegalDao : LegalDao {
    private val _provisions = MutableStateFlow<List<LegalProvisionEntity>>(emptyList())
    private val _cases = MutableStateFlow<List<CaseRecordEntity>>(emptyList())

    override fun getAllProvisions(): Flow<List<LegalProvisionEntity>> = _provisions.asStateFlow()

    override suspend fun getAllProvisionsDirect(): List<LegalProvisionEntity> = _provisions.value

    override fun getAllCases(): Flow<List<CaseRecordEntity>> = _cases.asStateFlow()

    override suspend fun getAllCasesDirect(): List<CaseRecordEntity> = _cases.value

    override suspend fun getCaseById(id: String): CaseRecordEntity? {
        return _cases.value.find { it.id == id }
    }

    override suspend fun insertProvisions(provisions: List<LegalProvisionEntity>) {
        val current = _provisions.value.toMutableList()
        provisions.forEach { p ->
            val idx = current.indexOfFirst { it.id == p.id }
            if (idx >= 0) {
                current[idx] = p
            } else {
                current.add(p)
            }
        }
        _provisions.value = current
    }

    override suspend fun insertCases(cases: List<CaseRecordEntity>) {
        val current = _cases.value.toMutableList()
        cases.forEach { c ->
            val idx = current.indexOfFirst { it.id == c.id }
            if (idx >= 0) {
                current[idx] = c
            } else {
                current.add(c)
            }
        }
        _cases.value = current
    }
}
