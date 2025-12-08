package ru.app.ShowMyGame.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.app.ShowMyGame.entities.User;
import ru.app.ShowMyGame.repositories.UserRepository;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
public class UserService
{
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FileService fileService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User getUserById(Integer id)
    {
        return userRepository.getUserById(id).orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }
    public List<User> getAllUsers()
    {
        return userRepository.getAllUsers();
    }
    public User addNewUser(User user, String rawPassword, MultipartFile avatarFile) throws IOException
    {
        if (userRepository.getUserByEmail(user.getEmail()).isPresent())
        {
            throw new RuntimeException("Пользователь с таким email уже существует");
        }
        User newUser = userRepository.addNewUser(user);
        String hashedPassword = passwordEncoder.encode(rawPassword);
        newUser.setPasswordHash(hashedPassword);
        if (avatarFile != null && !avatarFile.isEmpty())
        {
            String fileName = fileService.storeUserAvatar(avatarFile, newUser);
            newUser.setAvatarFileName(fileName);
        }
        return userRepository.editUser(newUser);
    }
    public User editUser(User user, MultipartFile avatarFile) throws IOException
    {
        User existUser = userRepository.getUserById(user.getId()).orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        existUser.setUsername(user.getUsername());
        existUser.setEmail(user.getEmail());
        existUser.setPasswordHash(user.getPasswordHash());
        existUser.setRole(user.getRole());
        existUser.setDescription(user.getDescription());
        if (avatarFile != null && !avatarFile.isEmpty())
        {
            String fileName = fileService.storeUserAvatar(avatarFile, existUser);
            existUser.setAvatarFileName(fileName);
        }
        return userRepository.editUser(existUser);
    }
    public void deleteUserById(Integer id)
    {
        User user = userRepository.getUserById(id).orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        fileService.deleteUserAvatar(user);
        userRepository.deleteUser(user);
    }
    public User logIn(String email, String rawPassword)
    {
        User user = userRepository.getUserByEmail(email).orElseThrow(() -> new RuntimeException("Пользователь с таким адресом электронной почты не найден"));
        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash()))
        {
            throw new RuntimeException("Неверный пароль");
        }
        return user;
    }

    public void changeUserPassword(User user, String oldPassword, String newPassword)
    {
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash()))
        {
            throw new RuntimeException("Неверный текущий пароль");
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.editUser(user);
    }
}
