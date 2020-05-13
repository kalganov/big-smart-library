package leti.project.search.controller

import leti.project.search.IntegrationTest
import org.junit.Test

import org.junit.Assert.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

class BookControllerTest : IntegrationTest() {

    @Test
    fun findAll() {
        mockMvc.perform(MockMvcRequestBuilders.get("api/search/all"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun findByTitle() {
    }

    @Test
    fun delete() {
    }
}