package com.example.passwordmanager.authentication.pin

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.example.passwordmanager.UserStatus
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object UserStatusProtoFileSerializer : Serializer<UserStatus> {

    override val defaultValue: UserStatus
        get() = UserStatus.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): UserStatus {
        try {
            return UserStatus.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto file.", exception)
        }
    }

    override suspend fun writeTo(t: UserStatus, output: OutputStream) = t.writeTo(output)

}