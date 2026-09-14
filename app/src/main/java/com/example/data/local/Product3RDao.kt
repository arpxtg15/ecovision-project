package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface Product3RDao {

    @Query("SELECT * FROM product_3r_catalog ORDER BY name ASC")
    fun getAll3RProducts(): Flow<List<Product3REntity>>

    @Query("""
        SELECT * FROM product_3r_catalog 
        WHERE name LIKE '%' || :query || '%' 
           OR brandOrType LIKE '%' || :query || '%'
           OR resinCodeOrStandard LIKE '%' || :query || '%'
           OR category LIKE '%' || :query || '%'
           OR threeRClassification LIKE '%' || :query || '%'
        ORDER BY name ASC
    """)
    fun searchProducts(query: String): Flow<List<Product3REntity>>

    @Query("SELECT * FROM product_3r_catalog WHERE threeRClassification = :classification ORDER BY name ASC")
    fun getProductsByClassification(classification: String): Flow<List<Product3REntity>>

    @Query("SELECT * FROM product_3r_catalog WHERE id = :id LIMIT 1")
    fun getProductById(id: String): Flow<Product3REntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<Product3REntity>)

    @Query("SELECT COUNT(*) FROM product_3r_catalog")
    suspend fun countProducts(): Int
}
