package dev.freddiesilver.stocksim

/**
 * Generic repository interface for basic CRUD operations
 */
interface Repository<T> {
    fun findById(id: Long): T? // Find an entity by its ID

    fun findAll(): List<T> // Retrieve all entities

    fun update(entity: T) // Save a new or existing entity

    fun deleteById(id: Long) // Delete an entity by its ID

    fun clear() // Delete all entries
}
