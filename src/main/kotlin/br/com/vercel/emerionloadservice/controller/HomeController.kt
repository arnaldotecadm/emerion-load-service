package br.com.vercel.emerionloadservice.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController()
class HomeController {

    @GetMapping("ping")
    fun ping(): String {
        return "ping"
    }
}