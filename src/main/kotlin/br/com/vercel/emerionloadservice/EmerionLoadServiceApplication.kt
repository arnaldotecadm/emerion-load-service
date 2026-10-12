package br.com.vercel.emerionloadservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class EmerionLoadServiceApplication

fun main(args: Array<String>) {
    runApplication<EmerionLoadServiceApplication>(*args)
}
