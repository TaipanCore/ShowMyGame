package ru.app.ShowMyGame.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import ru.app.ShowMyGame.entities.User;
import ru.app.ShowMyGame.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class AuthController
{
    @Autowired
    private UserService userService;

    @GetMapping({"/", "/auth"})
    public String authPage()
    {
        return "auth-page";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session)
    {
        session.invalidate();
        return "auth-page";
    }

    @PostMapping("/api/auth/register")
    @ResponseBody
    public ResponseEntity<?> register(@RequestParam("email") String email, @RequestParam("password") String password, @RequestParam("role") String roleStr, @RequestParam("username") String username, @RequestParam(value = "avatar", required = false) MultipartFile avatarFile)
    {
        try
        {
            User user = new User();
            user.setEmail(email);
            user.setUsername(username);
            user.setRole(User.UserRole.valueOf(roleStr));
            User savedUser = userService.addNewUser(user, password, avatarFile);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("id", savedUser.getId());
            response.put("email", savedUser.getEmail());
            response.put("username", savedUser.getUsername());
            response.put("role", savedUser.getRole().name());
            response.put("avatarFileName", savedUser.getAvatarFileName());
            response.put("message", "Регистрация успешна");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        catch (RuntimeException e)
        {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("success", false, "error", e.getMessage()));
        }
        catch (IOException e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("success", false, "error", "Ошибка загрузки аватара"));
        }
    }

    @PostMapping("/api/auth/login")
    @ResponseBody
    public ResponseEntity<?> login(@RequestParam("email") String email, @RequestParam("password") String password, HttpSession session)
    {
        try
        {
            User user = userService.logIn(email, password);

            session.setAttribute("userId", user.getId());
            session.setAttribute("userEmail", user.getEmail());
            session.setAttribute("username", user.getUsername());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("id", user.getId());
            response.put("email", user.getEmail());
            response.put("username", user.getUsername());
            response.put("avatarFileName", user.getAvatarFileName());
            response.put("message", "Вход выполнен успешно");

            return ResponseEntity.ok(response);

        }
        catch (RuntimeException e)
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "error", e.getMessage()));
        }
    }
}
