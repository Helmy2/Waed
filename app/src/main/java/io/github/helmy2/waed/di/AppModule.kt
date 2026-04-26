package io.github.helmy2.waed.di

import androidx.room.Room
import io.github.helmy2.waed.data.local.WaedDatabase
import io.github.helmy2.waed.data.local.migrations.MIGRATION_1_2
import io.github.helmy2.waed.data.local.dao.CustomerDao
import io.github.helmy2.waed.data.repository.CustomerRepository
import io.github.helmy2.waed.ui.screen.detail.CustomerDetailViewModel
import io.github.helmy2.waed.ui.screen.search.SearchViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            WaedDatabase::class.java,
            "waed_database"
        ).addMigrations(MIGRATION_1_2).build()
    }

    single<CustomerDao> {
        get<WaedDatabase>().customerDao()
    }

    singleOf(::CustomerRepository)

    viewModelOf(::SearchViewModel)
    viewModelOf(::CustomerDetailViewModel)
}