package com.nvkhang96.fossilfileexplorer.di

import com.nvkhang96.fossilfileexplorer.feature_file_explorer.data.local.file_folder_provider.FileFolderProvider
import com.nvkhang96.fossilfileexplorer.feature_file_explorer.data.local.file_folder_provider.FileFolderProviderImpl
import com.nvkhang96.fossilfileexplorer.feature_file_explorer.data.repository.FileFolderRepositoryImpl
import com.nvkhang96.fossilfileexplorer.feature_file_explorer.domain.repository.FileFolderRepository
import com.nvkhang96.fossilfileexplorer.feature_file_explorer.domain.use_case.FileFolderUseCases
import com.nvkhang96.fossilfileexplorer.feature_file_explorer.domain.use_case.GetFileFolder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFileFolderProvider(): FileFolderProvider {
        return FileFolderProviderImpl()
    }

    @Provides
    @Singleton
    fun provideFileFolderRepository(fileFolderProvider: FileFolderProvider): FileFolderRepository {
        return FileFolderRepositoryImpl(fileFolderProvider)
    }

    @Provides
    @Singleton
    fun provideFileFolderUseCases(repository: FileFolderRepository): FileFolderUseCases {
        return FileFolderUseCases(
            getFileFolder = GetFileFolder(repository)
        )
    }
}