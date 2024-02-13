package com.gdd.domain.usecase.relay

import com.gdd.domain.model.Point
import com.gdd.domain.model.relay.RecommendedPath
import com.gdd.domain.repository.ProjectRepository
import javax.inject.Inject

class RecommendPathUseCase @Inject constructor(
    private val projectRepository: ProjectRepository
){
    suspend operator fun invoke(startCoordinate: Point, endCoordinate: Point): Result<RecommendedPath>{
        val res = projectRepository.recommendPath(startCoordinate, endCoordinate)
        /*
        res.getOrNull()?.let {
            it.shortestPath.forEachIndexed{ idx, p ->
                println("{" +
                        "   x: ${p.x}, " +
                        "   y: ${p.y}" +
                        "},")
            }

        }
         */
        return res
    }
}