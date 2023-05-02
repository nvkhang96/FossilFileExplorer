package com.nvkhang96.fossilfileexplorer.feature_file_explorer.domain.use_case

interface GetPathListUseCase {
    operator fun invoke(path: String, rootPath: String): List<Pair<String, String>>
}