package com.example.passwordmanager.webDetailsList

import com.example.passwordmanager.webDetailsList.model.WebDetails
import com.google.firebase.firestore.QuerySnapshot

interface WebDetailsTransformer {
    fun map(snapshot: QuerySnapshot?): List<WebDetails>
    fun mapNFilterData(snapshot: QuerySnapshot?, query: String): List<WebDetails>
}

class WebDetailsDataTransformer : WebDetailsTransformer {
    override fun map(snapshot: QuerySnapshot?): List<WebDetails> {
        val list = snapshot?.documents?.map {
            WebDetails(
                name = it["name"].toString(), //PRIVATE CONST VAL
                username = it["username"].toString(),//PRIVATE CONST VAL
                password = it["password"].toString(),//PRIVATE CONST VAL
                icon = it["logo"].toString(),//PRIVATE CONST VAL
                belongToAdmin = it["belongToAdmin"].toString().toBoolean()//PRIVATE CONST VAL
            )
        }.orEmpty()
        return list
    }

    override fun mapNFilterData(snapshot: QuerySnapshot?, query: String): List<WebDetails> {
        return map(snapshot).filter { it.name.contains(query) }
    }

}