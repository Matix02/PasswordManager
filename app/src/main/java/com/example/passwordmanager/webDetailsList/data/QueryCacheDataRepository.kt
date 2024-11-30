package com.example.passwordmanager.webDetailsList.data

import com.example.passwordmanager.webDetailsList.model.QueryData
import javax.inject.Inject

private const val VALUE_TO_AUTO_GENERATE_ID = 0

interface QueryCacheDataRepository {

    suspend fun getQueryCacheList(): List<QueryData>

    suspend fun fetchCommonQueries(input: String): List<QueryData>

    suspend fun saveQuery(query: String)

    suspend fun deleteQuery(query: QueryData)

    suspend fun findQuery(queryText: String): QueryData?
}

class QueryDataRepository @Inject constructor(
    private val queryCacheDataDao: QueryCacheDataDao
) : QueryCacheDataRepository {

    override suspend fun getQueryCacheList(): List<QueryData> {
        return queryCacheDataDao.getQueries()
    }

    override suspend fun fetchCommonQueries(input: String): List<QueryData> {
        return queryCacheDataDao.fetchCommonQueries(input)
    }

    override suspend fun saveQuery(query: String) {
        if (findQuery(query)?.query != query) {
            queryCacheDataDao.saveQuery(QueryData(VALUE_TO_AUTO_GENERATE_ID, query))
        }
    }

    override suspend fun deleteQuery(query: QueryData) {
        queryCacheDataDao.deleteQuery(query)
    }

    override suspend fun findQuery(queryText: String): QueryData? {
        return queryCacheDataDao.findQueryBy(queryText)
    }

}