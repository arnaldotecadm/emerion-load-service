package br.com.vercel.emerionloadservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class EmerionLoadServiceApplication

fun main(args: Array<String>) {
    runApplication<EmerionLoadServiceApplication>(*args)
}
