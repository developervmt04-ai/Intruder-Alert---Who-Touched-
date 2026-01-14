package com.example.thirdeye.di

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.room.Room
import com.android.billingclient.api.BillingClient
import com.example.thirdeye.billing.BillingManager
import com.example.thirdeye.data.localData.LockImagePrefs
import com.example.thirdeye.data.localData.PurchasePrefs
import com.example.thirdeye.data.localData.db.IntruderDB
import com.example.thirdeye.data.localData.db.IntruderDao
import com.example.thirdeye.ui.intruders.IntruderRepo
import com.example.thirdeye.ui.onboarding.paywall.PlansRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)


object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext app: Context
    ): IntruderDB =
        Room.databaseBuilder(
            app,
            IntruderDB::class.java,
            "intruder_db"
        ).build()
    @Provides

    fun provideIntruderDao(db: IntruderDB): IntruderDao =db.intruderDao()
    @Provides
    @Singleton

    fun provideIntruderRepo(dao: IntruderDao): IntruderRepo = IntruderRepo(dao)

    @Provides
    @Singleton
    fun providesLockImagePrefs(
        @ApplicationContext app: Context
    ): LockImagePrefs = LockImagePrefs(app)
    @Provides
    @Singleton
    fun providePlansRepo(): PlansRepo = PlansRepo()

    @Provides
    @Singleton
    fun providePurchasePrefs(@ApplicationContext context: Context): PurchasePrefs{

        return PurchasePrefs(context)
    }
    @Provides
    @Singleton
    fun provideBillingClient(context: Context): BillingClient{
        return BillingClient.newBuilder(context).enablePendingPurchases().build()
    }



    @Provides
    @Singleton
    fun provideBillingManager(
        @ApplicationContext context: Context,
        prefs: PurchasePrefs
    ): BillingManager {
        return BillingManager(context, prefs)
    }



}