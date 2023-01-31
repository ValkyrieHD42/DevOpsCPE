package fr.takima.training.sampleapplication.IT;

import fr.takima.training.simpleapi.SimpleApiApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(classes = {SimpleApiApplication.class})
class DepartmentControllerTestIT {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @Sql({"/InsertData.sql"})
    void testGetDepartmentByName() throws Exception {
        mockMvc.perform(get("/departments/ASI/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", equalTo(1)))
                .andExpect(jsonPath("name", equalTo("ASI")));
    }

    @Test
    @Sql({"/InsertData.sql"})
    void testGetNonExistingDepartmentByName() throws Exception {
        mockMvc.perform(get("/departments/NIMPORTEQUOI/"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Sql({"/InsertData.sql"})
    void testGetDepartmentStudentsByName() throws Exception {
        mockMvc.perform(get("/departments/ASI/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", equalTo(1)))
                .andExpect(jsonPath("$[0].firstname", equalTo("Gautier")))
                .andExpect(jsonPath("$[1].lastname", equalTo("Le Bloas")))
                .andExpect(jsonPath("$[1].department.id", equalTo(1)))
                .andExpect(jsonPath("$[1].department.name", equalTo("ASI")));
    }

    @Test
    @Sql({"/InsertData.sql"})
    void testGetNonExistingDepartmentStudentsByName() throws Exception {
        mockMvc.perform(get("/departments/NIMPORTEQUOI/students"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Sql({"/InsertData.sql"})
    void testGetDepartmentCountByName() throws Exception {
        mockMvc.perform(get("/departments/ASI/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", equalTo(48)));
    }

    @Test
    @Sql({"/InsertData.sql"})
    void testGetNonExistingDepartmentCountsByName() throws Exception {
        mockMvc.perform(get("/departments/NIMPORTEQUOI/count"))
                .andExpect(status().isNotFound());
    }
}
