package com.example.legalens.data.local

import com.example.legalens.data.model.CaseRecordEntity
import com.example.legalens.data.model.LegalProvisionEntity
import kotlinx.coroutines.flow.Flow

interface LegalDao {
    fun getAllProvisions(): Flow<List<LegalProvisionEntity>>
    suspend fun getAllProvisionsDirect(): List<LegalProvisionEntity>
    fun getAllCases(): Flow<List<CaseRecordEntity>>
    suspend fun getAllCasesDirect(): List<CaseRecordEntity>
    suspend fun getCaseById(id: String): CaseRecordEntity?
    suspend fun insertProvisions(provisions: List<LegalProvisionEntity>)
    suspend fun insertCases(cases: List<CaseRecordEntity>)
}
