package ru.app.ShowMyGame.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.app.ShowMyGame.entities.User;
import ru.app.ShowMyGame.services.UserService;
import ru.app.ShowMyGame.helpers.SessionHelper;

import java.io.IOException;
import java.util.List;

@Controller
public class UserController
{
    @Autowired
    private UserService userService;
    @Autowired
    private SessionHelper sessionHelper;

    @GetMapping("/profile/{id}")
    public String userProfilePage(@PathVariable Integer id, Model model)
    {
        User user = userService.getUserById(id);

        model.addAttribute("user", user);
        return "profile-page";
    }
    @GetMapping("/profile/{id}/edit")
    public String editProfilePage(@PathVariable Integer id, Model model)
    {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "edit-profile";
    }

    @PostMapping("/profile/{id}/update")
    public String updateProfile(@PathVariable Integer id, @RequestParam String username, @RequestParam(required = false) String description, @RequestParam(required = false) MultipartFile avatarFile, RedirectAttributes redirectAttributes)
    {
        try
        {
            User user = userService.getUserById(id);
            user.setUsername(username);
            user.setDescription(description);
            userService.editUser(user, avatarFile);

            redirectAttributes.addFlashAttribute("success", "Профиль успешно обновлен!");
            return "redirect:/profile/" + id;

        }
        catch (Exception e)
        {
            redirectAttributes.addFlashAttribute("error", "Ошибка: " + e.getMessage());
            return "redirect:/profile/" + id + "/edit";
        }
    }

    @GetMapping("/api/user/{id}")
    @ResponseBody
    public User getUserById(@PathVariable("id") Integer id)
    {
        return userService.getUserById(id);
    }

    @GetMapping("/api/users")
    @ResponseBody
    public List<User> getAllUsers()
    {
        return userService.getAllUsers();
    }

    @PostMapping("/api/user")
    @ResponseBody
    public User addNewUser(@RequestPart("user") User user, @RequestPart(value = "rawPassword") String rawPassword, @RequestPart(value = "avatarFile", required = false) MultipartFile avatarFile) throws IOException
    {
        return userService.addNewUser(user, rawPassword, avatarFile);
    }

    @PutMapping("/api/user")
    @ResponseBody
    public User editUser(@RequestParam("id") Integer id, @RequestParam("username") String username, @RequestParam(value = "description", required = false) String description, @RequestPart(value = "avatarFile", required = false) MultipartFile avatarFile) throws IOException
    {
        User user = userService.getUserById(id);
        user.setUsername(username);
        user.setDescription(description);
        return userService.editUser(user, avatarFile);
    }

    @DeleteMapping("/api/user/{id}")
    @ResponseBody
    public String deleteUserById(@PathVariable("id") Integer id) throws IOException
    {
        userService.deleteUserById(id);
        return "Удален пользователь с id:" + id;
    }
}