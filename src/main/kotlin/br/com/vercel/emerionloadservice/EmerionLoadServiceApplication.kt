package br.com.vercel.emerionloadservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
class EmerionLoadServiceApplication

fun main(args: Array<String>) {
	runApplication<EmerionLoadServiceApplication>(*args)
}
