package br.com.vercel.emerionloadservice.service

import br.com.vercel.emerionloadservice.model.Icms
import br.com.vercel.emerionloadservice.repository.IcmsQueryRepository
import br.com.vercel.emerionloadservice.repository.mapper.IcmsMapper.toModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class IcmsService(
    private val icmsQueryRepository: IcmsQueryRepository
) {

    fun getAllIcms(pageable: Pageable): Page<Icms> {
        return icmsQueryRepository.findAllPaged(pageable).toModel()
    }

    fun getIcmsByKey(codicm: String, tipicm: String): Icms {
        return icmsQueryRepository.findByKey(codicm, tipicm)?.toModel()
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }
}
