package edu.kcg.web3.finalproject.config

import edu.kcg.web3.finalproject.FinalProjectApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer

class ServletInitializer : SpringBootServletInitializer() {

    override fun configure(application: SpringApplicationBuilder): SpringApplicationBuilder {
        return application.sources(FinalProjectApplication::class.java)
    }

}
