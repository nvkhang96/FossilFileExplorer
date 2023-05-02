package com.nvkhang96.fossilfileexplorer.feature_file_explorer.domain.use_case

class GetPathListUseCaseImpl : GetPathListUseCase {
    override fun invoke(path: String, rootPath: String): List<Pair<String, String>> {
        return path
            .drop(rootPath.length)
            .split("/")
            .filter { it.isNotBlank() }
            .runningFold(Pair("Internal storage", rootPath)) { acc, s ->
                Pair(s, "${acc.second}/$s")
            }
    }
}