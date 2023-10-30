package de.ksbrwsk.people;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Optional;

import static de.ksbrwsk.people.Constants.API;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = {PeopleController.class})
@AutoConfigureRestDocs
public class MvcRestDocsTest {

    private static final String API_ONE = API + "/4711";

    @RegisterExtension
    final RestDocumentationExtension restDocumentation = new RestDocumentationExtension();

    MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    PersonRepository personRepository;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    void handleNotFound() throws Exception {
        this.mockMvc.perform(get("/api/peple").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(document("handle-not-found"));

    }

    @Test
    void handleFindAll() throws Exception {
        when(this.personRepository.findAll())
                .thenReturn(List.of(new Person("4711", "Name1"), new Person("4712", "Name2")));
        this.mockMvc.perform(get(API)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("handle-find-all",
                        responseFields(
                                fieldWithPath("[].id").description("The person's id"),
                                fieldWithPath("[].name").description("The person's name"))));
    }

    @Test
    void handleFindById() throws Exception {
        when(this.personRepository.findById("4711"))
                .thenReturn(Optional.of(new Person("4711", "Name")));
        this.mockMvc.perform(get(API_ONE)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("handle-find-by-id",
                        responseFields(
                                fieldWithPath("id").description("The person's id"),
                                fieldWithPath("name").description("The person's name"))));
    }

    @Test
    void handleFindByIdNotFound() throws Exception {
        when(this.personRepository.findById("4711"))
                .thenReturn(Optional.empty());
        this.mockMvc.perform(get(API_ONE)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(document("handle-find-by-id-not-found"));
    }

    @Test
    void handleDeleteByIdNotFound() throws Exception {
        when(this.personRepository.findById("4711"))
                .thenReturn(Optional.empty());
        this.mockMvc.perform(delete(API_ONE)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(document("handle-delete-by-id-not-found"));
    }

    @Test
    void handleDeleteById() throws Exception {
        when(this.personRepository.findById("4711"))
                .thenReturn(Optional.of(new Person("4711", "Name")));
        doNothing().when(this.personRepository).delete(any());
        this.mockMvc.perform(delete(API_ONE)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(document("handle-delete-by-id"));
    }

    @Test
    void handleUpdateValid() throws Exception {
        when(this.personRepository.findById("4711"))
                .thenReturn(Optional.of(new Person("4711", "Name")));
        when(this.personRepository.save(new Person("4711", "Update")))
                .thenReturn(new Person("4711", "Update"));
        this.mockMvc.perform(put(API_ONE)
                        .content(this.objectMapper.writeValueAsString(new Person("4711", "Update")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("handle-update-valid",
                        requestFields(fieldWithPath("id").description("The person's id"),
                                fieldWithPath("name").description("The person's name")
                        ),
                        responseFields(
                                fieldWithPath("id").description("The person's id"),
                                fieldWithPath("name").description("The person's name"))));
    }

    @Test
    void handleUpdateInvalid() throws Exception {
        when(this.personRepository.findById("4711"))
                .thenReturn(Optional.of(new Person("4711", "Name")));
        this.mockMvc.perform(put(API_ONE)
                        .content(this.objectMapper.writeValueAsString(new Person("4711", "NameMuchToooooooLong")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(document("handle-update-invalid",
                        requestFields(fieldWithPath("id").description("The person's id"),
                                fieldWithPath("name").description("The person's name")
                        )));
    }

    @Test
    void handleUpdateNotFound() throws Exception {
        when(this.personRepository.findById("4711"))
                .thenReturn(Optional.empty());
        this.mockMvc.perform(put(API_ONE)
                        .content(this.objectMapper.writeValueAsString(new Person("4711", "Name")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(document("handle-update-not-found",
                        requestFields(fieldWithPath("id").description("The person's id"),
                                fieldWithPath("name").description("The person's name")
                        )));
    }

    @Test
    void handleCreateValid() throws Exception {
        when(this.personRepository.save(new Person("Name")))
                .thenReturn(new Person("4711", "Name"));
        this.mockMvc.perform(post(API)
                        .content(this.objectMapper.writeValueAsString(new Person("Name")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", API_ONE))
                .andDo(document("handle-create-valid",
                        requestFields(
                                fieldWithPath("id").description("The person's id"),
                                fieldWithPath("name").description("The person's name")
                        ),
                        responseFields(
                                fieldWithPath("id").description("The person's id"),
                                fieldWithPath("name").description("The person's name"))));
    }

    @Test
    void handleCreateInvalid() throws Exception {
        this.mockMvc.perform(post(API)
                        .content(this.objectMapper.writeValueAsString(new Person("NameMuchToooooooLong")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(document("handle-create-invalid",
                        requestFields(
                                fieldWithPath("id").description("The person's id"),
                                fieldWithPath("name").description("The person's name")
                        )));
    }

}
