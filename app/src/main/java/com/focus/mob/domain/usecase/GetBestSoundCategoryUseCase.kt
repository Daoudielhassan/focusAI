package com.focus.mob.domain.usecase

import com.focus.mob.data.SessionRecord
import javax.inject.Inject

class GetBestSoundCategoryUseCase @Inject constructor() {

    /**
     * Returns the [SessionRecord.ambiance] value that appears most often.
     * Returns null if there are no sessions.
     */
    operator fun invoke(sessions: List<SessionRecord>): String? {
        if (sessions.isEmpty()) return null

        return sessions
            .filter { it.ambiance.isNotBlank() }
            .groupingBy { it.ambiance }
            .eachCount()
            .maxByOrNull { it.value }
            ?.key
    }
}
