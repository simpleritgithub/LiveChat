package com.Samadhan.livechat.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class) // Application-wide
object FirebaseModule {

    @Provides
    fun provideFirebaseAuth(): FirebaseAuth {
        val auth = FirebaseAuth.getInstance()
        auth.setLanguageCode("en")
        return auth
    }

    @Provides
    fun provideFireStore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance() // Corrected the FireStore instance retrieval
    }


    @Provides
    fun provideFStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance() // Corrected the FireStore instance retrieval
    }

}