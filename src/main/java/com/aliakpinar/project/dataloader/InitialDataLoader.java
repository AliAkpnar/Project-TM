package com.aliakpinar.project.dataloader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.aliakpinar.project.model.Role;
import com.aliakpinar.project.model.Task;
import com.aliakpinar.project.model.User;
import com.aliakpinar.project.service.RoleService;
import com.aliakpinar.project.service.TaskService;
import com.aliakpinar.project.service.UserService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class InitialDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private UserService userService;
    private TaskService taskService;
    private RoleService roleService;
    private final Logger logger = LoggerFactory.getLogger(InitialDataLoader.class);
    @SuppressWarnings("unused")
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Value("${default.admin.mail}")
    private String defaultAdminMail;
    @Value("${default.admin.name}")
    private String defaultAdminName;
    @Value("${default.admin.password}")
    private String defaultAdminPassword;
    @Value("${default.admin.image}")
    private String defaultAdminImage;

    @Autowired
    public InitialDataLoader(UserService userService, TaskService taskService, RoleService roleService) {
        this.userService = userService;
        this.taskService = taskService;
        this.roleService = roleService;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        //ROLES 
        roleService.createRole(new Role("ADMIN"));
        roleService.createRole(new Role("USER"));
        roleService.findAll().stream().map(role -> "saved role: " + role.getRole()).forEach(logger::info);

        //USERS 
        
        User admin = new User(
                defaultAdminMail,
                defaultAdminName,
                defaultAdminPassword,
                defaultAdminImage);
        userService.createUser(admin);
        userService.changeRoleToAdmin(admin);

        
        userService.createUser(new User(
                "ali@dev.com",
                "Ali",
                "12345",
                "images/user.png"));
        
        userService.createUser(new User(
                "ozan@dev.com",
                "Ozan",
                "12345",
                "images/user.png"));
		 
        userService.findAll().stream()
                .map(u -> "saved user: " + u.getName())
                .forEach(logger::info);


        LocalDate today = LocalDate.now();

        taskService.createTask(new Task(
                "Fix the problems on extranet front everyday",
                "NEGATIVE",
                today.minusDays(13),
                true,
                userService.getUserByEmail("ali@dev.com").getName(),
                userService.getUserByEmail("ali@dev.com")
        ));
        
        taskService.createTask(new Task(
                "develop the backend service for SMS event & listener","OK",
                today.plusDays(8),
                true,
                userService.getUserByEmail("ozan@dev.com").getName(),
                userService.getUserByEmail("ozan@dev.com")
        ));

        taskService.findAll().stream().map(t -> "saved task: '" + t.getName()
                + "' for owner: " + getOwnerNameOrNoOwner(t)).forEach(logger::info);
    }

    private String getOwnerNameOrNoOwner(Task task) {
        return task.getOwner() == null ? "no owner" : task.getOwner().getName();
    }
}
