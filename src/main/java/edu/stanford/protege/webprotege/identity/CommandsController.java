package edu.stanford.protege.webprotege.identity;

import edu.stanford.protege.webprotege.identity.ids.IdGenerationService;
import edu.stanford.protege.webprotege.identity.project.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/identity-commands")
public class CommandsController {


    private final ProjectService projectService;
    private final IdGenerationService idGenerationService;


    public CommandsController(ProjectService projectService,
                              IdGenerationService idGenerationService) {
        this.projectService = projectService;
        this.idGenerationService = idGenerationService;
    }


    @PostMapping(value = "/register-projects")
    public ResponseEntity<List<String>> registerProjects(@RequestBody List<String> projectIds) {
        List<String> registeredProjects = projectService.registerProjects(projectIds);
        return ResponseEntity.ok(registeredProjects);
    }


    @GetMapping("/id")
    public String generateId(@RequestBody String prefix) {
        return idGenerationService.generateUniqueId(prefix);
    }
}
