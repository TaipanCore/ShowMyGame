package ru.app.ShowMyGame.helpers;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ru.app.ShowMyGame.entities.User;
import ru.app.ShowMyGame.services.UserService;

@Component
public class SessionHelper
{
    @Autowired
    private UserService userService;

    public User getCurrentUser()
    {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null)
        {
            HttpSession session = attributes.getRequest().getSession(false);
            if (session != null)
            {
                Integer userId = (Integer) session.getAttribute("userId");
                if (userId != null)
                {
                    return userService.getUserById(userId);
                }
            }
        }
        return null;
    }

    public boolean isAuthenticated()
    {
        return getCurrentUser() != null;
    }
}