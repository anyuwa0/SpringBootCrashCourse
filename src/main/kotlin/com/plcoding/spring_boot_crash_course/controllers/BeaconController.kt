package com.plcoding.spring_boot_crash_course.controllers

import com.plcoding.spring_boot_crash_course.controllers.BeaconController.BeaconResponse
import com.plcoding.spring_boot_crash_course.controllers.NoteController.NoteResponse
import com.plcoding.spring_boot_crash_course.database.model.Beacon
import com.plcoding.spring_boot_crash_course.database.model.Note
import com.plcoding.spring_boot_crash_course.database.repository.BeaconRepository
import com.plcoding.spring_boot_crash_course.database.repository.NoteRepository
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.time.Instant

// POST http://localhost:8085/notes
// GET http://localhost:8085/notes?ownerId=123
// DELETE http://localhost:8085/notes/123

@RestController
@RequestMapping("/beacons")
class BeaconController(
    private val repository: BeaconRepository,
    private val beaconRepository: BeaconRepository
) {

    data class BeaconRequest(
        val id: String?,
        @field:NotBlank(message = "Title can't be blank.")
        val macAddress: String,
        val distance: Float,
        val rssi: Int,
    )

    data class BeaconResponse(
        val id: String,
        val macAddress: String,
        val distance: Float,
        val rssi: Int,
        val createdAt: Instant,
    )

    @PostMapping
    fun save(
        @Valid @RequestBody body: BeaconRequest
    ): BeaconResponse {
        val ownerId = SecurityContextHolder.getContext().authentication.principal as String
        val note = repository.save(
            Beacon(
                id = body.id?.let { ObjectId(it) } ?: ObjectId.get(),
                macAddress = body.macAddress,
                distance = body.distance,
                rssi = body.rssi,
                createdAt = Instant.now(),
                ownerId = ObjectId(ownerId)
            )
        )

        return note.toResponse()
    }

    @GetMapping
    fun findByOwnerId(): List<BeaconResponse> {
        val ownerId = SecurityContextHolder.getContext().authentication.principal as String
        return repository.findByOwnerId(ObjectId(ownerId)).map {
            it.toResponse()
        }
    }

    @DeleteMapping(path = ["/{id}"])
    fun deleteById(@PathVariable id: String) {
        val note = beaconRepository.findById(ObjectId(id)).orElseThrow {
            IllegalArgumentException("Note not found")
        }
        val ownerId = SecurityContextHolder.getContext().authentication.principal as String
        if(note.ownerId.toHexString() == ownerId) {
            repository.deleteById(ObjectId(id))
        }
    }
}

private fun Beacon.toResponse(): BeaconController.BeaconResponse {
    return BeaconResponse(
        id = id.toHexString(),
        macAddress = macAddress,
        distance = distance,
        rssi = rssi,
        createdAt = createdAt
    )
}