package com.plcoding.spring_boot_crash_course.database.repository

import com.plcoding.spring_boot_crash_course.database.model.Beacon
import com.plcoding.spring_boot_crash_course.database.model.Note
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface BeaconRepository: MongoRepository<Beacon, ObjectId> {
    fun findByOwnerId(ownerId: ObjectId): List<Beacon>
}