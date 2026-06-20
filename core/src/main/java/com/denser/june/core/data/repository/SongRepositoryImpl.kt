package com.denser.june.core.data.repository

import com.denser.june.core.data.mappers.mapSonglinkResponseToSongDetails
import com.denser.june.core.data.remote.SonglinkApiService
import com.denser.june.core.data.remote.SpotifyScraper
import com.denser.june.core.data.remote.DeezerFetcher
import com.denser.june.core.data.remote.ItunesFetcher
import com.denser.june.core.domain.repository.SongRepository
import com.denser.june.core.domain.model.SongDetails
import com.denser.june.core.domain.preferences.PrivacyPreferences
import kotlinx.coroutines.flow.first

class SongRepositoryImpl(
    private val apiService: SonglinkApiService,
    private val spotifyScraper: SpotifyScraper,
    private val deezerFetcher: DeezerFetcher,
    private val itunesFetcher: ItunesFetcher
) : SongRepository {

    override suspend fun fetchSongDetails(url: String): Result<SongDetails> {
        return try {
            val response = apiService.getSongLinks(url)
            var details = mapSonglinkResponseToSongDetails(response)
                ?: return Result.failure(Exception("Could not parse song details"))

            var previewUrl: String? = null
            var previewProvider: String? = null

            val spotifyId = response.linksByPlatform["spotify"]
                ?.entityUniqueId
                ?.split("::")
                ?.lastOrNull()

            if (spotifyId != null) {
                previewUrl = spotifyScraper.fetchPreviewUrl(spotifyId)
                if (previewUrl != null) {
                    previewProvider = "Spotify"
                }
            }

            if (previewUrl == null) {
                val deezerId = response.linksByPlatform["deezer"]
                    ?.entityUniqueId
                    ?.split("::")
                    ?.lastOrNull()
                if (deezerId != null) {
                    previewUrl = deezerFetcher.fetchPreviewUrl(deezerId)
                    if (previewUrl != null) {
                        previewProvider = "Deezer"
                    }
                }
            }

            if (previewUrl == null) {
                val appleMusicId = (response.linksByPlatform["appleMusic"] ?: response.linksByPlatform["itunes"])
                    ?.entityUniqueId
                    ?.split("::")
                    ?.lastOrNull()
                if (appleMusicId != null) {
                    previewUrl = itunesFetcher.fetchPreviewUrl(appleMusicId)
                    if (previewUrl != null) {
                        previewProvider = "Apple Music"
                    }
                }
            }

            if (previewUrl != null) {
                details = details.copy(
                    previewUrl = previewUrl,
                    previewUrlProvider = previewProvider
                )
            }

            Result.success(details)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}