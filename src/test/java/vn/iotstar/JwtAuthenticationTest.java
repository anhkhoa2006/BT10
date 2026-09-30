package vn.iotstar;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import vn.iotstar.models.LoginResponse;
import vn.iotstar.models.LoginUserModel;
import vn.iotstar.models.RegisterUserModel;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "security.jwt.secret-key=3cfa76ef14937c1c0ea519f8fc057a80fcd04a7420f8e8bcd0a7567c272e007b",
        "security.jwt.expiration-time=3600000"
})
class JwtAuthenticationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCompleteJwtFlow() throws Exception {
        // 1. Sign up new user
        RegisterUserModel registerUser = new RegisterUserModel();
        registerUser.setEmail("trungnh@hcmute.edu.vn");
        registerUser.setPassword("123456");
        registerUser.setFullName("Nguyen Huu Trung");

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("trungnh@hcmute.edu.vn"))
                .andExpect(jsonPath("$.fullName").value("Nguyen Huu Trung"));

        // 2. Login to get JWT Token
        LoginUserModel loginUser = new LoginUserModel();
        loginUser.setEmail("trungnh@hcmute.edu.vn");
        loginUser.setPassword("123456");

        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.expiresIn").value(3600000))
                .andReturn();

        String responseString = loginResult.getResponse().getContentAsString();
        LoginResponse loginResponse = objectMapper.readValue(responseString, LoginResponse.class);
        String token = loginResponse.getToken();

        // 3. Access protected endpoint /users/me with Bearer Token
        mockMvc.perform(get("/users/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("trungnh@hcmute.edu.vn"))
                .andExpect(jsonPath("$.fullName").value("Nguyen Huu Trung"));

        // 4. Access protected endpoint /users with Bearer Token
        mockMvc.perform(get("/users")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("trungnh@hcmute.edu.vn"));

        // 5. Access protected endpoint WITHOUT token should fail (Forbidden / 403)
        mockMvc.perform(get("/users/me"))
                .andExpect(status().isForbidden());
    }
}
