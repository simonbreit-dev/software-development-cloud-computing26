package de.quadflal.sdfccbackend;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@SpringBootTest
@ActiveProfiles("test")
public abstract class AbstractOpenApiContractTest {

    protected static final String JSON = MediaType.APPLICATION_JSON_VALUE;
    protected static final UUID RESOURCE_ID = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6");
    protected static final UUID RESTAURANT_ID = UUID.fromString("4fa85f64-5717-4562-b3fc-2c963f66afa6");

    @Autowired
    private WebApplicationContext webApplicationContext;

    protected MockMvc mockMvc;

    @BeforeEach
    void setUpMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }
}
