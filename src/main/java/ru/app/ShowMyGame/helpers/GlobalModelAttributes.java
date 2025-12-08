package ru.app.ShowMyGame.helpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes
{

    @Autowired
    private SessionHelper sessionHelper;

    @ModelAttribute
    public void addAttributes(org.springframework.ui.Model model)
    {
        model.addAttribute("sessionHelper", sessionHelper);
    }
}
