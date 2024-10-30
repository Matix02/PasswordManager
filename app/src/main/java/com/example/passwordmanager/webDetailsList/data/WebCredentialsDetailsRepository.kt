package com.example.passwordmanager.webDetailsList.data

import android.util.Log
import com.example.passwordmanager.NewWebCredentialItem
import com.example.passwordmanager.webDetailsList.WebDetailsDataTransformer
import com.example.passwordmanager.webDetailsList.model.WebDetails
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface WebCredentialsDetailsRepository {

    suspend fun getWebDetailsList(): List<WebDetails>

    suspend fun findCredentialsBy(query: String): List<WebDetails>

    suspend fun addWebCredential(webCredential: NewWebCredentialItem)

    suspend fun saveWebCredential(oldIndexName: String, updatedWebCredential: NewWebCredentialItem)

    suspend fun deleteWebCredential(indexName: String)

}

private const val WEB_DETAILS_COLLECTION_NAME = "webcredentialsdetails"

class WebDetailsRepository @Inject constructor(
    private val db: FirebaseFirestore
) : WebCredentialsDetailsRepository { //Inject

    private val webDetailsDataMapper = WebDetailsDataTransformer() //inject this too

    override suspend fun getWebDetailsList(): List<WebDetails> {
        Log.d("MGG3", "START getWebDetailsList")
        return try {
            val querySnapshot = db.collection(WEB_DETAILS_COLLECTION_NAME).get().await()
            if (querySnapshot.isEmpty.not()) {
                webDetailsDataMapper.map(querySnapshot)
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("MGG3", "Fetching FirebaseFirestore Error = $e")
            emptyList()
        }
    }

    override suspend fun findCredentialsBy(query: String): List<WebDetails> {
        Log.d("MGG3", "START getWebDetailsList")
        return try {
            val querySnapshot = db.collection(WEB_DETAILS_COLLECTION_NAME).get().await()

            if (querySnapshot.isEmpty.not()) {
                webDetailsDataMapper.mapNFilterData(querySnapshot, query)
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("MGG3", "Fetching FirebaseFirestore Error = $e")
            emptyList()
        }
    }

    override suspend fun addWebCredential(webCredential: NewWebCredentialItem) {
        db.collection(WEB_DETAILS_COLLECTION_NAME)
            .add(webCredential)
            .addOnSuccessListener {
                Log.d("MGG3", "Successful of adding the Item")
            }
            .addOnFailureListener {
                Log.d("MGG3", "Failure of adding the Item")
            }
    }

    override suspend fun saveWebCredential(oldIndexName: String, updatedWebCredential: NewWebCredentialItem) {
        val item = HashMap<String, String>().apply {
            put("name", updatedWebCredential.name)
            put("username", updatedWebCredential.username)
            put("password", updatedWebCredential.password)
            if (updatedWebCredential.urlIcon.isNotEmpty()) put("logo", updatedWebCredential.urlIcon)
        }
        val id = db.collection(WEB_DETAILS_COLLECTION_NAME).whereEqualTo("name", oldIndexName)
            .get().await().documents.map { it.id }.takeIf { it.isNotEmpty() }?.first()

        Log.d("MGG3", "FoundNextOne = $id")

        id?.let {
            db.collection(WEB_DETAILS_COLLECTION_NAME).document(id)
                .set(item)
                .addOnSuccessListener {
                    Log.d("MGG3", "Successful of updating the Item")
                }
                .addOnFailureListener {
                    Log.d("MGG3", "Failure of updating  the Item")
                }
        } ?: Log.d("MGG3", "Id is null")
    }

    override suspend fun deleteWebCredential(indexName: String) {
        val id = db.collection(WEB_DETAILS_COLLECTION_NAME).whereEqualTo("name", indexName)
            .get().await().documents.map { it.id }.takeIf { it.isNotEmpty() }?.first()

        id?.let {
            db.collection(WEB_DETAILS_COLLECTION_NAME).document(it)
                .delete()
                .addOnSuccessListener { Log.d("MGG3", "DocumentSnapshot successfully deleted!") }
                .addOnFailureListener { e -> Log.w("MGG3", "Error deleting document", e) }
        }
    }

}