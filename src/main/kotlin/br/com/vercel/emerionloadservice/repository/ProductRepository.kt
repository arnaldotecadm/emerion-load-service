package br.com.vercel.emerionloadservice.repository

import br.com.vercel.emerionloadservice.data.DummyEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.util.Objects

@Repository
interface ProductRepository : PagingAndSortingRepository<DummyEntity, Long>{

    @Query(
        nativeQuery = true,
        value = """
            select * from geremp
        """
    )
    fun getProducts() : List<Any>
}