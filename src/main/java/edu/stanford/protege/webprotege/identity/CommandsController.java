package edu.stanford.protege.webprotege.identity;

import edu.stanford.protege.webprotege.identity.project.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/identity-commands")
public class CommandsController {


    private final ProjectService projectService;

    public CommandsController(ProjectService projectService) {
        this.projectService = projectService;
    }


    @PostMapping(value = "/register-projects")
    public ResponseEntity<List<String>> registerProjects(@RequestBody List<String> projectIds) {
        List<String> registeredProjects = projectService.registerProjects(projectIds);
        return ResponseEntity.ok(registeredProjects);
    }
}
