package br.com.vercel.emerionloadservice.data

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "geremp")
class DummyEntity {
    @Id
    private var codemp: Integer? = null
}