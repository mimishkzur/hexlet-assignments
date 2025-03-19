package exercise.controller;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import org.instancio.Instancio;
import org.instancio.Select;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import java.util.HashMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.datafaker.Faker;
import exercise.repository.TaskRepository;
import exercise.model.Task;

// BEGIN
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
// END
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Faker faker;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TaskRepository taskRepository;

    @Test
    public void testWelcomePage() throws Exception {
        var result = mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThat(body).contains("Welcome to Spring!");
    }

    @Test
    public void testIndex() throws Exception {
        var result = mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    // BEGIN
    private Task generateTask() {
        Task task = new Task();
        task.setTitle(faker.lorem().word());
        task.setDescription(faker.lorem().sentence(5));
        return task;
    }

    @Test
    public void testShow() throws Exception {
        Task task = generateTask();
        taskRepository.save(task);

        mockMvc.perform(get("/tasks/{id}", task.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(om.writeValueAsString(task)));
    }

    @Test
    public void testCreate() throws Exception {
        Task task = generateTask();

        var request = post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(task));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        var tasks = taskRepository.findAll();
        assertThat(tasks).hasSize(1);

        var actualTask = tasks.get(0);
        assertThat(actualTask.getTitle()).isEqualTo(task.getTitle());
        assertThat(actualTask.getDescription()).isEqualTo(task.getDescription());
        assertThat(actualTask.getCreatedAt()).isNotNull();
        assertThat(actualTask.getUpdatedAt()).isNotNull();
    }

    @Test
    public void testUpdate() throws Exception {
        Task task = generateTask();
        taskRepository.save(task);

        Task updatedTask = generateTask();

        var request = put("/tasks/{id}", task.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(updatedTask));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var actualTask = taskRepository.findById(task.getId()).get();

        assertThat(actualTask.getTitle()).isEqualTo(updatedTask.getTitle());
        assertThat(actualTask.getDescription()).isEqualTo(updatedTask.getDescription());
        assertThat(actualTask.getUpdatedAt()).isAfterOrEqualTo(task.getUpdatedAt());
    }

    @Test
    public void testDelete() throws Exception {
        Task task = generateTask();
        taskRepository.save(task);

        mockMvc.perform(delete("/tasks/{id}", task.getId()))
                .andExpect(status().isOk());

        assertThat(taskRepository.findAll()).isEmpty();
    }
    // END
}
